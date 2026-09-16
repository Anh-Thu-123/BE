package com.nagare.operations.web;

import com.nagare.identity.security.SecurityUtils;
import com.nagare.operations.model.TourLog;
import com.nagare.operations.repo.TourLogRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/departures/{id}/logs")
public class TourLogController {

    private final TourLogRepository tourLogRepository;

    public TourLogController(TourLogRepository tourLogRepository) {
        this.tourLogRepository = tourLogRepository;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TOUR_GUIDE','OPS_MANAGER')")
    public TourLog create(@PathVariable String id, @RequestBody TourLog log) {
        log.setDepartureId(id);
        log.setAuthorId(SecurityUtils.currentUser().getId());
        return tourLogRepository.save(log);
    }

    @GetMapping
    public java.util.List<TourLog> list(@PathVariable String id) {
        return tourLogRepository.findByDepartureId(id);
    }
}
