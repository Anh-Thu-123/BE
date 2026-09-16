package com.nagare.hr.model;

import com.nagare.common.model.AuditableEntity;
import java.time.Instant;
import java.time.LocalDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "leaveRequests")
public class LeaveRequest extends AuditableEntity {

    public enum Type { ANNUAL, SICK, UNPAID, BUSINESS_TRIP }
    public enum Status { PENDING, APPROVED, REJECTED }

    private String employeeId;
    private Type type;
    private LocalDate fromDate;
    private LocalDate toDate;
    private boolean halfDay;
    private String reason;
    private Status status = Status.PENDING;
    private String approverId;
    private Instant decidedAt;
    private String decisionNote;

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public LocalDate getFromDate() { return fromDate; }
    public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }
    public LocalDate getToDate() { return toDate; }
    public void setToDate(LocalDate toDate) { this.toDate = toDate; }
    public boolean isHalfDay() { return halfDay; }
    public void setHalfDay(boolean halfDay) { this.halfDay = halfDay; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getApproverId() { return approverId; }
    public void setApproverId(String approverId) { this.approverId = approverId; }
    public Instant getDecidedAt() { return decidedAt; }
    public void setDecidedAt(Instant decidedAt) { this.decidedAt = decidedAt; }
    public String getDecisionNote() { return decisionNote; }
    public void setDecisionNote(String decisionNote) { this.decisionNote = decisionNote; }
}
