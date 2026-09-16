package com.nagare.sales.repo;

import com.nagare.sales.model.Booking;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookingRepository extends MongoRepository<Booking, String> {
    Optional<Booking> findByCode(String code);
    List<Booking> findByCustomerId(String customerId);
    List<Booking> findByDepartureId(String departureId);
    List<Booking> findByStatusAndHoldExpiresAtBefore(Booking.Status status, Instant instant);
}
