package com.nagare.catalog.web;

import com.nagare.catalog.model.AddOnService;
import com.nagare.catalog.repo.AddOnServiceRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/add-ons")
public class AddOnController {

    private final AddOnServiceRepository addOnServiceRepository;

    public AddOnController(AddOnServiceRepository addOnServiceRepository) {
        this.addOnServiceRepository = addOnServiceRepository;
    }

    /** Loc dich vu con kip dat theo leadTimeDays neu co departureDate duoc truyen. */
    @GetMapping
    public List<AddOnService> list(@RequestParam(required = false) String tourId,
                                    @RequestParam(required = false) String departureId,
                                    @RequestParam(required = false) String departDate) {
        List<AddOnService> all = addOnServiceRepository.findByStatus(AddOnService.Status.ACTIVE);
        if (departDate == null) return all;
        LocalDate d = LocalDate.parse(departDate);
        long daysUntil = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), d);
        return all.stream().filter(a -> daysUntil >= a.getLeadTimeDays()).toList();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OPS_MANAGER','TOUR_DESIGNER')")
    public AddOnService create(@RequestBody AddOnService addOnService) {
        return addOnServiceRepository.save(addOnService);
    }
}
