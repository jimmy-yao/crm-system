package com.pk.crm.service;

import com.pk.crm.dto.LoginRequestDTO;
import com.pk.crm.dto.LoginResponseDTO;
import com.pk.crm.dto.UserDTO;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    LoginResponseDTO login(LoginRequestDTO loginRequest);

    /**
     * 创建用户
     * @param userDTO 用户信息
     * @return 创建的用户
     */
    UserDTO createUser(UserDTO userDTO);

    /**
     * 根据ID获取用户
     * @param id 用户ID
     * @return 用户信息
     */
    UserDTO getUserById(Long id);

    /**
     * 根据用户名获取用户
     * @param username 用户名
     * @return 用户信息
     */
    UserDTO getUserByUsername(String username);

    /**
     * 更新用户信息
     * @param userDTO 用户信息
     * @return 更新后的用户
     */
    UserDTO updateUser(UserDTO userDTO);

    /**
     * 删除用户
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 获取所有用户
     * @return 用户列表
     */
    List<UserDTO> getAllUsers();

    /**
     * 搜索用户
     * @param keyword 搜索关键词
     * @return 用户列表
     */
    List<UserDTO> searchUsers(String keyword);

    /**
     * 为用户分配角色
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    void assignRolesToUser(Long userId, List<Long> roleIds);

    /**
     * 启用/禁用用户
     * @param id 用户ID
     * @param status 状态
     */
    void updateUserStatus(Long id, Integer status);

    /**
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);
}