<template>
  <div class="permissions-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>权限管理</h2>
        <p>管理系统权限和资源访问控制</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="showAddDialog = true">
          <el-icon><Plus /></el-icon>
          新增权限
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="搜索">
          <el-input
            v-model="searchForm.keyword"
            placeholder="输入权限名称、编码或描述"
            clearable
            style="width: 300px"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="选择类型" clearable style="width: 120px">
            <el-option label="菜单" value="MENU" />
            <el-option label="按钮" value="BUTTON" />
            <el-option label="接口" value="API" />
          </el-select>
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

    <!-- 权限树表 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="permissionList"
        stripe
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="permissionName" label="权限名称" min-width="200">
          <template #default="{ row }">
            <div class="permission-info">
              <el-icon class="permission-icon" :style="{ color: getPermissionColor(row.permissionType) }">
                <component :is="getPermissionIcon(row.permissionType)" />
              </el-icon>
              <span class="permission-name">{{ row.permissionName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="permissionCode" label="权限编码" width="180">
          <template #default="{ row }">
            <el-tag size="small" type="info">{{ row.permissionCode }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="permissionType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="getPermissionTypeColor(row.permissionType)">
              {{ row.permissionType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="resourceUrl" label="资源URL" min-width="180" show-overflow-tooltip />
        <el-table-column prop="sortOrder" label="排序" width="80" />
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
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">
              查看
            </el-button>
            <el-button type="primary" link @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="success" link @click="handleAddChild(row)">
              添加子权限
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑权限对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="editingPermission ? '编辑权限' : (parentPermission ? '新增子权限' : '新增权限')"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="permissionFormRef"
        :model="permissionForm"
        :rules="permissionRules"
        label-width="100px"
      >
        <el-form-item label="上级权限" v-if="parentPermission">
          <el-input :value="parentPermission.permissionName" disabled />
        </el-form-item>
        <el-form-item label="权限名称" prop="permissionName">
          <el-input v-model="permissionForm.permissionName" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限编码" prop="permissionCode">
          <el-input 
            v-model="permissionForm.permissionCode" 
            placeholder="请输入权限编码"
            :disabled="!!editingPermission"
          />
        </el-form-item>
        <el-form-item label="权限类型" prop="permissionType">
          <el-select v-model="permissionForm.permissionType" placeholder="选择权限类型" style="width: 100%">
            <el-option label="菜单" value="MENU" />
            <el-option label="按钮" value="BUTTON" />
            <el-option label="接口" value="API" />
          </el-select>
        </el-form-item>
        <el-form-item label="资源URL" prop="resourceUrl">
          <el-input v-model="permissionForm.resourceUrl" placeholder="请输入资源URL" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number 
            v-model="permissionForm.sortOrder" 
            :min="0" 
            :max="999" 
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="permissionForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入权限描述"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="permissionForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ editingPermission ? '更新' : '创建' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 权限详情对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      title="权限详情"
      width="600px"
    >
      <div v-if="selectedPermission" class="permission-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="权限名称">
            {{ selectedPermission.permissionName }}
          </el-descriptions-item>
          <el-descriptions-item label="权限编码">
            <el-tag size="small" type="info">{{ selectedPermission.permissionCode }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="权限类型">
            <el-tag size="small" :type="getPermissionTypeColor(selectedPermission.permissionType)">
              {{ selectedPermission.permissionType }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="selectedPermission.status === 1 ? 'success' : 'danger'" size="small">
              {{ selectedPermission.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="资源URL" :span="2">
            {{ selectedPermission.resourceUrl || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="排序">
            {{ selectedPermission.sortOrder }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ formatDate(selectedPermission.createdTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="更新时间" :span="2">
            {{ formatDate(selectedPermission.updatedTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">
            {{ selectedPermission.description || '-' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, 
  Search,
  Menu,
  Operation,
  Link
} from '@element-plus/icons-vue'
import { getPermissions, createPermission, updatePermission, deletePermission } from '@/api/permissions'
import { formatDate } from '@/utils/date'

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const permissionList = ref([])
const selectedPermissions = ref([])
const showAddDialog = ref(false)
const showDetailDialog = ref(false)
const editingPermission = ref(null)
const selectedPermission = ref(null)
const parentPermission = ref(null)

// 表单引用
const permissionFormRef = ref(null)

// 搜索表单
const searchForm = reactive({
  keyword: '',
  type: '',
  status: null
})

// 权限表单
const permissionForm = reactive({
  permissionName: '',
  permissionCode: '',
  permissionType: 'API',
  resourceUrl: '',
  parentId: 0,
  sortOrder: 0,
  description: '',
  status: 1
})

// 表单验证规则
const permissionRules = {
  permissionName: [
    { required: true, message: '请输入权限名称', trigger: 'blur' },
    { min: 2, max: 50, message: '权限名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  permissionCode: [
    { required: true, message: '请输入权限编码', trigger: 'blur' },
    { min: 2, max: 100, message: '权限编码长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  permissionType: [
    { required: true, message: '请选择权限类型', trigger: 'change' }
  ],
  description: [
    { max: 200, message: '描述不能超过200个字符', trigger: 'blur' }
  ]
}

// 方法
const loadPermissions = async () => {
  loading.value = true
  try {
    let response
    if (searchForm.keyword) {
      response = await getPermissions({ keyword: searchForm.keyword })
    } else {
      response = await getPermissions()
    }
    let permissions = response.data || []
    
    // 根据类型和状态筛选
    if (searchForm.type || searchForm.status !== null) {
      permissions = filterPermissions(permissions)
    }
    
    permissionList.value = permissions
  } catch (error) {
    console.error('加载权限列表失败:', error)
    ElMessage.error('加载权限列表失败')
  } finally {
    loading.value = false
  }
}

const filterPermissions = (permissions) => {
  return permissions.filter(permission => {
    let match = true
    
    if (searchForm.type && permission.permissionType !== searchForm.type) {
      match = false
    }
    
    if (searchForm.status !== null && permission.status !== searchForm.status) {
      match = false
    }
    
    // 递归过滤子权限
    if (permission.children && permission.children.length > 0) {
      permission.children = filterPermissions(permission.children)
    }
    
    return match
  })
}

const handleSearch = () => {
  loadPermissions()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.type = ''
  searchForm.status = null
  loadPermissions()
}

const handleSelectionChange = (selection) => {
  selectedPermissions.value = selection
}

const handleView = (permission) => {
  selectedPermission.value = permission
  showDetailDialog.value = true
}

const handleEdit = (permission) => {
  editingPermission.value = permission
  Object.assign(permissionForm, {
    permissionName: permission.permissionName,
    permissionCode: permission.permissionCode,
    permissionType: permission.permissionType,
    resourceUrl: permission.resourceUrl || '',
    parentId: permission.parentId,
    sortOrder: permission.sortOrder,
    description: permission.description || '',
    status: permission.status
  })
  showAddDialog.value = true
}

const handleAddChild = (permission) => {
  parentPermission.value = permission
  permissionForm.parentId = permission.id
  showAddDialog.value = true
}

const handleDelete = async (permission) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除权限 "${permission.permissionName}" 吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deletePermission(permission.id)
    ElMessage.success('删除成功')
    loadPermissions()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除权限失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const handleStatusChange = async (permission) => {
  try {
    await updatePermission(permission.id, { status: permission.status })
    ElMessage.success('状态更新成功')
  } catch (error) {
    console.error('更新状态失败:', error)
    ElMessage.error('状态更新失败')
    // 恢复原状态
    permission.status = permission.status === 1 ? 0 : 1
  }
}

const handleSubmit = async () => {
  if (!permissionFormRef.value) return
  
  try {
    await permissionFormRef.value.validate()
    
    submitting.value = true
    
    if (editingPermission.value) {
      // 编辑权限
      await updatePermission(editingPermission.value.id, permissionForm)
      ElMessage.success('更新成功')
    } else {
      // 新增权限
      await createPermission(permissionForm)
      ElMessage.success('创建成功')
    }
    
    showAddDialog.value = false
    loadPermissions()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

const handleDialogClose = () => {
  editingPermission.value = null
  parentPermission.value = null
  permissionFormRef.value?.resetFields()
  Object.assign(permissionForm, {
    permissionName: '',
    permissionCode: '',
    permissionType: 'API',
    resourceUrl: '',
    parentId: 0,
    sortOrder: 0,
    description: '',
    status: 1
  })
}

const getPermissionIcon = (type) => {
  const icons = {
    'MENU': Menu,
    'BUTTON': Operation,
    'API': Link
  }
  return icons[type] || Menu
}

const getPermissionColor = (type) => {
  const colors = {
    'MENU': '#409EFF',
    'BUTTON': '#67C23A',
    'API': '#E6A23C'
  }
  return colors[type] || '#909399'
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
  loadPermissions()
})
</script>

<style lang="scss" scoped>
.permissions-page {
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
      .permission-info {
        display: flex;
        align-items: center;
        
        .permission-icon {
          margin-right: 8px;
          font-size: 16px;
        }
        
        .permission-name {
          font-weight: 500;
        }
      }
    }
  }
  
  .permission-detail {
    .el-descriptions {
      :deep(.el-descriptions__label) {
        font-weight: 500;
      }
    }
  }
}

@media (max-width: 768px) {
  .permissions-page {
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
    }
  }
}
</style>