package com.nagare.sales.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nagare.catalog.repo.TourRepository;
import com.nagare.common.counter.SequenceGeneratorService;
import com.nagare.common.error.ApiException;
import com.nagare.documents.service.VisaCaseService;
import com.nagare.sales.model.Booking;
import com.nagare.sales.repo.BookingRepository;
import com.nagare.scheduling.model.Departure;
import com.nagare.scheduling.repo.DepartureRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Luong 1 - giu cho tour ghep. Kiem tra dung dem ghe theo occupiesSeat va tu choi khi khong du cho -
 * logic transaction (tang seatsHeld) phai chay dung TRUOC khi lo qua Mongo that.
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock BookingRepository bookingRepository;
    @Mock DepartureRepository departureRepository;
    @Mock TourRepository tourRepository;
    @Mock SequenceGeneratorService sequenceGeneratorService;
    @Mock VisaCaseService visaCaseService;

    BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(bookingRepository, departureRepository, tourRepository,
                sequenceGeneratorService, visaCaseService);
    }

    private Departure departureWithCapacity(int capacity, int held, int confirmed) {
        Departure d = new Departure();
        d.setId("dep1");
        d.setCapacity(capacity);
        d.setSeatsHeld(held);
        d.setSeatsConfirmed(confirmed);
        d.setCurrency("VND");
        d.setPriceAdult(1000);
        d.setPriceChild(800);
        d.setPriceInfant(0);
        return d;
    }

    private Booking bookingWithPax(int adultsOccupyingSeat, int infantsNotOccupying) {
        Booking b = new Booking();
        b.setDepartureId("dep1");
        java.util.ArrayList<Booking.Pax> pax = new java.util.ArrayList<>();
        for (int i = 0; i < adultsOccupyingSeat; i++) {
            Booking.Pax p = new Booking.Pax();
            p.setPaxType(Booking.PaxType.ADULT);
            p.setOccupiesSeat(true);
            pax.add(p);
        }
        for (int i = 0; i < infantsNotOccupying; i++) {
            Booking.Pax p = new Booking.Pax();
            p.setPaxType(Booking.PaxType.INFANT);
            p.setOccupiesSeat(false);
            pax.add(p);
        }
        b.setPax(pax);
        return b;
    }

    @Test
    void tuChoiGiuCho_khiKhongDuChoTrong() {
        when(departureRepository.findById("dep1")).thenReturn(Optional.of(departureWithCapacity(10, 9, 0)));
        Booking booking = bookingWithPax(2, 0); // can 2 cho, chi con 1

        ApiException ex = assertThrows(ApiException.class, () -> bookingService.hold(booking));
        assertEquals("NOT_ENOUGH_SEATS", ex.getCode());
    }

    @Test
    void giuCho_ChiDemNguoiChiemGhe_TreEmDuoi2TuoiKhongTinh() {
        Departure departure = departureWithCapacity(10, 0, 0);
        when(departureRepository.findById("dep1")).thenReturn(Optional.of(departure));
        when(sequenceGeneratorService.nextCode("BK")).thenReturn("BK-2026-0001");
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(departureRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Booking booking = bookingWithPax(2, 1); // 2 nguoi lon + 1 tre so sinh (khong chiem ghe)
        Booking result = bookingService.hold(booking);

        assertEquals(Booking.Status.HELD, result.getStatus());
        assertEquals(2, departure.getSeatsHeld(), "Chi 2 nguoi lon chiem ghe, tre so sinh khong tinh");
    }

    @Test
    void xacNhanDon_ChuyenTuSeatsHeldSangSeatsConfirmed() {
        Departure departure = departureWithCapacity(10, 2, 0);
        Booking booking = bookingWithPax(2, 0);
        booking.setId("bk1");
        booking.setStatus(Booking.Status.HELD);

        when(bookingRepository.findById("bk1")).thenReturn(Optional.of(booking));
        when(departureRepository.findById("dep1")).thenReturn(Optional.of(departure));
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(departureRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        bookingService.confirm("bk1", "staff1");

        assertEquals(0, departure.getSeatsHeld());
        assertEquals(2, departure.getSeatsConfirmed());
    }

    @Test
    void huyDonDaXacNhan_TraLaiSeatsConfirmed() {
        Departure departure = departureWithCapacity(10, 0, 3);
        Booking booking = bookingWithPax(3, 0);
        booking.setId("bk2");
        booking.setStatus(Booking.Status.CONFIRMED);

        when(bookingRepository.findById("bk2")).thenReturn(Optional.of(booking));
        when(departureRepository.findById("dep1")).thenReturn(Optional.of(departure));
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(departureRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        bookingService.cancel("bk2", "Khach doi y", null, "staff1");

        assertEquals(0, departure.getSeatsConfirmed(), "Huy don da xac nhan phai tra lai seatsConfirmed (nhanh bi bo sot o ban 1.0)");
        assertEquals(Booking.Status.CANCELLED, booking.getStatus());
    }

    @Test
    void suaDanhSachKhach_TuChoi_khiThemNguoiVuotSucChua() {
        Departure departure = departureWithCapacity(5, 0, 5); // da day
        Booking booking = bookingWithPax(5, 0);
        booking.setId("bk3");
        booking.setStatus(Booking.Status.CONFIRMED);

        when(bookingRepository.findById("bk3")).thenReturn(Optional.of(booking));
        when(departureRepository.findById("dep1")).thenReturn(Optional.of(departure));

        List<Booking.Pax> newList = new java.util.ArrayList<>(booking.getPax());
        Booking.Pax extra = new Booking.Pax();
        extra.setPaxType(Booking.PaxType.ADULT);
        extra.setOccupiesSeat(true);
        newList.add(extra);

        ApiException ex = assertThrows(ApiException.class, () -> bookingService.updatePax("bk3", newList));
        assertEquals("NOT_ENOUGH_SEATS", ex.getCode());
    }
}
