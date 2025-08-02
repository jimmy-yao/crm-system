package com.pk.crm.service;

import com.pk.crm.dto.CustomerDTO;

import java.util.List;

/**
 * Customer Service Interface
 */
public interface CustomerService {

    /**
     * Add a new customer
     * @param customerDTO Customer data
     * @return The new customer with ID
     */
    CustomerDTO addCustomer(CustomerDTO customerDTO);

    /**
     * Get customer by ID
     * @param id Customer ID
     * @return Customer data
     */
    CustomerDTO getCustomerById(Long id);

    /**
     * Update customer information
     * @param customerDTO Customer data
     * @return The updated customer
     */
    CustomerDTO updateCustomer(CustomerDTO customerDTO);

    /**
     * Delete a customer
     * @param id Customer ID
     */
    void deleteCustomer(Long id);

    /**
     * Get all customers
     * @return List of customers
     */
    List<CustomerDTO> getAllCustomers();
    
    /**
     * Search customers by keyword
     * @param keyword Search keyword
     * @return List of matching customers
     */
    List<CustomerDTO> searchCustomers(String keyword);
}
