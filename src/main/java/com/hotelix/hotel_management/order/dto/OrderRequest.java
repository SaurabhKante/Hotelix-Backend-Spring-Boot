package com.hotelix.hotel_management.order.dto;

import java.util.List;

public class OrderRequest {
    private Integer table_id;
    private List<OrderItemRequest> order_items;

    // getters and setters

    public Integer getTable_id() {
        return table_id;
    }

    public void setTable_id(Integer table_id) {
        this.table_id = table_id;
    }

    public List<OrderItemRequest> getOrder_items() {
        return order_items;
    }

    public void setOrder_items(List<OrderItemRequest> order_items) {
        this.order_items = order_items;
    }
}

