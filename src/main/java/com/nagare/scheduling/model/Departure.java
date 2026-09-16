package com.nagare.scheduling.model;

import com.nagare.common.model.AuditableEntity;
import com.nagare.common.model.Bilingual;
import java.time.LocalDate;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * departures: ngay khoi hanh cua tour ghep. Cho trong = capacity - seatsHeld - seatsConfirmed,
 * TINH KHI DOC, khong luu san. operatorId / danh sach HDV KHONG nam o day - xem assignments.
 */
@Document(collection = "departures")
public class Departure extends AuditableEntity {

    public enum Status { OPEN, GUARANTEED, FULL, CLOSED, CANCELLED, COMPLETED }

    private String tourId;
    private String code;
    private LocalDate departDate;
    private LocalDate returnDate;
    private int capacity;
    private int minPax;
    private int seatsHeld;
    private int seatsConfirmed;
    private String currency;
    private double priceAdult;
    private double priceChild;
    private double priceInfant;
    private double singleSupplement;
    private Double fxRateToVnd;
    private Status status = Status.OPEN;
    private Bilingual meetingPoint;
    private String note;
    private String cancelReason;

    public int seatsAvailable() {
        return capacity - seatsHeld - seatsConfirmed;
    }

    public String getTourId() { return tourId; }
    public void setTourId(String tourId) { this.tourId = tourId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public LocalDate getDepartDate() { return departDate; }
    public void setDepartDate(LocalDate departDate) { this.departDate = departDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public int getMinPax() { return minPax; }
    public void setMinPax(int minPax) { this.minPax = minPax; }
    public int getSeatsHeld() { return seatsHeld; }
    public void setSeatsHeld(int seatsHeld) { this.seatsHeld = seatsHeld; }
    public int getSeatsConfirmed() { return seatsConfirmed; }
    public void setSeatsConfirmed(int seatsConfirmed) { this.seatsConfirmed = seatsConfirmed; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public double getPriceAdult() { return priceAdult; }
    public void setPriceAdult(double priceAdult) { this.priceAdult = priceAdult; }
    public double getPriceChild() { return priceChild; }
    public void setPriceChild(double priceChild) { this.priceChild = priceChild; }
    public double getPriceInfant() { return priceInfant; }
    public void setPriceInfant(double priceInfant) { this.priceInfant = priceInfant; }
    public double getSingleSupplement() { return singleSupplement; }
    public void setSingleSupplement(double singleSupplement) { this.singleSupplement = singleSupplement; }
    public Double getFxRateToVnd() { return fxRateToVnd; }
    public void setFxRateToVnd(Double fxRateToVnd) { this.fxRateToVnd = fxRateToVnd; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Bilingual getMeetingPoint() { return meetingPoint; }
    public void setMeetingPoint(Bilingual meetingPoint) { this.meetingPoint = meetingPoint; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }
}
