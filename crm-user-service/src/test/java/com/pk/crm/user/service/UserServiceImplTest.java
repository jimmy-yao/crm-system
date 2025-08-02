package com.pk.crm.user.service;

import com.pk.crm.dto.UserDTO;
import com.pk.crm.exception.UserNotFoundException;
import com.pk.crm.exception.ValidationException;
import com.pk.crm.user.mapper.PermissionMapper;
import com.pk.crm.user.mapper.RoleMapper;
import com.pk.crm.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private PermissionMapper permissionMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDTO testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserDTO()
                .setId(1L)
                .setUsername("testuser")
                .setPassword("encodedPassword")
                .setRealName("测试用户")
                .setEmail("test@example.com")
                .setPhone("13900000000")
                .setStatus(1);
    }

    @Test
    void createUser_Success() {
        // Given
        UserDTO newUser = new UserDTO()
                .setUsername("newuser")
                .setPassword("123456")
                .setRealName("新用户")
                .setEmail("newuser@example.com");

        when(userMapper.countByUsername("newuser")).thenReturn(0);
        when(userMapper.countByEmail("newuser@example.com")).thenReturn(0);
        when(passwordEncoder.encode("123456")).thenReturn("encodedPassword");
        when(userMapper.insert(any(UserDTO.class))).thenAnswer(invocation -> {
            UserDTO user = invocation.getArgument(0);
            user.setId(2L);
            return 1;
        });

        // When
        UserDTO result = userService.createUser(newUser);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("newuser", result.getUsername());
        assertNull(result.getPassword()); // 密码不应该返回
        verify(userMapper).insert(any(UserDTO.class));
    }

    @Test
    void createUser_UsernameExists() {
        // Given
        UserDTO newUser = new UserDTO()
                .setUsername("existinguser")
                .setPassword("123456")
                .setRealName("新用户");

        when(userMapper.countByUsername("existinguser")).thenReturn(1);

        // When & Then
        assertThrows(ValidationException.class, () -> userService.createUser(newUser));
        verify(userMapper, never()).insert(any());
    }

    @Test
    void getUserById_Success() {
        // Given
        when(userMapper.findById(1L)).thenReturn(testUser);
        when(roleMapper.findByUserId(1L)).thenReturn(Arrays.asList());

        // When
        UserDTO result = userService.getUserById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
        assertNull(result.getPassword()); // 密码不应该返回
        verify(userMapper).findById(1L);
        verify(roleMapper).findByUserId(1L);
    }

    @Test
    void getUserById_NotFound() {
        // Given
        when(userMapper.findById(999L)).thenReturn(null);

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(999L));
        verify(userMapper).findById(999L);
    }

    @Test
    void getAllUsers_Success() {
        // Given
        List<UserDTO> users = Arrays.asList(testUser, 
                new UserDTO().setId(2L).setUsername("user2"));
        when(userMapper.findAll()).thenReturn(users);

        // When
        List<UserDTO> result = userService.getAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        // 验证密码已被清除
        result.forEach(user -> assertNull(user.getPassword()));
        verify(userMapper).findAll();
    }

    @Test
    void deleteUser_Success() {
        // Given
        when(userMapper.findById(1L)).thenReturn(testUser);
        when(roleMapper.deleteUserRolesByUserId(1L)).thenReturn(1);
        when(userMapper.delete(1L)).thenReturn(1);

        // When
        assertDoesNotThrow(() -> userService.deleteUser(1L));

        // Then
        verify(userMapper).findById(1L);
        verify(roleMapper).deleteUserRolesByUserId(1L);
        verify(userMapper).delete(1L);
    }

    @Test
    void deleteUser_NotFound() {
        // Given
        when(userMapper.findById(999L)).thenReturn(null);

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(999L));
        verify(userMapper).findById(999L);
        verify(userMapper, never()).delete(anyLong());
    }
}