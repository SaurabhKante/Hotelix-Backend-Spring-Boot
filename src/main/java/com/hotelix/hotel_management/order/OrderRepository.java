package com.hotelix.hotel_management.order;

import com.hotelix.hotel_management.common.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderRepository {
    private final BaseRepository baseRepository;

    public OrderRepository(BaseRepository baseRepository) {
        this.baseRepository = baseRepository;
    }

    public List<Object[]> getPendingDues() {
        String sql = "SELECT customer_name, mobile_number, due_amount, created_at \n" +
                "        FROM dues_master \n" +
                "        WHERE due_status = 'pending'\n" +
                "        ORDER BY created_at DESC";
        return baseRepository.executeQuery(sql);
    }

    public List<Object[]> getAllPendingOrders() {
        String sql = "SELECT table_id, total_amount \n" +
                "        FROM orders \n" +
                "        WHERE order_status = 'pending'";
        return baseRepository.executeQuery(sql);
    }


    public List<Object[]> findPendingOrderByTable(Integer tableId) {
        String sql = """
            SELECT order_id, total_amount
            FROM orders
            WHERE table_id = ? AND order_status = 'pending'
        """;
        return baseRepository.executeQuery(sql, tableId);
    }

    public Object createOrder(Integer tableId, Double totalAmount, String createdAt) {
        String sql = """
            INSERT INTO orders (table_id, total_amount, created_at, final_amount)
            VALUES (?, ?, ?, ?)
        """;
        return baseRepository.executeInsert(sql, tableId, totalAmount, createdAt, totalAmount);
    }

    public int updateOrderTotal(Integer orderId, Double updatedAmount) {
        String sql = "UPDATE orders SET total_amount = ? WHERE order_id = ?";
        return baseRepository.executeUpdate(sql, updatedAmount, orderId);
    }

    public void addOrderItems(List<Object[]> orderItemsData) {
        for (Object[] row : orderItemsData) {
            String sql = """
                INSERT INTO order_items (order_id, dish_id, quantity, price, total_price)
                VALUES (?, ?, ?, ?, ?)
            """;
            baseRepository.executeInsert(sql, row);
        }
    }

    public List<Object[]> getPendingOrderDetailByTableId(int tableId) {
        String sql = "SELECT o.order_id, o.total_amount, o.created_at, o.final_amount, o.discount " +
                "FROM orders o " +
                "WHERE o.table_id = ? AND o.order_status = 'pending'";
        return baseRepository.executeQuery(sql, tableId);
    }

    public List<Object[]> getOrderItemsByOrderId(int orderId) {
        String sql = "SELECT " +
                "oi.order_item_id, oi.dish_id, d.dish_name, oi.quantity, oi.price, oi.total_price " +
                "FROM order_items oi " +
                "JOIN dish_master d ON oi.dish_id = d.dish_id " +
                "WHERE oi.order_id = ?";
        return baseRepository.executeQuery(sql, orderId);
    }

    public Integer getPendingOrderByTableId(int tableId) {
        String sql = "SELECT order_id FROM orders WHERE table_id = ? AND order_status = 'pending'";
        List<Object> results = baseRepository.executeQuerySingleColumn(sql, tableId);

        if (results.isEmpty()) {
            return null;
        }

        return ((Number) results.get(0)).intValue(); // directly cast single column value
    }




    public int updateOrder(Object discount, Object finalAmount, Object orderId) {
        String sql = "UPDATE orders SET order_status = 'completed', discount = ?, final_amount = ? WHERE order_id = ?";
        return baseRepository.executeUpdate(sql, discount, finalAmount, orderId);
    }

    public void insertPayment(Object orderId, String method, Object amount, Object transactionId) {
        String sql = "INSERT INTO payments (order_id, payment_method, amount_paid, transaction_id) VALUES (?, ?, ?, ?)";
        baseRepository.executeInsert(sql, orderId, method, amount, transactionId);
    }

    public void insertDue(Object orderId, String customerName, String mobileNumber, Object totalAmount, Object paidAmount) {
        String sql = "INSERT INTO dues_master (order_id, customer_name, mobile_number, total_amount, paid_amount) VALUES (?, ?, ?, ?, ?)";
        baseRepository.executeInsert(sql, orderId, customerName, mobileNumber, totalAmount, paidAmount);
    }

    public List<Object[]> getOrderById(Object orderId) {
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        return baseRepository.executeQuery(sql, orderId);
    }


    public List<Object[]> getOrderSummary(String startDate, String endDate) {
        String sql = """
            SELECT 
              SUM(CASE WHEN p.payment_method = 'cash' THEN p.amount_paid ELSE 0 END) AS total_cash,
              SUM(CASE WHEN p.payment_method = 'upi' THEN p.amount_paid ELSE 0 END) AS total_upi,
              SUM(CASE WHEN p.payment_method = 'card' THEN p.amount_paid ELSE 0 END) AS total_card,
              SUM(p.amount_paid) AS total_collection,
              (
                SELECT SUM(due_amount)
                FROM (
                  SELECT DISTINCT d.order_id, d.due_amount
                  FROM dues_master d
                  JOIN orders o ON o.order_id = d.order_id
                  WHERE o.created_at BETWEEN ? AND ?
                ) AS distinct_due_amounts
              ) AS total_due
            FROM orders o
            LEFT JOIN payments p ON o.order_id = p.order_id
            WHERE o.created_at BETWEEN ? AND ?
        """;
        return baseRepository.executeQuery(sql, startDate, endDate, startDate, endDate);
    }

    // ✅ Orders Query
    public List<Object[]> getOrders(String startDate, String endDate) {
        String sql = """
            SELECT 
              o.order_id,
              o.table_id,
              o.total_amount,
              o.discount,
              o.final_amount,
              o.created_at,
              t.table_name,
              (
                  SELECT SUM(p_sub.amount_paid)
                  FROM payments p_sub
                  WHERE p_sub.order_id = o.order_id
              ) AS paid_amount
            FROM orders o
            LEFT JOIN table_master t ON o.table_id = t.table_id
            WHERE o.created_at BETWEEN ? AND ?
            GROUP BY o.order_id
        """;
        return baseRepository.executeQuery(sql, startDate, endDate);
    }

    // ✅ Payments per order
    public List<Object[]> getPayments(String startDate, String endDate) {
        String sql = """
            SELECT 
              o.order_id,
              p.payment_method,
              p.amount_paid,
              p.transaction_id
            FROM orders o
            LEFT JOIN payments p ON o.order_id = p.order_id
            WHERE o.created_at BETWEEN ? AND ?
        """;
        return baseRepository.executeQuery(sql, startDate, endDate);
    }

    // ✅ Order Items per order
    public List<Object[]> getOrderItems(String startDate, String endDate) {
        String sql = """
            SELECT 
              o.order_id,
              dm.dish_name,
              oi.quantity,
              oi.price,
              oi.total_price
            FROM orders o
            LEFT JOIN order_items oi ON o.order_id = oi.order_id
            LEFT JOIN dish_master dm ON oi.dish_id = dm.dish_id
            WHERE o.created_at BETWEEN ? AND ?
        """;
        return baseRepository.executeQuery(sql, startDate, endDate);
    }

    // ✅ Due details per order
    public List<Object[]> getDueDetails(String startDate, String endDate) {
        String sql = """
            SELECT 
              o.order_id,
              d.due_amount,
              d.customer_name,
              d.mobile_number
            FROM orders o
            LEFT JOIN dues_master d ON o.order_id = d.order_id
            WHERE o.created_at BETWEEN ? AND ?
        """;
        return baseRepository.executeQuery(sql, startDate, endDate);
    }

    public List<Object[]> findPendingDue(String customerName, String mobileNumber) {
        String sql = "SELECT * FROM dues_master " +
                "WHERE (customer_name = ? OR mobile_number = ?) AND due_status = 'pending' " +
                "ORDER BY updated_at LIMIT 1";
        return baseRepository.executeQuery(sql, customerName, mobileNumber);
    }

    public Object insertPayment(int orderId, String paymentMethod, double amountPaid, String paymentDate, String transactionId) {
        String sql = "INSERT INTO payments (order_id, payment_method, amount_paid, payment_date, transaction_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        return baseRepository.executeInsert(sql, orderId, paymentMethod, amountPaid, paymentDate, transactionId);
    }

    public int completeDue(int dueId, String updatedAt) {
        String sql = "UPDATE dues_master " +
                "SET paid_amount = total_amount, due_status = 'completed', updated_at = ? " +
                "WHERE due_id = ?";
        return baseRepository.executeUpdate(sql, updatedAt, dueId);
    }

    public int updatePartialDue(int dueId, double paymentAmount, String updatedAt) {
        String sql = "UPDATE dues_master " +
                "SET paid_amount = paid_amount + ?, updated_at = ? " +
                "WHERE due_id = ?";
        return baseRepository.executeUpdate(sql, paymentAmount, updatedAt, dueId);
    }

    public List<Object[]> getPendingOrdersForDelete(Integer table_id){
        String sql = "SELECT * FROM orders \n" +
                "        WHERE table_id = ? AND order_status = 'pending'";
        return baseRepository.executeQuery(sql,table_id);
    }

    public int DeletePendingOrder(Integer table_id){
        String sql ="DELETE FROM orders \n" +
                "        WHERE table_id = ? AND order_status = 'pending'";
        return baseRepository.executeUpdate(sql,table_id);
    }
}
