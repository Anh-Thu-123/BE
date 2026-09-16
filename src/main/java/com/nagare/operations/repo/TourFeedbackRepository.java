package com.nagare.operations.repo;

import com.nagare.operations.model.TourFeedback;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TourFeedbackRepository extends MongoRepository<TourFeedback, String> {
    List<TourFeedback> findByDepartureId(String departureId);
}
