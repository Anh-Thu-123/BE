package com.nagare.sales.service;

import com.nagare.catalog.model.Tour;
import com.nagare.catalog.repo.TourRepository;
import com.nagare.common.counter.SequenceGeneratorService;
import com.nagare.common.error.ApiException;
import com.nagare.documents.model.VisaCase;
import com.nagare.documents.service.VisaCaseService;
import com.nagare.sales.model.Booking;
import com.nagare.sales.repo.BookingRepository;
import com.nagare.scheduling.model.Departure;
import com.nagare.scheduling.repo.DepartureRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Luong 1 - Khach giu cho tour ghep, cong ba nhanh bi bo sot o ban 1.0 (muc 06):
 * sua danh sach khach, huy don da xac nhan, doan thieu khach toi thieu.
 *
 * Dem ghe theo occupiesSeat (khong theo tong so khach) - tre duoi 2 tuoi khong chiem ghe.
 * Ba thao tac hold / pax-update / confirm deu chay trong MongoDB transaction that (@Transactional),
 * dung tren Atlas M0 (replica set) theo dung CLAUDE.md.
 */
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final DepartureRepository departureRepository;
    private final TourRepository tourRepository;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final VisaCaseService visaCaseService;

    public BookingService(BookingRepository bookingRepository, DepartureRepository departureRepository,
                           TourRepository tourRepository, SequenceGeneratorService sequenceGeneratorService,
                           VisaCaseService visaCaseService) {
        this.bookingRepository = bookingRepository;
        this.departureRepository = departureRepository;
        this.tourRepository = tourRepository;
        this.sequenceGeneratorService = sequenceGeneratorService;
        this.visaCaseService = visaCaseService;
    }

    /** Buoc 5 luong 1: mot transaction tang seatsHeld va tao booking HELD, holdExpiresAt = +48h. */
    @Transactional
    public Booking hold(Booking booking) {
        Departure departure = departureRepository.findById(booking.getDepartureId())
                .orElseThrow(() -> ApiException.notFound("Doan khoi hanh"));

        long seatsNeeded = booking.seatsOccupied();
        if (departure.seatsAvailable() < seatsNeeded) {
            throw ApiException.conflict("NOT_ENOUGH_SEATS", "Doan khong con du cho trong");
        }

        departure.setSeatsHeld(departure.getSeatsHeld() + (int) seatsNeeded);
        departureRepository.save(departure);

        booking.setCode(sequenceGeneratorService.nextCode("BK"));
        booking.setKind(Booking.Kind.JOIN);
        booking.setStatus(Booking.Status.HELD);
        booking.setHoldExpiresAt(Instant.now().plus(48, ChronoUnit.HOURS));
        capturePricing(booking, departure);
        addTimeline(booking, "HOLD", "Giu cho tour ghep");
        return bookingRepository.save(booking);
    }

    /** Buoc 8 luong 1: chuyen seatsHeld -> seatsConfirmed, sinh ho so visa neu la tour Nhat. */
    @Transactional
    public Booking confirm(String bookingId, String actorId) {
        Booking booking = getOrThrow(bookingId);
        if (booking.getStatus() != Booking.Status.HELD) {
            throw ApiException.conflict("INVALID_STATE", "Chi xac nhan duoc don dang giu cho");
        }
        Departure departure = departureRepository.findById(booking.getDepartureId())
                .orElseThrow(() -> ApiException.notFound("Doan khoi hanh"));

        long seats = booking.seatsOccupied();
        departure.setSeatsHeld(departure.getSeatsHeld() - (int) seats);
        departure.setSeatsConfirmed(departure.getSeatsConfirmed() + (int) seats);
        departureRepository.save(departure);

        booking.setStatus(Booking.Status.CONFIRMED);
        booking.setHoldExpiresAt(null);
        addTimeline(booking, "CONFIRM", "Xac nhan don, chuyen thanh cho chac");
        Booking saved = bookingRepository.save(booking);

        Tour tour = booking.getTourId() != null ? tourRepository.findById(booking.getTourId()).orElse(null) : null;
        if (tour != null && (tour.getType() == Tour.Type.OUTBOUND || tour.getType() == Tour.Type.INBOUND)) {
            VisaCase.CaseType caseType = tour.getType() == Tour.Type.INBOUND
                    ? VisaCase.CaseType.TEMP_RESIDENCE : VisaCase.CaseType.JAPAN_VISA;
            visaCaseService.generateForBooking(saved, departure.getDepartDate(), caseType, actorId);
        }
        return saved;
    }

    /**
     * Huy don - nhanh bi bo sot o ban 1.0. Phai xu ly ca hai truong hop:
     * dang giu cho (tra seatsHeld) va da xac nhan (tra seatsConfirmed + REFUND neu da thu tien).
     */
    @Transactional
    public Booking cancel(String bookingId, String reason, Double refundAmount, String actorId) {
        Booking booking = getOrThrow(bookingId);
        if (booking.getStatus() == Booking.Status.CANCELLED || booking.getStatus() == Booking.Status.COMPLETED) {
            throw ApiException.conflict("INVALID_STATE", "Don da o trang thai cuoi, khong the huy");
        }
        Departure departure = departureRepository.findById(booking.getDepartureId()).orElse(null);
        long seats = booking.seatsOccupied();

        if (departure != null) {
            if (booking.getStatus() == Booking.Status.HELD) {
                departure.setSeatsHeld(Math.max(0, departure.getSeatsHeld() - (int) seats));
            } else if (booking.getStatus() == Booking.Status.CONFIRMED) {
                departure.setSeatsConfirmed(Math.max(0, departure.getSeatsConfirmed() - (int) seats));
            }
            departureRepository.save(departure);
        }

        if (refundAmount != null && refundAmount > 0) {
            Booking.Payment refund = new Booking.Payment();
            refund.setDirection(Booking.PaymentDirection.REFUND);
            refund.setAmount(refundAmount);
            refund.setMethod(Booking.PaymentMethod.BANK_TRANSFER);
            refund.setPaidAt(Instant.now());
            refund.setRecordedBy(actorId);
            refund.setNote("Hoan tien do huy don: " + reason);
            booking.getPayments().add(refund);
        }

        booking.setStatus(Booking.Status.CANCELLED);
        booking.setCancelReason(reason);
        addTimeline(booking, "CANCEL", reason);
        return bookingRepository.save(booking);
    }

    /**
     * Sua danh sach khach sau khi da giu cho / xac nhan - nhanh bi bo sot o ban 1.0.
     * Moi thay doi pax[] phai dieu chinh dung bo dem ghe trong CUNG mot transaction;
     * them nguoi ma doan da day thi tu choi, khong am tham vuot suc chua.
     */
    @Transactional
    public Booking updatePax(String bookingId, List<Booking.Pax> newPaxList) {
        Booking booking = getOrThrow(bookingId);
        if (booking.getStatus() != Booking.Status.HELD && booking.getStatus() != Booking.Status.CONFIRMED) {
            throw ApiException.conflict("INVALID_STATE", "Chi sua duoc danh sach khach cua don dang giu cho hoac da xac nhan");
        }
        Departure departure = departureRepository.findById(booking.getDepartureId())
                .orElseThrow(() -> ApiException.notFound("Doan khoi hanh"));

        long oldSeats = booking.seatsOccupied();
        long newSeats = newPaxList.stream().filter(Booking.Pax::isOccupiesSeat).count();
        long delta = newSeats - oldSeats;

        if (delta > 0 && departure.seatsAvailable() < delta) {
            throw ApiException.conflict("NOT_ENOUGH_SEATS", "Doan khong con du cho de them khach");
        }

        if (booking.getStatus() == Booking.Status.HELD) {
            departure.setSeatsHeld((int) (departure.getSeatsHeld() + delta));
        } else {
            departure.setSeatsConfirmed((int) (departure.getSeatsConfirmed() + delta));
        }
        departureRepository.save(departure);

        booking.setPax(newPaxList);
        addTimeline(booking, "UPDATE_PAX", "Sua danh sach khach, chenh lech ghe: " + delta);
        return bookingRepository.save(booking);
    }

    public Booking recordPayment(String bookingId, Booking.Payment payment) {
        Booking booking = getOrThrow(bookingId);
        payment.setPaidAt(payment.getPaidAt() != null ? payment.getPaidAt() : Instant.now());
        booking.getPayments().add(payment);
        addTimeline(booking, payment.getDirection().name(), "So tien: " + payment.getAmount());
        return bookingRepository.save(booking);
    }

    private void capturePricing(Booking booking, Departure departure) {
        Booking.Pricing pricing = booking.getPricing();
        pricing.setUnitPriceAdult(departure.getPriceAdult());
        pricing.setUnitPriceChild(departure.getPriceChild());
        pricing.setUnitPriceInfant(departure.getPriceInfant());
        pricing.setCurrency(departure.getCurrency());
        pricing.setFxRateToVnd(departure.getFxRateToVnd());

        int adults = (int) booking.getPax().stream().filter(p -> p.getPaxType() == Booking.PaxType.ADULT).count();
        int children = (int) booking.getPax().stream().filter(p -> p.getPaxType() == Booking.PaxType.CHILD).count();
        int infants = (int) booking.getPax().stream().filter(p -> p.getPaxType() == Booking.PaxType.INFANT).count();
        pricing.setAdultCount(adults);
        pricing.setChildCount(children);
        pricing.setInfantCount(infants);

        double addOnTotal = booking.getAddOns().stream()
                .mapToDouble(a -> a.getUnitPrice() * a.getQuantity()).sum();
        pricing.setAddOnTotal(addOnTotal);

        double subtotal = adults * departure.getPriceAdult() + children * departure.getPriceChild()
                + infants * departure.getPriceInfant();
        pricing.setSubtotal(subtotal);
        pricing.setTotal(subtotal + addOnTotal - pricing.getDiscount());
    }

    private void addTimeline(Booking booking, String action, String note) {
        Booking.TimelineEntry entry = new Booking.TimelineEntry();
        entry.setAction(action);
        entry.setNote(note);
        booking.getTimeline().add(entry);
    }

    private Booking getOrThrow(String id) {
        return bookingRepository.findById(id).orElseThrow(() -> ApiException.notFound("Booking"));
    }
}
