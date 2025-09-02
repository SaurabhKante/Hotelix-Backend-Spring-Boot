package com.hotelix.hotel_management.dish.dto;

public class AddChildRequestBody {
    private String dish_name;
    private Integer parent_dish_id;
    private String description;
    private Integer price;

    public AddChildRequestBody(String dish_name, Integer parent_dish_id, String description, Integer price) {
        this.dish_name = dish_name;
        this.parent_dish_id = parent_dish_id;
        this.description = description;
        this.price = price;
    }

    public String getDish_name() {
        return dish_name;
    }

    public void setDish_name(String dish_name) {
        this.dish_name = dish_name;
    }

    public Integer getParent_dish_id() {
        return parent_dish_id;
    }

    public void setParent_dish_id(Integer parent_dish_id) {
        this.parent_dish_id = parent_dish_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
