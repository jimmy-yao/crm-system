package com.pk.crm.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pk.crm.dto.CustomerDTO;
import com.pk.crm.exception.CustomerNotFoundException;
import com.pk.crm.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomerDTO testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new CustomerDTO()
                .setId(1L)
                .setName("张三")
                .setPhone("13800138000")
                .setEmail("zhangsan@example.com")
                .setDescription("测试客户");
    }

    @Test
    void getAllCustomers_Success() throws Exception {
        // Given
        List<CustomerDTO> customers = Arrays.asList(testCustomer);
        when(customerService.getAllCustomers()).thenReturn(customers);

        // When & Then
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("张三"));

        verify(customerService).getAllCustomers();
    }

    @Test
    void getCustomerById_Success() throws Exception {
        // Given
        when(customerService.getCustomerById(1L)).thenReturn(testCustomer);

        // When & Then
        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("张三"));

        verify(customerService).getCustomerById(1L);
    }

    @Test
    void getCustomerById_NotFound() throws Exception {
        // Given
        when(customerService.getCustomerById(999L)).thenThrow(new CustomerNotFoundException(999L));

        // When & Then
        mockMvc.perform(get("/api/customers/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("CUSTOMER_NOT_FOUND"));

        verify(customerService).getCustomerById(999L);
    }

    @Test
    void addCustomer_Success() throws Exception {
        // Given
        CustomerDTO newCustomer = new CustomerDTO()
                .setName("李四")
                .setPhone("13900139000")
                .setEmail("lisi@example.com");

        CustomerDTO savedCustomer = new CustomerDTO()
                .setId(2L)
                .setName("李四")
                .setPhone("13900139000")
                .setEmail("lisi@example.com");

        when(customerService.addCustomer(any(CustomerDTO.class))).thenReturn(savedCustomer);

        // When & Then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCustomer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("李四"));

        verify(customerService).addCustomer(any(CustomerDTO.class));
    }

    @Test
    void addCustomer_ValidationError() throws Exception {
        // Given
        CustomerDTO invalidCustomer = new CustomerDTO()
                .setName("") // 空名称
                .setEmail("invalid-email"); // 无效邮箱

        // When & Then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCustomer)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));

        verify(customerService, never()).addCustomer(any());
    }

    @Test
    void updateCustomer_Success() throws Exception {
        // Given
        CustomerDTO updateData = new CustomerDTO()
                .setName("张三更新")
                .setPhone("13800138001")
                .setEmail("zhangsan_new@example.com");

        CustomerDTO updatedCustomer = new CustomerDTO()
                .setId(1L)
                .setName("张三更新")
                .setPhone("13800138001")
                .setEmail("zhangsan_new@example.com");

        when(customerService.updateCustomer(any(CustomerDTO.class))).thenReturn(updatedCustomer);

        // When & Then
        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("张三更新"));

        verify(customerService).updateCustomer(any(CustomerDTO.class));
    }

    @Test
    void deleteCustomer_Success() throws Exception {
        // Given
        doNothing().when(customerService).deleteCustomer(1L);

        // When & Then
        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isNoContent());

        verify(customerService).deleteCustomer(1L);
    }

    @Test
    void deleteCustomer_NotFound() throws Exception {
        // Given
        doThrow(new CustomerNotFoundException(999L)).when(customerService).deleteCustomer(999L);

        // When & Then
        mockMvc.perform(delete("/api/customers/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("CUSTOMER_NOT_FOUND"));

        verify(customerService).deleteCustomer(999L);
    }
}