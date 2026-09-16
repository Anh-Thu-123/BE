package com.nagare.importing.web;

import com.nagare.catalog.model.Tour;
import com.nagare.catalog.repo.TourRepository;
import com.nagare.common.model.Bilingual;
import com.nagare.sales.model.Customer;
import com.nagare.sales.repo.CustomerRepository;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/** Nhap lieu hang loat tu file Excel cu (tour va khach hang), theo muc 01 va 03. */
@RestController
@RequestMapping("/api/import")
@PreAuthorize("hasAnyRole('DIRECTOR','SECRETARY')")
public class ImportController {

    private final TourRepository tourRepository;
    private final CustomerRepository customerRepository;

    public ImportController(TourRepository tourRepository, CustomerRepository customerRepository) {
        this.tourRepository = tourRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * Cot mong doi: code | title_vi | title_ja | type | durationDays | durationNights | basePriceAdult | currency.
     * preview=true chi doc va tra ve, khong ghi - "xem truoc roi moi ghi" theo muc 07.
     */
    @PostMapping("/tours")
    public List<Tour> importTours(@RequestParam MultipartFile file, @RequestParam(defaultValue = "true") boolean preview) throws Exception {
        List<Tour> tours = new ArrayList<>();
        try (InputStream is = file.getInputStream(); Workbook wb = WorkbookFactory.create(is)) {
            Sheet sheet = wb.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // header
                if (row.getCell(0) == null) continue;
                Tour t = new Tour();
                t.setCode(cell(row, 0));
                Bilingual title = new Bilingual(cell(row, 1), cell(row, 2));
                t.setTitle(title);
                t.setSlug(new Bilingual(slugify(cell(row, 1)), slugify(cell(row, 2))));
                try { t.setType(Tour.Type.valueOf(cell(row, 3))); } catch (Exception ignored) {}
                t.setDurationDays((int) numCell(row, 4));
                t.setDurationNights((int) numCell(row, 5));
                t.setBasePriceAdult(numCell(row, 6));
                t.setCurrency(cell(row, 7));
                tours.add(t);
            }
        }
        if (!preview) {
            tourRepository.saveAll(tours);
        }
        return tours;
    }

    /**
     * Cot mong doi: fullName | phone | email | nationality.
     * Gop theo so dien thoai TRONG chinh file import (khach cu trong Excel), khac voi viec KHONG tu gop
     * tai khoan web moi voi ho so cu - hai tinh huong khac nhau, xem CLAUDE.md.
     */
    @PostMapping("/customers")
    public List<Customer> importCustomers(@RequestParam MultipartFile file, @RequestParam(defaultValue = "true") boolean preview) throws Exception {
        List<Customer> customers = new ArrayList<>();
        try (InputStream is = file.getInputStream(); Workbook wb = WorkbookFactory.create(is)) {
            Sheet sheet = wb.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                if (row.getCell(0) == null) continue;
                Customer c = new Customer();
                c.setFullName(cell(row, 0));
                c.setPhone(cell(row, 1));
                c.setEmail(cell(row, 2));
                c.setNationality(cell(row, 3));
                c.setSource(Customer.Source.IMPORT);
                customers.add(c);
            }
        }
        if (!preview) {
            customerRepository.saveAll(customers);
        }
        return customers;
    }

    private String cell(Row row, int idx) {
        var c = row.getCell(idx);
        return c == null ? null : c.toString().trim();
    }

    private double numCell(Row row, int idx) {
        var c = row.getCell(idx);
        if (c == null) return 0;
        try { return c.getNumericCellValue(); } catch (Exception e) {
            try { return Double.parseDouble(c.toString().trim()); } catch (Exception ex) { return 0; }
        }
    }

    private String slugify(String s) {
        if (s == null) return null;
        return s.toLowerCase().trim().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
    }
}
