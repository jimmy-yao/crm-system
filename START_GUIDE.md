# CRM系统启动指南

## 🚀 快速启动

### 1. 启动基础服务
```bash
# 启动MySQL和Nacos
docker-compose up -d mysql nacos

# 等待服务启动
sleep 30

# 初始化数据库
docker exec -i crm-mysql mysql -uroot -p123456 < database/init.sql
```

### 2. 启动后端服务
```bash
# Windows
cd crm-backend
start-backend.bat

# Linux/Mac
cd crm-backend
./start-backend.sh
```

### 3. 启动前端服务
```bash
cd crm-frontend
npm install  # 首次运行需要安装依赖
npm run dev
```

## 📋 服务访问地址

- **前端界面**: http://localhost:3000
- **API文档**: http://localhost:8080/swagger-ui/index.html
- **健康检查**: http://localhost:8080/actuator/health
- **Nacos控制台**: http://localhost:8848/nacos (nacos/nacos)

## 🔑 默认登录账户

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin  | 123456 | 超级管理员 |
| manager| 123456 | 经理 |
| user   | 123456 | 普通用户 |

## 🧪 测试JWT认证

```bash
./test-jwt-auth.sh
```

## 📁 项目结构

```
crm-system-v3/
├── crm-backend/          # 后端服务(独立Maven工程)
├── crm-frontend/         # 前端Vue应用
├── database/            # 数据库脚本
├── docker-compose.yml   # Docker编排文件
└── test-jwt-auth.sh     # JWT认证测试脚本
```

## ⚠️ 注意事项

1. **启动顺序**: MySQL → Nacos → 后端服务 → 前端服务
2. **端口检查**: 确保3000, 8080, 8848, 3306等端口未被占用
3. **数据库连接**: 确保能访问 `mysql5.sqlpub.com:3310`
4. **Java版本**: 后端需要Java 8+
5. **Node.js版本**: 前端需要Node.js 16+