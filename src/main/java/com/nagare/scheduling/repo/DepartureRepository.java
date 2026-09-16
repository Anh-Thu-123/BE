package com.nagare.scheduling.repo;

import com.nagare.scheduling.model.Departure;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DepartureRepository extends MongoRepository<Departure, String> {
    List<Departure> findByTourId(String tourId);
    List<Departure> findByDepartDateBetween(LocalDate from, LocalDate to);
    List<Departure> findByStatusAndDepartDateBetween(Departure.Status status, LocalDate from, LocalDate to);
}
