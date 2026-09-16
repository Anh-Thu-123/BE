package com.nagare.catalog.model;

import com.nagare.common.model.AuditableEntity;
import com.nagare.common.model.Bilingual;
import java.util.List;
import org.springframework.data.mongodb.core.mapping.Document;

/** addOnServices: dich vu dac quyen muc 7.3 ho so nang luc - gan vao tung don, gia rieng, khong nam trong gia tour. */
@Document(collection = "addOnServices")
public class AddOnService extends AuditableEntity {

    public enum Category { CONCERT, WORKSHOP, COSTUME_PHOTO, PRIVATE_ONSEN, SIM_WIFI, OTHER }
    public enum PricingUnit { PER_PAX, PER_BOOKING, PER_GROUP }
    public enum Status { ACTIVE, INACTIVE }

    private String code;
    private Bilingual name;
    private Bilingual description;
    private Category category;
    private PricingUnit pricingUnit;
    private double price;
    private String currency;
    private AppliesTo appliesTo;
    private int leadTimeDays;
    private boolean requiresConfirmation;
    private Status status = Status.ACTIVE;

    public static class AppliesTo {
        private List<String> tourTypes;
        private List<String> tourIds;

        public List<String> getTourTypes() { return tourTypes; }
        public void setTourTypes(List<String> tourTypes) { this.tourTypes = tourTypes; }
        public List<String> getTourIds() { return tourIds; }
        public void setTourIds(List<String> tourIds) { this.tourIds = tourIds; }
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Bilingual getName() { return name; }
    public void setName(Bilingual name) { this.name = name; }
    public Bilingual getDescription() { return description; }
    public void setDescription(Bilingual description) { this.description = description; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public PricingUnit getPricingUnit() { return pricingUnit; }
    public void setPricingUnit(PricingUnit pricingUnit) { this.pricingUnit = pricingUnit; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public AppliesTo getAppliesTo() { return appliesTo; }
    public void setAppliesTo(AppliesTo appliesTo) { this.appliesTo = appliesTo; }
    public int getLeadTimeDays() { return leadTimeDays; }
    public void setLeadTimeDays(int leadTimeDays) { this.leadTimeDays = leadTimeDays; }
    public boolean isRequiresConfirmation() { return requiresConfirmation; }
    public void setRequiresConfirmation(boolean requiresConfirmation) { this.requiresConfirmation = requiresConfirmation; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
