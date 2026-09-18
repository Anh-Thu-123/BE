package com.nagare.catalog.web;

import com.nagare.catalog.model.Tour;
import com.nagare.catalog.repo.TourRepository;
import com.nagare.common.error.ApiException;
import com.nagare.common.storage.FileStorageService;
import java.io.IOException;
import java.util.ArrayList;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Anh tour upload qua backend vao GridFS (truoc day upload thang len Cloudinary bang chu ky).
 * Anh tour khong nhay cam nen phuc vu cong khai qua PublicFileController, khong can URL ky han.
 */
@RestController
@RequestMapping("/api/tours/{id}/images")
public class TourImageController {

    private final FileStorageService fileStorageService;
    private final TourRepository tourRepository;

    public TourImageController(FileStorageService fileStorageService, TourRepository tourRepository) {
        this.fileStorageService = fileStorageService;
        this.tourRepository = tourRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('TOUR_DESIGNER')")
    public Tour upload(@PathVariable String id, @RequestParam MultipartFile file) throws IOException {
        Tour tour = tourRepository.findById(id).orElseThrow(() -> ApiException.notFound("Tour"));
        String fileId = fileStorageService.store(file, "public/tours/" + id);

        Tour.TourImage image = new Tour.TourImage();
        image.setPublicId(fileId);
        if (tour.getImages() == null) {
            tour.setImages(new ArrayList<>());
        }
        tour.getImages().add(image);
        return tourRepository.save(tour);
    }
}
