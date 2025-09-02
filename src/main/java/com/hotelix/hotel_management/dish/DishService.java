package com.hotelix.hotel_management.dish;

import com.hotelix.hotel_management.common.ApiResponse;
import com.hotelix.hotel_management.dish.dto.DishUpdateResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DishService {
    private final DishRepository dishRepository;

    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    public ApiResponse <List<Map<String,Object>>> getAllParentDishes(){
        try {
            List<Object[]> results = dishRepository.getAllParentDishes();
            List<Map<String,Object>> parentDishes = results.stream().map(row -> {
                Object[] cols = row;
                Map<String, Object> map = new HashMap<>();
                map.put("dish_id", cols[0]);
                map.put("dish_name", cols[1]);
                map.put("parent_dish_id", cols[2]); // null allowed
                map.put("dish_type", cols[3]);
                map.put("description", cols[4]);
                map.put("price", cols[5]);
                map.put("is_active", cols[6]);
                map.put("created_at", cols[7]);
                map.put("updated_at", cols[8]);
                return map;
            }).collect(Collectors.toList());
            return ApiResponse.SUCCESS("Parent dishes retrieved successfully", parentDishes);
        } catch (Exception e) {
            e.printStackTrace(); // log full stack trace
            return ApiResponse.failure("Failed to retrieve parent dishes: " + e);
        }

    }




    public ApiResponse <List<Map<String,Object>>> getChildDishesByParent(Integer parent_dish_id){
        try {
            List<Object[]> results = dishRepository.getChildDishesByParent(parent_dish_id);
            List<Map<String,Object>> childDishes = results.stream().map(row -> {
                Object[] cols = row;
                Map<String, Object> map = new HashMap<>();
                map.put("dish_id", cols[0]);
                map.put("dish_name", cols[1]);
                map.put("parent_dish_id", cols[2]);
                map.put("dish_type", cols[3]);
                map.put("description", cols[4]);
                map.put("price", cols[5]);
                map.put("is_active", cols[6]);
                map.put("created_at", cols[7]);
                map.put("updated_at", cols[8]);
                return map;
            }).collect(Collectors.toList());
            return ApiResponse.SUCCESS("Child dishes for parent ID " + parent_dish_id + " retrieved successfully", childDishes);
        } catch (Exception e) {
            e.printStackTrace(); // log full stack trace
            return ApiResponse.failure("Failed to retrieve parent dishes: " + e);
        }

    }

    public ApiResponse<Object> addParentDish(String dish_name, String description){
        if (dish_name == null || dish_name.equals("")) {
            return ApiResponse.failure("Missing dish_name");
        }
        try {
            Object insertedId = dishRepository.addParentDish(dish_name, description);

            return ApiResponse.SUCCESS("Parent Dish added successfully",
                    new DishService.DishResponse((Long) insertedId));
        } catch (Exception e) {
            return ApiResponse.failure("An error occurred while adding the Parent Dish "+ e);
        }
    }

    public ApiResponse<Object> addChildDish(String dish_name, Integer parent_dish_id, String description, Integer price){
        if (dish_name == null || dish_name.equals("") || parent_dish_id == null || price==null) {
            return ApiResponse.failure("Dish name price and parent dish ID are required");
        }
        try {
            Object insertedId = dishRepository.addChildDish(dish_name,parent_dish_id, description,price);

            return ApiResponse.SUCCESS("Child Dish added successfully",
                    new DishService.DishResponse((Long) insertedId));
        } catch (Exception e) {
            return ApiResponse.failure("An error occurred while adding the Child Dish "+ e);
        }
    }

    public ApiResponse<DishUpdateResponse> deleteDish(Integer dish_id){
        if (dish_id == null) {
            return ApiResponse.failure("Dish Id is required");
        }
        try {
            int rowsAffected = dishRepository.deleteDish(dish_id);
            if (rowsAffected > 0) {
                return ApiResponse.SUCCESS(
                        "Dish deleted (set to inactive) successfully",
                        new DishUpdateResponse(dish_id));

            } else {
                return ApiResponse.failure("No dish found with id " + dish_id);
            } }catch (Exception e) {
            return ApiResponse.failure("Failed to delete dish "+ e);
        }
    }


    public ApiResponse<Object> updateChildDish(Integer child_dish_id,String dish_name,String dish_description,Integer dish_price){
        if (child_dish_id == null) {
            return ApiResponse.failure("Child dish ID is required");
        }

        if ((dish_name == null || dish_name.isEmpty()) &&
                (dish_description == null || dish_description.isEmpty()) &&
                dish_price == null) {
            return ApiResponse.failure("At least one field (dish_name, dish_description, dish_price) is required to update");
        }
        try {
              int rowsAffected = dishRepository.updateChildDish(child_dish_id,dish_name,dish_description,dish_price);
            if (rowsAffected > 0) {
                return ApiResponse.SUCCESS(
                        "Child dish updated successfully",
                        new DishUpdateResponse(child_dish_id));

            } else {
                return ApiResponse.failure("No dish found with id " + child_dish_id);
            }
        }catch (Exception e) {
            return ApiResponse.failure("Failed to update child dish"+ e.getMessage());
        }

    }

    public record DishResponse(Long insertedId) {
    }


}
