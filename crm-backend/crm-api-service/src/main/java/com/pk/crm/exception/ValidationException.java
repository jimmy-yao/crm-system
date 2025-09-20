package com.pk.crm.exception;

/**
 * 数据验证异常
 */
public class ValidationException extends CrmException {
    
    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
    }
}