package com.nagare.scheduling.model;

import com.nagare.common.model.AuditableEntity;
import java.time.LocalDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * assignments: NGUON SU THAT DUY NHAT ve viec ai di doan nao, ke ca dieu hanh vien.
 * Khong ghi lai o departures - mot su that mot cho.
 */
@Document(collection = "assignments")
@CompoundIndex(name = "employee_range", def = "{'employeeId': 1, 'startDate': 1, 'endDate': 1}")
public class Assignment extends AuditableEntity {

    public enum Role { LEAD_GUIDE, ASSISTANT_GUIDE, OPERATOR }
    public enum Status { PLANNED, CONFIRMED, DONE, CANCELLED }

    private String employeeId;
    private String departureId;
    private Role role;
    private LocalDate startDate;
    private LocalDate endDate;
    private Status status = Status.PLANNED;
    private String handoverFromId;

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getDepartureId() { return departureId; }
    public void setDepartureId(String departureId) { this.departureId = departureId; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getHandoverFromId() { return handoverFromId; }
    public void setHandoverFromId(String handoverFromId) { this.handoverFromId = handoverFromId; }
}
