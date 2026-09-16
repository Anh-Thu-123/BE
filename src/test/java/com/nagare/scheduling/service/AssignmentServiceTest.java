package com.nagare.scheduling.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.nagare.common.error.ApiException;
import com.nagare.hr.model.LeaveRequest;
import com.nagare.hr.repo.LeaveRequestRepository;
import com.nagare.scheduling.model.Assignment;
import com.nagare.scheduling.model.Departure;
import com.nagare.scheduling.repo.AssignmentRepository;
import com.nagare.scheduling.repo.DepartureRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Luong 3 - chan trung lich HDV luc tao assignment: phai kiem ca assignments hien co LAN leaveRequests da duyet. */
@ExtendWith(MockitoExtension.class)
class AssignmentServiceTest {

    @Mock AssignmentRepository assignmentRepository;
    @Mock LeaveRequestRepository leaveRequestRepository;
    @Mock DepartureRepository departureRepository;

    AssignmentService service;

    @BeforeEach
    void setUp() {
        service = new AssignmentService(assignmentRepository, leaveRequestRepository, departureRepository);
    }

    private Departure departureFrom(LocalDate depart, LocalDate ret) {
        Departure d = new Departure();
        d.setDepartDate(depart);
        d.setReturnDate(ret);
        return d;
    }

    @Test
    void tuChoi_khiTrungVoiAssignmentHienCo() {
        Assignment newAssignment = new Assignment();
        newAssignment.setEmployeeId("emp1");
        newAssignment.setDepartureId("dep-new");
        newAssignment.setStartDate(LocalDate.of(2026, 5, 10));
        newAssignment.setEndDate(LocalDate.of(2026, 5, 15));

        when(departureRepository.findById("dep-new"))
                .thenReturn(Optional.of(departureFrom(LocalDate.of(2026, 5, 10), LocalDate.of(2026, 5, 15))));

        Assignment existing = new Assignment();
        existing.setDepartureId("dep-old-hokkaido");
        when(assignmentRepository.findByEmployeeIdAndStatusNotAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                anyString(), any(), any(), any())).thenReturn(List.of(existing));

        ApiException ex = assertThrows(ApiException.class, () -> service.create(newAssignment));
        assertEquals("SCHEDULE_CONFLICT", ex.getCode());
    }

    @Test
    void tuChoi_khiTrungVoiDonNghiPhepDaDuyet() {
        Assignment newAssignment = new Assignment();
        newAssignment.setEmployeeId("emp1");
        newAssignment.setDepartureId("dep-new");
        newAssignment.setStartDate(LocalDate.of(2026, 5, 10));
        newAssignment.setEndDate(LocalDate.of(2026, 5, 15));

        when(departureRepository.findById("dep-new"))
                .thenReturn(Optional.of(departureFrom(LocalDate.of(2026, 5, 10), LocalDate.of(2026, 5, 15))));
        when(assignmentRepository.findByEmployeeIdAndStatusNotAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                anyString(), any(), any(), any())).thenReturn(List.of());
        when(leaveRequestRepository.findByEmployeeIdAndStatusAndFromDateLessThanEqualAndToDateGreaterThanEqual(
                anyString(), any(), any(), any())).thenReturn(List.of(new LeaveRequest()));

        ApiException ex = assertThrows(ApiException.class, () -> service.create(newAssignment));
        assertEquals("LEAVE_CONFLICT", ex.getCode());
    }

    @Test
    void choPhep_khiKhongTrung() {
        Assignment newAssignment = new Assignment();
        newAssignment.setEmployeeId("emp1");
        newAssignment.setDepartureId("dep-new");
        newAssignment.setStartDate(LocalDate.of(2026, 5, 10));
        newAssignment.setEndDate(LocalDate.of(2026, 5, 15));

        when(departureRepository.findById("dep-new"))
                .thenReturn(Optional.of(departureFrom(LocalDate.of(2026, 5, 10), LocalDate.of(2026, 5, 15))));
        when(assignmentRepository.findByEmployeeIdAndStatusNotAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                anyString(), any(), any(), any())).thenReturn(List.of());
        when(leaveRequestRepository.findByEmployeeIdAndStatusAndFromDateLessThanEqualAndToDateGreaterThanEqual(
                anyString(), any(), any(), any())).thenReturn(List.of());
        when(assignmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Assignment saved = service.create(newAssignment);
        assertEquals("emp1", saved.getEmployeeId());
    }
}
