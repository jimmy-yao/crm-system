-- 插入示例客户数据
INSERT INTO customers (name, phone, email, description) VALUES
('张三', '13800138000', 'zhangsan@example.com', '重要客户，长期合作伙伴'),
('李四', '13900139000', 'lisi@example.com', '新客户，有潜力'),
('王五', '13700137000', 'wangwu@example.com', '老客户，需要维护关系'),
('赵六', '13600136000', 'zhaoliu@example.com', '大客户，优先服务'),
('钱七', '13500135000', 'qianqi@example.com', '普通客户');

-- 插入默认用户数据（密码都是123456，使用BCrypt加密）
INSERT INTO users (username, password, real_name, email, phone, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '系统管理员', 'admin@example.com', '13800000001', 1),
('manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '部门经理', 'manager@example.com', '13800000002', 1),
('user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '普通用户', 'user@example.com', '13800000003', 1);

-- 插入默认角色数据
INSERT INTO roles (role_name, role_code, description, status) VALUES
('超级管理员', 'SUPER_ADMIN', '拥有系统所有权限', 1),
('管理员', 'ADMIN', '拥有大部分管理权限', 1),
('经理', 'MANAGER', '拥有部门管理权限', 1),
('普通用户', 'USER', '拥有基本操作权限', 1);

-- 插入默认权限数据
INSERT INTO permissions (permission_name, permission_code, permission_type, resource_url, parent_id, sort_order, description, status) VALUES
-- 系统管理
('系统管理', 'system:manage', 'MENU', '/system', 0, 1, '系统管理菜单', 1),
('用户管理', 'user:manage', 'MENU', '/system/user', 1, 1, '用户管理菜单', 1),
('角色管理', 'role:manage', 'MENU', '/system/role', 1, 2, '角色管理菜单', 1),
('权限管理', 'permission:manage', 'MENU', '/system/permission', 1, 3, '权限管理菜单', 1),

-- 用户管理权限
('查看用户', 'user:view', 'API', '/api/users', 2, 1, '查看用户列表', 1),
('创建用户', 'user:create', 'API', '/api/users', 2, 2, '创建新用户', 1),
('编辑用户', 'user:edit', 'API', '/api/users/*', 2, 3, '编辑用户信息', 1),
('删除用户', 'user:delete', 'API', '/api/users/*', 2, 4, '删除用户', 1),
('分配角色', 'user:assign:role', 'API', '/api/users/*/roles', 2, 5, '为用户分配角色', 1),

-- 角色管理权限
('查看角色', 'role:view', 'API', '/api/roles', 3, 1, '查看角色列表', 1),
('创建角色', 'role:create', 'API', '/api/roles', 3, 2, '创建新角色', 1),
('编辑角色', 'role:edit', 'API', '/api/roles/*', 3, 3, '编辑角色信息', 1),
('删除角色', 'role:delete', 'API', '/api/roles/*', 3, 4, '删除角色', 1),
('分配权限', 'role:assign:permission', 'API', '/api/roles/*/permissions', 3, 5, '为角色分配权限', 1),

-- 权限管理权限
('查看权限', 'permission:view', 'API', '/api/permissions', 4, 1, '查看权限列表', 1),
('创建权限', 'permission:create', 'API', '/api/permissions', 4, 2, '创建新权限', 1),
('编辑权限', 'permission:edit', 'API', '/api/permissions/*', 4, 3, '编辑权限信息', 1),
('删除权限', 'permission:delete', 'API', '/api/permissions/*', 4, 4, '删除权限', 1),

-- 客户管理
('客户管理', 'customer:manage', 'MENU', '/customer', 0, 2, '客户管理菜单', 1),
('查看客户', 'customer:view', 'API', '/api/customers', 18, 1, '查看客户列表', 1),
('创建客户', 'customer:create', 'API', '/api/customers', 18, 2, '创建新客户', 1),
('编辑客户', 'customer:edit', 'API', '/api/customers/*', 18, 3, '编辑客户信息', 1),
('删除客户', 'customer:delete', 'API', '/api/customers/*', 18, 4, '删除客户', 1),
('搜索客户', 'customer:search', 'API', '/api/customers/search', 18, 5, '搜索客户', 1);

-- 插入用户角色关联数据
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), -- admin -> SUPER_ADMIN
(2, 3), -- manager -> MANAGER
(3, 4); -- user -> USER

-- 插入角色权限关联数据
-- 超级管理员拥有所有权限
INSERT INTO role_permissions (role_id, permission_id) 
SELECT 1, id FROM permissions WHERE status = 1;

-- 管理员拥有除系统管理外的权限
INSERT INTO role_permissions (role_id, permission_id) 
SELECT 2, id FROM permissions WHERE status = 1 AND permission_code NOT LIKE 'system:%' AND permission_code NOT LIKE 'user:%' AND permission_code NOT LIKE 'role:%' AND permission_code NOT LIKE 'permission:%';

-- 经理拥有客户管理权限
INSERT INTO role_permissions (role_id, permission_id) 
SELECT 3, id FROM permissions WHERE status = 1 AND permission_code LIKE 'customer:%';

-- 普通用户只有查看客户权限
INSERT INTO role_permissions (role_id, permission_id) 
SELECT 4, id FROM permissions WHERE status = 1 AND permission_code IN ('customer:view', 'customer:search');