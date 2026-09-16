package com.nagare.catalog.service;

import com.nagare.catalog.model.Tour;
import com.nagare.catalog.repo.TourRepository;
import com.nagare.common.error.ApiException;
import java.time.Instant;
import org.springframework.stereotype.Service;

/**
 * Xuat ban tach rieng theo tung ngon ngu. Tour Inbound BAT BUOC co ban tieng Nhat da PUBLISHED
 * moi duoc publish (doi tuong cua no la khach Nhat) - xem muc 05.
 */
@Service
public class TourPublicationService {

    private final TourRepository tourRepository;

    public TourPublicationService(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    public Tour submitReview(String tourId, String lang) {
        Tour tour = get(tourId);
        langPub(tour, lang).setStatus(Tour.PubStatus.PENDING_REVIEW);
        return tourRepository.save(tour);
    }

    public Tour publish(String tourId, String lang) {
        Tour tour = get(tourId);
        if (tour.getType() == Tour.Type.INBOUND && "vi".equals(lang)
                && tour.getPublication().getJa().getStatus() != Tour.PubStatus.PUBLISHED) {
            throw ApiException.badRequest("JA_REQUIRED",
                    "Tour Inbound bat buoc co ban tieng Nhat da xuat ban truoc");
        }
        Tour.LangPub pub = langPub(tour, lang);
        pub.setStatus(Tour.PubStatus.PUBLISHED);
        pub.setPublishedAt(Instant.now());
        return tourRepository.save(tour);
    }

    public Tour reject(String tourId, String lang) {
        Tour tour = get(tourId);
        langPub(tour, lang).setStatus(Tour.PubStatus.DRAFT);
        return tourRepository.save(tour);
    }

    private Tour get(String id) {
        return tourRepository.findById(id).orElseThrow(() -> ApiException.notFound("Tour"));
    }

    private Tour.LangPub langPub(Tour tour, String lang) {
        if ("ja".equalsIgnoreCase(lang)) return tour.getPublication().getJa();
        if ("vi".equalsIgnoreCase(lang)) return tour.getPublication().getVi();
        throw ApiException.badRequest("INVALID_LANG", "lang phai la vi hoac ja");
    }
}
