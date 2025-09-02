package com.hotelix.hotel_management.expense;

import com.hotelix.hotel_management.common.BaseRepository;
import com.hotelix.hotel_management.common.TimeUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExpenseRepository {
    private BaseRepository baseRepository;

    public ExpenseRepository(BaseRepository baseRepository) {
        this.baseRepository = baseRepository;
    }

    public List<Object[]> getAllVendors(){
        String sql="SELECT vendor_id, vendor_name FROM Vendors ORDER BY vendor_name";
        return baseRepository.executeQuery(sql);
    }

    public List<Object[]> getObjectsByDateRange(String startDate, String endDate) {
        String sql = """
            SELECT o.object_name, o.vendor_id, o.price, o.payment_method, o.created_at, v.vendor_name
            FROM objects o
            LEFT JOIN vendors v ON o.vendor_id = v.vendor_id
            WHERE DATE(o.created_at) BETWEEN ? AND ?
            ORDER BY o.created_at ASC
            """;
        return baseRepository.executeQuery(sql, startDate, endDate);
    }


        public Object addObject(String name, Integer vendor_id, Integer price, String payment_method){
            String currentTimeInIndia = TimeUtils.getCurrentTimeInIndia();
         String sql="INSERT INTO Objects (object_name, vendor_id, price, payment_method, created_at)\n" +
                 "        VALUES (?, ?, ?, ?, ?)";
         return baseRepository.executeInsert(sql,name,vendor_id,price,payment_method,currentTimeInIndia);
        }
}
