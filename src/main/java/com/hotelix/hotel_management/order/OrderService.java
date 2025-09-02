package com.hotelix.hotel_management.order;

import com.hotelix.hotel_management.common.ApiResponse;
import com.hotelix.hotel_management.common.TimeUtils;
import com.hotelix.hotel_management.dish.dto.DishUpdateResponse;
import com.hotelix.hotel_management.order.dto.OrderItemRequest;
import com.hotelix.hotel_management.order.dto.OrderRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.*;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ApiResponse<List<Map<String, Object>>> getPendingDues() {
        try {
            List<Object[]> results = orderRepository.getPendingDues();
            List<Map<String, Object>> dues = results.stream().map(row -> {
                Object[] cols = row;
                Map<String, Object> map = new HashMap<>();
                map.put("customer_name", cols[0]);
                map.put("mobile_number", cols[1]);
                map.put("due_amount", cols[2]);
                map.put("created_at", ((java.sql.Timestamp) row[3]).toLocalDateTime().format(formatter));
                return map;
            }).collect(Collectors.toList());
            if (dues.isEmpty()) {
                return ApiResponse.SUCCESS("No dues found.", dues);
            } else {
                return ApiResponse.SUCCESS("Dues retrieved successfully.", dues);
            }
        } catch (Exception e) {
            e.printStackTrace(); // log full stack trace
            return ApiResponse.failure("Failed to fetch pending dues. Please try again. " + e);
        }
    }

    public ApiResponse<List<Map<String, Object>>> getAllPendingOrders() {
        try {
            List<Object[]> results = orderRepository.getAllPendingOrders();
            List<Map<String, Object>> orders = results.stream().map(row -> {
                Object[] cols = row;
                Map<String, Object> map = new HashMap<>();
                map.put("table_id", cols[0]);
                map.put("total_amount", cols[1]);
                return map;
            }).collect(Collectors.toList());
            if (orders.isEmpty()) {
                return ApiResponse.SUCCESS("No pending orders found..", orders);
            } else {
                return ApiResponse.SUCCESS("Pending orders retrieved successfully.", orders);
            }
        } catch (Exception e) {
            e.printStackTrace(); // log full stack trace
            return ApiResponse.failure("Failed to retrieve pending orders. Please try again later. " + e);
        }
    }



    @Transactional
    public ApiResponse<Map<String, Object>> createOrder(OrderRequest request) {
        try {
            if (request.getTable_id() == null || request.getOrder_items() == null || request.getOrder_items().isEmpty()) {
                return ApiResponse.failure("Invalid input: 'table_id' and 'order_items' are required.");
            }

            // calculate new total
            double newOrderTotal = request.getOrder_items().stream()
                    .mapToDouble(i -> i.getQuantity() * i.getPrice())
                    .sum();

            // check existing pending order
            List<Object[]> existingOrder = orderRepository.findPendingOrderByTable(request.getTable_id());

            Integer orderId;
            double finalTotal;

            if (!existingOrder.isEmpty()) {
                orderId = ((Number) existingOrder.get(0)[0]).intValue();
                double existingTotal = ((Number) existingOrder.get(0)[1]).doubleValue();
                finalTotal = existingTotal + newOrderTotal;

                orderRepository.updateOrderTotal(orderId, finalTotal);
            } else {
                String currentTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).format(formatter);
                Object newOrderId = orderRepository.createOrder(request.getTable_id(), newOrderTotal, currentTime);
                orderId = ((Number) newOrderId).intValue();
                finalTotal = newOrderTotal;
            }

            // prepare order_items
            List<Object[]> orderItemsData = new ArrayList<>();
            for (OrderItemRequest item : request.getOrder_items()) {
                orderItemsData.add(new Object[]{
                        orderId,
                        item.getDish_id(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getQuantity() * item.getPrice()
                });
            }
            orderRepository.addOrderItems(orderItemsData);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("order_id", orderId);
            responseData.put("total_amount", finalTotal);
            responseData.put("order_items", orderItemsData.stream().map(arr -> Map.of(
                    "dish_id", arr[1],
                    "quantity", arr[2],
                    "price", arr[3],
                    "total_price", arr[4]
            )).toList());

            return ApiResponse.SUCCESS("Order processed successfully.", responseData);

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.failure("Failed to process order. " + e.getMessage());
        }
    }

    public ApiResponse<Map<String, Object>> getPendingOrderItems(int tableId) {
        try {
            List<Object[]> pendingOrders = orderRepository.getPendingOrderDetailByTableId(tableId);

            if (pendingOrders.isEmpty()) {
                Map<String, Object> result = new HashMap<>();
                result.put("message", "No pending orders found for this table.");
                result.put("order_items", Collections.emptyList());
                result.put("total_amount", 0);
                result.put("discount", 0);
                result.put("final_amount", 0);
                return ApiResponse.SUCCESS("No pending orders found.", result);
            }

            Object[] orderRow = pendingOrders.get(0);
            int orderId = ((Number) orderRow[0]).intValue();
            double totalAmount = ((Number) orderRow[1]).doubleValue();
            Timestamp createdAt = (Timestamp) orderRow[2];
            double finalAmount = ((Number) orderRow[3]).doubleValue();
            double discount = ((Number) orderRow[4]).doubleValue();

            String createdAtIST = TimeUtils.formatToIndia(createdAt);

            List<Object[]> orderItemsRows = orderRepository.getOrderItemsByOrderId(orderId);

            List<Map<String, Object>> orderItems = new ArrayList<>();
            for (Object[] row : orderItemsRows) {
                Map<String, Object> item = new HashMap<>();
                item.put("order_item_id", row[0]);
                item.put("dish_id", row[1]);
                item.put("dish_name", row[2]);
                item.put("quantity", row[3]);
                item.put("price", row[4]);
                item.put("total_price", row[5]);
                orderItems.add(item);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Pending order items fetched successfully.");
            response.put("order_items", orderItems);
            response.put("total_amount", totalAmount);
            response.put("created_at", createdAtIST);
            response.put("discount", discount);
            response.put("final_amount", finalAmount);

            return ApiResponse.SUCCESS("Pending order items retrieved successfully.", response);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.failure("Failed to fetch pending order items. " + e.getMessage());
        }
    }

    public ApiResponse<Map<String, Object>> addPaymentDetails(Map<String, Object> requestBody) {
        try {
            Object tableId = requestBody.get("table_id");
            Object discount = requestBody.get("discount");
            Object finalAmount = requestBody.get("final_amount");
            List<Map<String, Object>> paymentMethods = (List<Map<String, Object>>) requestBody.get("payment_methods");
            Map<String, Object> dueDetails = (Map<String, Object>) requestBody.get("due_details");

            // 1. Find pending order
            Object orderId = orderRepository.getPendingOrderByTableId((Integer) tableId);
            if (orderId == null) {
                return ApiResponse.failure("No pending order found for the specified table.");
            }

            // 2. Update orders table
            int updated = orderRepository.updateOrder(discount, finalAmount, orderId);
            if (updated == 0) {
                return ApiResponse.failure("Order not found or could not be updated.");
            }

            // 3. Handle payments
            List<Map<String, Object>> responsePayments = new ArrayList<>();

            for (Map<String, Object> payment : paymentMethods) {
                String method = (String) payment.get("method");
                Object amount = payment.get("amount");
                Object transactionId = payment.get("transaction_id");

                Map<String, Object> paymentMap = new HashMap<>();
                paymentMap.put("payment_method", method);
                paymentMap.put("amount_paid", amount);

                if ("due".equalsIgnoreCase(method)) {
                    if (dueDetails != null) {
                        String customerName = (String) dueDetails.get("customer_name");
                        String mobileNumber = (String) dueDetails.get("mobile_number");

                        orderRepository.insertDue(orderId, customerName, mobileNumber, finalAmount,
                                (Double.valueOf(finalAmount.toString()) - Double.valueOf(amount.toString())));
                    }
                    // ❌ No transaction_id for due
                } else {
                    orderRepository.insertPayment(orderId, method, amount, transactionId);
                    paymentMap.put("transaction_id", transactionId);
                }

                responsePayments.add(paymentMap);
            }

            // 4. Due response (✅ paid_amount should be null)
            Map<String, Object> responseDue = null;
            boolean hasDue = responsePayments.stream()
                    .anyMatch(pm -> "due".equalsIgnoreCase((String) pm.get("payment_method")));

            if (hasDue && dueDetails != null) {
                responseDue = new HashMap<>();
                responseDue.put("customer_name", dueDetails.get("customer_name"));
                responseDue.put("mobile_number", dueDetails.get("mobile_number"));
                responseDue.put("total_amount", Double.valueOf(finalAmount.toString()));
                responseDue.put("paid_amount", null); // ✅ as per expected response
            }

            // 5. Fetch updated order (fix indexes)
            List<Object[]> updatedOrders = orderRepository.getOrderById(orderId);
            Object[] updatedOrder = updatedOrders.get(0);

            Map<String, Object> response = new HashMap<>();
            response.put("order_id", updatedOrder[0]);
            response.put("discount", updatedOrder[6]);
            response.put("final_amount", updatedOrder[7]);
            response.put("order_status", updatedOrder[4]);
            response.put("payments", responsePayments);
            response.put("due_details", responseDue);

            return ApiResponse.SUCCESS("Payment processed successfully.", response);

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.failure("Failed to process payment. " + e.getMessage());
        }
    }


    public ApiResponse<Map<String, Object>> getOrderDetails(String startDate, String endDate) {
        try {
            // ✅ Default values
            if (startDate == null || endDate == null) {
                String today = TimeUtils.getCurrentTimeInIndia().substring(0, 10);
                startDate = today + " 00:00:00";
                endDate = today + " 23:59:59";
            }

            // ✅ Summary
            List<Object[]> summaryRows = orderRepository.getOrderSummary(startDate, endDate);
            Object[] s = summaryRows.get(0);
            Map<String, Object> summary = new LinkedHashMap<>();
            summary.put("total_cash", s[0] != null ? s[0].toString() : "0");
            summary.put("total_upi", s[1] != null ? s[1].toString() : "0");
            summary.put("total_card", s[2] != null ? s[2].toString() : "0");
            summary.put("total_collection", s[3] != null ? s[3].toString() : "0");
            summary.put("total_due", s[4] != null ? s[4].toString() : "0");

            // ✅ Payments grouped by order_id
            List<Object[]> paymentRows = orderRepository.getPayments(startDate, endDate);
            Map<Integer, List<Map<String, Object>>> paymentMap = paymentRows.stream().collect(
                    Collectors.groupingBy(
                            row -> ((Number) row[0]).intValue(),
                            Collectors.mapping(row -> {
                                Map<String, Object> map = new LinkedHashMap<>();
                                map.put("payment_method", row[1]);
                                map.put("amount_paid", row[2]);
                                map.put("transaction_id", row[3]);
                                return map;
                            }, Collectors.toList())
                    )
            );

            // ✅ Order items grouped by order_id
            List<Object[]> itemRows = orderRepository.getOrderItems(startDate, endDate);
            Map<Integer, List<Map<String, Object>>> itemMap = itemRows.stream().collect(
                    Collectors.groupingBy(
                            row -> ((Number) row[0]).intValue(),
                            Collectors.mapping(row -> {
                                Map<String, Object> map = new LinkedHashMap<>();
                                map.put("dish_name", row[1]);
                                map.put("quantity", row[2]);
                                map.put("price", row[3]);
                                map.put("total_price", row[4]);
                                return map;
                            }, Collectors.toList())
                    )
            );

            // ✅ Due details mapped by order_id
            List<Object[]> dueRows = orderRepository.getDueDetails(startDate, endDate);
            Map<Integer, Map<String, Object>> dueMap = dueRows.stream()
                    .filter(row -> row[1] != null)
                    .collect(Collectors.toMap(
                            row -> ((Number) row[0]).intValue(),
                            row -> {
                                Map<String, Object> map = new LinkedHashMap<>();
                                map.put("due_amount", row[1] != null ? row[1] : null);
                                map.put("customer_name", row[2] != null ? row[2] : null);
                                map.put("mobile_number", row[3] != null ? row[3] : null);
                                return map;
                            }
                    ));

            // ✅ Default due_details (always return keys with null values)
            Map<String, Object> defaultDueDetails = new LinkedHashMap<>();
            defaultDueDetails.put("due_amount", null);
            defaultDueDetails.put("customer_name", null);
            defaultDueDetails.put("mobile_number", null);

            // ✅ Orders
            List<Object[]> orderRows = orderRepository.getOrders(startDate, endDate);
            List<Map<String, Object>> orders = orderRows.stream().map(row -> {
                Map<String, Object> order = new LinkedHashMap<>();
                int orderId = ((Number) row[0]).intValue();
                order.put("order_id", orderId);
                order.put("table_id", row[1]);

                // 🔹 Convert amounts to String
                order.put("total_amount", row[2] != null ? String.format("%.2f", Double.parseDouble(row[2].toString())) : "0.00");
                order.put("discount", row[3]);
                order.put("final_amount", row[4] != null ? String.format("%.2f", Double.parseDouble(row[4].toString())) : "0.00");
                order.put("created_at", TimeUtils.formatToIndia((java.sql.Timestamp) row[5]));
                order.put("table_name", row[6]);
                order.put("paid_amount", row[7] != null ? row[7].toString() : "0");

                order.put("order_items", itemMap.getOrDefault(orderId, Collections.emptyList()));
                order.put("payments", paymentMap.getOrDefault(orderId, Collections.emptyList()));
                order.put("due_details", dueMap.getOrDefault(orderId, defaultDueDetails));
                return order;
            }).collect(Collectors.toList());

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("summary", summary);
            response.put("orders", orders);

            return ApiResponse.SUCCESS("Order details fetched successfully", response);

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.failure("Error fetching order details: " + e.getMessage());
        }
    }


    public ApiResponse<String> payDueAmount(String customerName, String mobileNumber,
                                            String paymentMethod, Double paymentAmount) {
        try {
            if ((customerName == null || customerName.isEmpty()) &&
                    (mobileNumber == null || mobileNumber.isEmpty())) {
                return ApiResponse.failure("Customer name or mobile number is required.");
            }

            if (paymentMethod == null || !(paymentMethod.equals("cash") || paymentMethod.equals("card") || paymentMethod.equals("upi"))) {
                return ApiResponse.failure("Invalid payment method. Choose from 'cash', 'card', or 'upi'.");
            }

            if (paymentAmount == null || paymentAmount <= 0) {
                return ApiResponse.failure("Payment amount must be greater than 0.");
            }

            // ✅ Fetch pending due
            List<Object[]> dueList = orderRepository.findPendingDue(customerName, mobileNumber);
            if (dueList.isEmpty()) {
                return ApiResponse.failure("No pending dues found for the given customer.");
            }

            Object[] dueEntry = dueList.get(0);
            int dueId = ((Number) dueEntry[0]).intValue();
            int orderId = ((Number) dueEntry[1]).intValue();
            double dueAmount = Double.parseDouble(dueEntry[9].toString()); // assuming column 3 is due_amount
            Double paidAmount = dueEntry[4] != null ? Double.parseDouble(dueEntry[4].toString()) : 0.0;

            if (paymentAmount > dueAmount) {
                return ApiResponse.failure("Payment amount cannot exceed the due amount.");
            }

            double remainingDue = dueAmount - paymentAmount;
            String currentTime = TimeUtils.getCurrentTimeInIndia();

            // ✅ Insert payment record
            String transactionId = paymentMethod.equals("upi") ? "UPI" + System.currentTimeMillis() : null;
            orderRepository.insertPayment(orderId, paymentMethod, paymentAmount, currentTime, transactionId);

            // ✅ Update dues
            if (remainingDue <= 0) {
                orderRepository.completeDue(dueId, currentTime);
            } else {
                orderRepository.updatePartialDue(dueId, paymentAmount, currentTime);
            }

            return ApiResponse.SUCCESS("Payment recorded successfully.", null);

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.failure("Failed to process payment: " + e.getMessage());
        }
    }

    public ApiResponse<DeleteOrderResponse> deletePendingOrders(Integer table_id){
        try {
            List<Object[]> pendingOrder = orderRepository.getPendingOrdersForDelete(table_id);
            if (pendingOrder.isEmpty()){
                return ApiResponse.failure("No pending order found for the given Table ID.");
            }
            int rowsAffected = orderRepository.DeletePendingOrder(table_id);
                return ApiResponse.SUCCESS(
                        "Pending order deleted successfully.",
                        new OrderService.DeleteOrderResponse(table_id));

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.failure("Failed to delete pending order. Please try again later. " + e.getMessage());
        }
    }

    public record DeleteOrderResponse(Integer table_id) {
    }
}
