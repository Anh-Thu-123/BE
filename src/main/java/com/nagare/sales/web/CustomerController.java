package com.nagare.sales.web;

import com.nagare.sales.model.Customer;
import com.nagare.sales.service.CustomerService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    public record MergeRequest(@NotBlank String targetId) {}

    /** Gop hai ho so nghi trung - thao tac thu cong co kiem duyet, khong bao gio tu dong. */
    @PostMapping("/{id}/merge")
    @PreAuthorize("hasAnyRole('MKT_STAFF','SECRETARY','MKT_MANAGER')")
    public Customer merge(@PathVariable String id, @RequestBody MergeRequest req) {
        return customerService.merge(id, req.targetId());
    }
}
