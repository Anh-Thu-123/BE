package com.nagare.catalog.repo;

import com.nagare.catalog.model.Tour;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TourRepository extends MongoRepository<Tour, String> {
    Optional<Tour> findBySlugVi(String slugVi);
    Optional<Tour> findBySlugJa(String slugJa);
}
