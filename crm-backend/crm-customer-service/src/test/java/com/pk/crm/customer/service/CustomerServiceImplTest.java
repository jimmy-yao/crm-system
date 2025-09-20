package com.pk.crm.customer.service;

import com.pk.crm.customer.mapper.CustomerMapper;
import com.pk.crm.dto.CustomerDTO;
import com.pk.crm.exception.CustomerNotFoundException;
import com.pk.crm.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

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
    void addCustomer_Success() {
        // Given
        CustomerDTO newCustomer = new CustomerDTO()
                .setName("李四")
                .setPhone("13900139000")
                .setEmail("lisi@example.com");

        when(customerMapper.insert(any(CustomerDTO.class))).thenAnswer(invocation -> {
            CustomerDTO customer = invocation.getArgument(0);
            customer.setId(2L);
            return 1;
        });

        // When
        CustomerDTO result = customerService.addCustomer(newCustomer);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("李四", result.getName());
        verify(customerMapper).insert(newCustomer);
    }

    @Test
    void addCustomer_ValidationError() {
        // Given
        CustomerDTO invalidCustomer = new CustomerDTO().setName(""); // 空名称

        // When & Then
        assertThrows(ValidationException.class, () -> customerService.addCustomer(invalidCustomer));
        verify(customerMapper, never()).insert(any());
    }

    @Test
    void getCustomerById_Success() {
        // Given
        when(customerMapper.findById(1L)).thenReturn(testCustomer);

        // When
        CustomerDTO result = customerService.getCustomerById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testCustomer.getId(), result.getId());
        assertEquals(testCustomer.getName(), result.getName());
        verify(customerMapper).findById(1L);
    }

    @Test
    void getCustomerById_NotFound() {
        // Given
        when(customerMapper.findById(999L)).thenReturn(null);

        // When & Then
        assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerById(999L));
        verify(customerMapper).findById(999L);
    }

    @Test
    void getCustomerById_NullId() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> customerService.getCustomerById(null));
        verify(customerMapper, never()).findById(anyLong());
    }

    @Test
    void updateCustomer_Success() {
        // Given
        when(customerMapper.findById(1L)).thenReturn(testCustomer);
        when(customerMapper.update(any(CustomerDTO.class))).thenReturn(1);

        CustomerDTO updateData = new CustomerDTO()
                .setId(1L)
                .setName("张三更新")
                .setPhone("13800138001")
                .setEmail("zhangsan_new@example.com");

        // When
        CustomerDTO result = customerService.updateCustomer(updateData);

        // Then
        assertNotNull(result);
        assertEquals(updateData.getName(), result.getName());
        verify(customerMapper).findById(1L);
        verify(customerMapper).update(updateData);
    }

    @Test
    void updateCustomer_NotFound() {
        // Given
        when(customerMapper.findById(999L)).thenReturn(null);

        CustomerDTO updateData = new CustomerDTO()
                .setId(999L)
                .setName("不存在的客户");

        // When & Then
        assertThrows(CustomerNotFoundException.class, () -> customerService.updateCustomer(updateData));
        verify(customerMapper).findById(999L);
        verify(customerMapper, never()).update(any());
    }

    @Test
    void deleteCustomer_Success() {
        // Given
        when(customerMapper.findById(1L)).thenReturn(testCustomer);
        when(customerMapper.delete(1L)).thenReturn(1);

        // When
        assertDoesNotThrow(() -> customerService.deleteCustomer(1L));

        // Then
        verify(customerMapper).findById(1L);
        verify(customerMapper).delete(1L);
    }

    @Test
    void deleteCustomer_NotFound() {
        // Given
        when(customerMapper.findById(999L)).thenReturn(null);

        // When & Then
        assertThrows(CustomerNotFoundException.class, () -> customerService.deleteCustomer(999L));
        verify(customerMapper).findById(999L);
        verify(customerMapper, never()).delete(anyLong());
    }

    @Test
    void getAllCustomers_Success() {
        // Given
        List<CustomerDTO> customers = Arrays.asList(testCustomer, 
                new CustomerDTO().setId(2L).setName("李四"));
        when(customerMapper.findAll()).thenReturn(customers);

        // When
        List<CustomerDTO> result = customerService.getAllCustomers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testCustomer.getName(), result.get(0).getName());
        verify(customerMapper).findAll();
    }

    @Test
    void searchCustomers_Success() {
        // Given
        String keyword = "张";
        List<CustomerDTO> customers = Arrays.asList(testCustomer);
        when(customerMapper.searchCustomers(keyword)).thenReturn(customers);

        // When
        List<CustomerDTO> result = customerService.searchCustomers(keyword);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCustomer.getName(), result.get(0).getName());
        verify(customerMapper).searchCustomers(keyword);
    }

    @Test
    void searchCustomers_EmptyKeyword() {
        // Given
        List<CustomerDTO> allCustomers = Arrays.asList(testCustomer);
        when(customerMapper.findAll()).thenReturn(allCustomers);

        // When
        List<CustomerDTO> result = customerService.searchCustomers("");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(customerMapper).findAll();
        verify(customerMapper, never()).searchCustomers(any());
    }
}