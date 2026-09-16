package com.nagare.catalog.repo;

import com.nagare.catalog.model.AddOnService;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AddOnServiceRepository extends MongoRepository<AddOnService, String> {
    List<AddOnService> findByStatus(AddOnService.Status status);
}
