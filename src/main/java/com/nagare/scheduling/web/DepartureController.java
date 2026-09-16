package com.nagare.scheduling.web;

import com.nagare.common.error.ApiException;
import com.nagare.sales.model.Booking;
import com.nagare.sales.repo.BookingRepository;
import com.nagare.sales.service.BookingService;
import com.nagare.scheduling.model.Departure;
import com.nagare.scheduling.repo.DepartureRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/departures")
public class DepartureController {

    private final DepartureRepository departureRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    public DepartureController(DepartureRepository departureRepository, BookingRepository bookingRepository,
                                BookingService bookingService) {
        this.departureRepository = departureRepository;
        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
    }

    @PostMapping
    @PreAuthorize("hasRole('OPS_MANAGER')")
    public Departure create(@RequestBody Departure departure) {
        return departureRepository.save(departure);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('OPS_MANAGER')")
    public Departure update(@PathVariable String id, @RequestBody Departure patch) {
        Departure existing = departureRepository.findById(id).orElseThrow(() -> ApiException.notFound("Doan khoi hanh"));
        if (patch.getCapacity() > 0) existing.setCapacity(patch.getCapacity());
        if (patch.getStatus() != null) existing.setStatus(patch.getStatus());
        if (patch.getPriceAdult() > 0) existing.setPriceAdult(patch.getPriceAdult());
        if (patch.getPriceChild() > 0) existing.setPriceChild(patch.getPriceChild());
        if (patch.getPriceInfant() > 0) existing.setPriceInfant(patch.getPriceInfant());
        return departureRepository.save(existing);
    }

    @GetMapping("/calendar")
    public List<Departure> calendar(@RequestParam String from, @RequestParam String to) {
        return departureRepository.findByDepartDateBetween(LocalDate.parse(from), LocalDate.parse(to));
    }

    /** Doan chua du khach toi thieu - canh bao muc 15 ngay truoc khoi hanh, quyet dinh huy hay ghep thuoc Dieu hanh. */
    @GetMapping("/below-minimum")
    @PreAuthorize("hasRole('OPS_MANAGER')")
    public List<Departure> belowMinimum() {
        LocalDate warnDate = LocalDate.now().plusDays(15);
        return departureRepository.findByStatusAndDepartDateBetween(Departure.Status.OPEN, LocalDate.now(), warnDate)
                .stream().filter(d -> d.getSeatsConfirmed() < d.getMinPax()).toList();
    }

    public record CancelRequest(String reason) {}

    /** Huy doan keo theo huy hang loat don kem ly do rieng, hoan tien day du (nhap tay so tien theo muc 10). */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('OPS_MANAGER')")
    public void cancelDeparture(@PathVariable String id, @RequestBody CancelRequest req) {
        Departure departure = departureRepository.findById(id).orElseThrow(() -> ApiException.notFound("Doan khoi hanh"));
        departure.setStatus(Departure.Status.CANCELLED);
        departure.setCancelReason(req.reason());
        departureRepository.save(departure);

        List<Booking> bookings = bookingRepository.findByDepartureId(id);
        for (Booking b : bookings) {
            if (b.getStatus() == Booking.Status.HELD || b.getStatus() == Booking.Status.CONFIRMED) {
                Double refund = b.getStatus() == Booking.Status.CONFIRMED && b.totalPaid() > 0 ? b.totalPaid() : null;
                bookingService.cancel(b.getId(), "Huy doan: " + req.reason(), refund, "system");
            }
        }
    }
}
