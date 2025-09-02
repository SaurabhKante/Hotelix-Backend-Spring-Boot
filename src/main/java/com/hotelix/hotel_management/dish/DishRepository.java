package com.hotelix.hotel_management.dish;

import com.hotelix.hotel_management.common.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class DishRepository {
    private final BaseRepository baseRepository;

    public DishRepository(BaseRepository baseRepository) {
        this.baseRepository = baseRepository;
    }


    public List<Object[]> getAllParentDishes(){
        String sql = "SELECT * FROM dish_master\n" +
                "        WHERE dish_type = 'Parent' AND is_active = TRUE";
        return baseRepository.executeQuery(sql);
    }

    public List<Object[]> getChildDishesByParent(Integer parent_dish_id){
        String sql = "SELECT * FROM dish_master\n" +
                "        WHERE dish_type = 'Child' AND parent_dish_id = ? AND is_active = 1";
        return baseRepository.executeQuery(sql,parent_dish_id);
    }

    public Object addParentDish(String dish_name, String description) {
        String sql = "INSERT INTO dish_master (dish_name, dish_type, description)\n" +
                "      VALUES (?, 'Parent', ?)";
        return baseRepository.executeInsert(sql, dish_name, description);
    }

    public Object addChildDish(String dish_name, Integer parent_dish_id, String description, Integer price) {
        String sql = "INSERT INTO dish_master (dish_name, dish_type, parent_dish_id, description, price)\n" +
                "      VALUES (?, 'Child', ?, ?, ?)";
        return baseRepository.executeInsert(sql, dish_name, parent_dish_id,description,price);
    }

    public int deleteDish(Integer dish_id){
        String sql = """
                UPDATE dish_master
                        SET is_active = 0
                        WHERE dish_id = ?""";
        return baseRepository.executeUpdate(sql,dish_id);
    }


    public int updateChildDish(Integer child_dish_id,String dish_name,String dish_description,Integer dish_price){
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("UPDATE dish_master SET ");

        List<String> updates = new ArrayList<>();

        if (dish_name != null && !dish_name.isEmpty()) {
            updates.add("dish_name = ?");
            params.add(dish_name);
        }

        if (dish_description != null && !dish_description.isEmpty()) {
            updates.add("description = ?");
            params.add(dish_description);
        }

        if (dish_price != null) {
            updates.add("price = ?");
            params.add(dish_price);
        }

        // if no updates requested
        if (updates.isEmpty()) {
            return 0;
        }

        sql.append(String.join(", ", updates));
        sql.append(" WHERE dish_id = ? AND dish_type = 'Child' AND is_active = 1");

        params.add(child_dish_id);


        return baseRepository.executeUpdate(sql.toString(),params.toArray());
    }

}
