package com.nagare.documents.model;

import com.nagare.common.model.AuditableEntity;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

/** visaCases: mot ho so cho moi hanh khach can giay to, tro bang paxId (khong bao gio bang vi tri mang). */
@Document(collection = "visaCases")
@CompoundIndex(name = "booking_pax_unique", def = "{'bookingId': 1, 'paxId': 1}", unique = true)
public class VisaCase extends AuditableEntity {

    public enum CaseType { JAPAN_VISA, TEMP_RESIDENCE, VISIT_JAPAN_WEB, MINOR_CONSENT }
    public enum Status { COLLECTING, SUBMITTED, EXTRA_REQUIRED, APPROVED, REJECTED }
    public enum DocStatus { PENDING, APPROVED, REJECTED }

    private String bookingId;
    private String paxId;
    private String customerName;
    private CaseType caseType;
    private Status status = Status.COLLECTING;
    private Instant deadline;
    private Instant appointmentAt;
    private String handlerId;
    private List<DocumentItem> documents = new ArrayList<>();
    private List<Note> notes = new ArrayList<>();
    private Instant purgeAfter;

    public static class DocumentItem {
        private String docId;
        private String docType;
        private String publicId;
        private String mimeType;
        private String uploadedBy;
        private Instant uploadedAt;
        private DocStatus status = DocStatus.PENDING;
        private String rejectReason;

        public String getDocId() { return docId; }
        public void setDocId(String docId) { this.docId = docId; }
        public String getDocType() { return docType; }
        public void setDocType(String docType) { this.docType = docType; }
        public String getPublicId() { return publicId; }
        public void setPublicId(String publicId) { this.publicId = publicId; }
        public String getMimeType() { return mimeType; }
        public void setMimeType(String mimeType) { this.mimeType = mimeType; }
        public String getUploadedBy() { return uploadedBy; }
        public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }
        public Instant getUploadedAt() { return uploadedAt; }
        public void setUploadedAt(Instant uploadedAt) { this.uploadedAt = uploadedAt; }
        public DocStatus getStatus() { return status; }
        public void setStatus(DocStatus status) { this.status = status; }
        public String getRejectReason() { return rejectReason; }
        public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
    }

    public static class Note {
        private Instant at = Instant.now();
        private String by;
        private String text;

        public Instant getAt() { return at; }
        public void setAt(Instant at) { this.at = at; }
        public String getBy() { return by; }
        public void setBy(String by) { this.by = by; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public String getPaxId() { return paxId; }
    public void setPaxId(String paxId) { this.paxId = paxId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public CaseType getCaseType() { return caseType; }
    public void setCaseType(CaseType caseType) { this.caseType = caseType; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Instant getDeadline() { return deadline; }
    public void setDeadline(Instant deadline) { this.deadline = deadline; }
    public Instant getAppointmentAt() { return appointmentAt; }
    public void setAppointmentAt(Instant appointmentAt) { this.appointmentAt = appointmentAt; }
    public String getHandlerId() { return handlerId; }
    public void setHandlerId(String handlerId) { this.handlerId = handlerId; }
    public List<DocumentItem> getDocuments() { return documents; }
    public void setDocuments(List<DocumentItem> documents) { this.documents = documents; }
    public List<Note> getNotes() { return notes; }
    public void setNotes(List<Note> notes) { this.notes = notes; }
    public Instant getPurgeAfter() { return purgeAfter; }
    public void setPurgeAfter(Instant purgeAfter) { this.purgeAfter = purgeAfter; }
}
