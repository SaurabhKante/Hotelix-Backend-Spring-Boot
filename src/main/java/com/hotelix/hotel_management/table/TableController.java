package com.hotelix.hotel_management.table;

import com.hotelix.hotel_management.common.ApiResponse;
import com.hotelix.hotel_management.table.dto.InsertTableRequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class TableController {
    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping("/api/tables/v1/get-all-tables")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllTables(){
        ApiResponse<List<Map<String, Object>>> response = tableService.getAllTables();
        return ResponseEntity.ok(response);
    }


    @PostMapping("/api/tables/v1/add")
    public ResponseEntity <ApiResponse<Object>> addTable(@RequestBody InsertTableRequestBody request){
        ApiResponse<Object> response=tableService.addTable(request.getTable_name(),request.getSeat_capacity());
        return ResponseEntity.ok(response);
    }
}
