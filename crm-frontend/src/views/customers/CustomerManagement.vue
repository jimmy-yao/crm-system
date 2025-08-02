<template>
  <div class="customers-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>客户管理</h2>
        <p>管理和维护客户信息</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="showAddDialog = true">
          <el-icon><Plus /></el-icon>
          新增客户
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="搜索">
          <el-input
            v-model="searchForm.keyword"
            placeholder="输入客户姓名、手机号或邮箱"
            clearable
            style="width: 300px"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 客户列表 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="customerList"
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="客户姓名" min-width="120">
          <template #default="{ row }">
            <div class="customer-info">
              <el-avatar :size="32" class="customer-avatar">
                {{ row.name.charAt(0) }}
              </el-avatar>
              <span class="customer-name">{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createdTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdTime) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getCustomerStatus(row)" size="small">
              {{ getCustomerStatusText(row) }}
            </el-tag>
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

    <!-- 新增/编辑客户对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="editingCustomer ? '编辑客户' : '新增客户'"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="customerFormRef"
        :model="customerForm"
        :rules="customerRules"
        label-width="80px"
      >
        <el-form-item label="客户姓名" prop="name">
          <el-input v-model="customerForm.name" placeholder="请输入客户姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="customerForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="customerForm.email" placeholder="请输入邮箱地址" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="customerForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入客户描述"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ editingCustomer ? '更新' : '创建' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 客户详情对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      title="客户详情"
      width="600px"
    >
      <div v-if="selectedCustomer" class="customer-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="客户姓名">
            {{ selectedCustomer.name }}
          </el-descriptions-item>
          <el-descriptions-item label="手机号">
            {{ selectedCustomer.phone || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="邮箱">
            {{ selectedCustomer.email || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getCustomerStatus(selectedCustomer)" size="small">
              {{ getCustomerStatusText(selectedCustomer) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">
            {{ formatDate(selectedCustomer.createdTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="更新时间" :span="2">
            {{ formatDate(selectedCustomer.updatedTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">
            {{ selectedCustomer.description || '-' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { getCustomers, createCustomer, updateCustomer, deleteCustomer } from '@/api/customers'
import { formatDate } from '@/utils/date'

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const customerList = ref([])
const selectedCustomers = ref([])
const showAddDialog = ref(false)
const showDetailDialog = ref(false)
const editingCustomer = ref(null)
const selectedCustomer = ref(null)

// 搜索表单
const searchForm = reactive({
  keyword: ''
})

// 分页
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 客户表单
const customerForm = reactive({
  name: '',
  phone: '',
  email: '',
  description: ''
})

// 表单引用
const customerFormRef = ref(null)

// 表单验证规则
const customerRules = {
  name: [
    { required: true, message: '请输入客户姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '姓名长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  description: [
    { max: 500, message: '描述不能超过500个字符', trigger: 'blur' }
  ]
}

// 方法
const loadCustomers = async () => {
  loading.value = true
  try {
    let response
    if (searchForm.keyword) {
      response = await getCustomers({ keyword: searchForm.keyword })
    } else {
      response = await getCustomers()
    }
    const customers = response.data || []
    
    customerList.value = customers
    pagination.total = customers.length
  } catch (error) {
    console.error('加载客户列表失败:', error)
    ElMessage.error('加载客户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadCustomers()
}

const handleReset = () => {
  searchForm.keyword = ''
  pagination.page = 1
  loadCustomers()
}

const handleSelectionChange = (selection) => {
  selectedCustomers.value = selection
}

const handleView = (customer) => {
  selectedCustomer.value = customer
  showDetailDialog.value = true
}

const handleEdit = (customer) => {
  editingCustomer.value = customer
  Object.assign(customerForm, {
    name: customer.name,
    phone: customer.phone || '',
    email: customer.email || '',
    description: customer.description || ''
  })
  showAddDialog.value = true
}

const handleDelete = async (customer) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除客户 "${customer.name}" 吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteCustomer(customer.id)
    ElMessage.success('删除成功')
    loadCustomers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除客户失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  if (!customerFormRef.value) return
  
  try {
    await customerFormRef.value.validate()
    
    submitting.value = true
    
    if (editingCustomer.value) {
      // 编辑客户
      await updateCustomer(editingCustomer.value.id, customerForm)
      ElMessage.success('更新成功')
    } else {
      // 新增客户
      await createCustomer(customerForm)
      ElMessage.success('创建成功')
    }
    
    showAddDialog.value = false
    loadCustomers()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

const handleDialogClose = () => {
  editingCustomer.value = null
  customerFormRef.value?.resetFields()
  Object.assign(customerForm, {
    name: '',
    phone: '',
    email: '',
    description: ''
  })
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadCustomers()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadCustomers()
}

const getCustomerStatus = (customer) => {
  const days = Math.floor((new Date() - new Date(customer.createdTime)) / (1000 * 60 * 60 * 24))
  if (days <= 7) return 'success'
  if (days <= 30) return 'warning'
  return ''
}

const getCustomerStatusText = (customer) => {
  const days = Math.floor((new Date() - new Date(customer.createdTime)) / (1000 * 60 * 60 * 24))
  if (days <= 7) return '新客户'
  if (days <= 30) return '活跃'
  return '普通'
}

// 生命周期
onMounted(() => {
  loadCustomers()
})
</script>

<style lang="scss" scoped>
.customers-page {
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
      .customer-info {
        display: flex;
        align-items: center;
        
        .customer-avatar {
          margin-right: 12px;
          background-color: var(--el-color-primary);
        }
        
        .customer-name {
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
  
  .customer-detail {
    .el-descriptions {
      :deep(.el-descriptions__label) {
        font-weight: 500;
      }
    }
  }
}

@media (max-width: 768px) {
  .customers-page {
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
          
          .el-input {
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