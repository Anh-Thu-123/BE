package com.nagare.operations.model;

import com.nagare.common.model.AuditableEntity;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.mongodb.core.mapping.Document;

/** tourLogs: nhat ky HDV viet trong tour va bao cao tong ket. Diem hai long chuyen sang tourFeedback (loi 11). */
@Document(collection = "tourLogs")
public class TourLog extends AuditableEntity {

    public enum Type { DAILY_LOG, INCIDENT, FINAL_REPORT }

    private String departureId;
    private String authorId;
    private Type type;
    private Integer day;
    private String content;
    private List<String> photos = new ArrayList<>();
    private List<Expense> expenses = new ArrayList<>();

    public static class Expense {
        private String item;
        private double amount;
        private String receiptPublicId;

        public String getItem() { return item; }
        public void setItem(String item) { this.item = item; }
        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
        public String getReceiptPublicId() { return receiptPublicId; }
        public void setReceiptPublicId(String receiptPublicId) { this.receiptPublicId = receiptPublicId; }
    }

    public String getDepartureId() { return departureId; }
    public void setDepartureId(String departureId) { this.departureId = departureId; }
    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public Integer getDay() { return day; }
    public void setDay(Integer day) { this.day = day; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public List<String> getPhotos() { return photos; }
    public void setPhotos(List<String> photos) { this.photos = photos; }
    public List<Expense> getExpenses() { return expenses; }
    public void setExpenses(List<Expense> expenses) { this.expenses = expenses; }
}
