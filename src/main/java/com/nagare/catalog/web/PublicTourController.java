package com.nagare.catalog.web;

import com.nagare.catalog.model.Tour;
import com.nagare.catalog.repo.TourRepository;
import com.nagare.common.error.ApiException;
import com.nagare.identity.service.RateLimiterService;
import com.nagare.sales.model.TourRequest;
import com.nagare.sales.repo.TourRequestRepository;
import com.nagare.scheduling.model.Departure;
import com.nagare.scheduling.repo.DepartureRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

/** /api/public/* khong can token - duy nhat duoc Next.js goi luc dung trang tinh. */
@RestController
@RequestMapping("/api/public")
public class PublicTourController {

    private final TourRepository tourRepository;
    private final DepartureRepository departureRepository;
    private final TourRequestRepository tourRequestRepository;
    private final MongoTemplate mongoTemplate;
    private final RateLimiterService rateLimiterService;

    public PublicTourController(TourRepository tourRepository, DepartureRepository departureRepository,
                                 TourRequestRepository tourRequestRepository, MongoTemplate mongoTemplate,
                                 RateLimiterService rateLimiterService) {
        this.tourRepository = tourRepository;
        this.departureRepository = departureRepository;
        this.tourRequestRepository = tourRequestRepository;
        this.mongoTemplate = mongoTemplate;
        this.rateLimiterService = rateLimiterService;
    }

    @GetMapping("/tours")
    public List<Tour> list(@RequestParam(required = false) String type, @RequestParam(required = false) String theme,
                            @RequestParam(defaultValue = "vi") String lang) {
        Query query = new Query();
        if (type != null) query.addCriteria(Criteria.where("type").is(type));
        if (theme != null) query.addCriteria(Criteria.where("theme").is(theme));
        query.addCriteria(Criteria.where("publication." + lang + ".status").is(Tour.PubStatus.PUBLISHED.name()));
        return mongoTemplate.find(query, Tour.class);
    }

    @GetMapping("/tours/{slug}")
    public Tour bySlug(@PathVariable String slug) {
        return tourRepository.findBySlugVi(slug)
                .or(() -> tourRepository.findBySlugJa(slug))
                .orElseThrow(() -> ApiException.notFound("Tour"));
    }

    /** Lich con cho - khong cache, luon goi truc tiep de so cho chinh xac. */
    @GetMapping("/tours/{id}/departures")
    public List<Departure> departures(@PathVariable String id) {
        return departureRepository.findByTourId(id).stream()
                .filter(d -> d.getStatus() == Departure.Status.OPEN || d.getStatus() == Departure.Status.GUARANTEED)
                .toList();
    }

    @PostMapping("/tour-requests")
    public TourRequest submitTourRequest(@RequestBody TourRequest request, HttpServletRequest httpRequest) {
        String ip = clientIp(httpRequest);
        if (!rateLimiterService.tryTourRequest(ip)) {
            throw ApiException.conflict("RATE_LIMITED", "Qua nhieu yeu cau, thu lai sau");
        }
        request.setStatus(TourRequest.Status.NEW);
        return tourRequestRepository.save(request);
    }

    private String clientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();
        return request.getRemoteAddr();
    }
}
