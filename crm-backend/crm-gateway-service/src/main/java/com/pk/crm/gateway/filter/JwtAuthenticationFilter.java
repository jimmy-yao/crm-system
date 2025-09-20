package com.pk.crm.gateway.filter;

import com.pk.crm.gateway.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT认证过滤器
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        log.debug("Processing request: {}", requestURI);
        
        // 跳过登录和健康检查等公开接口
        if (isPublicPath(requestURI)) {
            log.debug("Public path, skipping JWT authentication: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractTokenFromRequest(request);
        
        if (StringUtils.hasText(token)) {
            try {
                String username = jwtUtil.getUsernameFromToken(token);
                
                if (StringUtils.hasText(username) && jwtUtil.validateToken(token, username)) {
                    // 从token中获取用户信息
                    Long userId = jwtUtil.getUserIdFromToken(token);
                    List<String> roles = jwtUtil.getRolesFromToken(token);
                    List<String> permissions = jwtUtil.getPermissionsFromToken(token);
                    
                    // 创建权限列表
                    List<SimpleGrantedAuthority> authorities = permissions.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
                    
                    // 添加角色权限（以ROLE_前缀）
                    roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
                    
                    // 创建认证对象
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(username, null, authorities);
                    
                    // 设置用户详细信息
                    authentication.setDetails(userId);
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("JWT authentication successful for user: {}", username);
                } else {
                    log.warn("Invalid JWT token for user: {}", username);
                }
            } catch (Exception e) {
                log.warn("JWT token validation failed: {}", e.getMessage());
            }
        } else {
            log.debug("No JWT token found in request");
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中提取JWT token
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 判断是否为公开路径
     */
    private boolean isPublicPath(String path) {
        return path.equals("/api/auth/login") ||
               path.equals("/api/auth/logout") ||
               path.startsWith("/actuator/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs") ||
               path.equals("/favicon.ico");
    }
}