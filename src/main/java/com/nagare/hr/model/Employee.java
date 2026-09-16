package com.nagare.hr.model;

import com.nagare.common.model.AuditableEntity;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "employees")
public class Employee extends AuditableEntity {

    public enum Department { BGD, OPERATIONS, SALES }
    public enum Status { ACTIVE, OFFBOARDED }

    private String userId;
    private String code;
    private String fullName;
    private String position;
    private Department department;
    private String phone;
    private String email;
    private List<String> languages; // vi|ja|en
    private LocalDate joinedAt;
    private Status status = Status.ACTIVE;
    private GuideProfile guideProfile;

    public static class GuideProfile {
        private String licenseNo;
        private LocalDate licenseExpiry;
        private List<String> specialties;

        public String getLicenseNo() { return licenseNo; }
        public void setLicenseNo(String licenseNo) { this.licenseNo = licenseNo; }
        public LocalDate getLicenseExpiry() { return licenseExpiry; }
        public void setLicenseExpiry(LocalDate licenseExpiry) { this.licenseExpiry = licenseExpiry; }
        public List<String> getSpecialties() { return specialties; }
        public void setSpecialties(List<String> specialties) { this.specialties = specialties; }
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<String> getLanguages() { return languages; }
    public void setLanguages(List<String> languages) { this.languages = languages; }
    public LocalDate getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDate joinedAt) { this.joinedAt = joinedAt; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public GuideProfile getGuideProfile() { return guideProfile; }
    public void setGuideProfile(GuideProfile guideProfile) { this.guideProfile = guideProfile; }
}
