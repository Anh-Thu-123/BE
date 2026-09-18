package com.nagare.documents.web;

import com.nagare.common.audit.AuditLogService;
import com.nagare.common.error.ApiException;
import com.nagare.documents.model.VisaCase;
import com.nagare.documents.repo.VisaCaseRepository;
import com.nagare.documents.service.VisaDocumentStorageService;
import com.nagare.identity.security.SecurityUtils;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/visa-cases")
public class VisaCaseController {

    private final VisaCaseRepository visaCaseRepository;
    private final VisaDocumentStorageService visaDocumentStorageService;
    private final AuditLogService auditLogService;

    public VisaCaseController(VisaCaseRepository visaCaseRepository, VisaDocumentStorageService visaDocumentStorageService,
                               AuditLogService auditLogService) {
        this.visaCaseRepository = visaCaseRepository;
        this.visaDocumentStorageService = visaDocumentStorageService;
        this.auditLogService = auditLogService;
    }

    // Ma tran muc 04: GD chi xem (o), TK toan quyen (●). TPDH/HDV/CSKH/KH la "cua minh" (◑)
    // va can loc theo pham vi rieng - chua co trong lan implement dau, ghi nhan la viec con lai.
    @GetMapping
    @PreAuthorize("hasAnyRole('SECRETARY', 'DIRECTOR')")
    public List<VisaCase> list(@RequestParam(required = false) String status) {
        List<VisaCase> cases = status != null
                ? visaCaseRepository.findByStatus(VisaCase.Status.valueOf(status))
                : visaCaseRepository.findAll();
        return cases.stream().sorted(Comparator.comparing(VisaCase::getDeadline)).toList();
    }

    @PostMapping("/{id}/documents")
    public VisaCase uploadDocument(@PathVariable String id, @RequestParam String docType,
                                    @RequestParam MultipartFile file) throws java.io.IOException {
        VisaCase visaCase = visaCaseRepository.findById(id).orElseThrow(() -> ApiException.notFound("Ho so visa"));
        String publicId = visaDocumentStorageService.uploadPrivate(file, id);

        VisaCase.DocumentItem item = new VisaCase.DocumentItem();
        item.setDocId(UUID.randomUUID().toString());
        item.setDocType(docType);
        item.setPublicId(publicId);
        item.setMimeType(file.getContentType());
        item.setUploadedBy(SecurityUtils.currentUser().getId());
        item.setUploadedAt(Instant.now());
        visaCase.getDocuments().add(item);
        return visaCaseRepository.save(visaCase);
    }

    @GetMapping("/{id}/documents/{docId}/url")
    public String documentUrl(@PathVariable String id, @PathVariable String docId) {
        VisaCase visaCase = visaCaseRepository.findById(id).orElseThrow(() -> ApiException.notFound("Ho so visa"));
        VisaCase.DocumentItem doc = visaCase.getDocuments().stream().filter(d -> d.getDocId().equals(docId))
                .findFirst().orElseThrow(() -> ApiException.notFound("Tai lieu"));
        auditLogService.log("VIEW_DOCUMENT", "visaCases", id, "Xem tai lieu " + docId);
        return visaDocumentStorageService.signedUrl(doc.getPublicId());
    }

    public record StatusRequest(String status) {}

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('SECRETARY')")
    public VisaCase setStatus(@PathVariable String id, @RequestBody StatusRequest req) {
        VisaCase visaCase = visaCaseRepository.findById(id).orElseThrow(() -> ApiException.notFound("Ho so visa"));
        visaCase.setStatus(VisaCase.Status.valueOf(req.status()));
        return visaCaseRepository.save(visaCase);
    }
}
