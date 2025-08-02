package com.pk.crm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 权限数据传输对象
 */
@Data
@Accessors(chain = true)
public class PermissionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    
    @NotBlank(message = "权限名称不能为空")
    @Size(min = 2, max = 50, message = "权限名称长度必须在2-50个字符之间")
    private String permissionName;
    
    @NotBlank(message = "权限编码不能为空")
    @Size(min = 2, max = 100, message = "权限编码长度必须在2-100个字符之间")
    private String permissionCode;
    
    private String permissionType; // MENU-菜单 BUTTON-按钮 API-接口
    
    private String resourceUrl; // 资源URL
    
    private Long parentId; // 父权限ID
    
    private Integer sortOrder; // 排序
    
    @Size(max = 200, message = "权限描述不能超过200个字符")
    private String description;
    
    private Integer status; // 0-禁用 1-启用
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;
}