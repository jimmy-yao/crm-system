package com.pk.crm.exception;

/**
 * 授权异常
 */
public class AuthorizationException extends CrmException {
    
    public AuthorizationException(String message) {
        super("AUTHORIZATION_FAILED", message);
    }
}