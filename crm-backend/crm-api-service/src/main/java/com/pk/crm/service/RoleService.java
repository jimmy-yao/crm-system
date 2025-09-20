package com.pk.crm.service;

import com.pk.crm.dto.RoleDTO;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService {

    /**
     * 创建角色
     * @param roleDTO 角色信息
     * @return 创建的角色
     */
    RoleDTO createRole(RoleDTO roleDTO);

    /**
     * 根据ID获取角色
     * @param id 角色ID
     * @return 角色信息
     */
    RoleDTO getRoleById(Long id);

    /**
     * 更新角色信息
     * @param roleDTO 角色信息
     * @return 更新后的角色
     */
    RoleDTO updateRole(RoleDTO roleDTO);

    /**
     * 删除角色
     * @param id 角色ID
     */
    void deleteRole(Long id);

    /**
     * 获取所有角色
     * @return 角色列表
     */
    List<RoleDTO> getAllRoles();

    /**
     * 搜索角色
     * @param keyword 搜索关键词
     * @return 角色列表
     */
    List<RoleDTO> searchRoles(String keyword);

    /**
     * 为角色分配权限
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     */
    void assignPermissionsToRole(Long roleId, List<Long> permissionIds);

    /**
     * 启用/禁用角色
     * @param id 角色ID
     * @param status 状态
     */
    void updateRoleStatus(Long id, Integer status);

    /**
     * 根据用户ID获取角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    List<RoleDTO> getRolesByUserId(Long userId);
}