package com.hotelix.hotel_management.common;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    private static final ZoneId INDIA_ZONE = ZoneId.of("Asia/Kolkata");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Get current time in IST
    public static String getCurrentTimeInIndia() {
        LocalDateTime now = LocalDateTime.now(INDIA_ZONE);
        return now.format(FORMATTER);
    }

    // Convert a Timestamp to IST formatted string
    public static String formatToIndia(Timestamp timestamp) {
        if (timestamp == null) return null;
        LocalDateTime localDateTime = timestamp.toInstant().atZone(INDIA_ZONE).toLocalDateTime();
        return localDateTime.format(FORMATTER);
    }
}
