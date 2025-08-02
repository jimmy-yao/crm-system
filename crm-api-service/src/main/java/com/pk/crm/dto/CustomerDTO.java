package com.pk.crm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Customer Data Transfer Object
 */
@Data
@Accessors(chain = true)
public class CustomerDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    
    @NotBlank(message = "客户姓名不能为空")
    @Size(min = 2, max = 50, message = "客户姓名长度必须在2-50个字符之间")
    private String name;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Size(max = 500, message = "描述信息不能超过500个字符")
    private String description;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;
}
