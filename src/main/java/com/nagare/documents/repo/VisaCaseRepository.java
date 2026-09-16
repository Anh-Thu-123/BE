package com.nagare.documents.repo;

import com.nagare.documents.model.VisaCase;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VisaCaseRepository extends MongoRepository<VisaCase, String> {
    List<VisaCase> findByBookingId(String bookingId);
    Optional<VisaCase> findByBookingIdAndPaxId(String bookingId, String paxId);
    List<VisaCase> findByStatus(VisaCase.Status status);
}
