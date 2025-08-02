package com.pk.crm.user.service;

import com.pk.crm.dto.PermissionDTO;
import com.pk.crm.exception.ValidationException;
import com.pk.crm.service.PermissionService;
import com.pk.crm.user.mapper.PermissionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@DubboService
@RequiredArgsConstructor
@Slf4j
public class PermissionServiceImpl implements PermissionService {

    private final PermissionMapper permissionMapper;

    @Override
    public PermissionDTO createPermission(PermissionDTO permissionDTO) {
        log.info("Creating new permission: {}", permissionDTO.getPermissionCode());
        
        // 验证权限编码唯一性
        if (permissionMapper.countByPermissionCode(permissionDTO.getPermissionCode()) > 0) {
            throw new ValidationException("权限编码已存在");
        }
        
        // 设置默认值
        if (permissionDTO.getStatus() == null) {
            permissionDTO.setStatus(1);
        }
        if (permissionDTO.getParentId() == null) {
            permissionDTO.setParentId(0L);
        }
        if (permissionDTO.getSortOrder() == null) {
            permissionDTO.setSortOrder(0);
        }
        if (!StringUtils.hasText(permissionDTO.getPermissionType())) {
            permissionDTO.setPermissionType("API");
        }
        
        try {
            permissionMapper.insert(permissionDTO);
            log.info("Successfully created permission with ID: {}", permissionDTO.getId());
            return permissionDTO;
        } catch (Exception e) {
            log.error("Failed to create permission: {}", permissionDTO.getPermissionCode(), e);
            throw new RuntimeException("创建权限失败", e);
        }
    }

    @Override
    public PermissionDTO getPermissionById(Long id) {
        log.debug("Getting permission by ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("权限ID不能为空");
        }
        
        PermissionDTO permission = permissionMapper.findById(id);
        if (permission == null) {
            throw new ValidationException("权限不存在");
        }
        
        return permission;
    }

    @Override
    public PermissionDTO updatePermission(PermissionDTO permissionDTO) {
        log.info("Updating permission with ID: {}", permissionDTO.getId());
        
        if (permissionDTO.getId() == null) {
            throw new ValidationException("权限ID不能为空");
        }
        
        // 检查权限是否存在
        PermissionDTO existingPermission = permissionMapper.findById(permissionDTO.getId());
        if (existingPermission == null) {
            throw new ValidationException("权限不存在");
        }
        
        // 验证权限编码唯一性（排除自己）
        PermissionDTO permissionWithCode = permissionMapper.findByPermissionCode(permissionDTO.getPermissionCode());
        if (permissionWithCode != null && !permissionWithCode.getId().equals(permissionDTO.getId())) {
            throw new ValidationException("权限编码已存在");
        }
        
        try {
            int updatedRows = permissionMapper.update(permissionDTO);
            if (updatedRows == 0) {
                throw new ValidationException("权限不存在");
            }
            
            log.info("Successfully updated permission with ID: {}", permissionDTO.getId());
            return getPermissionById(permissionDTO.getId());
        } catch (Exception e) {
            log.error("Failed to update permission with ID: {}", permissionDTO.getId(), e);
            throw new RuntimeException("更新权限失败", e);
        }
    }

    @Override
    public void deletePermission(Long id) {
        log.info("Deleting permission with ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("权限ID不能为空");
        }
        
        // 检查权限是否存在
        PermissionDTO existingPermission = permissionMapper.findById(id);
        if (existingPermission == null) {
            throw new ValidationException("权限不存在");
        }
        
        try {
            // 先删除角色权限关联
            permissionMapper.deleteRolePermissionsByPermissionId(id);
            
            // 再删除权限
            int deletedRows = permissionMapper.delete(id);
            if (deletedRows == 0) {
                throw new ValidationException("权限不存在");
            }
            
            log.info("Successfully deleted permission with ID: {}", id);
        } catch (Exception e) {
            log.error("Failed to delete permission with ID: {}", id, e);
            throw new RuntimeException("删除权限失败", e);
        }
    }

    @Override
    public List<PermissionDTO> getAllPermissions() {
        log.debug("Getting all permissions");
        
        try {
            List<PermissionDTO> permissions = permissionMapper.findAll();
            log.info("Found {} permissions", permissions.size());
            return permissions;
        } catch (Exception e) {
            log.error("Failed to get all permissions", e);
            throw new RuntimeException("获取权限列表失败", e);
        }
    }

    @Override
    public List<PermissionDTO> getPermissionTree() {
        log.debug("Getting permission tree");
        
        try {
            List<PermissionDTO> allPermissions = permissionMapper.findAll();
            return buildPermissionTree(allPermissions);
        } catch (Exception e) {
            log.error("Failed to get permission tree", e);
            throw new RuntimeException("获取权限树失败", e);
        }
    }

    @Override
    public List<PermissionDTO> searchPermissions(String keyword) {
        log.debug("Searching permissions with keyword: {}", keyword);
        
        if (!StringUtils.hasText(keyword)) {
            return getAllPermissions();
        }
        
        try {
            List<PermissionDTO> permissions = permissionMapper.searchPermissions(keyword.trim());
            log.info("Found {} permissions matching keyword: {}", permissions.size(), keyword);
            return permissions;
        } catch (Exception e) {
            log.error("Failed to search permissions with keyword: {}", keyword, e);
            throw new RuntimeException("搜索权限失败", e);
        }
    }

    @Override
    public void updatePermissionStatus(Long id, Integer status) {
        log.info("Updating permission status: {}, status: {}", id, status);
        
        if (id == null) {
            throw new IllegalArgumentException("权限ID不能为空");
        }
        
        // 检查权限是否存在
        PermissionDTO existingPermission = permissionMapper.findById(id);
        if (existingPermission == null) {
            throw new ValidationException("权限不存在");
        }
        
        try {
            int updatedRows = permissionMapper.updateStatus(id, status);
            if (updatedRows == 0) {
                throw new ValidationException("权限不存在");
            }
            
            log.info("Successfully updated permission status: {}", id);
        } catch (Exception e) {
            log.error("Failed to update permission status: {}", id, e);
            throw new RuntimeException("更新权限状态失败", e);
        }
    }

    @Override
    public List<PermissionDTO> getPermissionsByUserId(Long userId) {
        log.debug("Getting permissions by user ID: {}", userId);
        
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        try {
            List<PermissionDTO> permissions = permissionMapper.findByUserId(userId);
            log.debug("Found {} permissions for user: {}", permissions.size(), userId);
            return permissions;
        } catch (Exception e) {
            log.error("Failed to get permissions by user ID: {}", userId, e);
            throw new RuntimeException("获取用户权限失败", e);
        }
    }

    @Override
    public List<PermissionDTO> getPermissionsByRoleId(Long roleId) {
        log.debug("Getting permissions by role ID: {}", roleId);
        
        if (roleId == null) {
            throw new IllegalArgumentException("角色ID不能为空");
        }
        
        try {
            List<PermissionDTO> permissions = permissionMapper.findByRoleId(roleId);
            log.debug("Found {} permissions for role: {}", permissions.size(), roleId);
            return permissions;
        } catch (Exception e) {
            log.error("Failed to get permissions by role ID: {}", roleId, e);
            throw new RuntimeException("获取角色权限失败", e);
        }
    }

    /**
     * 构建权限树结构
     */
    private List<PermissionDTO> buildPermissionTree(List<PermissionDTO> allPermissions) {
        // 按父ID分组
        Map<Long, List<PermissionDTO>> permissionMap = allPermissions.stream()
                .collect(Collectors.groupingBy(PermissionDTO::getParentId));
        
        // 获取根节点（parentId = 0）
        List<PermissionDTO> rootPermissions = permissionMap.getOrDefault(0L, new ArrayList<>());
        
        // 递归构建树结构
        for (PermissionDTO permission : rootPermissions) {
            buildChildren(permission, permissionMap);
        }
        
        return rootPermissions;
    }

    /**
     * 递归构建子节点
     */
    private void buildChildren(PermissionDTO parent, Map<Long, List<PermissionDTO>> permissionMap) {
        List<PermissionDTO> children = permissionMap.get(parent.getId());
        if (children != null && !children.isEmpty()) {
            // 这里可以为PermissionDTO添加children字段，或者使用其他方式表示层级关系
            for (PermissionDTO child : children) {
                buildChildren(child, permissionMap);
            }
        }
    }
}