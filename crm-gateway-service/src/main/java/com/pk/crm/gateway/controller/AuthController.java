package com.pk.crm.gateway.controller;

import com.pk.crm.dto.LoginRequestDTO;
import com.pk.crm.dto.LoginResponseDTO;
import com.pk.crm.dto.RoleDTO;
import com.pk.crm.dto.UserDTO;
import com.pk.crm.gateway.dto.ErrorResponse;
import com.pk.crm.gateway.util.JwtUtil;
import com.pk.crm.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "认证管理", description = "用户登录、登出等认证相关操作")
public class AuthController {

    @DubboReference
    private UserService userService;
    
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequestDTO loginRequest) {
        log.info("Received login request for user: {}", loginRequest.getUsername());
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 临时简化：只验证admin用户
            if ("admin".equals(loginRequest.getUsername()) && "123456".equals(loginRequest.getPassword())) {
                
                // 生成JWT token
                String token = jwtUtil.generateToken("admin", 1L, "系统管理员");
                
                response.put("token", token);
                response.put("userInfo", Map.of(
                    "id", 1L,
                    "username", "admin",
                    "realName", "系统管理员",
                    "email", "admin@example.com"
                ));
                response.put("roles", List.of("SUPER_ADMIN"));
                response.put("permissions", List.of());
                
                log.info("User login successful: {}", loginRequest.getUsername());
                return response;
            } else {
                log.warn("Invalid credentials for user: {}", loginRequest.getUsername());
                response.put("error", "Invalid credentials");
                return response;
            }
            
        } catch (Exception e) {
            log.error("Login failed for user: {}", loginRequest.getUsername(), e);
            response.put("error", "Login failed: " + e.getMessage());
            return response;
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出系统")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登出成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> logout() {
        log.info("User logout");
        // 这里可以实现token失效逻辑
        return ResponseEntity.ok().build();
    }
}