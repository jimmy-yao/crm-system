package com.pk.crm.gateway.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 错误响应DTO
 */
@Data
@Accessors(chain = true)
public class ErrorResponse {
    
    private String errorCode;
    private String errorMessage;
    private String path;
    private LocalDateTime timestamp;
    
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }
}