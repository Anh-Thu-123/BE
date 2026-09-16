package com.nagare.sales.model;

import com.nagare.common.model.AuditableEntity;
import com.nagare.common.model.Bilingual;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tourRequests")
public class TourRequest extends AuditableEntity {

    public enum Direction { OUTBOUND, INBOUND, DOMESTIC }
    public enum Status { NEW, ASSIGNED, QUOTING, QUOTED, WON, LOST }

    private String customerId;
    private Booking.Contact contact;
    private Direction direction;
    private List<String> destinations;
    private LocalDate desiredFrom;
    private LocalDate desiredTo;
    private boolean flexibleDays;
    private int paxAdult;
    private int paxChild;
    private Double budgetPerPax;
    private List<String> interests;
    private Status status = Status.NEW;
    private String assigneeId;
    private List<Quote> quotes = new ArrayList<>();
    private String convertedBookingId;
    private String lostReason;

    public static class Quote {
        private int version;
        private Bilingual summary;
        private List<Tour.ItineraryDay> itinerary;
        private double pricePerPax;
        private double total;
        private Instant validUntil;
        private String createdBy;
        private Instant createdAt = Instant.now();

        public int getVersion() { return version; }
        public void setVersion(int version) { this.version = version; }
        public Bilingual getSummary() { return summary; }
        public void setSummary(Bilingual summary) { this.summary = summary; }
        public List<Tour.ItineraryDay> getItinerary() { return itinerary; }
        public void setItinerary(List<Tour.ItineraryDay> itinerary) { this.itinerary = itinerary; }
        public double getPricePerPax() { return pricePerPax; }
        public void setPricePerPax(double pricePerPax) { this.pricePerPax = pricePerPax; }
        public double getTotal() { return total; }
        public void setTotal(double total) { this.total = total; }
        public Instant getValidUntil() { return validUntil; }
        public void setValidUntil(Instant validUntil) { this.validUntil = validUntil; }
        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    }

    // placeholder de tranh phu thuoc vong: dung cau truc rieng thay vi catalog.Tour truc tiep
    public static class Tour {
        public static class ItineraryDay {
            private int day;
            private Bilingual title;
            private Bilingual detail;

            public int getDay() { return day; }
            public void setDay(int day) { this.day = day; }
            public Bilingual getTitle() { return title; }
            public void setTitle(Bilingual title) { this.title = title; }
            public Bilingual getDetail() { return detail; }
            public void setDetail(Bilingual detail) { this.detail = detail; }
        }
    }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public Booking.Contact getContact() { return contact; }
    public void setContact(Booking.Contact contact) { this.contact = contact; }
    public Direction getDirection() { return direction; }
    public void setDirection(Direction direction) { this.direction = direction; }
    public List<String> getDestinations() { return destinations; }
    public void setDestinations(List<String> destinations) { this.destinations = destinations; }
    public LocalDate getDesiredFrom() { return desiredFrom; }
    public void setDesiredFrom(LocalDate desiredFrom) { this.desiredFrom = desiredFrom; }
    public LocalDate getDesiredTo() { return desiredTo; }
    public void setDesiredTo(LocalDate desiredTo) { this.desiredTo = desiredTo; }
    public boolean isFlexibleDays() { return flexibleDays; }
    public void setFlexibleDays(boolean flexibleDays) { this.flexibleDays = flexibleDays; }
    public int getPaxAdult() { return paxAdult; }
    public void setPaxAdult(int paxAdult) { this.paxAdult = paxAdult; }
    public int getPaxChild() { return paxChild; }
    public void setPaxChild(int paxChild) { this.paxChild = paxChild; }
    public Double getBudgetPerPax() { return budgetPerPax; }
    public void setBudgetPerPax(Double budgetPerPax) { this.budgetPerPax = budgetPerPax; }
    public List<String> getInterests() { return interests; }
    public void setInterests(List<String> interests) { this.interests = interests; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getAssigneeId() { return assigneeId; }
    public void setAssigneeId(String assigneeId) { this.assigneeId = assigneeId; }
    public List<Quote> getQuotes() { return quotes; }
    public void setQuotes(List<Quote> quotes) { this.quotes = quotes; }
    public String getConvertedBookingId() { return convertedBookingId; }
    public void setConvertedBookingId(String convertedBookingId) { this.convertedBookingId = convertedBookingId; }
    public String getLostReason() { return lostReason; }
    public void setLostReason(String lostReason) { this.lostReason = lostReason; }
}
