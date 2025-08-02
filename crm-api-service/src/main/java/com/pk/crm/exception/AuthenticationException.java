package com.pk.crm.exception;

/**
 * 认证异常
 */
public class AuthenticationException extends CrmException {
    
    public AuthenticationException(String message) {
        super("AUTHENTICATION_FAILED", message);
    }
}