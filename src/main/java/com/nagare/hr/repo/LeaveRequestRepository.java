package com.nagare.hr.repo;

import com.nagare.hr.model.LeaveRequest;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LeaveRequestRepository extends MongoRepository<LeaveRequest, String> {
    List<LeaveRequest> findByEmployeeId(String employeeId);
    List<LeaveRequest> findByStatus(LeaveRequest.Status status);
    List<LeaveRequest> findByEmployeeIdAndStatusAndFromDateLessThanEqualAndToDateGreaterThanEqual(
            String employeeId, LeaveRequest.Status status, java.time.LocalDate to, java.time.LocalDate from);
}
