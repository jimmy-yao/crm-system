package com.pk.crm.gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * 安全配置类
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final DatabaseUserDetailsService databaseUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF（开发环境，生产环境需要启用）
            .csrf(csrf -> csrf.disable())
            
            // 配置CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 配置授权规则
            .authorizeHttpRequests(authz -> authz
                // 健康检查端点允许匿名访问
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                // API文档相关端点允许匿名访问
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // 登录和登出端点允许匿名访问
                .requestMatchers("/api/auth/login", "/api/auth/logout").permitAll()
                // 系统管理相关接口需要管理员权限
                .requestMatchers("/api/users/**", "/api/roles/**", "/api/permissions/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                // 客户管理接口需要相应权限
                .requestMatchers("/api/customers/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "USER")
                // 其他所有请求需要认证
                .anyRequest().authenticated()
            )
            
            // 使用HTTP Basic认证（简单起见，生产环境建议使用JWT）
            .httpBasic(httpBasic -> httpBasic.and())
            
            // 无状态会话管理
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // 使用注入的数据库用户认证服务
        return databaseUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}