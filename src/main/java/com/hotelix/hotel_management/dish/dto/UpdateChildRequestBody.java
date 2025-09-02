package com.hotelix.hotel_management.dish.dto;

public class UpdateChildRequestBody {
    private String dish_name;
    private String dish_description;
    private Integer dish_price;

    public UpdateChildRequestBody(String dish_name, String dish_description, Integer dish_price) {
        this.dish_name = dish_name;
        this.dish_description = dish_description;
        this.dish_price = dish_price;
    }

    public String getDish_name() {
        return dish_name;
    }

    public void setDish_name(String dish_name) {
        this.dish_name = dish_name;
    }

    public String getDish_description() {
        return dish_description;
    }

    public void setDish_description(String dish_description) {
        this.dish_description = dish_description;
    }

    public Integer getDish_price() {
        return dish_price;
    }

    public void setDish_price(Integer dish_price) {
        this.dish_price = dish_price;
    }
}
