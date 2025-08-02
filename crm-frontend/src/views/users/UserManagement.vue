<template>
  <div class="users-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>用户管理</h2>
        <p>管理系统用户和权限</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="showAddDialog = true">
          <el-icon>
            <Plus />
          </el-icon>
          新增用户
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="搜索">
          <el-input v-model="searchForm.keyword" placeholder="输入用户名、姓名、邮箱或手机号" clearable style="width: 300px"
            @keyup.enter="handleSearch">
            <template #prefix>
              <el-icon>
                <Search />
              </el-icon>
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

    <!-- 用户列表 -->
    <el-card class="table-card" shadow="never">
      <el-table v-loading="loading" :data="userList" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="32" class="user-avatar">
                {{ row.realName.charAt(0) }}
              </el-avatar>
              <span class="user-name">{{ row.realName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column label="角色" min-width="150">
          <template #default="{ row }">
            <el-tag v-for="role in row.roles" :key="role.id" size="small" class="role-tag">
              {{ role.roleName }}
            </el-tag>
            <span v-if="!row.roles || row.roles.length === 0" class="no-role">
              未分配角色
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录" width="180">
          <template #default="{ row }">
            {{ row.lastLoginTime ? formatDate(row.lastLoginTime) : '从未登录' }}
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">
              查看
            </el-button>
            <el-button type="primary" link @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="warning" link @click="handleAssignRoles(row)">
              分配角色
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.size"
          :total="pagination.total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <!-- 新增/编辑用户对话框 -->
    <el-dialog v-model="showAddDialog" :title="editingUser ? '编辑用户' : '新增用户'" width="600px" @close="handleDialogClose">
      <el-form ref="userFormRef" :model="userForm" :rules="userRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名" :disabled="!!editingUser" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!editingUser">
          <el-input v-model="userForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="userForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱地址" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="userForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ editingUser ? '更新' : '创建' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 用户详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="用户详情" width="600px">
      <div v-if="selectedUser" class="user-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户名">
            {{ selectedUser.username }}
          </el-descriptions-item>
          <el-descriptions-item label="真实姓名">
            {{ selectedUser.realName }}
          </el-descriptions-item>
          <el-descriptions-item label="邮箱">
            {{ selectedUser.email || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="手机号">
            {{ selectedUser.phone || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="selectedUser.status === 1 ? 'success' : 'danger'" size="small">
              {{ selectedUser.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="最后登录">
            {{ selectedUser.lastLoginTime ? formatDate(selectedUser.lastLoginTime) : '从未登录' }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">
            {{ formatDate(selectedUser.createdTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="更新时间" :span="2">
            {{ formatDate(selectedUser.updatedTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="角色" :span="2">
            <el-tag v-for="role in selectedUser.roles" :key="role.id" size="small" class="role-tag">
              {{ role.roleName }}
            </el-tag>
            <span v-if="!selectedUser.roles || selectedUser.roles.length === 0">
              未分配角色
            </span>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- 分配角色对话框 -->
    <el-dialog v-model="showRoleDialog" title="分配角色" width="500px">
      <div v-if="selectedUser">
        <p class="dialog-desc">为用户 "{{ selectedUser.realName }}" 分配角色：</p>
        <el-checkbox-group v-model="selectedRoleIds">
          <el-checkbox v-for="role in allRoles" :key="role.id" :label="role.id" :disabled="role.status === 0">
            {{ role.roleName }}
            <span class="role-desc">{{ role.description }}</span>
          </el-checkbox>
        </el-checkbox-group>
      </div>

      <template #footer>
        <el-button @click="showRoleDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleRoleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { getUsers, createUser, updateUser, deleteUser } from '@/api/users'
import { getRoles } from '@/api/roles'
import { formatDate } from '@/utils/date'

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const userList = ref([])
const allRoles = ref([])
const selectedUsers = ref([])
const showAddDialog = ref(false)
const showDetailDialog = ref(false)
const showRoleDialog = ref(false)
const editingUser = ref(null)
const selectedUser = ref(null)
const selectedRoleIds = ref([])

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

// 用户表单
const userForm = reactive({
  username: '',
  password: '',
  realName: '',
  email: '',
  phone: '',
  status: 1
})

// 表单引用
const userFormRef = ref(null)

// 表单验证规则
const userRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度在 3 到 50 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 100, message: '密码长度在 6 到 100 个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '姓名长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 方法
const loadUsers = async () => {
  loading.value = true
  try {
    let response
    if (searchForm.keyword) {
      response = await getUsers({ keyword: searchForm.keyword })
    } else {
      response = await getUsers()
    }
    let users = response.data || []

    // 根据状态筛选
    if (searchForm.status !== null) {
      users = users.filter(user => user.status === searchForm.status)
    }

    userList.value = users
    pagination.total = users.length
  } catch (error) {
    console.error('加载用户列表失败:', error)
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

const loadRoles = async () => {
  try {
    const response = await getRoles()
    allRoles.value = response.data || []
  } catch (error) {
    console.error('加载角色列表失败:', error)
    ElMessage.error('加载角色列表失败')
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadUsers()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.status = null
  pagination.page = 1
  loadUsers()
}

const handleSelectionChange = (selection) => {
  selectedUsers.value = selection
}

const handleView = (user) => {
  selectedUser.value = user
  showDetailDialog.value = true
}

const handleEdit = (user) => {
  editingUser.value = user
  Object.assign(userForm, {
    username: user.username,
    password: '',
    realName: user.realName,
    email: user.email || '',
    phone: user.phone || '',
    status: user.status
  })
  showAddDialog.value = true
}

const handleDelete = async (user) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 "${user.realName}" 吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deleteUser(user.id)
    ElMessage.success('删除成功')
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除用户失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const handleStatusChange = async (user) => {
  try {
    await updateUser(user.id, { status: user.status })
    ElMessage.success('状态更新成功')
  } catch (error) {
    console.error('更新状态失败:', error)
    ElMessage.error('状态更新失败')
    // 恢复原状态
    user.status = user.status === 1 ? 0 : 1
  }
}

const handleAssignRoles = async (user) => {
  selectedUser.value = user
  selectedRoleIds.value = user.roles ? user.roles.map(role => role.id) : []
  showRoleDialog.value = true
}

const handleSubmit = async () => {
  if (!userFormRef.value) return

  try {
    await userFormRef.value.validate()

    submitting.value = true

    if (editingUser.value) {
      // 编辑用户
      await updateUser(editingUser.value.id, userForm)
      ElMessage.success('更新成功')
    } else {
      // 新增用户
      await createUser(userForm)
      ElMessage.success('创建成功')
    }

    showAddDialog.value = false
    loadUsers()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

const handleRoleSubmit = async () => {
  if (!selectedUser.value) return

  try {
    submitting.value = true
    await updateUser(selectedUser.value.id, { roleIds: selectedRoleIds.value })
    ElMessage.success('角色分配成功')
    showRoleDialog.value = false
    loadUsers()
  } catch (error) {
    console.error('分配角色失败:', error)
    ElMessage.error('角色分配失败')
  } finally {
    submitting.value = false
  }
}

const handleDialogClose = () => {
  editingUser.value = null
  userFormRef.value?.resetFields()
  Object.assign(userForm, {
    username: '',
    password: '',
    realName: '',
    email: '',
    phone: '',
    status: 1
  })
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadUsers()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadUsers()
}

// 生命周期
onMounted(() => {
  loadUsers()
  loadRoles()
})
</script>

<style lang="scss" scoped>
.users-page {
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
      .user-info {
        display: flex;
        align-items: center;

        .user-avatar {
          margin-right: 12px;
          background-color: var(--el-color-primary);
        }

        .user-name {
          font-weight: 500;
        }
      }

      .role-tag {
        margin-right: 8px;
        margin-bottom: 4px;
      }

      .no-role {
        color: var(--el-text-color-placeholder);
        font-size: 12px;
      }
    }

    .pagination-wrapper {
      padding: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }

  .user-detail {
    .el-descriptions {
      :deep(.el-descriptions__label) {
        font-weight: 500;
      }
    }

    .role-tag {
      margin-right: 8px;
      margin-bottom: 4px;
    }
  }

  .dialog-desc {
    margin-bottom: 20px;
    color: var(--el-text-color-regular);
  }

  :deep(.el-checkbox-group) {
    .el-checkbox {
      display: block;
      margin-bottom: 12px;
      margin-right: 0;

      .role-desc {
        margin-left: 8px;
        color: var(--el-text-color-placeholder);
        font-size: 12px;
      }
    }
  }
}

@media (max-width: 768px) {
  .users-page {
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