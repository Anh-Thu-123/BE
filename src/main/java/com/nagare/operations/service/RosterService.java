package com.nagare.operations.service;

import com.nagare.sales.model.Booking;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Luong 4 - HDV chi thay du truong giay to (ho chieu) trong khoang tu 3 ngay truoc den 3 ngay
 * sau khoi hanh. Ngoai khoang do, HDV van thay doan da dan nhung khong con thay giay to khach.
 * Tach thanh service thuan de test khong can DB / HTTP.
 */
public class RosterService {

    private RosterService() {}

    public static boolean isDocumentWindowOpen(LocalDate departDate, LocalDate returnDate, LocalDate today) {
        LocalDate windowStart = departDate.minusDays(3);
        LocalDate windowEnd = returnDate.plusDays(3);
        return !today.isBefore(windowStart) && !today.isAfter(windowEnd);
    }

    public static Map<String, Object> toRosterEntry(Booking.Pax pax, boolean documentWindowOpen) {
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("paxId", pax.getPaxId());
        entry.put("fullName", pax.getFullName());
        entry.put("dob", pax.getDob());
        entry.put("nationality", pax.getNationality());
        entry.put("dietary", pax.getDietary());
        entry.put("note", pax.getNote());
        if (documentWindowOpen) {
            entry.put("passportNo", pax.getPassportNo());
            entry.put("passportExpiry", pax.getPassportExpiry());
        }
        return entry;
    }
}
