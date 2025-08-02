package com.pk.crm.exception;

/**
 * 客户未找到异常
 */
public class CustomerNotFoundException extends CrmException {
    
    public CustomerNotFoundException(Long customerId) {
        super("CUSTOMER_NOT_FOUND", "Customer not found with id: " + customerId);
    }
}