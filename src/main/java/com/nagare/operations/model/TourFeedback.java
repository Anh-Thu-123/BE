package com.nagare.operations.model;

import com.nagare.common.model.AuditableEntity;
import java.time.Instant;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

/** tourFeedback: khach cham sau tour - NGUON DUY NHAT cho chi so hieu suat HDV, khong phai HDV tu cham. */
@Document(collection = "tourFeedback")
@CompoundIndex(name = "departure_booking_unique", def = "{'departureId': 1, 'bookingId': 1}", unique = true)
public class TourFeedback extends AuditableEntity {

    private String departureId;
    private String bookingId;
    private String customerId;
    private int guideScore;
    private int serviceScore;
    private int overallScore;
    private String comment;
    private boolean wouldRecommend;
    private Instant submittedAt = Instant.now();
    private boolean visibleToGuide = true;

    public String getDepartureId() { return departureId; }
    public void setDepartureId(String departureId) { this.departureId = departureId; }
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public int getGuideScore() { return guideScore; }
    public void setGuideScore(int guideScore) { this.guideScore = guideScore; }
    public int getServiceScore() { return serviceScore; }
    public void setServiceScore(int serviceScore) { this.serviceScore = serviceScore; }
    public int getOverallScore() { return overallScore; }
    public void setOverallScore(int overallScore) { this.overallScore = overallScore; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public boolean isWouldRecommend() { return wouldRecommend; }
    public void setWouldRecommend(boolean wouldRecommend) { this.wouldRecommend = wouldRecommend; }
    public Instant getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Instant submittedAt) { this.submittedAt = submittedAt; }
    public boolean isVisibleToGuide() { return visibleToGuide; }
    public void setVisibleToGuide(boolean visibleToGuide) { this.visibleToGuide = visibleToGuide; }
}
