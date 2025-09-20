package com.pk.crm.validator;

import com.pk.crm.dto.CustomerDTO;
import com.pk.crm.exception.ValidationException;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * 客户数据验证器
 */
public class CustomerValidator {

    private static final int NAME_MIN_LENGTH = 2;
    private static final int NAME_MAX_LENGTH = 50;
    private static final int DESCRIPTION_MAX_LENGTH = 500;

    /**
     * 中国大陆手机号格式校验，覆盖了三大运营商、广电及虚拟运营商的已知号段。
     */
    private static final Pattern CHINA_MOBILE_PHONE_PATTERN = Pattern.compile("^1(3\\d|4[579]|5[0-35-9]|6[2567]|7[0-35-8]|8\\d|9[0-35-9])\\d{8}$");

    /**
     * 通用邮箱格式校验。
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");

    /**
     * 工具类不应被实例化，故将构造函数私有化。
     */
    private CustomerValidator() {
    }

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
        
        if (customerDTO.getName().length() < NAME_MIN_LENGTH || customerDTO.getName().length() > NAME_MAX_LENGTH) {
            throw new ValidationException("客户姓名长度必须在 " + NAME_MIN_LENGTH + "-" + NAME_MAX_LENGTH + " 个字符之间");
        }
        
        // 验证手机号（如果提供）
        if (StringUtils.hasText(customerDTO.getPhone())) {
            if (!CHINA_MOBILE_PHONE_PATTERN.matcher(customerDTO.getPhone()).matches()) {
                throw new ValidationException("手机号格式不正确");
            }
        }
        
        // 验证邮箱（如果提供）
        if (StringUtils.hasText(customerDTO.getEmail())) {
            if (!EMAIL_PATTERN.matcher(customerDTO.getEmail()).matches()) {
                throw new ValidationException("邮箱格式不正确");
            }
        }
        
        // 验证描述长度
        if (customerDTO.getDescription() != null && customerDTO.getDescription().length() > DESCRIPTION_MAX_LENGTH) {
            throw new ValidationException("描述信息不能超过 " + DESCRIPTION_MAX_LENGTH + " 个字符");
        }
    }
}