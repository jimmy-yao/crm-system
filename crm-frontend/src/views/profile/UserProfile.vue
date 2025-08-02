<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人资料</span>
        </div>
      </template>
      
      <div class="profile-content">
        <!-- 头像区域 -->
        <div class="avatar-section">
          <el-avatar :size="120" :src="userInfo.avatar" class="user-avatar">
            <el-icon><User /></el-icon>
          </el-avatar>
          <el-button type="primary" size="small" @click="handleAvatarUpload" class="upload-btn">
            更换头像
          </el-button>
        </div>

        <!-- 基本信息 -->
        <el-form
          ref="profileFormRef"
          :model="userInfo"
          :rules="rules"
          label-width="100px"
          class="profile-form"
        >
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="用户名" prop="username">
                <el-input v-model="userInfo.username" disabled />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="姓名" prop="name">
                <el-input v-model="userInfo.name" :disabled="!isEditing" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="userInfo.email" :disabled="!isEditing" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="userInfo.phone" :disabled="!isEditing" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="部门">
                <el-input v-model="userInfo.department" disabled />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="职位">
                <el-input v-model="userInfo.position" disabled />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="个人简介">
            <el-input
              v-model="userInfo.bio"
              type="textarea"
              :rows="4"
              :disabled="!isEditing"
              placeholder="请输入个人简介"
            />
          </el-form-item>
        </el-form>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <template v-if="!isEditing">
            <el-button type="primary" @click="handleEdit">
              <el-icon><Edit /></el-icon>
              编辑资料
            </el-button>
            <el-button @click="handleChangePassword">
              <el-icon><Lock /></el-icon>
              修改密码
            </el-button>
          </template>
          <template v-else>
            <el-button type="primary" @click="handleSave" :loading="saving">
              <el-icon><Check /></el-icon>
              保存
            </el-button>
            <el-button @click="handleCancel">
              <el-icon><Close /></el-icon>
              取消
            </el-button>
          </template>
        </div>
      </div>
    </el-card>

    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="passwordDialogVisible"
      title="修改密码"
      width="400px"
      :before-close="handlePasswordDialogClose"
    >
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            show-password
            placeholder="请输入原密码"
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            show-password
            placeholder="请输入新密码"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            show-password
            placeholder="请再次输入新密码"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePasswordSubmit" :loading="passwordSaving">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Edit, Lock, Check, Close } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'



// 响应式数据
const isEditing = ref(false)
const saving = ref(false)
const passwordDialogVisible = ref(false)
const passwordSaving = ref(false)

// 表单引用
const profileFormRef = ref()
const passwordFormRef = ref()

// 用户信息
const userInfo = reactive({
  id: '',
  username: '',
  name: '',
  email: '',
  phone: '',
  avatar: '',
  department: '',
  position: '',
  bio: ''
})

// 原始用户信息（用于取消编辑时恢复）
const originalUserInfo = reactive({})

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 获取用户信息
const getUserInfo = async () => {
  try {
    // 这里应该调用API获取用户信息
    // const response = await getUserProfile()
    // Object.assign(userInfo, response.data)
    
    // 模拟数据
    Object.assign(userInfo, {
      id: '1',
      username: 'admin',
      name: '管理员',
      email: 'admin@example.com',
      phone: '13800138000',
      avatar: '',
      department: '技术部',
      position: '系统管理员',
      bio: '负责系统管理和维护工作'
    })
    
    // 保存原始信息
    Object.assign(originalUserInfo, userInfo)
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  }
}

// 编辑资料
const handleEdit = () => {
  isEditing.value = true
}

// 保存资料
const handleSave = async () => {
  try {
    await profileFormRef.value.validate()
    saving.value = true
    
    // 这里应该调用API更新用户信息
    // await updateUserProfile(userInfo)
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    Object.assign(originalUserInfo, userInfo)
    isEditing.value = false
    ElMessage.success('保存成功')
  } catch (error) {
    if (error !== false) {
      ElMessage.error('保存失败')
    }
  } finally {
    saving.value = false
  }
}

// 取消编辑
const handleCancel = () => {
  Object.assign(userInfo, originalUserInfo)
  isEditing.value = false
}

// 头像上传
const handleAvatarUpload = () => {
  ElMessage.info('头像上传功能待实现')
}

// 修改密码
const handleChangePassword = () => {
  passwordDialogVisible.value = true
}

// 密码对话框关闭
const handlePasswordDialogClose = () => {
  passwordFormRef.value?.resetFields()
  Object.assign(passwordForm, {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  })
}

// 提交密码修改
const handlePasswordSubmit = async () => {
  try {
    await passwordFormRef.value.validate()
    passwordSaving.value = true
    
    // 这里应该调用API修改密码
    // await changePassword(passwordForm)
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    passwordDialogVisible.value = false
    handlePasswordDialogClose()
    ElMessage.success('密码修改成功')
  } catch (error) {
    if (error !== false) {
      ElMessage.error('密码修改失败')
    }
  } finally {
    passwordSaving.value = false
  }
}

onMounted(() => {
  getUserInfo()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.profile-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: 600;
}

.profile-content {
  padding: 20px 0;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 40px;
}

.user-avatar {
  margin-bottom: 15px;
  border: 3px solid #f0f0f0;
}

.upload-btn {
  margin-top: 10px;
}

.profile-form {
  margin-bottom: 30px;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 15px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

@media (max-width: 768px) {
  .profile-container {
    padding: 10px;
  }
  
  .action-buttons {
    flex-direction: column;
    align-items: center;
  }
  
  .action-buttons .el-button {
    width: 200px;
  }
}
</style>