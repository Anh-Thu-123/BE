package com.nagare.scheduling.repo;

import com.nagare.scheduling.model.Assignment;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AssignmentRepository extends MongoRepository<Assignment, String> {
    List<Assignment> findByDepartureId(String departureId);
    List<Assignment> findByEmployeeId(String employeeId);

    /**
     * Truy van chong lan chuan: startDate <= den && endDate >= tu.
     * Dung cho ca chan trung lich khi tao assignment LAN kiem tra nguoc khi duyet nghi phep.
     */
    List<Assignment> findByEmployeeIdAndStatusNotAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            String employeeId, Assignment.Status excludedStatus, LocalDate endDateNew, LocalDate startDateNew);
}
