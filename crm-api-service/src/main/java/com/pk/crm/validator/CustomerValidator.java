package com.pk.crm.validator;

import com.pk.crm.dto.CustomerDTO;
import com.pk.crm.exception.ValidationException;
import org.springframework.util.StringUtils;

/**
 * 客户数据验证器
 */
public class CustomerValidator {

    public static void validateForCreate(CustomerDTO customerDTO) {
        if (customerDTO == null) {
            throw new ValidationException("客户信息不能为空");
        }
        
        validateBasicInfo(customerDTO);
    }

    public static void validateForUpdate(CustomerDTO customerDTO) {
        if (customerDTO == null) {
            throw new ValidationException("客户信息不能为空");
        }
        
        if (customerDTO.getId() == null) {
            throw new ValidationException("更新客户时ID不能为空");
        }
        
        validateBasicInfo(customerDTO);
    }

    private static void validateBasicInfo(CustomerDTO customerDTO) {
        if (!StringUtils.hasText(customerDTO.getName())) {
            throw new ValidationException("客户姓名不能为空");
        }
        
        if (customerDTO.getName().length() < 2 || customerDTO.getName().length() > 50) {
            throw new ValidationException("客户姓名长度必须在2-50个字符之间");
        }
        
        // 验证手机号（如果提供）
        if (StringUtils.hasText(customerDTO.getPhone())) {
            if (!customerDTO.getPhone().matches("^1[3-9]\\d{9}$")) {
                throw new ValidationException("手机号格式不正确");
            }
        }
        
        // 验证邮箱（如果提供）
        if (StringUtils.hasText(customerDTO.getEmail())) {
            if (!customerDTO.getEmail().matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
                throw new ValidationException("邮箱格式不正确");
            }
        }
        
        // 验证描述长度
        if (customerDTO.getDescription() != null && customerDTO.getDescription().length() > 500) {
            throw new ValidationException("描述信息不能超过500个字符");
        }
    }
}