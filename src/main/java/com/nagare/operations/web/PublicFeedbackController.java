package com.nagare.operations.web;

import com.nagare.common.error.ApiException;
import com.nagare.operations.model.TourFeedback;
import com.nagare.operations.repo.TourFeedbackRepository;
import com.nagare.sales.model.Booking;
import com.nagare.sales.repo.BookingRepository;
import org.springframework.web.bind.annotation.*;

/**
 * Khach cham diem sau tour - khong can dang nhap, dung mot duong dan mot lan gan vao ma don (token = bookingId
 * o day de don gian; sinh xa hon co the doi thanh chuoi ngau nhien rieng neu can an toan hon).
 */
@RestController
@RequestMapping("/api/public/feedback")
public class PublicFeedbackController {

    private final TourFeedbackRepository tourFeedbackRepository;
    private final BookingRepository bookingRepository;

    public PublicFeedbackController(TourFeedbackRepository tourFeedbackRepository, BookingRepository bookingRepository) {
        this.tourFeedbackRepository = tourFeedbackRepository;
        this.bookingRepository = bookingRepository;
    }

    @PostMapping("/{token}")
    public TourFeedback submit(@PathVariable String token, @RequestBody TourFeedback feedback) {
        Booking booking = bookingRepository.findById(token).orElseThrow(() -> ApiException.notFound("Don hang"));
        feedback.setBookingId(booking.getId());
        feedback.setDepartureId(booking.getDepartureId());
        feedback.setCustomerId(booking.getCustomerId());
        return tourFeedbackRepository.save(feedback);
    }
}
