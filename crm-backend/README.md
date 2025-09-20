# CRM后端服务

基于Spring Boot + Dubbo + Nacos的微服务架构后端系统。

## 📁 项目结构

```
crm-backend/
├── crm-api-service/      # API接口定义模块
├── crm-customer-service/ # 客户服务模块  
├── crm-user-service/     # 用户权限服务模块
├── crm-gateway-service/  # 网关/控制器模块
├── start-backend.bat     # Windows启动脚本
├── start-backend.sh      # Linux/Mac启动脚本
└── pom.xml              # 后端Maven配置
```

## 🏗️ 技术架构

### 核心技术栈
- **Java 21** - 编程语言
- **Spring Boot 3.2.4** - 应用框架
- **Apache Dubbo 3.3.5** - RPC框架
- **Spring Cloud Alibaba** - 微服务生态
- **Nacos** - 服务发现与配置中心
- **MyBatis** - 数据持久化
- **MySQL 8.x** - 关系型数据库
- **Spring Security + JWT** - 安全认证

### 服务架构
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   前端应用       │───▶│   网关服务       │───▶│   Nacos注册中心  │
│  (Vue 3)        │    │ (Gateway)       │    │                │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                              ▼
                    ┌─────────────────┐
                    │   Dubbo RPC     │
                    └─────────────────┘
                              │
                    ┌─────────┴─────────┐
                    ▼                   ▼
            ┌─────────────────┐ ┌─────────────────┐
            │   用户服务       │ │   客户服务       │
            │ (User Service)  │ │(Customer Service)│
            └─────────────────┘ └─────────────────┘
                    │                   │
                    └─────────┬─────────┘
                              ▼
                    ┌─────────────────┐
                    │   MySQL数据库    │
                    └─────────────────┘
```

## 🚀 快速启动

### 前置条件
- JDK 21+
- Maven 3.6+
- MySQL 8.x 或 Docker
- Nacos 2.3.0+

### 方式一：使用启动脚本（推荐）

#### Windows
```bash
# 启动所有后端服务
start-backend.bat
```

#### Linux/Mac
```bash
# 启动所有后端服务
./start-backend.sh
```

### 方式二：手动启动

#### 1. 编译项目
```bash
mvn clean compile -DskipTests
```

#### 2. 启动服务（按顺序）
```bash
# 启动用户服务
cd crm-user-service
mvn spring-boot:run

# 启动客户服务（新终端）
cd crm-customer-service  
mvn spring-boot:run

# 启动网关服务（新终端）
cd crm-gateway-service
mvn spring-boot:run
```

## 🔧 服务配置

### 端口分配
| 服务 | 端口 | 协议 | 说明 |
|------|------|------|------|
| 网关服务 | 8080 | HTTP | 对外API接口 |
| 客户服务 | 8081 | HTTP | 内部管理端口 |
| 用户服务 | 20881 | Dubbo | RPC服务端口 |
| 客户服务 | 20880 | Dubbo | RPC服务端口 |

### 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://mysql5.sqlpub.com:3310/crm_system_db
    username: crm_db
    password: XAa4j7dhveN4MSzi
```

### Dubbo配置
```yaml
dubbo:
  registry:
    address: nacos://127.0.0.1:8848
  consumer:
    timeout: 5000
  provider:
    timeout: 5000
```

## 🔐 安全认证

### JWT认证流程
1. 用户通过 `/api/auth/login` 登录
2. 验证成功后返回JWT Token
3. 客户端在请求头中携带 `Authorization: Bearer <token>`
4. 网关服务验证Token并提取用户信息
5. 基于RBAC进行权限控制

### 权限模型
- **用户(User)** - 系统用户
- **角色(Role)** - 用户角色（如管理员、经理、普通用户）
- **权限(Permission)** - 具体操作权限（如查看客户、创建用户等）

## 📡 API接口

### 认证接口
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出

### 用户管理
- `GET /api/users` - 获取用户列表
- `POST /api/users` - 创建用户
- `PUT /api/users/{id}` - 更新用户
- `DELETE /api/users/{id}` - 删除用户

### 客户管理
- `GET /api/customers` - 获取客户列表
- `POST /api/customers` - 创建客户
- `PUT /api/customers/{id}` - 更新客户
- `DELETE /api/customers/{id}` - 删除客户

### 角色权限管理
- `GET /api/roles` - 获取角色列表
- `GET /api/permissions` - 获取权限列表
- `POST /api/users/{id}/roles` - 分配角色
- `POST /api/roles/{id}/permissions` - 分配权限

## 🧪 测试验证

### API文档
访问 http://localhost:8080/swagger-ui/index.html 查看完整API文档

### 健康检查
```bash
curl http://localhost:8080/actuator/health
```

### 登录测试
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "123456"}'
```

## 📊 监控运维

### 健康检查端点
- `/actuator/health` - 应用健康状态
- `/actuator/info` - 应用信息
- `/actuator/metrics` - 应用指标

### 日志配置
- 日志级别：DEBUG（开发环境）
- 日志输出：控制台 + 文件
- 错误日志单独记录

### Nacos管理
- 控制台：http://localhost:8848/nacos
- 用户名/密码：nacos/nacos
- 服务发现和配置管理

## 🔧 开发指南

### 添加新服务
1. 在 `crm-backend` 下创建新模块
2. 继承 `crm-backend` 父POM
3. 添加到 `crm-backend/pom.xml` 的 modules 中
4. 实现Dubbo服务接口
5. 配置Nacos注册

### 添加新API
1. 在 `crm-api-service` 中定义接口
2. 在对应服务中实现接口
3. 在 `crm-gateway-service` 中添加控制器
4. 配置权限控制

### 数据库变更
1. 修改 `database/init.sql`
2. 更新对应的Mapper和Entity
3. 添加数据库迁移脚本

## 🐛 故障排查

### 常见问题

#### 1. 服务启动失败
- 检查端口是否被占用
- 确认数据库连接配置
- 查看Nacos是否正常运行

#### 2. Dubbo调用超时
- 检查服务是否在Nacos中注册
- 确认网络连通性
- 调整超时配置

#### 3. JWT认证失败
- 检查Token格式是否正确
- 确认Token是否过期
- 验证JWT密钥配置

### 日志查看
```bash
# 查看应用日志
tail -f logs/crm-gateway-service.log
tail -f logs/crm-user-service.log
tail -f logs/crm-customer-service.log
```

## 📈 性能优化

### 建议配置
- JVM参数：`-Xms512m -Xmx1024m`
- 数据库连接池：HikariCP（已配置）
- Dubbo线程池：根据业务调整
- 缓存：考虑添加Redis

### 监控指标
- 响应时间
- 吞吐量
- 错误率
- 资源使用率

## 🔄 部署说明

### 开发环境
使用本地启动脚本，连接远程数据库

### 生产环境
1. 构建Docker镜像
2. 使用Docker Compose或K8s部署
3. 配置外部数据库和Nacos集群
4. 启用监控和日志收集