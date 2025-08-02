package com.pk.crm.exception;

import lombok.Getter;

/**
 * CRM业务异常基类
 */
@Getter
public class CrmException extends RuntimeException {
    
    private final String errorCode;
    private final String errorMessage;
    
    public CrmException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    
    public CrmException(String errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}