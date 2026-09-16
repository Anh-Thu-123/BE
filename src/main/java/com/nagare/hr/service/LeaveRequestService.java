package com.nagare.hr.service;

import com.nagare.common.error.ApiException;
import com.nagare.hr.model.Employee;
import com.nagare.hr.model.LeaveRequest;
import com.nagare.hr.repo.EmployeeRepository;
import com.nagare.hr.repo.LeaveRequestRepository;
import com.nagare.identity.model.Role;
import com.nagare.identity.model.User;
import com.nagare.identity.repo.UserRepository;
import com.nagare.scheduling.model.Assignment;
import com.nagare.scheduling.service.AssignmentService;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final AssignmentService assignmentService;

    public LeaveRequestService(LeaveRequestRepository leaveRequestRepository, EmployeeRepository employeeRepository,
                                UserRepository userRepository, AssignmentService assignmentService) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.assignmentService = assignmentService;
    }

    public record DecisionResult(LeaveRequest leaveRequest, List<Assignment> conflictingAssignments) {}

    public LeaveRequest create(LeaveRequest req) {
        req.setStatus(LeaveRequest.Status.PENDING);
        return leaveRequestRepository.save(req);
    }

    /**
     * Duyet/tu choi don nghi phep. Bat buoc: nguoi duyet khong duoc la chinh chu don (ke ca Giam doc).
     * Truoc khi duyet, tra ve danh sach assignment dang chong lan de FE hien canh bao -
     * duyet de len assignment la mot hanh dong CO CANH BAO, khong phai cu bam binh thuong (luong 3).
     */
    public DecisionResult decide(String leaveRequestId, String approverUserId, boolean approve, String note) {
        LeaveRequest lr = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> ApiException.notFound("Don nghi phep"));

        User approverUser = userRepository.findById(approverUserId)
                .orElseThrow(() -> ApiException.notFound("Tai khoan"));

        Employee requesterEmployee = employeeRepository.findById(lr.getEmployeeId())
                .orElseThrow(() -> ApiException.notFound("Nhan vien"));

        if (requesterEmployee.getUserId() != null && requesterEmployee.getUserId().equals(approverUserId)) {
            throw ApiException.forbidden("Khong the tu duyet don nghi phep cua chinh minh");
        }

        Role expectedApprover = LeaveApprovalChain.approverRoleFor(approverUser.getRole(), requesterEmployee.getDepartment());
        if (expectedApprover != null && approverUser.getRole() != expectedApprover && approverUser.getRole() != Role.DIRECTOR) {
            throw ApiException.forbidden("Ban khong nam trong chuoi duyet cua don nay");
        }

        List<Assignment> conflicts = assignmentService.findConflictingAssignmentsForLeave(
                lr.getEmployeeId(), lr.getFromDate(), lr.getToDate());

        lr.setStatus(approve ? LeaveRequest.Status.APPROVED : LeaveRequest.Status.REJECTED);
        lr.setApproverId(approverUserId);
        lr.setDecidedAt(Instant.now());
        lr.setDecisionNote(note);
        LeaveRequest saved = leaveRequestRepository.save(lr);
        return new DecisionResult(saved, conflicts);
    }
}
