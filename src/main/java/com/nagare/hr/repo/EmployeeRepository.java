package com.nagare.hr.repo;

import com.nagare.hr.model.Employee;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmployeeRepository extends MongoRepository<Employee, String> {
    Optional<Employee> findByUserId(String userId);
}
