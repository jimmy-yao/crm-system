package com.pk.crm.gateway.controller;

import com.pk.crm.dto.LoginRequestDTO;
import com.pk.crm.dto.LoginResponseDTO;
import com.pk.crm.exception.AuthenticationException;
import com.pk.crm.gateway.util.JwtUtil;
import com.pk.crm.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "使用用户名和密码进行登录，获取JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登录成功"),
            @ApiResponse(responseCode = "401", description = "用户名或密码错误")
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        log.info("Received login request for user: {}", loginRequest.getUsername());
        try {
            // 1. 调用用户服务进行登录验证
            LoginResponseDTO loginResponse = userService.login(loginRequest);

            // 2. 使用用户信息生成JWT
            String token = jwtUtil.generateToken(
                    loginResponse.getUserInfo(),
                    loginResponse.getRoles(),
                    loginResponse.getPermissions()
            );

            // 3. 构建成功的响应
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("userInfo", loginResponse.getUserInfo());
            response.put("roles", loginResponse.getRoles());
            response.put("permissions", loginResponse.getPermissions());

            log.info("User login successful: {}", loginRequest.getUsername());
            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            log.warn("Authentication failed for user: {}: {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getErrorMessage());
        } catch (Exception e) {
            log.error("An unexpected error occurred during login for user: {}", loginRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("登录时发生未知错误");
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出系统")
    public ResponseEntity<Void> logout() {
        log.info("User logout request received.");
        // 在基于JWT的无状态认证中，服务器端通常无需做任何事。
        // 客户端负责销毁Token。
        // 如果需要实现Token黑名单，可在此处添加逻辑。
        return ResponseEntity.ok().build();
    }
}
