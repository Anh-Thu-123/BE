package com.nagare.sales.service;

import com.nagare.common.error.ApiException;
import com.nagare.sales.model.Customer;
import com.nagare.sales.repo.CustomerRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Bay so 2 / loi 11: KHONG BAO GIO tu gan tai khoan hoac ho so cu theo so dien thoai trung -
 * ai biet so dien thoai cung se chiem duoc lich su don hang cua nguoi khac. Luon tao ho so moi,
 * gan nhan "nghi trung" neu trung phone, de nhan vien CSKH xac minh roi gop thu cong qua mergedIntoId.
 */
@Service
public class CustomerService {

    private static final String SUSPECT_DUPLICATE_TAG = "nghi trung";

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer findOrCreateByPhoneForBooking(String phone, String fullName, String userId, Customer.Source source) {
        if (userId != null) {
            var existing = customerRepository.findByUserId(userId);
            if (existing.isPresent()) return existing.get();
        }

        List<Customer> samePhone = customerRepository.findByPhone(phone);
        Customer c = new Customer();
        c.setUserId(userId);
        c.setFullName(fullName);
        c.setPhone(phone);
        c.setSource(source);
        if (!samePhone.isEmpty()) {
            c.setTags(List.of(SUSPECT_DUPLICATE_TAG));
        }
        return customerRepository.save(c);
    }

    public Customer merge(String sourceId, String targetId) {
        if (sourceId.equals(targetId)) {
            throw ApiException.badRequest("SAME_CUSTOMER", "Khong the gop mot ho so voi chinh no");
        }
        Customer source = customerRepository.findById(sourceId).orElseThrow(() -> ApiException.notFound("Ho so khach"));
        customerRepository.findById(targetId).orElseThrow(() -> ApiException.notFound("Ho so khach"));
        source.setMergedIntoId(targetId);
        return customerRepository.save(source);
    }
}
