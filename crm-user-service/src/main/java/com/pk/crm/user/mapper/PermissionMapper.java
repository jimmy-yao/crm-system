package com.pk.crm.user.mapper;

import com.pk.crm.dto.PermissionDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PermissionMapper {

    @Select("SELECT id, permission_name as permissionName, permission_code as permissionCode, " +
            "permission_type as permissionType, resource_url as resourceUrl, parent_id as parentId, " +
            "sort_order as sortOrder, description, status, " +
            "created_time as createdTime, updated_time as updatedTime " +
            "FROM permissions ORDER BY parent_id, sort_order")
    List<PermissionDTO> findAll();

    @Select("SELECT id, permission_name as permissionName, permission_code as permissionCode, " +
            "permission_type as permissionType, resource_url as resourceUrl, parent_id as parentId, " +
            "sort_order as sortOrder, description, status, " +
            "created_time as createdTime, updated_time as updatedTime " +
            "FROM permissions WHERE id = #{id}")
    PermissionDTO findById(Long id);

    @Select("SELECT id, permission_name as permissionName, permission_code as permissionCode, " +
            "permission_type as permissionType, resource_url as resourceUrl, parent_id as parentId, " +
            "sort_order as sortOrder, description, status, " +
            "created_time as createdTime, updated_time as updatedTime " +
            "FROM permissions WHERE permission_code = #{permissionCode}")
    PermissionDTO findByPermissionCode(String permissionCode);

    @Insert("INSERT INTO permissions (permission_name, permission_code, permission_type, resource_url, " +
            "parent_id, sort_order, description, status) " +
            "VALUES (#{permissionName}, #{permissionCode}, #{permissionType}, #{resourceUrl}, " +
            "#{parentId}, #{sortOrder}, #{description}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PermissionDTO permission);

    @Update("UPDATE permissions SET permission_name=#{permissionName}, permission_code=#{permissionCode}, " +
            "permission_type=#{permissionType}, resource_url=#{resourceUrl}, parent_id=#{parentId}, " +
            "sort_order=#{sortOrder}, description=#{description}, status=#{status} " +
            "WHERE id=#{id}")
    int update(PermissionDTO permission);

    @Update("UPDATE permissions SET status=#{status} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Delete("DELETE FROM permissions WHERE id = #{id}")
    int delete(Long id);

    @Select("SELECT COUNT(*) FROM permissions WHERE permission_code = #{permissionCode}")
    int countByPermissionCode(String permissionCode);

    @Select("SELECT id, permission_name as permissionName, permission_code as permissionCode, " +
            "permission_type as permissionType, resource_url as resourceUrl, parent_id as parentId, " +
            "sort_order as sortOrder, description, status, " +
            "created_time as createdTime, updated_time as updatedTime " +
            "FROM permissions WHERE permission_name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR permission_code LIKE CONCAT('%', #{keyword}, '%') " +
            "OR description LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY parent_id, sort_order")
    List<PermissionDTO> searchPermissions(String keyword);

    @Select("SELECT DISTINCT p.id, p.permission_name as permissionName, p.permission_code as permissionCode, " +
            "p.permission_type as permissionType, p.resource_url as resourceUrl, p.parent_id as parentId, " +
            "p.sort_order as sortOrder, p.description, p.status, " +
            "p.created_time as createdTime, p.updated_time as updatedTime " +
            "FROM permissions p " +
            "INNER JOIN role_permissions rp ON p.id = rp.permission_id " +
            "INNER JOIN user_roles ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND p.status = 1 " +
            "ORDER BY p.parent_id, p.sort_order")
    List<PermissionDTO> findByUserId(Long userId);

    @Select("SELECT p.id, p.permission_name as permissionName, p.permission_code as permissionCode, " +
            "p.permission_type as permissionType, p.resource_url as resourceUrl, p.parent_id as parentId, " +
            "p.sort_order as sortOrder, p.description, p.status, " +
            "p.created_time as createdTime, p.updated_time as updatedTime " +
            "FROM permissions p " +
            "INNER JOIN role_permissions rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId} AND p.status = 1 " +
            "ORDER BY p.parent_id, p.sort_order")
    List<PermissionDTO> findByRoleId(Long roleId);

    @Insert("INSERT INTO role_permissions (role_id, permission_id) VALUES (#{roleId}, #{permissionId})")
    int insertRolePermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    @Delete("DELETE FROM role_permissions WHERE role_id = #{roleId}")
    int deleteRolePermissionsByRoleId(Long roleId);

    @Delete("DELETE FROM role_permissions WHERE permission_id = #{permissionId}")
    int deleteRolePermissionsByPermissionId(Long permissionId);
}