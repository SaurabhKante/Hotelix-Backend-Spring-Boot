package com.hotelix.hotel_management.order;

import com.hotelix.hotel_management.common.ApiResponse;
import com.hotelix.hotel_management.order.dto.DeleteOrderRequest;
import com.hotelix.hotel_management.order.dto.OrderRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/api/orders/v1/get-dues")
    public ResponseEntity<ApiResponse<List<Map<String,Object>>>> getPendingDues(){
        ApiResponse<List<Map<String, Object>>> response = orderService.getPendingDues();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/orders/v1/get-amount")
    public ResponseEntity<ApiResponse<List<Map<String,Object>>>> getAllPendingOrders(){
        ApiResponse<List<Map<String, Object>>> response = orderService.getAllPendingOrders();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/orders/v1/create-order")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createOrder(@RequestBody OrderRequest request) {
        ApiResponse<Map<String, Object>> response = orderService.createOrder(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/orders/v1/get-order/{table_id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPendingOrderItems(
            @PathVariable("table_id") int tableId) {
        ApiResponse<Map<String, Object>> response = orderService.getPendingOrderItems(tableId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/orders/v1/add-payments")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addPaymentDetails(@RequestBody Map<String, Object> requestBody) {
        ApiResponse<Map<String, Object>> response = orderService.addPaymentDetails(requestBody);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/api/orders/v1/get-order-details")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOrderDetails(
            @RequestBody(required = false) Map<String, String> body) {
        String startDate = body != null ? body.get("startDate") : null;
        String endDate = body != null ? body.get("endDate") : null;
        return ResponseEntity.ok(orderService.getOrderDetails(startDate, endDate));
    }

    @PostMapping("/api/orders/v1/pay-dues")
    public ResponseEntity<ApiResponse<String>> payDueAmount(@RequestBody Map<String, Object> body) {
        String customerName = (String) body.get("customerName");
        String mobileNumber = (String) body.get("mobileNumber");
        String paymentMethod = (String) body.get("paymentMethod");
        Double paymentAmount = body.get("paymentAmount") != null ? Double.parseDouble(body.get("paymentAmount").toString()) : null;

        ApiResponse<String> response = orderService.payDueAmount(customerName, mobileNumber, paymentMethod, paymentAmount);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/orders/v1/delete-order")
    public ResponseEntity<ApiResponse<OrderService.DeleteOrderResponse>> deletePendingOrder(@RequestBody DeleteOrderRequest request){
        ApiResponse<OrderService.DeleteOrderResponse> response = orderService.deletePendingOrders(request.getTable_id());
        return ResponseEntity.ok(response);
    }
}
