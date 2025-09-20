package com.pk.crm.gateway.controller;

import com.pk.crm.dto.UserDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "用户管理", description = "用户信息的增删改查操作")
public class UserController {

    @DubboReference
    private UserService userService;

    @GetMapping
    @Operation(summary = "获取所有用户", description = "获取系统中所有用户的信息列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取用户列表",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        log.info("Received request to get all users");
        List<UserDTO> users = userService.getAllUsers();
        log.info("Returning {} users", users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取用户", description = "根据用户ID获取特定用户的详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取用户信息",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "用户不存在",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserDTO> getUserById(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id) {
        log.info("Received request to get user by ID: {}", id);
        UserDTO user = userService.getUserById(id);
        log.info("Returning user: {}", user.getUsername());
        return ResponseEntity.ok(user);
    }

    @PostMapping
    @Operation(summary = "创建新用户", description = "在系统中创建一个新的用户记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "用户创建成功",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "请求参数验证失败",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserDTO> createUser(
            @Parameter(description = "用户信息", required = true) @Valid @RequestBody UserDTO userDTO) {
        log.info("Received request to create new user: {}", userDTO.getUsername());
        UserDTO newUser = userService.createUser(userDTO);
        log.info("Successfully created user with ID: {}", newUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户信息", description = "根据用户ID更新用户的详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "用户信息更新成功",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "请求参数验证失败",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "用户不存在",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id,
            @Parameter(description = "更新的用户信息", required = true) @Valid @RequestBody UserDTO userDTO) {
        log.info("Received request to update user with ID: {}", id);
        userDTO.setId(id);
        UserDTO updatedUser = userService.updateUser(userDTO);
        log.info("Successfully updated user with ID: {}", id);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "根据用户ID删除用户记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "用户删除成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id) {
        log.info("Received request to delete user with ID: {}", id);
        userService.deleteUser(id);
        log.info("Successfully deleted user with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "搜索用户", description = "根据关键词搜索用户信息（支持用户名、真实姓名、邮箱、手机号模糊搜索）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "搜索成功",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<UserDTO>> searchUsers(
            @Parameter(description = "搜索关键词", required = true) @RequestParam String keyword) {
        log.info("Received request to search users with keyword: {}", keyword);
        List<UserDTO> users = userService.searchUsers(keyword);
        log.info("Found {} users matching keyword: {}", users.size(), keyword);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/{id}/roles")
    @Operation(summary = "为用户分配角色", description = "为指定用户分配角色列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "角色分配成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> assignRolesToUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id,
            @Parameter(description = "角色ID列表", required = true) @RequestBody List<Long> roleIds) {
        log.info("Received request to assign roles to user: {}, roles: {}", id, roleIds);
        userService.assignRolesToUser(id, roleIds);
        log.info("Successfully assigned roles to user: {}", id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新用户状态", description = "启用或禁用用户")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "状态更新成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> updateUserStatus(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id,
            @Parameter(description = "状态：0-禁用，1-启用", required = true) @RequestParam Integer status) {
        log.info("Received request to update user status: {}, status: {}", id, status);
        userService.updateUserStatus(id, status);
        log.info("Successfully updated user status: {}", id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/password")
    @Operation(summary = "修改密码", description = "用户修改自己的密码")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "密码修改成功"),
            @ApiResponse(responseCode = "400", description = "原密码错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "用户不存在",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> changePassword(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id,
            @Parameter(description = "原密码", required = true) @RequestParam String oldPassword,
            @Parameter(description = "新密码", required = true) @RequestParam String newPassword) {
        log.info("Received request to change password for user: {}", id);
        userService.changePassword(id, oldPassword, newPassword);
        log.info("Successfully changed password for user: {}", id);
        return ResponseEntity.ok().build();
    }
}