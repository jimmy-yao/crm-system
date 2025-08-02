package com.pk.crm.gateway.controller;

import com.pk.crm.dto.RoleDTO;
import com.pk.crm.gateway.dto.ErrorResponse;
import com.pk.crm.service.RoleService;
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
 * 角色管理控制器
 */
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "角色管理", description = "角色信息的增删改查操作")
public class RoleController {

    @DubboReference
    private RoleService roleService;

    @GetMapping
    @Operation(summary = "获取所有角色", description = "获取系统中所有角色的信息列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取角色列表",
                    content = @Content(schema = @Schema(implementation = RoleDTO.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        log.info("Received request to get all roles");
        List<RoleDTO> roles = roleService.getAllRoles();
        log.info("Returning {} roles", roles.size());
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取角色", description = "根据角色ID获取特定角色的详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取角色信息",
                    content = @Content(schema = @Schema(implementation = RoleDTO.class))),
            @ApiResponse(responseCode = "404", description = "角色不存在",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<RoleDTO> getRoleById(
            @Parameter(description = "角色ID", required = true) @PathVariable Long id) {
        log.info("Received request to get role by ID: {}", id);
        RoleDTO role = roleService.getRoleById(id);
        log.info("Returning role: {}", role.getRoleName());
        return ResponseEntity.ok(role);
    }

    @PostMapping
    @Operation(summary = "创建新角色", description = "在系统中创建一个新的角色记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "角色创建成功",
                    content = @Content(schema = @Schema(implementation = RoleDTO.class))),
            @ApiResponse(responseCode = "400", description = "请求参数验证失败",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<RoleDTO> createRole(
            @Parameter(description = "角色信息", required = true) @Valid @RequestBody RoleDTO roleDTO) {
        log.info("Received request to create new role: {}", roleDTO.getRoleName());
        RoleDTO newRole = roleService.createRole(roleDTO);
        log.info("Successfully created role with ID: {}", newRole.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(newRole);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新角色信息", description = "根据角色ID更新角色的详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "角色信息更新成功",
                    content = @Content(schema = @Schema(implementation = RoleDTO.class))),
            @ApiResponse(responseCode = "400", description = "请求参数验证失败",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "角色不存在",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<RoleDTO> updateRole(
            @Parameter(description = "角色ID", required = true) @PathVariable Long id,
            @Parameter(description = "更新的角色信息", required = true) @Valid @RequestBody RoleDTO roleDTO) {
        log.info("Received request to update role with ID: {}", id);
        roleDTO.setId(id);
        RoleDTO updatedRole = roleService.updateRole(roleDTO);
        log.info("Successfully updated role with ID: {}", id);
        return ResponseEntity.ok(updatedRole);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色", description = "根据角色ID删除角色记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "角色删除成功"),
            @ApiResponse(responseCode = "404", description = "角色不存在",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteRole(
            @Parameter(description = "角色ID", required = true) @PathVariable Long id) {
        log.info("Received request to delete role with ID: {}", id);
        roleService.deleteRole(id);
        log.info("Successfully deleted role with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "搜索角色", description = "根据关键词搜索角色信息（支持角色名称、角色编码、描述模糊搜索）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "搜索成功",
                    content = @Content(schema = @Schema(implementation = RoleDTO.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<RoleDTO>> searchRoles(
            @Parameter(description = "搜索关键词", required = true) @RequestParam String keyword) {
        log.info("Received request to search roles with keyword: {}", keyword);
        List<RoleDTO> roles = roleService.searchRoles(keyword);
        log.info("Found {} roles matching keyword: {}", roles.size(), keyword);
        return ResponseEntity.ok(roles);
    }

    @PostMapping("/{id}/permissions")
    @Operation(summary = "为角色分配权限", description = "为指定角色分配权限列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "权限分配成功"),
            @ApiResponse(responseCode = "404", description = "角色不存在",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> assignPermissionsToRole(
            @Parameter(description = "角色ID", required = true) @PathVariable Long id,
            @Parameter(description = "权限ID列表", required = true) @RequestBody List<Long> permissionIds) {
        log.info("Received request to assign permissions to role: {}, permissions: {}", id, permissionIds);
        roleService.assignPermissionsToRole(id, permissionIds);
        log.info("Successfully assigned permissions to role: {}", id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新角色状态", description = "启用或禁用角色")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "状态更新成功"),
            @ApiResponse(responseCode = "404", description = "角色不存在",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> updateRoleStatus(
            @Parameter(description = "角色ID", required = true) @PathVariable Long id,
            @Parameter(description = "状态：0-禁用，1-启用", required = true) @RequestParam Integer status) {
        log.info("Received request to update role status: {}, status: {}", id, status);
        roleService.updateRoleStatus(id, status);
        log.info("Successfully updated role status: {}", id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户角色", description = "根据用户ID获取该用户拥有的所有角色")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取用户角色",
                    content = @Content(schema = @Schema(implementation = RoleDTO.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<RoleDTO>> getRolesByUserId(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        log.info("Received request to get roles by user ID: {}", userId);
        List<RoleDTO> roles = roleService.getRolesByUserId(userId);
        log.info("Found {} roles for user: {}", roles.size(), userId);
        return ResponseEntity.ok(roles);
    }
}