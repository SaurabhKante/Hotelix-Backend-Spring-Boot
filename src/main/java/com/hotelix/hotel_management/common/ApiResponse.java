package com.hotelix.hotel_management.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "SUCCESS", "MESSAGE", "DATA" })
public class ApiResponse<T> {

    @JsonProperty("SUCCESS")
    private boolean success;

    @JsonProperty("MESSAGE")
    private String message;

    @JsonProperty("DATA")
    private T data;

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> SUCCESS(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> failure(String message) {
        return new ApiResponse<>(false, message, null);
    }

    // ✅ keep getters normal but Jackson will still use the annotated names
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
