package com.nagare.scheduling.web;

import com.nagare.hr.model.Employee;
import com.nagare.hr.repo.EmployeeRepository;
import com.nagare.identity.security.SecurityUtils;
import com.nagare.scheduling.model.Assignment;
import com.nagare.scheduling.repo.AssignmentRepository;
import com.nagare.scheduling.service.AssignmentService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final AssignmentRepository assignmentRepository;
    private final EmployeeRepository employeeRepository;

    public AssignmentController(AssignmentService assignmentService, AssignmentRepository assignmentRepository,
                                 EmployeeRepository employeeRepository) {
        this.assignmentService = assignmentService;
        this.assignmentRepository = assignmentRepository;
        this.employeeRepository = employeeRepository;
    }

    /** Tra 409 neu trung lich - xem AssignmentService (luong 3). */
    @PostMapping("/api/assignments")
    @PreAuthorize("hasRole('OPS_MANAGER')")
    public Assignment create(@RequestBody Assignment assignment) {
        return assignmentService.create(assignment);
    }

    @GetMapping("/api/assignments/availability")
    @PreAuthorize("hasRole('OPS_MANAGER')")
    public List<Employee> availability(@RequestParam String from, @RequestParam String to) {
        LocalDate f = LocalDate.parse(from);
        LocalDate t = LocalDate.parse(to);
        List<Employee> guides = employeeRepository.findAll().stream()
                .filter(e -> e.getGuideProfile() != null).toList();
        return guides.stream().filter(e -> assignmentService.findConflictingAssignmentsForLeave(e.getId(), f, t).isEmpty()
                        && assignmentRepository
                        .findByEmployeeIdAndStatusNotAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                                e.getId(), Assignment.Status.CANCELLED, t, f).isEmpty())
                .toList();
    }

    @GetMapping("/api/me/assignments")
    public List<Assignment> myAssignments() {
        String employeeId = SecurityUtils.currentUser().getEmployeeId();
        return assignmentRepository.findByEmployeeId(employeeId);
    }
}
