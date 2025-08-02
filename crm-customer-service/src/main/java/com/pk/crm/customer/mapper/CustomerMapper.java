package com.pk.crm.customer.mapper;

import com.pk.crm.dto.CustomerDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CustomerMapper {

    @Select("SELECT id, name, phone, email, description, created_time as createdTime, updated_time as updatedTime FROM customers ORDER BY created_time DESC")
    List<CustomerDTO> findAll();

    @Select("SELECT id, name, phone, email, description, created_time as createdTime, updated_time as updatedTime FROM customers WHERE id = #{id}")
    CustomerDTO findById(Long id);

    @Insert("INSERT INTO customers (name, phone, email, description) VALUES (#{name}, #{phone}, #{email}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CustomerDTO customer);

    @Update("UPDATE customers SET name=#{name}, phone=#{phone}, email=#{email}, description=#{description} WHERE id=#{id}")
    int update(CustomerDTO customer);

    @Delete("DELETE FROM customers WHERE id = #{id}")
    int delete(Long id);
    
    @Select("SELECT COUNT(*) FROM customers WHERE name = #{name}")
    int countByName(String name);
    
    @Select("SELECT COUNT(*) FROM customers WHERE phone = #{phone}")
    int countByPhone(String phone);
    
    @Select("SELECT COUNT(*) FROM customers WHERE email = #{email}")
    int countByEmail(String email);
    
    @Select("SELECT id, name, phone, email, description, created_time as createdTime, updated_time as updatedTime FROM customers WHERE name LIKE CONCAT('%', #{keyword}, '%') OR phone LIKE CONCAT('%', #{keyword}, '%') OR email LIKE CONCAT('%', #{keyword}, '%') ORDER BY created_time DESC")
    List<CustomerDTO> searchCustomers(String keyword);
}