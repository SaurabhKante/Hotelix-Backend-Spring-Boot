package com.hotelix.hotel_management.table;

import com.hotelix.hotel_management.common.ApiResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TableService {
    private final TableRepository tableRepository;

    public TableService(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }
    public ApiResponse <List<Map<String, Object>>> getAllTables(){
        try {
                List<Object[]> results = tableRepository.getAllActiveTables();
            List<Map<String, Object>> tables = results.stream().map(row -> {
                Object[] cols = row;
                return Map.of(
                        "table_id", cols[0],
                        "table_name", cols[1],
                        "seat_capacity", cols[2],
                        "is_active", cols[3],
                        "created_at",cols[4],
                        "updated_at",cols[5]
                );
            }).collect(Collectors.toList());
            return ApiResponse.SUCCESS("Tables retrieved successfully", tables);
        }catch (Exception e) {
            return ApiResponse.failure("An error occurred while retrieving the tables " + e);
        }

    }

    public ApiResponse<Object> addTable(String table_name, Integer seat_capacity) {
        if (table_name == null || seat_capacity == null) {
            return ApiResponse.failure("Missing table_name or seat_capacity");
        }

        try {
            Object insertedId = tableRepository.insertTable(table_name, seat_capacity);

            return ApiResponse.SUCCESS("Table added successfully",
                    new TableResponse((Long) insertedId));
        } catch (Exception e) {
            return ApiResponse.failure("An error occurred while adding the table "+ e);
        }
    }

    // inner response DTO for returning table_id
    public record TableResponse(Long table_id) {}


}

