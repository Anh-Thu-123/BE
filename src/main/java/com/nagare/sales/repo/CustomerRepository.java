package com.nagare.sales.repo;

import com.nagare.sales.model.Customer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerRepository extends MongoRepository<Customer, String> {
    List<Customer> findByPhone(String phone);
    Optional<Customer> findByUserId(String userId);
}
