package com.nagare.reporting.web;

import com.nagare.operations.repo.TourFeedbackRepository;
import com.nagare.sales.model.Booking;
import com.nagare.sales.repo.BookingRepository;
import com.nagare.scheduling.model.Departure;
import com.nagare.scheduling.repo.DepartureRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Bao cao chay tren TIEN THUC THU (mac dinh) theo ngay ghi nhan, hoac gia tri don da xac nhan -
 * hai chi so tach rieng, khong tron lan (quyet dinh muc 01).
 */
@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasAnyRole('DIRECTOR','OPS_MANAGER','MKT_MANAGER','SECRETARY')")
public class ReportController {

    private final BookingRepository bookingRepository;
    private final DepartureRepository departureRepository;
    private final TourFeedbackRepository tourFeedbackRepository;

    public ReportController(BookingRepository bookingRepository, DepartureRepository departureRepository,
                             TourFeedbackRepository tourFeedbackRepository) {
        this.bookingRepository = bookingRepository;
        this.departureRepository = departureRepository;
        this.tourFeedbackRepository = tourFeedbackRepository;
    }

    @GetMapping("/revenue")
    public Map<String, Object> revenue(@RequestParam String from, @RequestParam String to,
                                        @RequestParam(defaultValue = "actual") String basis) {
        Instant fromI = LocalDate.parse(from).atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant toI = LocalDate.parse(to).plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        List<Booking> bookings = bookingRepository.findAll();
        double total;
        if ("confirmed_value".equals(basis)) {
            total = bookings.stream().filter(b -> b.getStatus() == Booking.Status.CONFIRMED || b.getStatus() == Booking.Status.COMPLETED)
                    .mapToDouble(b -> b.getPricing().getTotal()).sum();
        } else {
            total = bookings.stream()
                    .flatMap(b -> b.getPayments().stream())
                    .filter(p -> p.getPaidAt() != null && !p.getPaidAt().isBefore(fromI) && p.getPaidAt().isBefore(toI))
                    .mapToDouble(p -> p.getDirection() == Booking.PaymentDirection.RECEIPT ? p.getAmount() : -p.getAmount())
                    .sum();
        }
        return Map.of("basis", basis, "from", from, "to", to, "total", total);
    }

    @GetMapping("/occupancy")
    public List<Map<String, Object>> occupancy() {
        List<Departure> departures = departureRepository.findAll();
        return departures.stream().map(d -> Map.<String, Object>of(
                "departureId", d.getId(),
                "code", d.getCode(),
                "capacity", d.getCapacity(),
                "seatsConfirmed", d.getSeatsConfirmed(),
                "minPax", d.getMinPax(),
                "occupancyRate", d.getCapacity() == 0 ? 0 : (double) d.getSeatsConfirmed() / d.getCapacity(),
                "meetsMinimum", d.getSeatsConfirmed() >= d.getMinPax()
        )).toList();
    }

    /** Diem lay tu tourFeedback (khach cham), KHONG PHAI HDV tu cham (loi 11). */
    @GetMapping("/guide-performance")
    public List<Map<String, Object>> guidePerformance() {
        return tourFeedbackRepository.findAll().stream()
                .collect(Collectors.groupingBy(f -> f.getDepartureId()))
                .entrySet().stream()
                .map(e -> Map.<String, Object>of(
                        "departureId", e.getKey(),
                        "avgGuideScore", e.getValue().stream().mapToInt(f -> f.getGuideScore()).average().orElse(0),
                        "feedbackCount", e.getValue().size()
                )).toList();
    }
}
