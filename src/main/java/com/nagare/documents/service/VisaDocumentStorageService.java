package com.nagare.documents.service;

import com.nagare.common.storage.FileAccessTokenService;
import com.nagare.common.storage.FileStorageService;
import java.io.IOException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Ho so visa upload QUA BACKEND (khac anh tour) de kiem quyen truoc, luu trong GridFS.
 * URL xem chi ky han 15 phut qua FileAccessTokenService, moi luot xem ghi vao auditLogs
 * (xem VisaCaseController.documentUrl()). Truoc day dung Cloudinary private folder + signed URL,
 * nay thay bang GridFS + JWT ngan han theo yeu cau khong dung dich vu ngoai.
 */
@Service
public class VisaDocumentStorageService {

    private final FileStorageService fileStorageService;
    private final FileAccessTokenService fileAccessTokenService;

    public VisaDocumentStorageService(FileStorageService fileStorageService, FileAccessTokenService fileAccessTokenService) {
        this.fileStorageService = fileStorageService;
        this.fileAccessTokenService = fileAccessTokenService;
    }

    public String uploadPrivate(MultipartFile file, String folder) throws IOException {
        return fileStorageService.store(file, "private/" + folder);
    }

    /** URL ky han 15 phut - khong bao gio tra URL cong khai vinh vien cho ho so visa. */
    public String signedUrl(String fileId) {
        String token = fileAccessTokenService.issue(fileId);
        return "/api/files/private/" + fileId + "?token=" + token;
    }
}
