package com.nagare.sales.model;

import com.nagare.common.model.AuditableEntity;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.mongodb.core.mapping.Document;

/** customers: KHONG TU GOP theo so dien thoai - moi lan trung thi tao ho so moi kem nhan "nghi trung". */
@Document(collection = "customers")
public class Customer extends AuditableEntity {

    public enum Source { WEB, PHONE, ZALO, WALK_IN, IMPORT }

    private String userId;
    private String fullName;
    private String phone;
    private String email;
    private String zalo;
    private String nationality;
    private LocalDate dob;
    private String address;
    private Source source;
    private List<String> tags;
    private String ownerId;
    private String mergedIntoId;
    private String note;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getZalo() { return zalo; }
    public void setZalo(String zalo) { this.zalo = zalo; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Source getSource() { return source; }
    public void setSource(Source source) { this.source = source; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    public String getMergedIntoId() { return mergedIntoId; }
    public void setMergedIntoId(String mergedIntoId) { this.mergedIntoId = mergedIntoId; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
