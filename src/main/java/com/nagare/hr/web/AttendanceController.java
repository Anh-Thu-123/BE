package com.nagare.hr.web;

import com.nagare.hr.model.Attendance;
import com.nagare.hr.repo.AttendanceRepository;
import com.nagare.identity.security.SecurityUtils;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

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

    @GetMapping
    public List<Attendance> byMonth(@RequestParam String month, @RequestParam String employeeId) {
        YearMonth ym = YearMonth.parse(month);
        return attendanceRepository.findByEmployeeIdAndDateBetween(employeeId, ym.atDay(1), ym.atEndOfMonth());
    }
}
