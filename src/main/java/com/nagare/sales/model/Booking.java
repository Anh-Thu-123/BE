package com.nagare.sales.model;

import com.nagare.common.model.AuditableEntity;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * bookings: trung tam he thong. KHONG luu paidAmount/balance - tinh khi doc tu payments[].
 * Moi hanh khach co paxId rieng, khong bao gio tro bang vi tri mang.
 */
@Document(collection = "bookings")
public class Booking extends AuditableEntity {

    public enum Kind { JOIN, CUSTOM }
    public enum Status { HELD, CONFIRMED, COMPLETED, CANCELLED }
    public enum PaxType { ADULT, CHILD, INFANT }
    public enum AddOnStatus { REQUESTED, CONFIRMED, UNAVAILABLE }
    public enum PaymentDirection { RECEIPT, REFUND }
    public enum PaymentMethod { CASH, BANK_TRANSFER }

    private String code;
    private Kind kind;
    private String departureId;
    private String tourId;
    private String tourRequestId;
    private Integer quoteVersion;
    private String customerId;
    private Contact contact;
    private List<Pax> pax = new ArrayList<>();
    private List<AddOn> addOns = new ArrayList<>();
    private Status status = Status.HELD;
    private Instant holdExpiresAt;
    private Pricing pricing = new Pricing();
    private List<Payment> payments = new ArrayList<>();
    private String salesOwnerId;
    private String source;
    private String cancelReason;
    private List<TimelineEntry> timeline = new ArrayList<>();

    public static class Contact {
        private String fullName;
        private String phone;
        private String email;
        private String zalo;

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getZalo() { return zalo; }
        public void setZalo(String zalo) { this.zalo = zalo; }
    }

    public static class Pax {
        private String paxId;
        private String fullName;
        private LocalDate dob;
        private String gender;
        private PaxType paxType;
        private boolean occupiesSeat = true;
        private String passportNo;
        private LocalDate passportExpiry;
        private String nationality;
        private String dietary;
        private String note;

        public String getPaxId() { return paxId; }
        public void setPaxId(String paxId) { this.paxId = paxId; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public LocalDate getDob() { return dob; }
        public void setDob(LocalDate dob) { this.dob = dob; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public PaxType getPaxType() { return paxType; }
        public void setPaxType(PaxType paxType) { this.paxType = paxType; }
        public boolean isOccupiesSeat() { return occupiesSeat; }
        public void setOccupiesSeat(boolean occupiesSeat) { this.occupiesSeat = occupiesSeat; }
        public String getPassportNo() { return passportNo; }
        public void setPassportNo(String passportNo) { this.passportNo = passportNo; }
        public LocalDate getPassportExpiry() { return passportExpiry; }
        public void setPassportExpiry(LocalDate passportExpiry) { this.passportExpiry = passportExpiry; }
        public String getNationality() { return nationality; }
        public void setNationality(String nationality) { this.nationality = nationality; }
        public String getDietary() { return dietary; }
        public void setDietary(String dietary) { this.dietary = dietary; }
        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }
    }

    public static class AddOn {
        private String addOnId;
        private String nameSnapshot;
        private double unitPrice;
        private int quantity;
        private List<String> paxIds;
        private AddOnStatus status = AddOnStatus.REQUESTED;

        public String getAddOnId() { return addOnId; }
        public void setAddOnId(String addOnId) { this.addOnId = addOnId; }
        public String getNameSnapshot() { return nameSnapshot; }
        public void setNameSnapshot(String nameSnapshot) { this.nameSnapshot = nameSnapshot; }
        public double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public List<String> getPaxIds() { return paxIds; }
        public void setPaxIds(List<String> paxIds) { this.paxIds = paxIds; }
        public AddOnStatus getStatus() { return status; }
        public void setStatus(AddOnStatus status) { this.status = status; }
    }

    /** unitPrices chup lai gia tai thoi diem chot - sua gia doan ve sau khong lam doi so tien don da ky. */
    public static class Pricing {
        private int adultCount;
        private int childCount;
        private int infantCount;
        private double unitPriceAdult;
        private double unitPriceChild;
        private double unitPriceInfant;
        private double addOnTotal;
        private double subtotal;
        private double discount;
        private double total;
        private String currency;
        private Double fxRateToVnd;

        public int getAdultCount() { return adultCount; }
        public void setAdultCount(int adultCount) { this.adultCount = adultCount; }
        public int getChildCount() { return childCount; }
        public void setChildCount(int childCount) { this.childCount = childCount; }
        public int getInfantCount() { return infantCount; }
        public void setInfantCount(int infantCount) { this.infantCount = infantCount; }
        public double getUnitPriceAdult() { return unitPriceAdult; }
        public void setUnitPriceAdult(double unitPriceAdult) { this.unitPriceAdult = unitPriceAdult; }
        public double getUnitPriceChild() { return unitPriceChild; }
        public void setUnitPriceChild(double unitPriceChild) { this.unitPriceChild = unitPriceChild; }
        public double getUnitPriceInfant() { return unitPriceInfant; }
        public void setUnitPriceInfant(double unitPriceInfant) { this.unitPriceInfant = unitPriceInfant; }
        public double getAddOnTotal() { return addOnTotal; }
        public void setAddOnTotal(double addOnTotal) { this.addOnTotal = addOnTotal; }
        public double getSubtotal() { return subtotal; }
        public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
        public double getDiscount() { return discount; }
        public void setDiscount(double discount) { this.discount = discount; }
        public double getTotal() { return total; }
        public void setTotal(double total) { this.total = total; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public Double getFxRateToVnd() { return fxRateToVnd; }
        public void setFxRateToVnd(Double fxRateToVnd) { this.fxRateToVnd = fxRateToVnd; }
    }

    /** direction RECEIPT|REFUND - da thu = tong RECEIPT - tong REFUND, tinh khi doc, khong luu san. */
    public static class Payment {
        private PaymentDirection direction;
        private double amount;
        private PaymentMethod method;
        private Instant paidAt;
        private String recordedBy;
        private String reference;
        private String note;

        public PaymentDirection getDirection() { return direction; }
        public void setDirection(PaymentDirection direction) { this.direction = direction; }
        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
        public PaymentMethod getMethod() { return method; }
        public void setMethod(PaymentMethod method) { this.method = method; }
        public Instant getPaidAt() { return paidAt; }
        public void setPaidAt(Instant paidAt) { this.paidAt = paidAt; }
        public String getRecordedBy() { return recordedBy; }
        public void setRecordedBy(String recordedBy) { this.recordedBy = recordedBy; }
        public String getReference() { return reference; }
        public void setReference(String reference) { this.reference = reference; }
        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }
    }

    public static class TimelineEntry {
        private Instant at = Instant.now();
        private String by;
        private String action;
        private String note;

        public Instant getAt() { return at; }
        public void setAt(Instant at) { this.at = at; }
        public String getBy() { return by; }
        public void setBy(String by) { this.by = by; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }
    }

    /** So liem suy ra khi doc: tong RECEIPT - tong REFUND. */
    public double totalPaid() {
        double receipt = payments.stream().filter(p -> p.getDirection() == PaymentDirection.RECEIPT)
                .mapToDouble(Payment::getAmount).sum();
        double refund = payments.stream().filter(p -> p.getDirection() == PaymentDirection.REFUND)
                .mapToDouble(Payment::getAmount).sum();
        return receipt - refund;
    }

    /** Cong no suy ra khi doc = tong tien don - da thu. */
    public double balance() {
        return pricing.getTotal() - totalPaid();
    }

    /** So khach chiem ghe - tre duoi 2 tuoi (INFANT) khong chiem ghe. */
    public long seatsOccupied() {
        return pax.stream().filter(Pax::isOccupiesSeat).count();
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Kind getKind() { return kind; }
    public void setKind(Kind kind) { this.kind = kind; }
    public String getDepartureId() { return departureId; }
    public void setDepartureId(String departureId) { this.departureId = departureId; }
    public String getTourId() { return tourId; }
    public void setTourId(String tourId) { this.tourId = tourId; }
    public String getTourRequestId() { return tourRequestId; }
    public void setTourRequestId(String tourRequestId) { this.tourRequestId = tourRequestId; }
    public Integer getQuoteVersion() { return quoteVersion; }
    public void setQuoteVersion(Integer quoteVersion) { this.quoteVersion = quoteVersion; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public Contact getContact() { return contact; }
    public void setContact(Contact contact) { this.contact = contact; }
    public List<Pax> getPax() { return pax; }
    public void setPax(List<Pax> pax) { this.pax = pax; }
    public List<AddOn> getAddOns() { return addOns; }
    public void setAddOns(List<AddOn> addOns) { this.addOns = addOns; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Instant getHoldExpiresAt() { return holdExpiresAt; }
    public void setHoldExpiresAt(Instant holdExpiresAt) { this.holdExpiresAt = holdExpiresAt; }
    public Pricing getPricing() { return pricing; }
    public void setPricing(Pricing pricing) { this.pricing = pricing; }
    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }
    public String getSalesOwnerId() { return salesOwnerId; }
    public void setSalesOwnerId(String salesOwnerId) { this.salesOwnerId = salesOwnerId; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }
    public List<TimelineEntry> getTimeline() { return timeline; }
    public void setTimeline(List<TimelineEntry> timeline) { this.timeline = timeline; }
}
