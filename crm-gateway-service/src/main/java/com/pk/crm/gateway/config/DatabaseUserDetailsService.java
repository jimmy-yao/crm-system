package com.pk.crm.gateway.config;

import com.pk.crm.dto.RoleDTO;
import com.pk.crm.dto.UserDTO;
import com.pk.crm.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 数据库用户详情服务
 */
@Service
@Slf4j
public class DatabaseUserDetailsService implements UserDetailsService {

    @DubboReference
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        
        try {
            // 从数据库获取用户信息
            UserDTO user = userService.getUserByUsername(username);
            
            if (user == null) {
                log.warn("User not found: {}", username);
                throw new UsernameNotFoundException("User not found: " + username);
            }
            
            // 检查用户状态
            if (user.getStatus() == 0) {
                log.warn("User is disabled: {}", username);
                throw new UsernameNotFoundException("User is disabled: " + username);
            }
            
            // 构建权限列表
            Collection<GrantedAuthority> authorities = buildAuthorities(user);
            
            // 创建Spring Security用户对象
            return User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .authorities(authorities)
                    .accountExpired(false)
                    .accountLocked(user.getStatus() == 0)
                    .credentialsExpired(false)
                    .disabled(user.getStatus() == 0)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error loading user: {}", username, e);
            throw new UsernameNotFoundException("Error loading user: " + username, e);
        }
    }
    
    /**
     * 构建用户权限列表
     */
    private Collection<GrantedAuthority> buildAuthorities(UserDTO user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        // 添加角色权限
        if (user.getRoles() != null) {
            for (RoleDTO role : user.getRoles()) {
                // 添加角色权限（Spring Security要求角色以ROLE_开头）
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleCode()));
                log.debug("Added role authority: ROLE_{}", role.getRoleCode());
            }
        }
        
        // 如果没有角色，给一个默认角色
        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            log.debug("Added default role authority: ROLE_USER");
        }
        
        return authorities;
    }
}