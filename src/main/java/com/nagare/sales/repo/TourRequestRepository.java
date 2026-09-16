package com.nagare.sales.repo;

import com.nagare.sales.model.TourRequest;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TourRequestRepository extends MongoRepository<TourRequest, String> {
    List<TourRequest> findByStatus(TourRequest.Status status);
    List<TourRequest> findByAssigneeId(String assigneeId);
}
