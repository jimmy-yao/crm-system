package com.pk.crm.gateway.controller;

import com.pk.crm.dto.LoginRequestDTO;
import com.pk.crm.dto.LoginResponseDTO;
import com.pk.crm.gateway.dto.ErrorResponse;
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
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户通过用户名和密码进行登录认证")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登录成功",
                    content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "请求参数验证失败",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "认证失败",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<LoginResponseDTO> login(
            @Parameter(description = "登录请求信息", required = true) @Valid @RequestBody LoginRequestDTO loginRequest) {
        log.info("Received login request for user: {}", loginRequest.getUsername());
        LoginResponseDTO response = userService.login(loginRequest);
        log.info("User login successful: {}", loginRequest.getUsername());
        return ResponseEntity.ok(response);
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