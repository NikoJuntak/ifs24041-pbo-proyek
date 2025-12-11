package org.delcom.app.configs;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private int status;
    private String message;
    private T data;

    // Constructor Kosong (Penting untuk framework JSON/Jackson)
    public ApiResponse() {
    }

    // Constructor dengan Parameter
    public ApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // Constructor Helper (Jika tanpa data)
    public ApiResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    // --- Getter & Setter ---
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}