package com.pk.crm.user.service;

import com.pk.crm.dto.RoleDTO;
import com.pk.crm.exception.ValidationException;
import com.pk.crm.service.RoleService;
import com.pk.crm.user.mapper.PermissionMapper;
import com.pk.crm.user.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@DubboService
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    @Override
    @Transactional
    public RoleDTO createRole(RoleDTO roleDTO) {
        log.info("Creating new role: {}", roleDTO.getRoleCode());

        // 验证角色编码唯一性
        if (roleMapper.countByRoleCode(roleDTO.getRoleCode()) > 0) {
            throw new ValidationException("角色编码已存在");
        }

        // 设置默认状态
        if (roleDTO.getStatus() == null) {
            roleDTO.setStatus(1);
        }

        try {
            roleMapper.insert(roleDTO);
            log.info("Successfully created role with ID: {}", roleDTO.getId());
            return roleDTO;
        } catch (DataAccessException e) {
            log.error("Failed to create role: {}", roleDTO.getRoleCode(), e);
            throw new RuntimeException("创建角色失败，数据库操作异常", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDTO getRoleById(Long id) {
        log.debug("Getting role by ID: {}", id);

        if (id == null) {
            throw new IllegalArgumentException("角色ID不能为空");
        }

        RoleDTO role = roleMapper.findById(id);
        if (role == null) {
            throw new ValidationException("角色不存在");
        }

        return role;
    }

    @Override
    @Transactional
    public RoleDTO updateRole(RoleDTO roleDTO) {
        log.info("Updating role with ID: {}", roleDTO.getId());

        if (roleDTO.getId() == null) {
            throw new ValidationException("角色ID不能为空");
        }

        // 检查角色是否存在
        RoleDTO existingRole = roleMapper.findById(roleDTO.getId());
        if (existingRole == null) {
            throw new ValidationException("角色不存在");
        }

        // 验证角色编码唯一性（排除自己）
        RoleDTO roleWithCode = roleMapper.findByRoleCode(roleDTO.getRoleCode());
        if (roleWithCode != null && !roleWithCode.getId().equals(roleDTO.getId())) {
            throw new ValidationException("角色编码已存在");
        }

        try {
            int updatedRows = roleMapper.update(roleDTO);
            if (updatedRows == 0) {
                throw new ValidationException("角色不存在");
            }

            log.info("Successfully updated role with ID: {}", roleDTO.getId());
            return roleDTO;
        } catch (DataAccessException e) {
            log.error("Failed to update role with ID: {}", roleDTO.getId(), e);
            throw new RuntimeException("更新角色失败，数据库操作异常", e);
        }
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        log.info("Deleting role with ID: {}", id);

        if (id == null) {
            throw new IllegalArgumentException("角色ID不能为空");
        }

        // 检查角色是否存在
        RoleDTO existingRole = roleMapper.findById(id);
        if (existingRole == null) {
            throw new ValidationException("角色不存在");
        }

        try {
            // 先删除所有与该角色相关的关联关系，确保数据完整性
            permissionMapper.deleteRolePermissionsByRoleId(id);
            roleMapper.deleteUserRolesByRoleId(id);

            // 再删除角色本身
            int deletedRows = roleMapper.delete(id);
            if (deletedRows == 0) {
                // 理论上，因为前面已经检查过存在性，所以这里不应该发生
                // 但作为防御性编程，仍然保留
                throw new ValidationException("角色不存在或已被删除");
            }

            log.info("Successfully deleted role with ID: {}", id);
        } catch (DataAccessException e) {
            log.error("Failed to delete role with ID: {}", id, e);
            throw new RuntimeException("删除角色失败，数据库操作异常", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDTO> getAllRoles() {
        log.debug("Getting all roles");

        try {
            List<RoleDTO> roles = roleMapper.findAll();
            log.info("Found {} roles", roles.size());
            return roles;
        } catch (DataAccessException e) {
            log.error("Failed to get all roles", e);
            throw new RuntimeException("获取角色列表失败，数据库操作异常", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDTO> searchRoles(String keyword) {
        log.debug("Searching roles with keyword: {}", keyword);

        if (!StringUtils.hasText(keyword)) {
            return getAllRoles();
        }

        try {
            List<RoleDTO> roles = roleMapper.searchRoles(keyword.trim());
            log.info("Found {} roles matching keyword: {}", roles.size(), keyword);
            return roles;
        } catch (DataAccessException e) {
            log.error("Failed to search roles with keyword: {}", keyword, e);
            throw new RuntimeException("搜索角色失败，数据库操作异常", e);
        }
    }

    @Override
    @Transactional
    public void assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        log.info("Assigning permissions to role: {}, permissions: {}", roleId, permissionIds);

        if (roleId == null) {
            throw new IllegalArgumentException("角色ID不能为空");
        }

        // 检查角色是否存在
        RoleDTO existingRole = roleMapper.findById(roleId);
        if (existingRole == null) {
            throw new ValidationException("角色不存在");
        }

        try {
            // 先删除现有权限关联
            permissionMapper.deleteRolePermissionsByRoleId(roleId);

            // 添加新的权限关联
            if (permissionIds != null && !permissionIds.isEmpty()) {
                for (Long permissionId : permissionIds) {
                    permissionMapper.insertRolePermission(roleId, permissionId);
                }
            }

            log.info("Successfully assigned permissions to role: {}", roleId);
        } catch (DataAccessException e) {
            log.error("Failed to assign permissions to role: {}", roleId, e);
            throw new RuntimeException("分配权限失败，数据库操作异常", e);
        }
    }

    @Override
    @Transactional
    public void updateRoleStatus(Long id, Integer status) {
        log.info("Updating role status: {}, status: {}", id, status);

        if (id == null) {
            throw new IllegalArgumentException("角色ID不能为空");
        }

        // 检查角色是否存在
        RoleDTO existingRole = roleMapper.findById(id);
        if (existingRole == null) {
            throw new ValidationException("角色不存在");
        }

        try {
            int updatedRows = roleMapper.updateStatus(id, status);
            if (updatedRows == 0) {
                throw new ValidationException("角色不存在");
            }

            log.info("Successfully updated role status: {}", id);
        } catch (DataAccessException e) {
            log.error("Failed to update role status: {}", id, e);
            throw new RuntimeException("更新角色状态失败", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDTO> getRolesByUserId(Long userId) {
        log.debug("Getting roles by user ID: {}", userId);

        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        try {
            List<RoleDTO> roles = roleMapper.findByUserId(userId);
            log.debug("Found {} roles for user: {}", roles.size(), userId);
            return roles;
        } catch (DataAccessException e) {
            log.error("Failed to get roles by user ID: {}", userId, e);
            throw new RuntimeException("获取用户角色失败", e);
        }
    }
}
