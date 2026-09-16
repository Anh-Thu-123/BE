package com.nagare.sales.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Kiem tra bay so 3 (dem ghe theo occupiesSeat, khong theo tong so khach) va
 * bay so 5 (thanh toan hai chieu RECEIPT/REFUND, tinh khi doc khong luu san).
 */
class BookingSeatAndPaymentTest {

    @Test
    void treEmDuoi2TuoiKhongChiemGhe() {
        Booking booking = new Booking();
        booking.setPax(List.of(
                pax(Booking.PaxType.ADULT, true),
                pax(Booking.PaxType.ADULT, true),
                pax(Booking.PaxType.INFANT, false) // tre duoi 2 tuoi, khong chiem ghe
        ));
        assertEquals(2, booking.seatsOccupied(), "Doan 3 nguoi nhung chi 2 nguoi lon chiem ghe");
    }

    @Test
    void daThu_LaTongReceiptTruTongRefund() {
        Booking booking = new Booking();
        Booking.Payment receipt1 = payment(Booking.PaymentDirection.RECEIPT, 10_000_000);
        Booking.Payment receipt2 = payment(Booking.PaymentDirection.RECEIPT, 5_000_000);
        Booking.Payment refund = payment(Booking.PaymentDirection.REFUND, 3_000_000);
        booking.setPayments(List.of(receipt1, receipt2, refund));
        assertEquals(12_000_000, booking.totalPaid(), 0.001);
    }

    @Test
    void congNo_LaTongTienTruDaThu() {
        Booking booking = new Booking();
        booking.getPricing().setTotal(20_000_000);
        booking.setPayments(List.of(payment(Booking.PaymentDirection.RECEIPT, 8_000_000)));
        assertEquals(12_000_000, booking.balance(), 0.001);
    }

    private Booking.Pax pax(Booking.PaxType type, boolean occupiesSeat) {
        Booking.Pax p = new Booking.Pax();
        p.setPaxType(type);
        p.setOccupiesSeat(occupiesSeat);
        return p;
    }

    private Booking.Payment payment(Booking.PaymentDirection direction, double amount) {
        Booking.Payment p = new Booking.Payment();
        p.setDirection(direction);
        p.setAmount(amount);
        return p;
    }
}
