package com.hotelix.hotel_management.expense;

import com.hotelix.hotel_management.common.ApiResponse;
import com.hotelix.hotel_management.dish.DishService;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    private ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ApiResponse<List<Map<String,Object>>> getAllVendors(){
        try {
            List<Object[]> results= expenseRepository.getAllVendors();
            List<Map<String,Object>> vendors = results.stream().map(row -> {
                Object[] cols = row;
                Map<String,Object> map = new HashMap<>();
                map.put("vendor_id", cols[0]);
                map.put("vendor_name", cols[1]);
                return map;
            }).collect(Collectors.toList());
            if (vendors.isEmpty()) {
                return ApiResponse.SUCCESS("No vendors found.",vendors);
            }else {return ApiResponse.SUCCESS("Vendors retrieved successfully.",vendors);
            }
        } catch (Exception e) {
            e.printStackTrace(); // log full stack trace
            return ApiResponse.failure("Failed to retrieve vendors. Please try again later. " + e);
        }
    }

    public ApiResponse<List<Map<String,Object>>> getObjectsByDateRange(String startDate, String endDate) {
        try {
            // default to today's date if not provided
            String currentDate = java.time.LocalDate.now().toString();
            String finalStart = (startDate == null || startDate.isEmpty()) ? currentDate : startDate;
            String finalEnd   = (endDate == null || endDate.isEmpty()) ? currentDate : endDate;

            // validate date format
            try {
                java.time.LocalDate.parse(finalStart);
                java.time.LocalDate.parse(finalEnd);
            } catch (Exception e) {
                return ApiResponse.failure("Invalid date format. Use 'YYYY-MM-DD'.");
            }

            List<Object[]> results = expenseRepository.getObjectsByDateRange(finalStart, finalEnd);

            List<Map<String,Object>> objects = results.stream().map(row -> {
                Map<String,Object> map = new HashMap<>();
                map.put("object_name", row[0]);
                map.put("vendor_id", row[1]);
                map.put("price", row[2]);
                map.put("payment_method", row[3]);
                map.put("created_at", ((java.sql.Timestamp) row[4]).toLocalDateTime().format(formatter));
                map.put("vendor_name", row[5]);
                return map;
            }).collect(Collectors.toList());

            return ApiResponse.SUCCESS("Objects retrieved successfully.", objects);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.failure("Failed to retrieve objects. Please try again later. " + e.getMessage());
        }
    }

    public ApiResponse<Object> addObject(String name, Integer vendor_id, Integer price, String payment_method){
        if (name == null || name.equals("") ||payment_method == null ||payment_method.equals("") || vendor_id == null || price==null ) {
            return ApiResponse.failure("All fields (name, vendor_id, price, payment_method) are required.");
        }
        try {
            Object insertedId = expenseRepository.addObject(name,vendor_id,price,payment_method);

            return ApiResponse.SUCCESS("Object added successfully.",
                    new ExpenseService.ExpenseResponse((Long) insertedId));
        } catch (Exception e) {
            return ApiResponse.failure("Failed to add object. Please try again later. "+ e);
        }
    }

    public record ExpenseResponse(Long insertedId) {
    }
}

