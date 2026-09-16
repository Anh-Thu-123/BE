package com.nagare.documents.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * Han nop visa = SOM HON trong hai moc: 30 ngay truoc khoi hanh, hoac 7 ngay ke tu hom nay.
 * Cong thuc cu chi lay moc dau nen don chot sat ngay sinh ho so DA QUA HAN NGAY LUC TAO -
 * day la loi 11 duoc sua trong ban 1.1. Tach thanh lop tuy nhien de test khong can DB that.
 */
public final class VisaDeadlineCalculator {

    private VisaDeadlineCalculator() {}

    public static Instant deadline(LocalDate departDate, LocalDate today) {
        LocalDate thirtyDaysBefore = departDate.minusDays(30);
        LocalDate sevenDaysFromNow = today.plusDays(7);
        // Lay moc MUON HON (sau hon) trong hai moc: binh thuong dung dung "30 ngay truoc khoi hanh";
        // nhung khi don chot sat ngay khien moc do roi vao QUA KHU, sa n xuong "7 ngay ke tu hom nay"
        // de han luon nam trong tuong lai. Neu lay moc SOM HON (min) se tai tao lai chinh loi 11 dang tranh.
        LocalDate later = thirtyDaysBefore.isAfter(sevenDaysFromNow) ? thirtyDaysBefore : sevenDaysFromNow;
        return later.atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    public static Instant deadline(LocalDate departDate) {
        return deadline(departDate, LocalDate.now());
    }
}
