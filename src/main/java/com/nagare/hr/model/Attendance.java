package com.nagare.hr.model;

import java.time.Instant;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

/** Ngay HDV dan doan duoc danh dau tu dong tu assignments, khong bat cham tay. */
@Document(collection = "attendance")
@CompoundIndex(name = "employee_date_unique", def = "{'employeeId': 1, 'date': 1}", unique = true)
public class Attendance {

    public enum Source { WEB, ON_TOUR, LEAVE }

    @Id
    private String id;
    private String employeeId;
    private LocalDate date;
    private Instant checkInAt;
    private Instant checkOutAt;
    private Source source;
    private String departureId;
    private String note;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Instant getCheckInAt() { return checkInAt; }
    public void setCheckInAt(Instant checkInAt) { this.checkInAt = checkInAt; }
    public Instant getCheckOutAt() { return checkOutAt; }
    public void setCheckOutAt(Instant checkOutAt) { this.checkOutAt = checkOutAt; }
    public Source getSource() { return source; }
    public void setSource(Source source) { this.source = source; }
    public String getDepartureId() { return departureId; }
    public void setDepartureId(String departureId) { this.departureId = departureId; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
