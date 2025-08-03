package com.pk.crm.user.service;

import com.pk.crm.dto.LoginRequestDTO;
import com.pk.crm.dto.LoginResponseDTO;
import com.pk.crm.dto.PermissionDTO;
import com.pk.crm.dto.RoleDTO;
import com.pk.crm.dto.UserDTO;
import com.pk.crm.exception.AuthenticationException;
import com.pk.crm.exception.UserNotFoundException;
import com.pk.crm.exception.ValidationException;
import com.pk.crm.service.UserService;
import com.pk.crm.user.mapper.PermissionMapper;
import com.pk.crm.user.mapper.RoleMapper;
import com.pk.crm.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@DubboService
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        log.info("User login attempt: {}", loginRequest.getUsername());
        
        // 查找用户
        UserDTO user = userMapper.findByUsername(loginRequest.getUsername());
        if (user == null) {
            log.warn("User not found: {}", loginRequest.getUsername());
            throw new AuthenticationException("用户名或密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() == 0) {
            log.warn("User is disabled: {}", loginRequest.getUsername());
            throw new AuthenticationException("用户已被禁用");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            log.warn("Invalid password for user: {}", loginRequest.getUsername());
            throw new AuthenticationException("用户名或密码错误");
        }
        
        // 更新最后登录时间
        userMapper.updateLastLoginTime(user.getId(), LocalDateTime.now());
        
        // 获取用户角色和权限
        List<RoleDTO> roles = roleMapper.findByUserId(user.getId());
        List<PermissionDTO> permissions = permissionMapper.findByUserId(user.getId());
        
        // 构建响应
        LoginResponseDTO response = new LoginResponseDTO()
                .setToken(generateToken(user)) // 这里简化处理，实际应该生成JWT
                .setUserInfo(user.setPassword(null)) // 不返回密码
                .setRoles(roles.stream().map(RoleDTO::getRoleCode).collect(Collectors.toList()))
                .setPermissions(permissions.stream().map(PermissionDTO::getPermissionCode).collect(Collectors.toList()));
        
        log.info("User login successful: {}", loginRequest.getUsername());
        return response;
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        log.info("Creating new user: {}", userDTO.getUsername());
        
        // 验证用户名唯一性
        if (userMapper.countByUsername(userDTO.getUsername()) > 0) {
            throw new ValidationException("用户名已存在");
        }
        
        // 验证邮箱唯一性
        if (StringUtils.hasText(userDTO.getEmail()) && userMapper.countByEmail(userDTO.getEmail()) > 0) {
            throw new ValidationException("邮箱已存在");
        }
        
        // 验证手机号唯一性
        if (StringUtils.hasText(userDTO.getPhone()) && userMapper.countByPhone(userDTO.getPhone()) > 0) {
            throw new ValidationException("手机号已存在");
        }
        
        // 加密密码
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        
        // 设置默认状态
        if (userDTO.getStatus() == null) {
            userDTO.setStatus(1);
        }
        
        try {
            userMapper.insert(userDTO);
            log.info("Successfully created user with ID: {}", userDTO.getId());
            return userDTO.setPassword(null); // 不返回密码
        } catch (Exception e) {
            log.error("Failed to create user: {}", userDTO.getUsername(), e);
            throw new RuntimeException("创建用户失败", e);
        }
    }

    @Override
    public UserDTO getUserById(Long id) {
        log.debug("Getting user by ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        UserDTO user = userMapper.findById(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }
        
        // 获取用户角色
        List<RoleDTO> roles = roleMapper.findByUserId(id);
        user.setRoles(roles);
        
        return user.setPassword(null); // 不返回密码
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        log.debug("Getting user by username: {}", username);
        
        if (!StringUtils.hasText(username)) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        
        UserDTO user = userMapper.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException(username);
        }
        
        // 获取用户角色
        List<RoleDTO> roles = roleMapper.findByUserId(user.getId());
        user.setRoles(roles);
        
        return user; // 保留密码用于认证
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO) {
        log.info("Updating user with ID: {}", userDTO.getId());
        
        if (userDTO.getId() == null) {
            throw new ValidationException("用户ID不能为空");
        }
        
        // 检查用户是否存在
        UserDTO existingUser = userMapper.findById(userDTO.getId());
        if (existingUser == null) {
            throw new UserNotFoundException(userDTO.getId());
        }
        
        // 验证邮箱唯一性（排除自己）
        if (StringUtils.hasText(userDTO.getEmail())) {
            UserDTO userWithEmail = userMapper.findByUsername(userDTO.getEmail());
            if (userWithEmail != null && !userWithEmail.getId().equals(userDTO.getId())) {
                throw new ValidationException("邮箱已存在");
            }
        }
        
        try {
            int updatedRows = userMapper.update(userDTO);
            if (updatedRows == 0) {
                throw new UserNotFoundException(userDTO.getId());
            }
            
            log.info("Successfully updated user with ID: {}", userDTO.getId());
            return getUserById(userDTO.getId());
        } catch (Exception e) {
            log.error("Failed to update user with ID: {}", userDTO.getId(), e);
            throw new RuntimeException("更新用户失败", e);
        }
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        // 检查用户是否存在
        UserDTO existingUser = userMapper.findById(id);
        if (existingUser == null) {
            throw new UserNotFoundException(id);
        }
        
        try {
            // 先删除用户角色关联
            roleMapper.deleteUserRolesByUserId(id);
            
            // 再删除用户
            int deletedRows = userMapper.delete(id);
            if (deletedRows == 0) {
                throw new UserNotFoundException(id);
            }
            
            log.info("Successfully deleted user with ID: {}", id);
        } catch (Exception e) {
            log.error("Failed to delete user with ID: {}", id, e);
            throw new RuntimeException("删除用户失败", e);
        }
    }

    @Override
    public List<UserDTO> getAllUsers() {
        log.debug("Getting all users");
        
        try {
            List<UserDTO> users = userMapper.findAll();
            // 不返回密码
            users.forEach(user -> user.setPassword(null));
            log.info("Found {} users", users.size());
            return users;
        } catch (Exception e) {
            log.error("Failed to get all users", e);
            throw new RuntimeException("获取用户列表失败", e);
        }
    }

    @Override
    public List<UserDTO> searchUsers(String keyword) {
        log.debug("Searching users with keyword: {}", keyword);
        
        if (!StringUtils.hasText(keyword)) {
            return getAllUsers();
        }
        
        try {
            List<UserDTO> users = userMapper.searchUsers(keyword.trim());
            // 不返回密码
            users.forEach(user -> user.setPassword(null));
            log.info("Found {} users matching keyword: {}", users.size(), keyword);
            return users;
        } catch (Exception e) {
            log.error("Failed to search users with keyword: {}", keyword, e);
            throw new RuntimeException("搜索用户失败", e);
        }
    }

    @Override
    public void assignRolesToUser(Long userId, List<Long> roleIds) {
        log.info("Assigning roles to user: {}, roles: {}", userId, roleIds);
        
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        // 检查用户是否存在
        UserDTO existingUser = userMapper.findById(userId);
        if (existingUser == null) {
            throw new UserNotFoundException(userId);
        }
        
        try {
            // 先删除现有角色关联
            roleMapper.deleteUserRolesByUserId(userId);
            
            // 添加新的角色关联
            if (roleIds != null && !roleIds.isEmpty()) {
                for (Long roleId : roleIds) {
                    roleMapper.insertUserRole(userId, roleId);
                }
            }
            
            log.info("Successfully assigned roles to user: {}", userId);
        } catch (Exception e) {
            log.error("Failed to assign roles to user: {}", userId, e);
            throw new RuntimeException("分配角色失败", e);
        }
    }

    @Override
    public void updateUserStatus(Long id, Integer status) {
        log.info("Updating user status: {}, status: {}", id, status);
        
        if (id == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        // 检查用户是否存在
        UserDTO existingUser = userMapper.findById(id);
        if (existingUser == null) {
            throw new UserNotFoundException(id);
        }
        
        try {
            int updatedRows = userMapper.updateStatus(id, status);
            if (updatedRows == 0) {
                throw new UserNotFoundException(id);
            }
            
            log.info("Successfully updated user status: {}", id);
        } catch (Exception e) {
            log.error("Failed to update user status: {}", id, e);
            throw new RuntimeException("更新用户状态失败", e);
        }
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("Changing password for user: {}", userId);
        
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        // 获取用户信息
        UserDTO user = userMapper.findById(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new AuthenticationException("原密码错误");
        }
        
        try {
            // 更新密码
            String encodedNewPassword = passwordEncoder.encode(newPassword);
            int updatedRows = userMapper.updatePassword(userId, encodedNewPassword);
            if (updatedRows == 0) {
                throw new UserNotFoundException(userId);
            }
            
            log.info("Successfully changed password for user: {}", userId);
        } catch (Exception e) {
            log.error("Failed to change password for user: {}", userId, e);
            throw new RuntimeException("修改密码失败", e);
        }
    }

    private String generateToken(UserDTO user) {
        // 生成JWT token
        // 注意：这里简化处理，实际应该在gateway层生成JWT
        // 为了保持兼容性，这里仍然返回简单的token格式
        // JWT的生成将在gateway层的AuthController中处理
        return "jwt_" + user.getId() + "_" + System.currentTimeMillis();
    }
}