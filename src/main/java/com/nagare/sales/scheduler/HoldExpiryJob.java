package com.nagare.sales.scheduler;

import com.nagare.sales.model.Booking;
import com.nagare.sales.repo.BookingRepository;
import com.nagare.sales.service.BookingService;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Buoc 9 luong 1: khong lien lac duoc sau 48 gio thi nha cho, huy don kem ly do het han giu cho. */
@Component
public class HoldExpiryJob {

    private static final Logger log = LoggerFactory.getLogger(HoldExpiryJob.class);

    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    public HoldExpiryJob(BookingRepository bookingRepository, BookingService bookingService) {
        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
    }

    @Scheduled(fixedDelayString = "PT5M")
    public void releaseExpiredHolds() {
        var expired = bookingRepository.findByStatusAndHoldExpiresAtBefore(Booking.Status.HELD, Instant.now());
        for (Booking b : expired) {
            try {
                bookingService.cancel(b.getId(), "Het han giu cho (khong lien lac duoc sau 48 gio)", null, "system");
            } catch (Exception e) {
                log.warn("Khong the nha cho giu qua han cho booking {}: {}", b.getId(), e.getMessage());
            }
        }
    }
}
