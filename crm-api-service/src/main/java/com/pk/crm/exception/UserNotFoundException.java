package com.pk.crm.exception;

/**
 * 用户未找到异常
 */
public class UserNotFoundException extends CrmException {
    
    public UserNotFoundException(Long userId) {
        super("USER_NOT_FOUND", "User not found with id: " + userId);
    }
    
    public UserNotFoundException(String username) {
        super("USER_NOT_FOUND", "User not found with username: " + username);
    }
}