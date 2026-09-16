package com.nagare.sales.web;

import com.nagare.common.counter.SequenceGeneratorService;
import com.nagare.common.error.ApiException;
import com.nagare.identity.security.SecurityUtils;
import com.nagare.sales.model.Booking;
import com.nagare.sales.model.TourRequest;
import com.nagare.sales.repo.BookingRepository;
import com.nagare.sales.repo.TourRequestRepository;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Luong 2 - tour thiet ke rieng: yeu cau -> phan cong -> bao gia (moi lan sua gia la version moi) -> chot don. */
@RestController
@RequestMapping("/api/tour-requests")
public class TourRequestController {

    private final TourRequestRepository tourRequestRepository;
    private final BookingRepository bookingRepository;
    private final SequenceGeneratorService sequenceGeneratorService;

    public TourRequestController(TourRequestRepository tourRequestRepository, BookingRepository bookingRepository,
                                  SequenceGeneratorService sequenceGeneratorService) {
        this.tourRequestRepository = tourRequestRepository;
        this.bookingRepository = bookingRepository;
        this.sequenceGeneratorService = sequenceGeneratorService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TOUR_DESIGNER','MKT_MANAGER','MKT_STAFF')")
    public List<TourRequest> list(@RequestParam(required = false) String status,
                                   @RequestParam(required = false) String assigneeId) {
        if (status != null) return tourRequestRepository.findByStatus(TourRequest.Status.valueOf(status));
        if (assigneeId != null) return tourRequestRepository.findByAssigneeId(assigneeId);
        return tourRequestRepository.findAll();
    }

    public record AssignRequest(@NotBlank String assigneeId) {}

    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasRole('MKT_MANAGER')")
    public TourRequest assign(@PathVariable String id, @RequestBody AssignRequest req) {
        TourRequest tr = get(id);
        tr.setAssigneeId(req.assigneeId());
        tr.setStatus(TourRequest.Status.ASSIGNED);
        return tourRequestRepository.save(tr);
    }

    /** Moi lan sua gia la MOT VERSION MOI trong quotes[], khong ghi de - co bang chung khi tranh chap gia. */
    @PostMapping("/{id}/quotes")
    @PreAuthorize("hasRole('TOUR_DESIGNER')")
    public TourRequest addQuote(@PathVariable String id, @RequestBody TourRequest.Quote quote) {
        TourRequest tr = get(id);
        int nextVersion = tr.getQuotes().stream().mapToInt(TourRequest.Quote::getVersion).max().orElse(0) + 1;
        quote.setVersion(nextVersion);
        quote.setCreatedBy(SecurityUtils.currentUser().getId());
        tr.getQuotes().add(quote);
        tr.setStatus(TourRequest.Status.QUOTED);
        return tourRequestRepository.save(tr);
    }

    /** Sinh booking kind=CUSTOM tu DUNG phien ban bao gia khach da dong y - khong dinh ngay khoi hanh nao. */
    @PostMapping("/{id}/convert")
    @PreAuthorize("hasAnyRole('TOUR_DESIGNER','MKT_STAFF','SECRETARY')")
    public Booking convert(@PathVariable String id, @RequestParam int quoteVersion) {
        TourRequest tr = get(id);
        TourRequest.Quote quote = tr.getQuotes().stream().filter(q -> q.getVersion() == quoteVersion).findFirst()
                .orElseThrow(() -> ApiException.notFound("Phien ban bao gia"));

        Booking booking = new Booking();
        booking.setCode(sequenceGeneratorService.nextCode("BK"));
        booking.setKind(Booking.Kind.CUSTOM);
        booking.setTourRequestId(tr.getId());
        booking.setQuoteVersion(quoteVersion);
        booking.setCustomerId(tr.getCustomerId());
        booking.setContact(tr.getContact());
        booking.setStatus(Booking.Status.CONFIRMED);
        booking.getPricing().setTotal(quote.getTotal());
        Booking saved = bookingRepository.save(booking);

        tr.setConvertedBookingId(saved.getId());
        tr.setStatus(TourRequest.Status.WON);
        tourRequestRepository.save(tr);
        return saved;
    }

    private TourRequest get(String id) {
        return tourRequestRepository.findById(id).orElseThrow(() -> ApiException.notFound("Yeu cau tour"));
    }
}
