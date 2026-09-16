package com.nagare.operations.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Luong 4 - HDV chi thay du truong giay to trong khoang 3 ngay truoc den 3 ngay sau chuyen di. */
class RosterServiceTest {

    private static final LocalDate DEPART = LocalDate.of(2026, 6, 10);
    private static final LocalDate RETURN = LocalDate.of(2026, 6, 15);

    @Test
    void moTruongGiayTo_trongKhoang3NgayTruocKhoiHanh() {
        assertTrue(RosterService.isDocumentWindowOpen(DEPART, RETURN, DEPART.minusDays(2)));
    }

    @Test
    void moTruongGiayTo_trongKhoang3NgaySauKhiVe() {
        assertTrue(RosterService.isDocumentWindowOpen(DEPART, RETURN, RETURN.plusDays(3)));
    }

    @Test
    void dongTruongGiayTo_quaXaTruocKhoiHanh() {
        assertFalse(RosterService.isDocumentWindowOpen(DEPART, RETURN, DEPART.minusDays(10)));
    }

    @Test
    void dongTruongGiayTo_quaXaSauKhiVe() {
        assertFalse(RosterService.isDocumentWindowOpen(DEPART, RETURN, RETURN.plusDays(10)));
    }

    @Test
    void banRutGon_khongChuaSoHoChieu_khiNgoaiKhoang() {
        var pax = new com.nagare.sales.model.Booking.Pax();
        pax.setPassportNo("B1234567");
        Map<String, Object> entry = RosterService.toRosterEntry(pax, false);
        assertFalse(entry.containsKey("passportNo"));
    }

    @Test
    void banDayDu_ChuaSoHoChieu_khiTrongKhoang() {
        var pax = new com.nagare.sales.model.Booking.Pax();
        pax.setPassportNo("B1234567");
        Map<String, Object> entry = RosterService.toRosterEntry(pax, true);
        assertTrue(entry.containsKey("passportNo"));
    }
}
