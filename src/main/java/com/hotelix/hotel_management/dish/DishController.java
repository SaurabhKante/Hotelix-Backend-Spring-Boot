package com.hotelix.hotel_management.dish;

import com.hotelix.hotel_management.common.ApiResponse;
import com.hotelix.hotel_management.dish.dto.AddChildRequestBody;
import com.hotelix.hotel_management.dish.dto.AddParentRequestBody;
import com.hotelix.hotel_management.dish.dto.DishUpdateResponse;
import com.hotelix.hotel_management.dish.dto.UpdateChildRequestBody;
import com.hotelix.hotel_management.table.dto.InsertTableRequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class DishController {
    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping("/api/dishes/v1/get-parent-dishes")
    public ResponseEntity<ApiResponse<List<Map<String,Object>>>> getParentDishes(){
        ApiResponse<List<Map<String,Object>>> response = dishService.getAllParentDishes();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/dishes/v1/get-child-dishes/{parent_dish_id}")
    public ResponseEntity<ApiResponse<List<Map<String,Object>>>> getParentDishes(@PathVariable Integer parent_dish_id){
        ApiResponse<List<Map<String,Object>>> response = dishService.getChildDishesByParent(parent_dish_id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/dishes/v1/add-parent-dish")
    public ResponseEntity<ApiResponse<Object>> addParentDish(@RequestBody AddParentRequestBody request){
        ApiResponse<Object> response = dishService.addParentDish(request.getDish_name(),request.getDescription());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/dishes/v1/add-child-dish")
    public ResponseEntity<ApiResponse<Object>> addChildDish(@RequestBody AddChildRequestBody request){
        ApiResponse<Object> response = dishService.addChildDish(request.getDish_name(),request.getParent_dish_id(),request.getDescription(),request.getPrice());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/dishes/v1/delete-dish/{dish_id}")
    public ResponseEntity<ApiResponse<DishUpdateResponse>> deleteDish(@PathVariable Integer dish_id){
        ApiResponse<DishUpdateResponse> response = dishService.deleteDish(dish_id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/dishes/v1/update-dish/{child_dish_id}")
    public ResponseEntity<ApiResponse<Object>> updateChildDish(@PathVariable Integer child_dish_id, @RequestBody UpdateChildRequestBody request){
        ApiResponse<Object> response = dishService.updateChildDish(child_dish_id, request.getDish_name(), request.getDish_description(), request.getDish_price());
        return ResponseEntity.ok(response);

    }
}
