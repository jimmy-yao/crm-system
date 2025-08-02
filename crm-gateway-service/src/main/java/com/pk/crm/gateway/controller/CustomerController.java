package com.pk.crm.gateway.controller;

import com.pk.crm.dto.CustomerDTO;
import com.pk.crm.gateway.dto.ErrorResponse;
import com.pk.crm.service.CustomerService;
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

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Allow all origins for simplicity during development
@Slf4j
@Tag(name = "客户管理", description = "客户信息的增删改查操作")
public class CustomerController {

    @DubboReference
    private CustomerService customerService;

    @GetMapping
    @Operation(summary = "获取所有客户", description = "获取系统中所有客户的信息列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取客户列表",
                    content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        log.info("Received request to get all customers");
        List<CustomerDTO> customers = customerService.getAllCustomers();
        log.info("Returning {} customers", customers.size());
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取客户", description = "根据客户ID获取特定客户的详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取客户信息",
                    content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
            @ApiResponse(responseCode = "404", description = "客户不存在",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CustomerDTO> getCustomerById(
            @Parameter(description = "客户ID", required = true) @PathVariable Long id) {
        log.info("Received request to get customer by ID: {}", id);
        CustomerDTO customer = customerService.getCustomerById(id);
        log.info("Returning customer: {}", customer.getName());
        return ResponseEntity.ok(customer);
    }

    @PostMapping
    @Operation(summary = "创建新客户", description = "在系统中创建一个新的客户记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "客户创建成功",
                    content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
            @ApiResponse(responseCode = "400", description = "请求参数验证失败",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CustomerDTO> addCustomer(
            @Parameter(description = "客户信息", required = true) @Valid @RequestBody CustomerDTO customerDTO) {
        log.info("Received request to add new customer: {}", customerDTO.getName());
        CustomerDTO newCustomer = customerService.addCustomer(customerDTO);
        log.info("Successfully created customer with ID: {}", newCustomer.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(newCustomer);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新客户信息", description = "根据客户ID更新客户的详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "客户信息更新成功",
                    content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
            @ApiResponse(responseCode = "400", description = "请求参数验证失败",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "客户不存在",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CustomerDTO> updateCustomer(
            @Parameter(description = "客户ID", required = true) @PathVariable Long id,
            @Parameter(description = "更新的客户信息", required = true) @Valid @RequestBody CustomerDTO customerDTO) {
        log.info("Received request to update customer with ID: {}", id);
        customerDTO.setId(id);
        CustomerDTO updatedCustomer = customerService.updateCustomer(customerDTO);
        log.info("Successfully updated customer with ID: {}", id);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除客户", description = "根据客户ID删除客户记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "客户删除成功"),
            @ApiResponse(responseCode = "404", description = "客户不存在",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "客户ID", required = true) @PathVariable Long id) {
        log.info("Received request to delete customer with ID: {}", id);
        customerService.deleteCustomer(id);
        log.info("Successfully deleted customer with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "搜索客户", description = "根据关键词搜索客户信息（支持姓名、手机号、邮箱模糊搜索）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "搜索成功",
                    content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<CustomerDTO>> searchCustomers(
            @Parameter(description = "搜索关键词", required = true) @RequestParam String keyword) {
        log.info("Received request to search customers with keyword: {}", keyword);
        List<CustomerDTO> customers = customerService.searchCustomers(keyword);
        log.info("Found {} customers matching keyword: {}", customers.size(), keyword);
        return ResponseEntity.ok(customers);
    }
}
