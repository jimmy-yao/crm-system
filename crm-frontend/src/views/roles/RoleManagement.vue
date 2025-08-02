<template>
  <div class="roles-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>角色管理</h2>
        <p>管理系统角色和权限分配</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="showAddDialog = true">
          <el-icon><Plus /></el-icon>
          新增角色
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="搜索">
          <el-input
            v-model="searchForm.keyword"
            placeholder="输入角色名称、编码或描述"
            clearable
            style="width: 300px"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="选择状态" clearable style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 角色列表 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="roleList"
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="roleName" label="角色名称" width="150">
          <template #default="{ row }">
            <div class="role-info">
              <el-icon class="role-icon" :style="{ color: getRoleColor(row.roleCode) }">
                <Avatar />
              </el-icon>
              <span class="role-name">{{ row.roleName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="roleCode" label="角色编码" width="150">
          <template #default="{ row }">
            <el-tag size="small" type="info">{{ row.roleCode }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">
              查看
            </el-button>
            <el-button type="primary" link @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="warning" link @click="handleAssignPermissions(row)">
              分配权限
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑角色对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="editingRole ? '编辑角色' : '新增角色'"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="roleFormRef"
        :model="roleForm"
        :rules="roleRules"
        label-width="80px"
      >
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input 
            v-model="roleForm.roleCode" 
            placeholder="请输入角色编码"
            :disabled="!!editingRole"
          />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="roleForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入角色描述"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="roleForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ editingRole ? '更新' : '创建' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 角色详情对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      title="角色详情"
      width="600px"
    >
      <div v-if="selectedRole" class="role-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="角色名称">
            {{ selectedRole.roleName }}
          </el-descriptions-item>
          <el-descriptions-item label="角色编码">
            <el-tag size="small" type="info">{{ selectedRole.roleCode }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="selectedRole.status === 1 ? 'success' : 'danger'" size="small">
              {{ selectedRole.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ formatDate(selectedRole.createdTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="更新时间" :span="2">
            {{ formatDate(selectedRole.updatedTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">
            {{ selectedRole.description || '-' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- 分配权限对话框 -->
    <el-dialog
      v-model="showPermissionDialog"
      title="分配权限"
      width="800px"
    >
      <div v-if="selectedRole">
        <p class="dialog-desc">为角色 "{{ selectedRole.roleName }}" 分配权限：</p>
        <el-tree
          ref="permissionTreeRef"
          :data="permissionTree"
          :props="treeProps"
          show-checkbox
          node-key="id"
          :default-checked-keys="selectedPermissionIds"
          class="permission-tree"
        >
          <template #default="{ data }">
            <div class="tree-node">
              <el-icon class="node-icon">
                <component :is="getPermissionIcon(data.permissionType)" />
              </el-icon>
              <span class="node-label">{{ data.permissionName }}</span>
              <el-tag size="small" :type="getPermissionTypeColor(data.permissionType)">
                {{ data.permissionType }}
              </el-tag>
            </div>
          </template>
        </el-tree>
      </div>
      
      <template #footer>
        <el-button @click="showPermissionDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handlePermissionSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, 
  Search, 
  Avatar,
  Menu,
  Operation,
  Link
} from '@element-plus/icons-vue'
import { getRoles, createRole, updateRole, deleteRole } from '@/api/roles'
import { getPermissions } from '@/api/permissions'
import { formatDate } from '@/utils/date'

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const roleList = ref([])
const permissionTree = ref([])
const selectedRoles = ref([])
const showAddDialog = ref(false)
const showDetailDialog = ref(false)
const showPermissionDialog = ref(false)
const editingRole = ref(null)
const selectedRole = ref(null)
const selectedPermissionIds = ref([])

// 表单引用
const roleFormRef = ref(null)
const permissionTreeRef = ref(null)

// 搜索表单
const searchForm = reactive({
  keyword: '',
  status: null
})

// 分页
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 角色表单
const roleForm = reactive({
  roleName: '',
  roleCode: '',
  description: '',
  status: 1
})

// 树形控件配置
const treeProps = {
  children: 'children',
  label: 'permissionName'
}

// 表单验证规则
const roleRules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 50, message: '角色名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    { min: 2, max: 50, message: '角色编码长度在 2 到 50 个字符', trigger: 'blur' },
    { pattern: /^[A-Z_]+$/, message: '角色编码只能包含大写字母和下划线', trigger: 'blur' }
  ],
  description: [
    { max: 200, message: '描述不能超过200个字符', trigger: 'blur' }
  ]
}

// 方法
const loadRoles = async () => {
  loading.value = true
  try {
    let response
    if (searchForm.keyword) {
      response = await getRoles({ keyword: searchForm.keyword })
    } else {
      response = await getRoles()
    }
    let roles = response.data || []
    
    // 根据状态筛选
    if (searchForm.status !== null) {
      roles = roles.filter(role => role.status === searchForm.status)
    }
    
    roleList.value = roles
    pagination.total = roles.length
  } catch (error) {
    console.error('加载角色列表失败:', error)
    ElMessage.error('加载角色列表失败')
  } finally {
    loading.value = false
  }
}

const loadPermissions = async () => {
  try {
    const response = await getPermissions()
    const permissions = response.data || []
    permissionTree.value = buildPermissionTree(permissions)
  } catch (error) {
    console.error('加载权限列表失败:', error)
    ElMessage.error('加载权限列表失败')
  }
}

const buildPermissionTree = (permissions) => {
  const map = new Map()
  const roots = []
  
  // 创建映射
  permissions.forEach(permission => {
    map.set(permission.id, { ...permission, children: [] })
  })
  
  // 构建树结构
  permissions.forEach(permission => {
    const node = map.get(permission.id)
    if (permission.parentId === 0) {
      roots.push(node)
    } else {
      const parent = map.get(permission.parentId)
      if (parent) {
        parent.children.push(node)
      }
    }
  })
  
  return roots
}

const handleSearch = () => {
  pagination.page = 1
  loadRoles()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.status = null
  pagination.page = 1
  loadRoles()
}

const handleSelectionChange = (selection) => {
  selectedRoles.value = selection
}

const handleView = (role) => {
  selectedRole.value = role
  showDetailDialog.value = true
}

const handleEdit = (role) => {
  editingRole.value = role
  Object.assign(roleForm, {
    roleName: role.roleName,
    roleCode: role.roleCode,
    description: role.description || '',
    status: role.status
  })
  showAddDialog.value = true
}

const handleDelete = async (role) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除角色 "${role.roleName}" 吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteRole(role.id)
    ElMessage.success('删除成功')
    loadRoles()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除角色失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const handleStatusChange = async (role) => {
  try {
    await updateRole(role.id, { status: role.status })
    ElMessage.success('状态更新成功')
  } catch (error) {
    console.error('更新状态失败:', error)
    ElMessage.error('状态更新失败')
    // 恢复原状态
    role.status = role.status === 1 ? 0 : 1
  }
}

const handleAssignPermissions = async (role) => {
  selectedRole.value = role
  try {
    const response = await getPermissions({ roleId: role.id })
    const permissions = response.data || []
    selectedPermissionIds.value = permissions.map(p => p.id)
    showPermissionDialog.value = true
  } catch (error) {
    console.error('加载角色权限失败:', error)
    ElMessage.error('加载角色权限失败')
  }
}

const handleSubmit = async () => {
  if (!roleFormRef.value) return
  
  try {
    await roleFormRef.value.validate()
    
    submitting.value = true
    
    if (editingRole.value) {
      // 编辑角色
      await updateRole(editingRole.value.id, roleForm)
      ElMessage.success('更新成功')
    } else {
      // 新增角色
      await createRole(roleForm)
      ElMessage.success('创建成功')
    }
    
    showAddDialog.value = false
    loadRoles()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

const handlePermissionSubmit = async () => {
  if (!selectedRole.value || !permissionTreeRef.value) return
  
  try {
    submitting.value = true
    const checkedKeys = permissionTreeRef.value.getCheckedKeys()
    const halfCheckedKeys = permissionTreeRef.value.getHalfCheckedKeys()
    const allKeys = [...checkedKeys, ...halfCheckedKeys]
    
    await updateRole(selectedRole.value.id, { permissionIds: allKeys })
    ElMessage.success('权限分配成功')
    showPermissionDialog.value = false
  } catch (error) {
    console.error('分配权限失败:', error)
    ElMessage.error('权限分配失败')
  } finally {
    submitting.value = false
  }
}

const handleDialogClose = () => {
  editingRole.value = null
  roleFormRef.value?.resetFields()
  Object.assign(roleForm, {
    roleName: '',
    roleCode: '',
    description: '',
    status: 1
  })
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadRoles()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadRoles()
}

const getRoleColor = (roleCode) => {
  const colors = {
    'SUPER_ADMIN': '#F56C6C',
    'ADMIN': '#E6A23C',
    'MANAGER': '#409EFF',
    'USER': '#67C23A'
  }
  return colors[roleCode] || '#909399'
}

const getPermissionIcon = (type) => {
  const icons = {
    'MENU': Menu,
    'BUTTON': Operation,
    'API': Link
  }
  return icons[type] || Menu
}

const getPermissionTypeColor = (type) => {
  const colors = {
    'MENU': 'primary',
    'BUTTON': 'success',
    'API': 'warning'
  }
  return colors[type] || ''
}

// 生命周期
onMounted(() => {
  loadRoles()
  loadPermissions()
})
</script>

<style lang="scss" scoped>
.roles-page {
  padding: 20px;
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 20px;
    
    .header-left {
      h2 {
        margin: 0 0 8px 0;
        color: var(--el-text-color-primary);
        font-size: 24px;
        font-weight: 600;
      }
      
      p {
        margin: 0;
        color: var(--el-text-color-regular);
        font-size: 14px;
      }
    }
  }
  
  .search-card {
    margin-bottom: 20px;
    
    :deep(.el-card__body) {
      padding: 20px;
    }
  }
  
  .table-card {
    :deep(.el-card__body) {
      padding: 0;
    }
    
    .el-table {
      .role-info {
        display: flex;
        align-items: center;
        
        .role-icon {
          margin-right: 8px;
          font-size: 16px;
        }
        
        .role-name {
          font-weight: 500;
        }
      }
    }
    
    .pagination-wrapper {
      padding: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }
  
  .role-detail {
    .el-descriptions {
      :deep(.el-descriptions__label) {
        font-weight: 500;
      }
    }
  }
  
  .dialog-desc {
    margin-bottom: 20px;
    color: var(--el-text-color-regular);
  }
  
  .permission-tree {
    max-height: 400px;
    overflow-y: auto;
    border: 1px solid var(--el-border-color);
    border-radius: 4px;
    padding: 10px;
    
    .tree-node {
      display: flex;
      align-items: center;
      flex: 1;
      
      .node-icon {
        margin-right: 8px;
        color: var(--el-color-primary);
      }
      
      .node-label {
        flex: 1;
        margin-right: 8px;
      }
    }
  }
}

@media (max-width: 768px) {
  .roles-page {
    padding: 10px;
    
    .page-header {
      flex-direction: column;
      align-items: stretch;
      
      .header-right {
        margin-top: 15px;
      }
    }
    
    .search-card {
      :deep(.el-form--inline) {
        .el-form-item {
          display: block;
          margin-right: 0;
          margin-bottom: 15px;
          
          .el-input,
          .el-select {
            width: 100% !important;
          }
        }
      }
    }
    
    .table-card {
      :deep(.el-table) {
        .el-table__body-wrapper {
          overflow-x: auto;
        }
      }
      
      .pagination-wrapper {
        justify-content: center;
        
        :deep(.el-pagination) {
          .el-pagination__sizes,
          .el-pagination__jump {
            display: none;
          }
        }
      }
    }
  }
}
</style>