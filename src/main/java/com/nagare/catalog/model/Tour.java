package com.nagare.catalog.model;

import com.nagare.common.model.AuditableEntity;
import com.nagare.common.model.Bilingual;
import java.time.Instant;
import java.util.List;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tours")
public class Tour extends AuditableEntity {

    public enum Type { OUTBOUND, INBOUND, DOMESTIC }
    public enum Theme { HEALING, YOUTH, ACADEMIC, CLASSIC, NATURE, ADVENTURE, HERITAGE }
    public enum PubStatus { DRAFT, PENDING_REVIEW, PUBLISHED, ARCHIVED }

    private String code;
    private Type type;
    private Theme theme;
    private Bilingual title;
    private Bilingual slug;
    private Bilingual summary;
    private List<Bilingual> highlights;
    private Bilingual targetAudience;
    private int durationDays;
    private int durationNights;
    private List<String> destinations;
    private List<ItineraryDay> itinerary;
    private List<Bilingual> inclusions;
    private List<Bilingual> exclusions;
    private String coverImage;
    private List<TourImage> images;
    private double basePriceAdult;
    private String currency; // VND|JPY
    private String designerId;
    private Publication publication = new Publication();

    public static class ItineraryDay {
        private int day;
        private Bilingual title;
        private Bilingual detail;
        private List<String> meals;
        private String accommodation;

        public int getDay() { return day; }
        public void setDay(int day) { this.day = day; }
        public Bilingual getTitle() { return title; }
        public void setTitle(Bilingual title) { this.title = title; }
        public Bilingual getDetail() { return detail; }
        public void setDetail(Bilingual detail) { this.detail = detail; }
        public List<String> getMeals() { return meals; }
        public void setMeals(List<String> meals) { this.meals = meals; }
        public String getAccommodation() { return accommodation; }
        public void setAccommodation(String accommodation) { this.accommodation = accommodation; }
    }

    public static class TourImage {
        private String publicId;
        private Bilingual alt;

        public String getPublicId() { return publicId; }
        public void setPublicId(String publicId) { this.publicId = publicId; }
        public Bilingual getAlt() { return alt; }
        public void setAlt(Bilingual alt) { this.alt = alt; }
    }

    /** Trang thai xuat ban tach rieng theo tung ngon ngu - xem muc 05 va nhat ky loi 11. */
    public static class Publication {
        private LangPub vi = new LangPub();
        private LangPub ja = new LangPub();

        public LangPub getVi() { return vi; }
        public void setVi(LangPub vi) { this.vi = vi; }
        public LangPub getJa() { return ja; }
        public void setJa(LangPub ja) { this.ja = ja; }
    }

    public static class LangPub {
        private PubStatus status = PubStatus.DRAFT;
        private Instant publishedAt;

        public PubStatus getStatus() { return status; }
        public void setStatus(PubStatus status) { this.status = status; }
        public Instant getPublishedAt() { return publishedAt; }
        public void setPublishedAt(Instant publishedAt) { this.publishedAt = publishedAt; }
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public Theme getTheme() { return theme; }
    public void setTheme(Theme theme) { this.theme = theme; }
    public Bilingual getTitle() { return title; }
    public void setTitle(Bilingual title) { this.title = title; }
    public Bilingual getSlug() { return slug; }
    public void setSlug(Bilingual slug) { this.slug = slug; }
    public Bilingual getSummary() { return summary; }
    public void setSummary(Bilingual summary) { this.summary = summary; }
    public List<Bilingual> getHighlights() { return highlights; }
    public void setHighlights(List<Bilingual> highlights) { this.highlights = highlights; }
    public Bilingual getTargetAudience() { return targetAudience; }
    public void setTargetAudience(Bilingual targetAudience) { this.targetAudience = targetAudience; }
    public int getDurationDays() { return durationDays; }
    public void setDurationDays(int durationDays) { this.durationDays = durationDays; }
    public int getDurationNights() { return durationNights; }
    public void setDurationNights(int durationNights) { this.durationNights = durationNights; }
    public List<String> getDestinations() { return destinations; }
    public void setDestinations(List<String> destinations) { this.destinations = destinations; }
    public List<ItineraryDay> getItinerary() { return itinerary; }
    public void setItinerary(List<ItineraryDay> itinerary) { this.itinerary = itinerary; }
    public List<Bilingual> getInclusions() { return inclusions; }
    public void setInclusions(List<Bilingual> inclusions) { this.inclusions = inclusions; }
    public List<Bilingual> getExclusions() { return exclusions; }
    public void setExclusions(List<Bilingual> exclusions) { this.exclusions = exclusions; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public List<TourImage> getImages() { return images; }
    public void setImages(List<TourImage> images) { this.images = images; }
    public double getBasePriceAdult() { return basePriceAdult; }
    public void setBasePriceAdult(double basePriceAdult) { this.basePriceAdult = basePriceAdult; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getDesignerId() { return designerId; }
    public void setDesignerId(String designerId) { this.designerId = designerId; }
    public Publication getPublication() { return publication; }
    public void setPublication(Publication publication) { this.publication = publication; }
}
