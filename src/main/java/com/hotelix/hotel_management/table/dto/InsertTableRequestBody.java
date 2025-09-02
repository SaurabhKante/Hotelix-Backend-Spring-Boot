package com.hotelix.hotel_management.table.dto;

public class InsertTableRequestBody {
    private String table_name;
    private Integer seat_capacity;

    // Getters & Setters
    public String getTable_name() { return table_name; }
    public void setTable_name(String table_name) { this.table_name = table_name; }

    public Integer getSeat_capacity() { return seat_capacity; }
    public void setSeat_capacity(Integer seat_capacity) { this.seat_capacity = seat_capacity; }
}