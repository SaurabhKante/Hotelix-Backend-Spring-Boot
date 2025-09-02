package com.hotelix.hotel_management.dish.dto;

public class DishUpdateResponse {
    private Integer dish_id;

    public DishUpdateResponse(Integer dish_id) {
        this.dish_id = dish_id;
    }

    public Integer getDish_id() {
        return dish_id;
    }

    public void setDish_id(Integer dish_id) {
        this.dish_id = dish_id;
    }
}

