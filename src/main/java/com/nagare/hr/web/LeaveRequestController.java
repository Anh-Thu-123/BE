package com.nagare.hr.web;

import com.nagare.common.error.ApiException;
import com.nagare.hr.model.LeaveRequest;
import com.nagare.hr.repo.LeaveRequestRepository;
import com.nagare.hr.service.LeaveRequestService;
import com.nagare.identity.model.Role;
import com.nagare.identity.security.SecurityUtils;
import com.nagare.identity.security.UserPrincipal;
import java.util.List;
import java.util.Set;
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

    // Chi 3 vai tro nay co toan quyen/duyet cho ca phong ban (muc 04: TPDH/TPMK dau muc ●,
    // Giam doc ●) - con lai la "cua minh" (◑), khong duoc thay employeeId de xem don nguoi khac.
    private static final Set<Role> CAN_SEE_ALL = Set.of(Role.DIRECTOR, Role.OPS_MANAGER, Role.MKT_MANAGER, Role.SECRETARY);

    @PostMapping
    public LeaveRequest create(@RequestBody LeaveRequest req) {
        UserPrincipal me = SecurityUtils.currentUser();
        if (me.getEmployeeId() == null) {
            throw ApiException.badRequest("NO_EMPLOYEE_PROFILE", "Tai khoan chua gan ho so nhan su");
        }
        // employeeId luon lay tu nguoi dang goi, khong tin client - tranh nop don thay ten nguoi khac.
        req.setEmployeeId(me.getEmployeeId());
        return leaveRequestService.create(req);
    }

    @GetMapping
    public List<LeaveRequest> list(@RequestParam(required = false) String employeeId) {
        UserPrincipal me = SecurityUtils.currentUser();
        if (CAN_SEE_ALL.contains(me.getRole())) {
            if (employeeId != null) return leaveRequestRepository.findByEmployeeId(employeeId);
            return leaveRequestRepository.findAll();
        }
        // Vai tro con lai chi xem duoc don cua chinh minh, bat ke query param truyen gi.
        if (me.getEmployeeId() == null) return List.of();
        return leaveRequestRepository.findByEmployeeId(me.getEmployeeId());
    }

    public record DecisionRequest(boolean approve, String note) {}

    @PatchMapping("/{id}/decision")
    public LeaveRequestService.DecisionResult decide(@PathVariable String id, @RequestBody DecisionRequest req) {
        String approverUserId = SecurityUtils.currentUser().getId();
        return leaveRequestService.decide(id, approverUserId, req.approve(), req.note());
    }
}
