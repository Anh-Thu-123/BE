package com.nagare.catalog.web;

import com.nagare.catalog.model.Tour;
import com.nagare.catalog.repo.TourRepository;
import com.nagare.catalog.service.TourPublicationService;
import com.nagare.common.error.ApiException;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tours")
public class TourController {

    private final TourRepository tourRepository;
    private final TourPublicationService publicationService;

    public TourController(TourRepository tourRepository, TourPublicationService publicationService) {
        this.tourRepository = tourRepository;
        this.publicationService = publicationService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TOUR_DESIGNER','MKT_MANAGER','DIRECTOR','SECRETARY','OPS_MANAGER')")
    public List<Tour> list() {
        return tourRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('TOUR_DESIGNER')")
    public Tour create(@RequestBody Tour tour) {
        return tourRepository.save(tour);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TOUR_DESIGNER')")
    public Tour update(@PathVariable String id, @RequestBody Tour update) {
        Tour existing = tourRepository.findById(id).orElseThrow(() -> ApiException.notFound("Tour"));
        update.setId(existing.getId());
        return tourRepository.save(update);
    }

    @PostMapping("/{id}/submit-review")
    @PreAuthorize("hasRole('TOUR_DESIGNER')")
    public Tour submitReview(@PathVariable String id, @RequestParam String lang) {
        return publicationService.submitReview(id, lang);
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasRole('MKT_MANAGER')")
    public Tour publish(@PathVariable String id, @RequestParam String lang) {
        return publicationService.publish(id, lang);
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('MKT_MANAGER')")
    public Tour reject(@PathVariable String id, @RequestParam String lang, @RequestBody(required = false) Map<String, String> body) {
        return publicationService.reject(id, lang);
    }
}
