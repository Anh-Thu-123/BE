package com.nagare.scheduling.service;

import com.nagare.common.error.ApiException;
import com.nagare.hr.model.LeaveRequest;
import com.nagare.hr.repo.LeaveRequestRepository;
import com.nagare.scheduling.model.Assignment;
import com.nagare.scheduling.model.Departure;
import com.nagare.scheduling.repo.AssignmentRepository;
import com.nagare.scheduling.repo.DepartureRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Luong 3 - Phan cong HDV khong trung lich. Chan ca hai chieu:
 * 1) luc tao assignment moi: kiem assignments hien co VA leaveRequests da duyet chong lan.
 * 2) luc duyet leave request: kiem assignments hien co chong lan (canh bao, khong chan cung -
 *    xem AssignmentConflictWarning dung o hr.LeaveRequestService).
 */
@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final DepartureRepository departureRepository;

    public AssignmentService(AssignmentRepository assignmentRepository,
                              LeaveRequestRepository leaveRequestRepository,
                              DepartureRepository departureRepository) {
        this.assignmentRepository = assignmentRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.departureRepository = departureRepository;
    }

    public Assignment create(Assignment a) {
        Departure departure = departureRepository.findById(a.getDepartureId())
                .orElseThrow(() -> ApiException.notFound("Doan khoi hanh"));
        if (a.getStartDate() == null) a.setStartDate(departure.getDepartDate());
        if (a.getEndDate() == null) a.setEndDate(departure.getReturnDate());

        List<Assignment> overlapping = findOverlappingAssignments(a.getEmployeeId(), a.getStartDate(), a.getEndDate());
        if (!overlapping.isEmpty()) {
            String names = overlapping.stream().map(Assignment::getDepartureId).distinct()
                    .reduce((x, y) -> x + ", " + y).orElse("");
            throw ApiException.conflict("SCHEDULE_CONFLICT",
                    "Nhan vien da duoc phan cong trung thoi gian voi doan: " + names);
        }

        List<LeaveRequest> approvedLeaves = leaveRequestRepository
                .findByEmployeeIdAndStatusAndFromDateLessThanEqualAndToDateGreaterThanEqual(
                        a.getEmployeeId(), LeaveRequest.Status.APPROVED, a.getEndDate(), a.getStartDate());
        if (!approvedLeaves.isEmpty()) {
            throw ApiException.conflict("LEAVE_CONFLICT",
                    "Nhan vien da co don nghi phep duoc duyet trong khoang thoi gian nay");
        }

        return assignmentRepository.save(a);
    }

    private List<Assignment> findOverlappingAssignments(String employeeId, LocalDate start, LocalDate end) {
        return assignmentRepository
                .findByEmployeeIdAndStatusNotAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        employeeId, Assignment.Status.CANCELLED, end, start);
    }

    /** Dung o buoc duyet nghi phep: tra ve cac assignment dang chong lan de hien canh bao, KHONG chan cung. */
    public List<Assignment> findConflictingAssignmentsForLeave(String employeeId, LocalDate from, LocalDate to) {
        return findOverlappingAssignments(employeeId, from, to);
    }
}
