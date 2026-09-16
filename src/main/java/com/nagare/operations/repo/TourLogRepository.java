package com.nagare.operations.repo;

import com.nagare.operations.model.TourLog;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TourLogRepository extends MongoRepository<TourLog, String> {
    List<TourLog> findByDepartureId(String departureId);
}
