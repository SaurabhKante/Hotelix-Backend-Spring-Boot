package com.hotelix.hotel_management.table;

import com.hotelix.hotel_management.common.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TableRepository {
    private final BaseRepository baseRepository;

    public TableRepository(BaseRepository baseRepository) {
        this.baseRepository = baseRepository;
    }

    public List<Object[]> getAllActiveTables(){
        String sql = "SELECT * FROM table_master WHERE is_active = TRUE";
        return baseRepository.executeQuery(sql);
    }

    public Object insertTable(String table_name, int seat_capacity) {
        String sql = "INSERT INTO table_master (table_name, seat_capacity) VALUES (?, ?)";
        return baseRepository.executeInsert(sql, table_name, seat_capacity);
    }
}
