package com.pk.crm.service;

import com.github.pagehelper.PageInfo;
import com.pk.crm.dto.CustomerDTO;

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
     * @param page current page
     * @param size page size
     * @return List of customers
     */
    PageInfo<CustomerDTO> getAllCustomers(int page, int size);
    
    /**
     * Search customers by keyword
     * @param keyword Search keyword
     * @param page current page
     * @param size page size
     * @return List of matching customers
     */
    PageInfo<CustomerDTO> searchCustomers(String keyword, int page, int size);
}
