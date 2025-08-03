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
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

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
        
        // 跳过登录接口
        String requestPath = request.getRequestURI();
        log.info("JWT过滤器处理请求: {}", requestPath);
        
        if ("/api/auth/login".equals(requestPath) || "/api/auth/logout".equals(requestPath) || "/api/health".equals(requestPath)) {
            log.info("跳过JWT验证: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            String jwt = getJwtFromRequest(request);
            log.info("从请求中获取JWT: {}", jwt != null ? "有token" : "无token");
            
            if (StringUtils.hasText(jwt)) {
                String username = jwtUtil.getUsernameFromToken(jwt);
                log.info("从JWT中解析用户名: {}", username);
                
                if (StringUtils.hasText(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    
                    if (jwtUtil.validateToken(jwt, username)) {
                        // 创建认证对象
                        // 这里简化处理，实际应该从数据库获取用户角色
                        List<SimpleGrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_USER")
                        );
                        
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(username, null, authorities);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        log.info("JWT认证成功，用户: {}", username);
                    } else {
                        log.warn("JWT token验证失败，用户: {}", username);
                    }
                } else {
                    log.warn("用户名为空或已有认证信息");
                }
            } else {
                log.warn("请求中没有JWT token");
            }
        } catch (Exception e) {
            log.error("JWT认证过程中发生错误: {}", e.getMessage(), e);
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中获取JWT token
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.info("Authorization头: {}", bearerToken);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            log.info("提取的JWT token: {}", token.substring(0, Math.min(20, token.length())) + "...");
            return token;
        }
        log.warn("无效的Authorization头格式");
        return null;
    }
}