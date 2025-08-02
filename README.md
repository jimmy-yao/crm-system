# CRM系统 v3

一个基于微服务架构的客户关系管理系统，采用Spring Boot + Dubbo + Nacos技术栈。

## 项目架构

```
crm-system-v3/
├── crm-api-service/      # API接口定义模块
├── crm-customer-service/ # 客户服务模块
├── crm-user-service/     # 用户权限服务模块
├── crm-gateway-service/  # 网关/控制器模块
└── pom.xml              # 父级Maven配置
```

## 技术栈

- **Java 17** - 编程语言
- **Spring Boot 3.2.5** - 应用框架
- **Apache Dubbo 3.2.12** - RPC框架
- **Spring Cloud Alibaba** - 微服务生态
- **Nacos** - 服务发现与配置中心
- **MyBatis** - 数据持久化
- **MySQL 8.x** - 关系型数据库
- **HikariCP** - 数据库连接池
- **Spring Security** - 安全框架
- **OpenAPI 3** - API文档
- **Docker** - 容器化部署
- **Logback** - 日志框架

## 功能特性

### 已实现功能
- ✅ 客户信息的CRUD操作
- ✅ 用户管理系统（注册、登录、用户信息管理）
- ✅ 角色管理系统（角色创建、分配、权限管理）
- ✅ 权限管理系统（RBAC权限控制）
- ✅ 统一异常处理
- ✅ 数据验证
- ✅ 日志记录
- ✅ 单元测试和集成测试
- ✅ 基于角色的访问控制（RBAC）
- ✅ API文档（Swagger UI）
- ✅ 健康检查和监控

### 核心改进
1. **异常处理**: 统一的异常处理机制，包含业务异常和系统异常
2. **数据验证**: 使用Bean Validation进行参数校验
3. **日志记录**: 完善的日志配置，支持文件输出和错误日志分离
4. **测试覆盖**: 单元测试和集成测试，确保代码质量
5. **安全性**: 基于Spring Security的HTTP Basic认证
6. **API文档**: 使用OpenAPI 3生成交互式API文档
7. **监控**: 集成Spring Boot Actuator进行应用监控

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.6+
- Docker & Docker Compose
- MySQL 8.x（可选，使用Docker自动启动）

### 方式一：使用Docker一键启动（推荐）

#### 1. 启动所有服务
```bash
# Linux/Mac
./start-services.sh

# Windows
start-services.bat
```

这将自动启动：
- MySQL 8.0 数据库
- Nacos 服务注册中心
- CRM Customer Service（客户管理服务）
- CRM User Service（用户权限管理服务）
- CRM Gateway Service（API网关服务）

#### 2. 访问服务

- **API文档**: http://localhost:8080/swagger-ui/index.html
- **健康检查**: http://localhost:8080/actuator/health
- **Nacos控制台**: http://localhost:8848/nacos (nacos/nacos)
- **MySQL**: localhost:3306 (root/123456)

### 方式二：手动启动

#### 1. 启动MySQL和Nacos
```bash
# 使用Docker Compose启动基础服务
docker-compose up -d mysql nacos
```

#### 2. 编译项目
```bash
mvn clean compile
```

#### 3. 启动应用服务

```bash
# 启动Provider服务
cd crm-provider
mvn spring-boot:run

# 启动Gateway服务（新终端）
cd crm-gateway
mvn spring-boot:run
```

### 方式三：使用本地MySQL

如果你有本地MySQL 8.x实例，请：

1. 创建数据库：
```sql
CREATE DATABASE crm_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行初始化脚本：
```bash
mysql -u root -p crm_db < database/init.sql
```

3. 修改配置文件 `crm-provider/src/main/resources/application.yaml` 中的数据库连接信息

### 5. 认证信息

系统使用HTTP Basic认证，默认用户：

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin  | admin123 | ADMIN |
| user   | user123  | USER  |

## API接口

### 认证管理接口

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/auth/login | 用户登录 |
| POST | /api/auth/logout | 用户登出 |

### 用户管理接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/users | 获取所有用户 |
| GET | /api/users/{id} | 根据ID获取用户 |
| GET | /api/users/search?keyword={keyword} | 搜索用户 |
| POST | /api/users | 创建新用户 |
| PUT | /api/users/{id} | 更新用户信息 |
| DELETE | /api/users/{id} | 删除用户 |
| POST | /api/users/{id}/roles | 为用户分配角色 |
| PUT | /api/users/{id}/status | 更新用户状态 |
| PUT | /api/users/{id}/password | 修改密码 |

### 角色管理接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/roles | 获取所有角色 |
| GET | /api/roles/{id} | 根据ID获取角色 |
| GET | /api/roles/search?keyword={keyword} | 搜索角色 |
| POST | /api/roles | 创建新角色 |
| PUT | /api/roles/{id} | 更新角色信息 |
| DELETE | /api/roles/{id} | 删除角色 |
| POST | /api/roles/{id}/permissions | 为角色分配权限 |
| PUT | /api/roles/{id}/status | 更新角色状态 |
| GET | /api/roles/user/{userId} | 获取用户角色 |

### 权限管理接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/permissions | 获取所有权限 |
| GET | /api/permissions/tree | 获取权限树 |
| GET | /api/permissions/{id} | 根据ID获取权限 |
| GET | /api/permissions/search?keyword={keyword} | 搜索权限 |
| POST | /api/permissions | 创建新权限 |
| PUT | /api/permissions/{id} | 更新权限信息 |
| DELETE | /api/permissions/{id} | 删除权限 |
| PUT | /api/permissions/{id}/status | 更新权限状态 |
| GET | /api/permissions/user/{userId} | 获取用户权限 |
| GET | /api/permissions/role/{roleId} | 获取角色权限 |

### 客户管理接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/customers | 获取所有客户 |
| GET | /api/customers/{id} | 根据ID获取客户 |
| GET | /api/customers/search?keyword={keyword} | 搜索客户（支持姓名、手机号、邮箱模糊搜索） |
| POST | /api/customers | 创建新客户 |
| PUT | /api/customers/{id} | 更新客户信息 |
| DELETE | /api/customers/{id} | 删除客户 |

### 请求示例

#### 用户登录
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456"
  }'
```

#### 创建用户
```bash
curl -X POST http://localhost:8080/api/users \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "123456",
    "realName": "新用户",
    "email": "newuser@example.com",
    "phone": "13900000000"
  }'
```

#### 为用户分配角色
```bash
curl -X POST http://localhost:8080/api/users/1/roles \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '[2, 3]'
```

#### 创建角色
```bash
curl -X POST http://localhost:8080/api/roles \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "roleName": "销售员",
    "roleCode": "SALES",
    "description": "销售人员角色"
  }'
```

#### 为角色分配权限
```bash
curl -X POST http://localhost:8080/api/roles/1/permissions \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '[19, 20, 21, 22, 23]'
```

#### 创建权限
```bash
curl -X POST http://localhost:8080/api/permissions \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "permissionName": "导出客户",
    "permissionCode": "customer:export",
    "permissionType": "API",
    "resourceUrl": "/api/customers/export",
    "parentId": 18,
    "sortOrder": 6,
    "description": "导出客户数据"
  }'
```

#### 创建客户
```bash
curl -X POST http://localhost:8080/api/customers \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "张三",
    "phone": "13800138000",
    "email": "zhangsan@example.com",
    "description": "重要客户"
  }'
```

#### 获取所有客户
```bash
curl -X GET http://localhost:8080/api/customers \
  -u admin:admin123
```

#### 搜索客户
```bash
curl -X GET "http://localhost:8080/api/customers/search?keyword=张" \
  -u admin:admin123
```

#### 根据ID获取客户
```bash
curl -X GET http://localhost:8080/api/customers/1 \
  -u admin:admin123
```

#### 更新客户信息
```bash
curl -X PUT http://localhost:8080/api/customers/1 \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "张三更新",
    "phone": "13800138001",
    "email": "zhangsan_new@example.com",
    "description": "更新后的客户信息"
  }'
```

#### 删除客户
```bash
curl -X DELETE http://localhost:8080/api/customers/1 \
  -u admin:admin123
```

## 数据模型

### User（用户）
```json
{
  "id": 1,
  "username": "admin",
  "realName": "系统管理员",
  "email": "admin@example.com",
  "phone": "13800000001",
  "status": 1,
  "lastLoginTime": "2024-02-08 15:30:00",
  "createdTime": "2024-02-08 10:00:00",
  "updatedTime": "2024-02-08 15:30:00",
  "roles": [
    {
      "id": 1,
      "roleName": "超级管理员",
      "roleCode": "SUPER_ADMIN"
    }
  ]
}
```

### Role（角色）
```json
{
  "id": 1,
  "roleName": "超级管理员",
  "roleCode": "SUPER_ADMIN",
  "description": "拥有系统所有权限",
  "status": 1,
  "createdTime": "2024-02-08 10:00:00",
  "updatedTime": "2024-02-08 10:00:00",
  "permissions": [
    {
      "id": 1,
      "permissionName": "系统管理",
      "permissionCode": "system:manage"
    }
  ]
}
```

### Permission（权限）
```json
{
  "id": 1,
  "permissionName": "系统管理",
  "permissionCode": "system:manage",
  "permissionType": "MENU",
  "resourceUrl": "/system",
  "parentId": 0,
  "sortOrder": 1,
  "description": "系统管理菜单",
  "status": 1,
  "createdTime": "2024-02-08 10:00:00",
  "updatedTime": "2024-02-08 10:00:00"
}
```

### Customer（客户）
```json
{
  "id": 1,
  "name": "张三",
  "phone": "13800138000",
  "email": "zhangsan@example.com",
  "description": "重要客户",
  "createdTime": "2024-02-08 10:30:00",
  "updatedTime": "2024-02-08 15:45:30"
}
```

### 字段说明

#### 用户字段
- **id**: 用户唯一标识（自动生成）
- **username**: 用户名（必填，唯一）
- **password**: 密码（必填，加密存储）
- **realName**: 真实姓名（必填）
- **email**: 邮箱地址（可选，唯一）
- **phone**: 手机号码（可选，唯一）
- **status**: 状态（0-禁用，1-启用）
- **lastLoginTime**: 最后登录时间
- **createdTime**: 创建时间（自动生成）
- **updatedTime**: 更新时间（自动维护）

#### 角色字段
- **id**: 角色唯一标识（自动生成）
- **roleName**: 角色名称（必填）
- **roleCode**: 角色编码（必填，唯一）
- **description**: 角色描述（可选）
- **status**: 状态（0-禁用，1-启用）
- **createdTime**: 创建时间（自动生成）
- **updatedTime**: 更新时间（自动维护）

#### 权限字段
- **id**: 权限唯一标识（自动生成）
- **permissionName**: 权限名称（必填）
- **permissionCode**: 权限编码（必填，唯一）
- **permissionType**: 权限类型（MENU-菜单，BUTTON-按钮，API-接口）
- **resourceUrl**: 资源URL（可选）
- **parentId**: 父权限ID（默认0）
- **sortOrder**: 排序（默认0）
- **description**: 权限描述（可选）
- **status**: 状态（0-禁用，1-启用）
- **createdTime**: 创建时间（自动生成）
- **updatedTime**: 更新时间（自动维护）

#### 客户字段
- **id**: 客户唯一标识（自动生成）
- **name**: 客户姓名（必填）
- **phone**: 手机号码（可选）
- **email**: 邮箱地址（可选）
- **description**: 客户描述（可选）
- **createdTime**: 创建时间（自动生成）
- **updatedTime**: 更新时间（自动维护）

### 验证规则

#### 用户验证规则
- **username**: 必填，3-50个字符，只能包含字母、数字和下划线
- **password**: 必填，6-100个字符
- **realName**: 必填，2-50个字符
- **email**: 可选，必须是有效的邮箱格式
- **phone**: 可选，必须是有效的中国手机号格式（1[3-9]xxxxxxxxx）

#### 角色验证规则
- **roleName**: 必填，2-50个字符
- **roleCode**: 必填，2-50个字符
- **description**: 可选，最多200个字符

#### 权限验证规则
- **permissionName**: 必填，2-50个字符
- **permissionCode**: 必填，2-100个字符
- **description**: 可选，最多200个字符

#### 客户验证规则
- **name**: 必填，2-50个字符
- **phone**: 可选，必须是有效的中国手机号格式（1[3-9]xxxxxxxxx）
- **email**: 可选，必须是有效的邮箱格式
- **description**: 可选，最多500个字符

### 数据库表结构
```sql
CREATE TABLE customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '客户ID',
    name VARCHAR(50) NOT NULL COMMENT '客户姓名',
    phone VARCHAR(20) COMMENT '手机号码',
    email VARCHAR(100) COMMENT '邮箱地址',
    description VARCHAR(500) COMMENT '客户描述',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_name (name),
    INDEX idx_phone (phone),
    INDEX idx_email (email),
    INDEX idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

## 测试

### 运行单元测试
```bash
mvn test
```

### 运行集成测试
```bash
mvn verify
```

### 测试覆盖率
项目包含以下测试：
- 服务层单元测试
- 控制器集成测试
- 异常处理测试
- 数据验证测试

## 监控和日志

### 监控端点
- `/actuator/health` - 健康检查
- `/actuator/info` - 应用信息
- `/actuator/metrics` - 应用指标

### 日志配置
- 日志文件位置: `logs/`
- 错误日志单独输出
- 支持日志轮转和压缩

## 部署

### 开发环境
使用Docker Compose自动启动MySQL 8.0和Nacos，开箱即用。

### 生产环境部署建议

#### 1. 数据库配置
- 使用独立的MySQL 8.x集群
- 配置主从复制和读写分离
- 启用SSL连接
- 定期备份数据

#### 2. 应用配置
- 使用外部配置文件管理敏感信息
- 配置生产级别的连接池参数
- 启用JVM性能调优参数

#### 3. 服务治理
- 配置外部Nacos集群
- 启用服务限流和熔断
- 配置负载均衡策略

#### 4. 安全加固
- 启用HTTPS
- 使用JWT替代Basic认证
- 配置API网关和防火墙
- 启用审计日志

#### 5. 监控告警
- 集成Prometheus + Grafana
- 配置应用性能监控(APM)
- 设置关键指标告警

### Docker生产部署示例

```yaml
# docker-compose.prod.yml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: crm_db
    volumes:
      - mysql_data:/var/lib/mysql
    deploy:
      replicas: 1
      resources:
        limits:
          memory: 2G
        reservations:
          memory: 1G

  crm-provider:
    image: crm-provider:latest
    environment:
      SPRING_PROFILES_ACTIVE: prod
      MYSQL_HOST: mysql
      MYSQL_PASSWORD: ${MYSQL_ROOT_PASSWORD}
    deploy:
      replicas: 2
      
  crm-gateway:
    image: crm-gateway:latest
    ports:
      - "80:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
    deploy:
      replicas: 2
```

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建Pull Request

## 许可证

MIT License

## 联系方式

如有问题，请联系开发团队：dev@example.com