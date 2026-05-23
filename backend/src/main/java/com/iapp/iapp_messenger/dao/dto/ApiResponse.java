package com.iapp.iapp_messenger.dao.dto;

/// Универсальная обёртка ответа backend API.
public class ApiResponse<T> {

    private T data;
    private String error;
    private Boolean success;

    /// Пустой конструктор нужен для сериализации и десериализации JSON.
    public ApiResponse() {}

    /// Создаёт ответ с данными, ошибкой и признаком успешности.
    public ApiResponse(T data, String error, Boolean success) {
        this.data = data;
        this.error = error;
        this.success = success;
    }

    /// Создаёт успешный ответ.
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(data, null, true);
    }

    /// Создаёт ответ с ошибкой.
    public static <T> ApiResponse<T> fail(String error) {
        return new ApiResponse<>(null, error, false);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}