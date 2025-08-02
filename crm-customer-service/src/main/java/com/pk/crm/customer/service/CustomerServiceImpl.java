package com.pk.crm.customer.service;

import com.pk.crm.customer.mapper.CustomerMapper;
import com.pk.crm.dto.CustomerDTO;
import com.pk.crm.exception.CustomerNotFoundException;
import com.pk.crm.service.CustomerService;
import com.pk.crm.validator.CustomerValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

@DubboService
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerMapper customerMapper;

    @Override
    public CustomerDTO addCustomer(CustomerDTO customerDTO) {
        log.info("Adding new customer: {}", customerDTO.getName());
        
        // 数据验证
        CustomerValidator.validateForCreate(customerDTO);
        
        try {
            customerMapper.insert(customerDTO);
            log.info("Successfully added customer with ID: {}", customerDTO.getId());
            return customerDTO;
        } catch (Exception e) {
            log.error("Failed to add customer: {}", customerDTO.getName(), e);
            throw new RuntimeException("Failed to add customer", e);
        }
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {
        log.debug("Getting customer by ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        
        CustomerDTO customer = customerMapper.findById(id);
        if (customer == null) {
            log.warn("Customer not found with ID: {}", id);
            throw new CustomerNotFoundException(id);
        }
        
        log.debug("Found customer: {}", customer.getName());
        return customer;
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("Updating customer with ID: {}", customerDTO.getId());
        
        // 数据验证
        CustomerValidator.validateForUpdate(customerDTO);
        
        // 检查客户是否存在
        CustomerDTO existingCustomer = customerMapper.findById(customerDTO.getId());
        if (existingCustomer == null) {
            throw new CustomerNotFoundException(customerDTO.getId());
        }
        
        try {
            int updatedRows = customerMapper.update(customerDTO);
            if (updatedRows == 0) {
                log.warn("No rows updated for customer ID: {}", customerDTO.getId());
                throw new CustomerNotFoundException(customerDTO.getId());
            }
            
            log.info("Successfully updated customer with ID: {}", customerDTO.getId());
            return customerDTO;
        } catch (Exception e) {
            log.error("Failed to update customer with ID: {}", customerDTO.getId(), e);
            throw new RuntimeException("Failed to update customer", e);
        }
    }

    @Override
    public void deleteCustomer(Long id) {
        log.info("Deleting customer with ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        
        // 检查客户是否存在
        CustomerDTO existingCustomer = customerMapper.findById(id);
        if (existingCustomer == null) {
            throw new CustomerNotFoundException(id);
        }
        
        try {
            int deletedRows = customerMapper.delete(id);
            if (deletedRows == 0) {
                log.warn("No rows deleted for customer ID: {}", id);
                throw new CustomerNotFoundException(id);
            }
            
            log.info("Successfully deleted customer with ID: {}", id);
        } catch (Exception e) {
            log.error("Failed to delete customer with ID: {}", id, e);
            throw new RuntimeException("Failed to delete customer", e);
        }
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        log.debug("Getting all customers");
        
        try {
            List<CustomerDTO> customers = customerMapper.findAll();
            log.info("Found {} customers", customers.size());
            return customers;
        } catch (Exception e) {
            log.error("Failed to get all customers", e);
            throw new RuntimeException("Failed to get customers", e);
        }
    }
    
    @Override
    public List<CustomerDTO> searchCustomers(String keyword) {
        log.debug("Searching customers with keyword: {}", keyword);
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCustomers();
        }
        
        try {
            List<CustomerDTO> customers = customerMapper.searchCustomers(keyword.trim());
            log.info("Found {} customers matching keyword: {}", customers.size(), keyword);
            return customers;
        } catch (Exception e) {
            log.error("Failed to search customers with keyword: {}", keyword, e);
            throw new RuntimeException("Failed to search customers", e);
        }
    }
}