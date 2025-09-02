package com.hotelix.hotel_management.expense.dto;

public class ExpenseAddRequest {
    private String name;
    private Integer vendor_id;
    private Integer price;
    private String payment_method;

    public ExpenseAddRequest(String name, Integer vendor_id, Integer price, String payment_method) {
        this.name = name;
        this.vendor_id = vendor_id;
        this.price = price;
        this.payment_method = payment_method;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(Integer vendor_id) {
        this.vendor_id = vendor_id;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }
}
