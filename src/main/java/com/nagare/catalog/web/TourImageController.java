package com.nagare.catalog.web;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Anh tour ky san de trinh duyet upload thang len Cloudinary - khong di qua RAM cua Render. */
@RestController
@RequestMapping("/api/tours/{id}/images")
public class TourImageController {

    private final Cloudinary cloudinary;

    public TourImageController(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @PostMapping("/signature")
    @PreAuthorize("hasRole('TOUR_DESIGNER')")
    public Map<String, Object> signature(@PathVariable String id) {
        long timestamp = System.currentTimeMillis() / 1000;
        Map<String, Object> paramsToSign = new TreeMap<>();
        paramsToSign.put("timestamp", timestamp);
        paramsToSign.put("folder", "public/tours/" + id);

        String signature = cloudinary.apiSignRequest(paramsToSign, cloudinary.config.apiSecret);
        return Map.of(
                "timestamp", timestamp,
                "signature", signature,
                "apiKey", cloudinary.config.apiKey,
                "cloudName", cloudinary.config.cloudName,
                "folder", "public/tours/" + id
        );
    }
}
