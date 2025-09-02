package com.hotelix.hotel_management.dish.dto;

public class AddParentRequestBody {
    private  String dish_name;
    private  String description;

    public String getDish_name() {
        return dish_name;
    }

    public void setDish_name(String dish_name) {
        this.dish_name = dish_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
