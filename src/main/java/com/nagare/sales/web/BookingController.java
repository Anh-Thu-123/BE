package com.nagare.sales.web;

import com.nagare.common.error.ApiException;
import com.nagare.identity.model.Role;
import com.nagare.identity.security.SecurityUtils;
import com.nagare.sales.model.Booking;
import com.nagare.sales.model.Customer;
import com.nagare.sales.repo.BookingRepository;
import com.nagare.sales.service.BookingService;
import com.nagare.sales.service.CustomerService;
import com.nagare.scheduling.model.Assignment;
import com.nagare.scheduling.repo.AssignmentRepository;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final CustomerService customerService;
    private final AssignmentRepository assignmentRepository;
    private final MongoTemplate mongoTemplate;

    public BookingController(BookingService bookingService, BookingRepository bookingRepository,
                              CustomerService customerService, AssignmentRepository assignmentRepository,
                              MongoTemplate mongoTemplate) {
        this.bookingService = bookingService;
        this.bookingRepository = bookingRepository;
        this.customerService = customerService;
        this.assignmentRepository = assignmentRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public record HoldRequest(@NotBlank String departureId, String fullName, @NotBlank String phone,
                               String email, List<Booking.Pax> pax, List<Booking.AddOn> addOns) {}

    /** Khach tu giu cho - so dien thoai bat buoc, KHONG tu gop theo phone (bay so 2). */
    @PostMapping("/hold")
    public Booking hold(@RequestBody HoldRequest req) {
        var user = SecurityUtils.currentUserOrNull();
        String userId = user != null ? user.getId() : null;
        Customer customer = customerService.findOrCreateByPhoneForBooking(req.phone(), req.fullName(), userId, Customer.Source.WEB);

        Booking booking = new Booking();
        booking.setDepartureId(req.departureId());
        booking.setCustomerId(customer.getId());
        Booking.Contact contact = new Booking.Contact();
        contact.setFullName(req.fullName());
        contact.setPhone(req.phone());
        contact.setEmail(req.email());
        booking.setContact(contact);
        if (req.pax() != null) booking.setPax(req.pax());
        if (req.addOns() != null) booking.setAddOns(req.addOns());
        booking.setSource("WEB");
        return bookingService.hold(booking);
    }

    /** CSKH nhap don qua dien thoai/Zalo. */
    @PostMapping
    @PreAuthorize("hasAnyRole('MKT_STAFF','SECRETARY')")
    public Booking createByStaff(@RequestBody Booking booking) {
        booking.setSalesOwnerId(SecurityUtils.currentUser().getId());
        return bookingService.hold(booking);
    }

    /** Pham vi tu thu hep theo vai tro NGAY TRONG CAU TRUY VAN - khong loc sau khi lay het du lieu. */
    @GetMapping
    public List<Booking> list(@RequestParam(required = false) String status,
                               @RequestParam(required = false) String departureId,
                               @RequestParam(required = false) String q) {
        var user = SecurityUtils.currentUser();
        Query query = new Query();
        if (status != null) query.addCriteria(Criteria.where("status").is(status));
        if (departureId != null) query.addCriteria(Criteria.where("departureId").is(departureId));
        if (q != null) query.addCriteria(Criteria.where("contact.fullName").regex(q, "i"));

        if (user.getRole() == Role.TOUR_GUIDE) {
            List<String> myDepartureIds = assignmentRepository.findByEmployeeId(user.getEmployeeId()).stream()
                    .map(Assignment::getDepartureId).toList();
            query.addCriteria(Criteria.where("departureId").in(myDepartureIds));
        } else if (user.getRole() == Role.CUSTOMER) {
            query.addCriteria(Criteria.where("customerId").is(user.getCustomerId()));
        }
        return mongoTemplate.find(query, Booking.class);
    }

    @GetMapping("/me")
    public List<Booking> myBookings() {
        var user = SecurityUtils.currentUser();
        if (user.getCustomerId() == null) return List.of();
        return bookingRepository.findByCustomerId(user.getCustomerId());
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('MKT_STAFF','SECRETARY')")
    public Booking confirm(@PathVariable String id) {
        return bookingService.confirm(id, SecurityUtils.currentUser().getId());
    }

    public record CancelRequest(@NotBlank String reason, Double refundAmount) {}

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('MKT_STAFF','SECRETARY')")
    public Booking cancel(@PathVariable String id, @RequestBody CancelRequest req) {
        return bookingService.cancel(id, req.reason(), req.refundAmount(), SecurityUtils.currentUser().getId());
    }

    @PutMapping("/{id}/pax")
    public Booking updatePax(@PathVariable String id, @RequestBody List<Booking.Pax> pax) {
        return bookingService.updatePax(id, pax);
    }

    @PutMapping("/{id}/add-ons")
    public Booking updateAddOns(@PathVariable String id, @RequestBody List<Booking.AddOn> addOns) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> ApiException.notFound("Booking"));
        booking.setAddOns(addOns);
        return bookingRepository.save(booking);
    }

    @PostMapping("/{id}/payments")
    @PreAuthorize("hasAnyRole('MKT_STAFF','SECRETARY')")
    public Booking addPayment(@PathVariable String id, @RequestBody Booking.Payment payment) {
        payment.setRecordedBy(SecurityUtils.currentUser().getId());
        return bookingService.recordPayment(id, payment);
    }
}
