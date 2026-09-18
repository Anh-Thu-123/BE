package com.nagare.common.web;

import com.nagare.common.storage.FileStorageService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Phuc vu tep khong nhay cam (anh tour) truc tiep tu GridFS, khong can dang nhap -
 * dung cho <img src> tren trang cong khai. Ho so visa (nhay cam) khong di qua day,
 * xem VisaCaseController.documentUrl() dung URL ky han rieng.
 */
@RestController
@RequestMapping("/api/public/files")
public class PublicFileController {

    private final FileStorageService fileStorageService;

    public PublicFileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<InputStreamResource> get(@PathVariable String id) {
        FileStorageService.StoredFile file = fileStorageService.load(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.contentType()))
                .cacheControl(org.springframework.http.CacheControl.maxAge(java.time.Duration.ofDays(30)).cachePublic())
                .body(new InputStreamResource(file.content()));
    }
}
