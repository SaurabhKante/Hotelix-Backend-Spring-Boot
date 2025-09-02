package com.hotelix.hotel_management.expense;

import com.hotelix.hotel_management.common.ApiResponse;
import com.hotelix.hotel_management.expense.dto.ExpenseAddRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ExpenseController {
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/api/expense/v1/get-vendors")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllVendors(){
        ApiResponse<List<Map<String, Object>>> response = expenseService.getAllVendors();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/expense/v1/get-object")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getObjectsByDateRange(
            @RequestBody Map<String, String> request) {
        String startDate = request.get("start_date");
        String endDate   = request.get("end_date");

        ApiResponse<List<Map<String, Object>>> response = expenseService.getObjectsByDateRange(startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/expense/v1/add-object")
    public ResponseEntity<ApiResponse<Object>> addObject(@RequestBody ExpenseAddRequest request){
        ApiResponse<Object> response = expenseService.addObject(request.getName(), request.getVendor_id(), request.getPrice(),request.getPayment_method());
        return ResponseEntity.ok(response);
    }
}
