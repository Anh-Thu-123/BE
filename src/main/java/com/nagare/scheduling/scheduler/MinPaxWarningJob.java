package com.nagare.scheduling.scheduler;

import com.nagare.common.notification.Notification;
import com.nagare.common.notification.NotificationRepository;
import com.nagare.identity.model.Role;
import com.nagare.identity.repo.UserRepository;
import com.nagare.scheduling.model.Departure;
import com.nagare.scheduling.repo.DepartureRepository;
import java.time.LocalDate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Canh bao doan chua du khach toi thieu o moc 15 ngay truoc khoi hanh, bao Truong phong Dieu hanh. */
@Component
public class MinPaxWarningJob {

    private final DepartureRepository departureRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public MinPaxWarningJob(DepartureRepository departureRepository, UserRepository userRepository,
                             NotificationRepository notificationRepository) {
        this.departureRepository = departureRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    @Scheduled(cron = "0 0 7 * * *")
    public void warnBelowMinimum() {
        LocalDate target = LocalDate.now().plusDays(15);
        var departures = departureRepository.findByStatusAndDepartDateBetween(Departure.Status.OPEN, target, target);
        var opsManagers = userRepository.findAll().stream().filter(u -> u.getRole() == Role.OPS_MANAGER).toList();

        for (Departure d : departures) {
            if (d.getSeatsConfirmed() < d.getMinPax()) {
                for (var manager : opsManagers) {
                    Notification n = new Notification();
                    n.setUserId(manager.getId());
                    n.setType("MIN_PAX_WARNING");
                    n.setTitle("Doan " + d.getCode() + " chua du khach toi thieu");
                    n.setBody("Con 15 ngay den khoi hanh, moi co " + d.getSeatsConfirmed() + "/" + d.getMinPax() + " khach.");
                    n.setLink("/admin/departures/" + d.getId());
                    notificationRepository.save(n);
                }
            }
        }
    }
}
