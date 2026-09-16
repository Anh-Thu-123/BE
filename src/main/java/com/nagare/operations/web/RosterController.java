package com.nagare.operations.web;

import com.nagare.common.error.ApiException;
import com.nagare.identity.security.SecurityUtils;
import com.nagare.operations.service.RosterService;
import com.nagare.sales.model.Booking;
import com.nagare.sales.repo.BookingRepository;
import com.nagare.scheduling.model.Assignment;
import com.nagare.scheduling.model.Departure;
import com.nagare.scheduling.repo.AssignmentRepository;
import com.nagare.scheduling.repo.DepartureRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/departures/{id}/roster")
public class RosterController {

    private final DepartureRepository departureRepository;
    private final BookingRepository bookingRepository;
    private final AssignmentRepository assignmentRepository;

    public RosterController(DepartureRepository departureRepository, BookingRepository bookingRepository,
                             AssignmentRepository assignmentRepository) {
        this.departureRepository = departureRepository;
        this.bookingRepository = bookingRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TOUR_GUIDE','OPS_MANAGER','DIRECTOR')")
    public List<Map<String, Object>> roster(@PathVariable String id) {
        Departure departure = departureRepository.findById(id).orElseThrow(() -> ApiException.notFound("Doan khoi hanh"));

        var user = SecurityUtils.currentUser();
        if (user.getRole().name().equals("TOUR_GUIDE")) {
            boolean assigned = assignmentRepository.findByDepartureId(id).stream()
                    .anyMatch(a -> a.getEmployeeId().equals(user.getEmployeeId())
                            && a.getStatus() != Assignment.Status.CANCELLED);
            if (!assigned) throw ApiException.forbidden("Ban khong duoc phan cong doan nay");
        }

        boolean docWindowOpen = RosterService.isDocumentWindowOpen(
                departure.getDepartDate(), departure.getReturnDate(), LocalDate.now());

        List<Booking> bookings = bookingRepository.findByDepartureId(id);
        return bookings.stream()
                .filter(b -> b.getStatus() == Booking.Status.CONFIRMED || b.getStatus() == Booking.Status.COMPLETED)
                .flatMap(b -> b.getPax().stream())
                .map(p -> RosterService.toRosterEntry(p, docWindowOpen))
                .toList();
    }
}
