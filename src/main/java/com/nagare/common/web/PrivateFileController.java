package com.nagare.common.web;

import com.nagare.common.error.ApiException;
import com.nagare.common.storage.FileAccessTokenService;
import com.nagare.common.storage.FileStorageService;
import io.jsonwebtoken.JwtException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Tai tep nhay cam (ho so visa) bang URL ky han 15 phut - thay the Cloudinary privateDownload.
 * Token duoc phat boi VisaCaseController.documentUrl() moi lan xem, khong yeu cau dang nhap
 * lai (URL tu than da la bang chung duoc phep xem trong thoi han).
 */
@RestController
@RequestMapping("/api/files/private")
public class PrivateFileController {

    private final FileAccessTokenService tokenService;
    private final FileStorageService fileStorageService;

    public PrivateFileController(FileAccessTokenService tokenService, FileStorageService fileStorageService) {
        this.tokenService = tokenService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<InputStreamResource> get(@PathVariable String id, @RequestParam String token) {
        String fileIdFromToken;
        try {
            fileIdFromToken = tokenService.verify(token);
        } catch (JwtException e) {
            throw ApiException.forbidden("URL da het han hoac khong hop le");
        }
        if (!fileIdFromToken.equals(id)) {
            throw ApiException.forbidden("Token khong khop voi tep");
        }
        FileStorageService.StoredFile file = fileStorageService.load(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.contentType()))
                .body(new InputStreamResource(file.content()));
    }
}
