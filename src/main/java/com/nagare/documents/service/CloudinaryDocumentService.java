package com.nagare.documents.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.util.Date;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Ho so visa upload QUA BACKEND (khac anh tour upload thang tu trinh duyet) de kiem quyen truoc,
 * luu trong thu muc rieng tu Cloudinary. URL xem chi ky han 15 phut, moi luot xem ghi vao auditLogs.
 */
@Service
public class CloudinaryDocumentService {

    private final Cloudinary cloudinary;

    public CloudinaryDocumentService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadPrivate(MultipartFile file, String folder) throws java.io.IOException {
        Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", "private/" + folder,
                "type", "authenticated",
                "resource_type", "auto"));
        return (String) result.get("public_id");
    }

    /** URL ky han 15 phut - khong bao gio tra URL cong khai vinh vien cho ho so visa. */
    public String signedUrl(String publicId) {
        try {
            long expiresAt = (System.currentTimeMillis() / 1000) + (15 * 60);
            return cloudinary.privateDownload(publicId, "jpg", ObjectUtils.asMap("expires_at", expiresAt));
        } catch (Exception e) {
            throw new IllegalStateException("Khong the tao URL ky cho tai lieu: " + publicId, e);
        }
    }
}
