package com.nagare.hr.web;

import com.nagare.common.error.ApiException;
import com.nagare.hr.model.Employee;
import com.nagare.hr.repo.EmployeeRepository;
import com.nagare.identity.model.User;
import com.nagare.identity.model.UserStatus;
import com.nagare.identity.repo.UserRepository;
import com.nagare.identity.security.SecurityUtils;
import com.nagare.scheduling.model.Assignment;
import com.nagare.scheduling.repo.AssignmentRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;
    private final MongoTemplate mongoTemplate;

    public EmployeeController(EmployeeRepository employeeRepository, UserRepository userRepository,
                               AssignmentRepository assignmentRepository, MongoTemplate mongoTemplate) {
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.mongoTemplate = mongoTemplate;
    }

    /** GD, Thu ky xem tat ca; truong phong chi xem khoi minh - quyen "cua minh" nam trong cau truy van. */
    @GetMapping
    @PreAuthorize("hasAnyRole('DIRECTOR','SECRETARY','OPS_MANAGER','MKT_MANAGER')")
    public List<Employee> list() {
        var user = SecurityUtils.currentUser();
        Query query = new Query();
        switch (user.getRole()) {
            case OPS_MANAGER -> query.addCriteria(Criteria.where("department").is(Employee.Department.OPERATIONS));
            case MKT_MANAGER -> query.addCriteria(Criteria.where("department").is(Employee.Department.SALES));
            default -> { /* DIRECTOR, SECRETARY: xem tat ca */ }
        }
        return mongoTemplate.find(query, Employee.class);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('DIRECTOR','SECRETARY')")
    public Employee create(@RequestBody Employee employee) {
        return employeeRepository.save(employee);
    }

    /** Khoa tai khoan + chuyen phan cong tuong lai cho nguoi khac, khong xoa trang (giu handoverFromId). */
    @PostMapping("/{id}/offboard")
    @PreAuthorize("hasAnyRole('DIRECTOR','SECRETARY')")
    public void offboard(@PathVariable String id, @RequestBody(required = false) OffboardRequest req) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> ApiException.notFound("Nhan vien"));
        employee.setStatus(Employee.Status.OFFBOARDED);
        employeeRepository.save(employee);

        if (employee.getUserId() != null) {
            userRepository.findById(employee.getUserId()).ifPresent(u -> {
                u.setStatus(UserStatus.LOCKED);
                u.setTokenVersion(u.getTokenVersion() + 1);
                userRepository.save(u);
            });
        }

        if (req != null && req.replacementEmployeeId() != null) {
            List<Assignment> future = assignmentRepository.findByEmployeeId(id).stream()
                    .filter(a -> a.getStartDate() != null && a.getStartDate().isAfter(LocalDate.now())
                            && a.getStatus() != Assignment.Status.CANCELLED)
                    .toList();
            for (Assignment a : future) {
                a.setHandoverFromId(id);
                a.setEmployeeId(req.replacementEmployeeId());
                assignmentRepository.save(a);
            }
        }
    }

    public record OffboardRequest(String replacementEmployeeId) {}
}
