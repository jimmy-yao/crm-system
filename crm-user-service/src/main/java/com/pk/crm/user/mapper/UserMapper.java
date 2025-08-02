package com.pk.crm.user.mapper;

import com.pk.crm.dto.UserDTO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT id, username, password, real_name as realName, email, phone, status, " +
            "last_login_time as lastLoginTime, created_time as createdTime, updated_time as updatedTime " +
            "FROM users ORDER BY created_time DESC")
    List<UserDTO> findAll();

    @Select("SELECT id, username, password, real_name as realName, email, phone, status, " +
            "last_login_time as lastLoginTime, created_time as createdTime, updated_time as updatedTime " +
            "FROM users WHERE id = #{id}")
    UserDTO findById(Long id);

    @Select("SELECT id, username, password, real_name as realName, email, phone, status, " +
            "last_login_time as lastLoginTime, created_time as createdTime, updated_time as updatedTime " +
            "FROM users WHERE username = #{username}")
    UserDTO findByUsername(String username);

    @Insert("INSERT INTO users (username, password, real_name, email, phone, status) " +
            "VALUES (#{username}, #{password}, #{realName}, #{email}, #{phone}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserDTO user);

    @Update("UPDATE users SET real_name=#{realName}, email=#{email}, phone=#{phone}, status=#{status} " +
            "WHERE id=#{id}")
    int update(UserDTO user);

    @Update("UPDATE users SET password=#{password} WHERE id=#{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    @Update("UPDATE users SET status=#{status} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Update("UPDATE users SET last_login_time=#{lastLoginTime} WHERE id=#{id}")
    int updateLastLoginTime(@Param("id") Long id, @Param("lastLoginTime") LocalDateTime lastLoginTime);

    @Delete("DELETE FROM users WHERE id = #{id}")
    int delete(Long id);

    @Select("SELECT COUNT(*) FROM users WHERE username = #{username}")
    int countByUsername(String username);

    @Select("SELECT COUNT(*) FROM users WHERE email = #{email}")
    int countByEmail(String email);

    @Select("SELECT COUNT(*) FROM users WHERE phone = #{phone}")
    int countByPhone(String phone);

    @Select("SELECT id, username, password, real_name as realName, email, phone, status, " +
            "last_login_time as lastLoginTime, created_time as createdTime, updated_time as updatedTime " +
            "FROM users WHERE username LIKE CONCAT('%', #{keyword}, '%') " +
            "OR real_name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR email LIKE CONCAT('%', #{keyword}, '%') " +
            "OR phone LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY created_time DESC")
    List<UserDTO> searchUsers(String keyword);
}