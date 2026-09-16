package com.nagare.hr.service;

import com.nagare.hr.model.Employee;
import com.nagare.identity.model.Role;

/**
 * Chuoi duyet nghi phep co dinh theo muc 04:
 * Van hanh -> Truong phong Dieu hanh; Kinh doanh -> Truong phong Marketing;
 * hai truong phong va Thu ky -> Giam doc; Giam doc khong can duyet (chi ghi nhan).
 * Tuyet doi: khong ai duyet duoc don cua chinh minh, ke ca Giam doc.
 */
public final class LeaveApprovalChain {

    private LeaveApprovalChain() {}

    public static Role approverRoleFor(Role requesterRole, Employee.Department department) {
        if (requesterRole == Role.DIRECTOR) {
            return null; // khong qua buoc duyet, chi ghi nhan
        }
        if (requesterRole == Role.OPS_MANAGER || requesterRole == Role.MKT_MANAGER || requesterRole == Role.SECRETARY) {
            return Role.DIRECTOR;
        }
        if (department == Employee.Department.OPERATIONS) {
            return Role.OPS_MANAGER;
        }
        if (department == Employee.Department.SALES) {
            return Role.MKT_MANAGER;
        }
        return Role.DIRECTOR;
    }
}
