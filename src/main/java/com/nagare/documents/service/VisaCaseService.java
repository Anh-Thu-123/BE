package com.nagare.documents.service;

import com.nagare.documents.model.VisaCase;
import com.nagare.documents.repo.VisaCaseRepository;
import com.nagare.sales.model.Booking;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

/** Luong 4 - sinh mot visaCase cho moi hanh khach khi booking tour Nhat chuyen sang CONFIRMED. */
@Service
public class VisaCaseService {

    private final VisaCaseRepository visaCaseRepository;

    public VisaCaseService(VisaCaseRepository visaCaseRepository) {
        this.visaCaseRepository = visaCaseRepository;
    }

    public void generateForBooking(Booking booking, LocalDate departDate, VisaCase.CaseType caseType, String handlerId) {
        for (Booking.Pax pax : booking.getPax()) {
            if (visaCaseRepository.findByBookingIdAndPaxId(booking.getId(), pax.getPaxId()).isPresent()) {
                continue;
            }
            VisaCase vc = new VisaCase();
            vc.setBookingId(booking.getId());
            vc.setPaxId(pax.getPaxId());
            vc.setCustomerName(pax.getFullName());
            vc.setCaseType(caseType);
            vc.setStatus(VisaCase.Status.COLLECTING);
            vc.setDeadline(VisaDeadlineCalculator.deadline(departDate));
            vc.setHandlerId(handlerId);
            visaCaseRepository.save(vc);
        }
    }
}
