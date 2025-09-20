package com.pk.crm.user.mapper;

import com.pk.crm.dto.RoleDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RoleMapper {

    @Select("SELECT id, role_name as roleName, role_code as roleCode, description, status, " +
            "created_time as createdTime, updated_time as updatedTime " +
            "FROM roles ORDER BY created_time DESC")
    List<RoleDTO> findAll();

    @Select("SELECT id, role_name as roleName, role_code as roleCode, description, status, " +
            "created_time as createdTime, updated_time as updatedTime " +
            "FROM roles WHERE id = #{id}")
    RoleDTO findById(Long id);

    @Select("SELECT id, role_name as roleName, role_code as roleCode, description, status, " +
            "created_time as createdTime, updated_time as updatedTime " +
            "FROM roles WHERE role_code = #{roleCode}")
    RoleDTO findByRoleCode(String roleCode);

    @Insert("INSERT INTO roles (role_name, role_code, description, status) " +
            "VALUES (#{roleName}, #{roleCode}, #{description}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RoleDTO role);

    @Update("UPDATE roles SET role_name=#{roleName}, role_code=#{roleCode}, description=#{description}, status=#{status} " +
            "WHERE id=#{id}")
    int update(RoleDTO role);

    @Update("UPDATE roles SET status=#{status} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Delete("DELETE FROM roles WHERE id = #{id}")
    int delete(Long id);

    @Select("SELECT COUNT(*) FROM roles WHERE role_code = #{roleCode}")
    int countByRoleCode(String roleCode);

    @Select("SELECT id, role_name as roleName, role_code as roleCode, description, status, " +
            "created_time as createdTime, updated_time as updatedTime " +
            "FROM roles WHERE role_name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR role_code LIKE CONCAT('%', #{keyword}, '%') " +
            "OR description LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY created_time DESC")
    List<RoleDTO> searchRoles(String keyword);

    @Select("SELECT r.id, r.role_name as roleName, r.role_code as roleCode, r.description, r.status, " +
            "r.created_time as createdTime, r.updated_time as updatedTime " +
            "FROM roles r " +
            "INNER JOIN user_roles ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.status = 1")
    List<RoleDTO> findByUserId(Long userId);

    @Insert("INSERT INTO user_roles (user_id, role_id) VALUES (#{userId}, #{roleId})")
    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Delete("DELETE FROM user_roles WHERE user_id = #{userId}")
    int deleteUserRolesByUserId(Long userId);

    @Delete("DELETE FROM user_roles WHERE role_id = #{roleId}")
    int deleteUserRolesByRoleId(Long roleId);
}