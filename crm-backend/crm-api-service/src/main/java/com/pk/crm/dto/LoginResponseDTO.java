package com.pk.crm.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 登录响应DTO
 */
@Data
@Accessors(chain = true)
public class LoginResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String token;
    private UserDTO userInfo;
    private List<String> permissions;
    private List<String> roles;
}