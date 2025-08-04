package com.example.demo.exception;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ErrorResponse {
	private LocalDateTime timestamp;
    private String message;
    private String path;

    public ErrorResponse(LocalDateTime timestamp, String message, String path) {
        this.timestamp = timestamp;
        this.message = message;
        this.path = path;
    }
}
