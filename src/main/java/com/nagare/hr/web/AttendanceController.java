package com.nagare.hr.web;

import com.nagare.common.error.ApiException;
import com.nagare.hr.model.Attendance;
import com.nagare.hr.repo.AttendanceRepository;
import com.nagare.identity.model.Role;
import com.nagare.identity.security.SecurityUtils;
import com.nagare.identity.security.UserPrincipal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
@PreAuthorize("!hasRole('CUSTOMER')")
public class AttendanceController {

    // Muc 04 "Cham cong": GD/TPDH/TPMK chi xem (o) toan cong ty, TK toan quyen (●);
    // TKT/HDV/CSKH la "cua minh" (◑) - chi thay du lieu cua chinh minh.
    private static final Set<Role> CAN_SEE_ALL = Set.of(Role.DIRECTOR, Role.SECRETARY, Role.OPS_MANAGER, Role.MKT_MANAGER);

    private final AttendanceRepository attendanceRepository;

    public AttendanceController(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    @PostMapping("/check-in")
    public Attendance checkIn() {
        String employeeId = SecurityUtils.currentUser().getEmployeeId();
        LocalDate today = LocalDate.now();
        Attendance a = attendanceRepository.findByEmployeeIdAndDate(employeeId, today).orElseGet(Attendance::new);
        a.setEmployeeId(employeeId);
        a.setDate(today);
        // Thu tu uu tien khi ghi de: ON_TOUR thang LEAVE thang WEB.
        if (a.getSource() == null || a.getSource() == Attendance.Source.WEB) {
            a.setSource(Attendance.Source.WEB);
        }
        a.setCheckInAt(Instant.now());
        return attendanceRepository.save(a);
    }

    /** employeeId tuy chon: Thu ky/truong phong xem bang cong ca cong ty; con lai chi thay cua minh. */
    @GetMapping
    public List<Attendance> byMonth(@RequestParam String month, @RequestParam(required = false) String employeeId) {
        UserPrincipal me = SecurityUtils.currentUser();
        YearMonth ym = YearMonth.parse(month);

        if (!CAN_SEE_ALL.contains(me.getRole())) {
            if (me.getEmployeeId() == null) return List.of();
            if (employeeId != null && !employeeId.equals(me.getEmployeeId())) {
                throw ApiException.forbidden("Chi xem duoc bang cong cua chinh minh");
            }
            return attendanceRepository.findByEmployeeIdAndDateBetween(me.getEmployeeId(), ym.atDay(1), ym.atEndOfMonth());
        }
        if (employeeId != null && !employeeId.isBlank()) {
            return attendanceRepository.findByEmployeeIdAndDateBetween(employeeId, ym.atDay(1), ym.atEndOfMonth());
        }
        return attendanceRepository.findByDateBetween(ym.atDay(1), ym.atEndOfMonth());
    }
}
