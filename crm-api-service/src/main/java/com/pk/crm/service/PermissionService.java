package com.pk.crm.service;

import com.pk.crm.dto.PermissionDTO;

import java.util.List;

/**
 * 权限服务接口
 */
public interface PermissionService {

    /**
     * 创建权限
     * @param permissionDTO 权限信息
     * @return 创建的权限
     */
    PermissionDTO createPermission(PermissionDTO permissionDTO);

    /**
     * 根据ID获取权限
     * @param id 权限ID
     * @return 权限信息
     */
    PermissionDTO getPermissionById(Long id);

    /**
     * 更新权限信息
     * @param permissionDTO 权限信息
     * @return 更新后的权限
     */
    PermissionDTO updatePermission(PermissionDTO permissionDTO);

    /**
     * 删除权限
     * @param id 权限ID
     */
    void deletePermission(Long id);

    /**
     * 获取所有权限
     * @return 权限列表
     */
    List<PermissionDTO> getAllPermissions();

    /**
     * 获取权限树结构
     * @return 权限树
     */
    List<PermissionDTO> getPermissionTree();

    /**
     * 搜索权限
     * @param keyword 搜索关键词
     * @return 权限列表
     */
    List<PermissionDTO> searchPermissions(String keyword);

    /**
     * 启用/禁用权限
     * @param id 权限ID
     * @param status 状态
     */
    void updatePermissionStatus(Long id, Integer status);

    /**
     * 根据用户ID获取权限列表
     * @param userId 用户ID
     * @return 权限列表
     */
    List<PermissionDTO> getPermissionsByUserId(Long userId);

    /**
     * 根据角色ID获取权限列表
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<PermissionDTO> getPermissionsByRoleId(Long roleId);
}