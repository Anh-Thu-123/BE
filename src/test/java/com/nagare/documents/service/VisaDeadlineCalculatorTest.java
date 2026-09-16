package com.nagare.documents.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

/** Kiem tra loi 11: han nop visa phai la moc SOM HON trong hai moc, khong duoc qua han ngay luc tao. */
class VisaDeadlineCalculatorTest {

    @Test
    void chonMoc30NgayTruocKhoiHanh_khiKhoiHanhConXa() {
        LocalDate today = LocalDate.of(2026, 1, 1);
        LocalDate departDate = LocalDate.of(2026, 6, 1); // con rat xa, 30 ngay truoc se som hon 7 ngay tu hom nay
        Instant deadline = VisaDeadlineCalculator.deadline(departDate, today);
        Instant expected = departDate.minusDays(30).atStartOfDay(ZoneOffset.UTC).toInstant();
        assertEquals(expected, deadline);
    }

    @Test
    void chonMoc7NgayKeTuHomNay_khiDonChotSatNgayKhoiHanh() {
        LocalDate today = LocalDate.of(2026, 1, 1);
        LocalDate departDate = LocalDate.of(2026, 1, 10); // chi con 9 ngay - 30 ngay truoc da o QUA KHU
        Instant deadline = VisaDeadlineCalculator.deadline(departDate, today);
        Instant expected = today.plusDays(7).atStartOfDay(ZoneOffset.UTC).toInstant();
        assertEquals(expected, deadline, "Phai lay moc 7 ngay ke tu hom nay, khong duoc de han da qua han ngay luc tao");
    }

    @Test
    void hanKhongDuocOQuaKhu_ngayLucTao() {
        LocalDate today = LocalDate.of(2026, 3, 1);
        LocalDate departDate = LocalDate.of(2026, 3, 5); // don chot rat sat ngay
        Instant deadline = VisaDeadlineCalculator.deadline(departDate, today);
        Instant todayStart = today.atStartOfDay(ZoneOffset.UTC).toInstant();
        org.junit.jupiter.api.Assertions.assertFalse(deadline.isBefore(todayStart),
                "Han nop khong duoc nam trong qua khu ngay khi vua sinh ho so");
    }
}
