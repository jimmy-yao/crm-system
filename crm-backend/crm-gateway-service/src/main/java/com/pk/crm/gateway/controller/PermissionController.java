package com.pk.crm.gateway.controller;

import com.pk.crm.dto.PermissionDTO;
import com.pk.crm.gateway.dto.ErrorResponse;
import com.pk.crm.service.PermissionService;
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
 * 权限管理控制器
 */
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "权限管理", description = "权限信息的增删改查操作")
public class PermissionController {

    @DubboReference
    private PermissionService permissionService;

    @GetMapping
    @Operation(summary = "获取所有权限", description = "获取系统中所有权限的信息列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取权限列表",
                    content = @Content(schema = @Schema(implementation = PermissionDTO.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<PermissionDTO>> getAllPermissions() {
        log.info("Received request to get all permissions");
        List<PermissionDTO> permissions = permissionService.getAllPermissions();
        log.info("Returning {} permissions", permissions.size());
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/tree")
    @Operation(summary = "获取权限树", description = "获取系统中所有权限的树形结构")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取权限树",
                    content = @Content(schema = @Schema(implementation = PermissionDTO.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<PermissionDTO>> getPermissionTree() {
        log.info("Received request to get permission tree");
        List<PermissionDTO> permissionTree = permissionService.getPermissionTree();
        log.info("Returning permission tree with {} root nodes", permissionTree.size());
        return ResponseEntity.ok(permissionTree);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取权限", description = "根据权限ID获取特定权限的详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取权限信息",
                    content = @Content(schema = @Schema(implementation = PermissionDTO.class))),
            @ApiResponse(responseCode = "404", description = "权限不存在",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PermissionDTO> getPermissionById(
            @Parameter(description = "权限ID", required = true) @PathVariable Long id) {
        log.info("Received request to get permission by ID: {}", id);
        PermissionDTO permission = permissionService.getPermissionById(id);
        log.info("Returning permission: {}", permission.getPermissionName());
        return ResponseEntity.ok(permission);
    }

    @PostMapping
    @Operation(summary = "创建新权限", description = "在系统中创建一个新的权限记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "权限创建成功",
                    content = @Content(schema = @Schema(implementation = PermissionDTO.class))),
            @ApiResponse(responseCode = "400", description = "请求参数验证失败",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PermissionDTO> createPermission(
            @Parameter(description = "权限信息", required = true) @Valid @RequestBody PermissionDTO permissionDTO) {
        log.info("Received request to create new permission: {}", permissionDTO.getPermissionName());
        PermissionDTO newPermission = permissionService.createPermission(permissionDTO);
        log.info("Successfully created permission with ID: {}", newPermission.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(newPermission);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新权限信息", description = "根据权限ID更新权限的详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "权限信息更新成功",
                    content = @Content(schema = @Schema(implementation = PermissionDTO.class))),
            @ApiResponse(responseCode = "400", description = "请求参数验证失败",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "权限不存在",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PermissionDTO> updatePermission(
            @Parameter(description = "权限ID", required = true) @PathVariable Long id,
            @Parameter(description = "更新的权限信息", required = true) @Valid @RequestBody PermissionDTO permissionDTO) {
        log.info("Received request to update permission with ID: {}", id);
        permissionDTO.setId(id);
        PermissionDTO updatedPermission = permissionService.updatePermission(permissionDTO);
        log.info("Successfully updated permission with ID: {}", id);
        return ResponseEntity.ok(updatedPermission);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除权限", description = "根据权限ID删除权限记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "权限删除成功"),
            @ApiResponse(responseCode = "404", description = "权限不存在",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deletePermission(
            @Parameter(description = "权限ID", required = true) @PathVariable Long id) {
        log.info("Received request to delete permission with ID: {}", id);
        permissionService.deletePermission(id);
        log.info("Successfully deleted permission with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "搜索权限", description = "根据关键词搜索权限信息（支持权限名称、权限编码、描述模糊搜索）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "搜索成功",
                    content = @Content(schema = @Schema(implementation = PermissionDTO.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<PermissionDTO>> searchPermissions(
            @Parameter(description = "搜索关键词", required = true) @RequestParam String keyword) {
        log.info("Received request to search permissions with keyword: {}", keyword);
        List<PermissionDTO> permissions = permissionService.searchPermissions(keyword);
        log.info("Found {} permissions matching keyword: {}", permissions.size(), keyword);
        return ResponseEntity.ok(permissions);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新权限状态", description = "启用或禁用权限")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "状态更新成功"),
            @ApiResponse(responseCode = "404", description = "权限不存在",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> updatePermissionStatus(
            @Parameter(description = "权限ID", required = true) @PathVariable Long id,
            @Parameter(description = "状态：0-禁用，1-启用", required = true) @RequestParam Integer status) {
        log.info("Received request to update permission status: {}, status: {}", id, status);
        permissionService.updatePermissionStatus(id, status);
        log.info("Successfully updated permission status: {}", id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户权限", description = "根据用户ID获取该用户拥有的所有权限")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取用户权限",
                    content = @Content(schema = @Schema(implementation = PermissionDTO.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<PermissionDTO>> getPermissionsByUserId(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        log.info("Received request to get permissions by user ID: {}", userId);
        List<PermissionDTO> permissions = permissionService.getPermissionsByUserId(userId);
        log.info("Found {} permissions for user: {}", permissions.size(), userId);
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/role/{roleId}")
    @Operation(summary = "获取角色权限", description = "根据角色ID获取该角色拥有的所有权限")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取角色权限",
                    content = @Content(schema = @Schema(implementation = PermissionDTO.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<PermissionDTO>> getPermissionsByRoleId(
            @Parameter(description = "角色ID", required = true) @PathVariable Long roleId) {
        log.info("Received request to get permissions by role ID: {}", roleId);
        List<PermissionDTO> permissions = permissionService.getPermissionsByRoleId(roleId);
        log.info("Found {} permissions for role: {}", permissions.size(), roleId);
        return ResponseEntity.ok(permissions);
    }
}