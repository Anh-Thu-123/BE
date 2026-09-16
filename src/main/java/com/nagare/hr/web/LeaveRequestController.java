package com.nagare.hr.web;

import com.nagare.hr.model.LeaveRequest;
import com.nagare.hr.repo.LeaveRequestRepository;
import com.nagare.hr.service.LeaveRequestService;
import com.nagare.identity.security.SecurityUtils;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave-requests")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;
    private final LeaveRequestRepository leaveRequestRepository;

    public LeaveRequestController(LeaveRequestService leaveRequestService, LeaveRequestRepository leaveRequestRepository) {
        this.leaveRequestService = leaveRequestService;
        this.leaveRequestRepository = leaveRequestRepository;
    }

    @PostMapping
    public LeaveRequest create(@RequestBody LeaveRequest req) {
        return leaveRequestService.create(req);
    }

    @GetMapping
    public List<LeaveRequest> list(@RequestParam(required = false) String employeeId) {
        if (employeeId != null) return leaveRequestRepository.findByEmployeeId(employeeId);
        return leaveRequestRepository.findAll();
    }

    public record DecisionRequest(boolean approve, String note) {}

    @PatchMapping("/{id}/decision")
    public LeaveRequestService.DecisionResult decide(@PathVariable String id, @RequestBody DecisionRequest req) {
        String approverUserId = SecurityUtils.currentUser().getId();
        return leaveRequestService.decide(id, approverUserId, req.approve(), req.note());
    }
}
