package com.pk.crm.customer.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pk.crm.customer.mapper.CustomerMapper;
import com.pk.crm.dto.CustomerDTO;
import com.pk.crm.exception.CustomerNotFoundException;
import com.pk.crm.exception.ValidationException;
import com.pk.crm.service.CustomerService;
import com.pk.crm.validator.CustomerValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@DubboService
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerMapper customerMapper;

    @Override
    @Transactional
    public CustomerDTO addCustomer(CustomerDTO customerDTO) {
        log.info("Adding new customer: {}", customerDTO.getName());

        // 数据验证
        CustomerValidator.validateForCreate(customerDTO);
        checkUniqueness(customerDTO);

        try {
            customerMapper.insert(customerDTO);
            log.info("Successfully added customer with ID: {}", customerDTO.getId());
            return customerDTO;
        } catch (DuplicateKeyException e) {
            log.error("Failed to add customer due to duplicate key: {}", customerDTO.getName(), e);
            throw new ValidationException("客户已存在，请检查电话或邮箱是否重复");
        } catch (DataAccessException e) {
            log.error("Failed to add customer due to data access exception: {}", customerDTO.getName(), e);
            throw new RuntimeException("添加客户失败，数据库操作异常", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("Updating customer with ID: {}", customerDTO.getId());

        // 数据验证
        CustomerValidator.validateForUpdate(customerDTO);
        checkUniqueness(customerDTO);

        try {
            int updatedRows = customerMapper.update(customerDTO);
            if (updatedRows == 0) {
                log.warn("No rows updated for customer ID: {}. Customer might not exist.", customerDTO.getId());
                throw new CustomerNotFoundException(customerDTO.getId());
            }

            log.info("Successfully updated customer with ID: {}", customerDTO.getId());
            return getCustomerById(customerDTO.getId());
        } catch (DuplicateKeyException e) {
            log.error("Failed to update customer due to duplicate key: {}", customerDTO.getId(), e);
            throw new ValidationException("更新失败，电话或邮箱已被其他客户使用");
        } catch (DataAccessException e) {
            log.error("Failed to update customer with ID: {}", customerDTO.getId(), e);
            throw new RuntimeException("更新客户失败，数据库操作异常", e);
        }
    }

    private void checkUniqueness(CustomerDTO customerDTO) {
        // 检查电话号码唯一性
        if (StringUtils.hasText(customerDTO.getPhone())) {
            CustomerDTO existingByPhone = customerMapper.findByPhone(customerDTO.getPhone());
            if (existingByPhone != null && !Objects.equals(existingByPhone.getId(), customerDTO.getId())) {
                throw new ValidationException("手机号 " + customerDTO.getPhone() + " 已被其他客户使用");
            }
        }

        // 检查邮箱唯一性
        if (StringUtils.hasText(customerDTO.getEmail())) {
            CustomerDTO existingByEmail = customerMapper.findByEmail(customerDTO.getEmail());
            if (existingByEmail != null && !Objects.equals(existingByEmail.getId(), customerDTO.getId())) {
                throw new ValidationException("邮箱 " + customerDTO.getEmail() + " 已被其他客户使用");
            }
        }
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        log.info("Deleting customer with ID: {}", id);

        if (id == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }

        try {
            int deletedRows = customerMapper.delete(id);
            if (deletedRows == 0) {
                log.warn("No rows deleted for customer ID: {}. Customer might not exist.", id);
                throw new CustomerNotFoundException(id);
            }
            log.info("Successfully deleted customer with ID: {}", id);
        } catch (DataAccessException e) {
            log.error("Failed to delete customer with ID: {}", id, e);
            throw new RuntimeException("删除客户失败，数据库操作异常", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageInfo<CustomerDTO> getAllCustomers(int page, int size) {
        log.debug("Getting all customers for page: {}, size: {}", page, size);
        try {
            return PageHelper.startPage(page, size).doSelectPageInfo(customerMapper::findAll);
        } catch (DataAccessException e) {
            log.error("Failed to get all customers", e);
            throw new RuntimeException("获取客户列表失败，数据库操作异常", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageInfo<CustomerDTO> searchCustomers(String keyword, int page, int size) {
        log.debug("Searching customers with keyword: {}, page: {}, size: {}", keyword, page, size);

        if (!StringUtils.hasText(keyword)) {
            return getAllCustomers(page, size);
        }

        try {
            return PageHelper.startPage(page, size).doSelectPageInfo(() -> customerMapper.searchCustomers(keyword.trim()));
        } catch (DataAccessException e) {
            log.error("Failed to search customers with keyword: {}", keyword, e);
            throw new RuntimeException("搜索客户失败，数据库操作异常", e);
        }
    }
}
