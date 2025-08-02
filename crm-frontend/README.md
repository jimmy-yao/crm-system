# CRM前端管理系统

一个基于Vue 3 + Element Plus的现代化CRM客户关系管理系统前端应用。

## 项目简介

本项目是CRM系统的前端管理界面，提供了完整的客户管理、用户管理、角色权限管理等功能。采用现代化的前端技术栈，具有响应式设计、主题切换、多标签页等特性。

## 技术栈

- **框架**: Vue 3.4.0 (Composition API)
- **UI组件库**: Element Plus 2.4.4
- **路由管理**: Vue Router 4.2.5
- **状态管理**: Pinia 2.1.7
- **HTTP客户端**: Axios 1.6.2
- **图表库**: ECharts 5.x
- **构建工具**: Vite 5.0.8
- **CSS预处理器**: Sass 1.69.5
- **代码规范**: ESLint + Prettier

## 功能特性

### 🎯 核心功能
- ✅ 用户登录/登出
- ✅ 仪表盘数据展示
- ✅ 客户信息管理
- ✅ 用户账户管理
- ✅ 角色权限管理
- ✅ 权限分配系统

### 🎨 界面特性
- ✅ 响应式设计，支持移动端
- ✅ 明暗主题切换
- ✅ 侧边栏折叠/展开
- ✅ 多标签页导航
- ✅ 面包屑导航
- ✅ 全屏模式
- ✅ 页面切换动画

### 🔧 开发特性
- ✅ 组件自动导入
- ✅ API自动导入
- ✅ 路由懒加载
- ✅ 代码分割优化
- ✅ 热更新开发

## 项目结构

```
crm-frontend/
├── public/                 # 静态资源
├── src/
│   ├── api/               # API接口
│   │   ├── request.js     # 请求拦截器
│   │   ├── auth.js        # 认证相关API
│   │   ├── customer.js    # 客户管理API
│   │   ├── user.js        # 用户管理API
│   │   ├── role.js        # 角色管理API
│   │   └── permission.js  # 权限管理API
│   ├── components/        # 公共组件
│   ├── layout/           # 布局组件
│   │   └── index.vue     # 主布局
│   ├── router/           # 路由配置
│   │   └── index.js      # 路由定义
│   ├── stores/           # 状态管理
│   │   ├── app.js        # 应用状态
│   │   └── user.js       # 用户状态
│   ├── styles/           # 样式文件
│   │   └── index.scss    # 全局样式
│   ├── utils/            # 工具函数
│   │   └── date.js       # 日期工具
│   ├── views/            # 页面组件
│   │   ├── dashboard/    # 仪表盘
│   │   ├── customers/    # 客户管理
│   │   ├── users/        # 用户管理
│   │   ├── roles/        # 角色管理
│   │   ├── permissions/  # 权限管理
│   │   └── login/        # 登录页面
│   ├── App.vue           # 根组件
│   └── main.js           # 入口文件
├── .env                  # 环境变量
├── .env.development      # 开发环境变量
├── .env.production       # 生产环境变量
├── .eslintrc.js          # ESLint配置
├── .prettierrc           # Prettier配置
├── index.html            # HTML模板
├── package.json          # 项目配置
├── vite.config.js        # Vite配置
└── README.md             # 项目文档
```

## 快速开始

### 环境要求

- Node.js >= 16.0.0
- npm >= 8.0.0 或 yarn >= 1.22.0

### 安装依赖

```bash
# 使用npm
npm install

# 或使用yarn
yarn install
```

### 开发环境

```bash
# 启动开发服务器
npm run dev

# 或使用yarn
yarn dev
```

访问 http://localhost:5173

### 生产构建

```bash
# 构建生产版本
npm run build

# 预览生产构建
npm run preview
```

### 代码检查

```bash
# ESLint检查
npm run lint

# 代码格式化
npm run format
```

## 环境配置

### 开发环境 (.env.development)

```env
# API基础URL
VITE_API_BASE_URL=http://localhost:8080

# 应用标题
VITE_APP_TITLE=CRM管理系统

# 是否启用Mock数据
VITE_USE_MOCK=false
```

### 生产环境 (.env.production)

```env
# API基础URL
VITE_API_BASE_URL=https://your-api-domain.com

# 应用标题
VITE_APP_TITLE=CRM管理系统

# 是否启用Mock数据
VITE_USE_MOCK=false
```

## 页面功能

### 🏠 仪表盘
- 数据统计卡片
- 客户增长趋势图表
- 客户分布饼图
- 最近客户列表
- 系统通知

### 👥 客户管理
- 客户列表展示
- 搜索和筛选功能
- 新增/编辑客户
- 客户详情查看
- 批量操作支持

### 👤 用户管理
- 用户列表管理
- 用户状态切换
- 角色分配功能
- 密码管理
- 搜索筛选

### 🛡️ 角色管理
- 角色CRUD操作
- 权限分配界面
- 权限树选择
- 角色状态管理

### 🔐 权限管理
- 权限树表展示
- 层级权限管理
- 权限类型分类
- 子权限添加

### 🔑 登录系统
- 用户名密码登录
- 记住密码功能
- 快速登录选项
- 响应式登录界面

## API接口

### 认证接口
```javascript
// 用户登录
POST /api/auth/login
{
  "username": "admin",
  "password": "123456"
}

// 用户登出
POST /api/auth/logout
```

### 客户管理接口
```javascript
// 获取客户列表
GET /api/customers

// 创建客户
POST /api/customers
{
  "name": "客户名称",
  "phone": "13800138000",
  "email": "customer@example.com",
  "description": "客户描述"
}

// 更新客户
PUT /api/customers/{id}

// 删除客户
DELETE /api/customers/{id}

// 搜索客户
GET /api/customers/search?keyword=关键词
```

### 用户管理接口
```javascript
// 获取用户列表
GET /api/users

// 创建用户
POST /api/users

// 分配角色
POST /api/users/{id}/roles
```

## 开发指南

### 组件开发规范

1. **组件命名**: 使用PascalCase命名
2. **文件结构**: 每个组件一个文件夹
3. **样式作用域**: 使用scoped样式
4. **Props验证**: 定义完整的props类型

```vue
<template>
  <div class="my-component">
    <!-- 组件内容 -->
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// 定义props
const props = defineProps({
  title: {
    type: String,
    required: true
  }
})

// 定义emits
const emit = defineEmits(['update', 'delete'])
</script>

<style lang="scss" scoped>
.my-component {
  // 组件样式
}
</style>
```

### API调用规范

```javascript
// api/example.js
import request from './request'

export const exampleApi = {
  // 获取列表
  getList: (params) => {
    return request({
      url: '/api/examples',
      method: 'get',
      params
    })
  },
  
  // 创建
  create: (data) => {
    return request({
      url: '/api/examples',
      method: 'post',
      data
    })
  }
}
```

### 状态管理规范

```javascript
// stores/example.js
import { defineStore } from 'pinia'

export const useExampleStore = defineStore('example', () => {
  const state = ref({})
  
  const getters = computed(() => {
    // 计算属性
  })
  
  const actions = {
    // 异步操作
  }
  
  return {
    state,
    getters,
    ...actions
  }
})
```

## 部署指南

### Nginx配置

```nginx
server {
    listen 80;
    server_name your-domain.com;
    root /var/www/crm-frontend/dist;
    index index.html;

    # 处理Vue Router的history模式
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # API代理
    location /api/ {
        proxy_pass http://your-backend-server:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### Docker部署

```dockerfile
# Dockerfile
FROM node:18-alpine as build-stage

WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production

COPY . .
RUN npm run build

FROM nginx:stable-alpine as production-stage
COPY --from=build-stage /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

```bash
# 构建镜像
docker build -t crm-frontend .

# 运行容器
docker run -d -p 80:80 --name crm-frontend crm-frontend
```

## 浏览器支持

- Chrome >= 87
- Firefox >= 78
- Safari >= 14
- Edge >= 88

## 性能优化

### 已实现的优化
- 路由懒加载
- 组件按需导入
- 图片懒加载
- 代码分割
- 资源压缩
- 缓存策略

### 性能监控
```javascript
// 页面加载时间监控
window.addEventListener('load', () => {
  const loadTime = performance.timing.loadEventEnd - performance.timing.navigationStart
  console.log('页面加载时间:', loadTime + 'ms')
})
```

## 常见问题

### Q: 如何修改API基础URL？
A: 修改`.env.development`或`.env.production`文件中的`VITE_API_BASE_URL`变量。

### Q: 如何添加新的菜单项？
A: 在`src/layout/index.vue`文件中的`menuList`数组中添加新的菜单配置。

### Q: 如何自定义主题色？
A: 修改`src/styles/index.scss`文件中的CSS变量。

### Q: 如何处理跨域问题？
A: 在`vite.config.js`中配置代理，或在后端设置CORS头。

## 更新日志

### v1.0.0 (2024-02-08)
- ✨ 初始版本发布
- ✨ 完整的CRM管理功能
- ✨ 响应式设计
- ✨ 主题切换功能
- ✨ 多标签页导航

## 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

- 项目地址: [https://github.com/your-username/crm-frontend](https://github.com/your-username/crm-frontend)
- 问题反馈: [https://github.com/your-username/crm-frontend/issues](https://github.com/your-username/crm-frontend/issues)
- 邮箱: dev@example.com

## 致谢

感谢以下开源项目：

- [Vue.js](https://vuejs.org/) - 渐进式JavaScript框架
- [Element Plus](https://element-plus.org/) - Vue 3组件库
- [Vite](https://vitejs.dev/) - 下一代前端构建工具
- [ECharts](https://echarts.apache.org/) - 数据可视化图表库