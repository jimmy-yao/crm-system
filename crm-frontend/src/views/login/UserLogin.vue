<template>
  <div class="login-container">
    <div class="login-box">
      <!-- Logo和标题 -->
      <div class="login-header">
        <div class="logo">
          <el-icon :size="48" color="#409EFF">
            <Management />
          </el-icon>
        </div>
        <h1 class="title">CRM管理系统</h1>
        <p class="subtitle">客户关系管理平台</p>
      </div>

      <!-- 登录表单 -->
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form" @keyup.enter="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" size="large" :prefix-icon="User" clearable />
        </el-form-item>

        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" size="large" :prefix-icon="Lock"
            show-password clearable />
        </el-form-item>

        <el-form-item>
          <div class="login-options">
            <el-checkbox v-model="rememberMe">记住密码</el-checkbox>
            <el-link type="primary" :underline="false" @click="showForgotPassword">忘记密码？</el-link>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" class="login-button" @click="handleLogin">
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 快速登录 -->
      <div class="quick-login">
        <el-divider>
          <span class="divider-text">快速登录</span>
        </el-divider>
        <div class="quick-accounts">
          <el-button v-for="account in quickAccounts" :key="account.username" :type="account.type" size="small"
            @click="quickLogin(account)">
            {{ account.label }}
          </el-button>
        </div>
      </div>

      <!-- 版权信息 -->
      <div class="login-footer">
        <p>&copy; 2024 CRM管理系统. All rights reserved.</p>
      </div>
    </div>

    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="decoration-circle circle-1"></div>
      <div class="decoration-circle circle-2"></div>
      <div class="decoration-circle circle-3"></div>
    </div>

    <!-- 忘记密码对话框 -->
    <el-dialog
      v-model="forgotPasswordVisible"
      title="忘记密码"
      width="400px"
      :before-close="handleForgotPasswordClose"
    >
      <el-form
        ref="forgotPasswordFormRef"
        :model="forgotPasswordForm"
        :rules="forgotPasswordRules"
        label-width="80px"
      >
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="forgotPasswordForm.email"
            placeholder="请输入注册邮箱"
            :prefix-icon="Message"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="验证码" prop="captcha">
          <div class="captcha-input">
            <el-input
              v-model="forgotPasswordForm.captcha"
              placeholder="请输入验证码"
              :prefix-icon="Key"
              clearable
            />
            <el-button
              type="primary"
              :disabled="captchaDisabled"
              @click="sendCaptcha"
              class="captcha-btn"
            >
              {{ captchaText }}
            </el-button>
          </div>
        </el-form-item>
        
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="forgotPasswordForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            :prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="forgotPasswordForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            :prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="forgotPasswordVisible = false">取消</el-button>
          <el-button type="primary" @click="handleResetPassword" :loading="resetPasswordLoading">
            重置密码
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Management, Message, Key } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import Cookies from 'js-cookie'

const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const loading = ref(false)
const rememberMe = ref(false)

// 忘记密码相关
const forgotPasswordVisible = ref(false)
const resetPasswordLoading = ref(false)
const captchaDisabled = ref(false)
const captchaText = ref('发送验证码')
const captchaCountdown = ref(0)

// 表单引用
const loginFormRef = ref(null)
const forgotPasswordFormRef = ref(null)

// 登录表单
const loginForm = reactive({
  username: '',
  password: ''
})

// 忘记密码表单
const forgotPasswordForm = reactive({
  email: '',
  captcha: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度在 3 到 50 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 100, message: '密码长度在 6 到 100 个字符', trigger: 'blur' }
  ]
}

// 忘记密码表单验证规则
const forgotPasswordRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码长度为6位', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 100, message: '密码长度在 6 到 100 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== forgotPasswordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 快速登录账户
const quickAccounts = [
  {
    username: 'admin',
    password: '123456',
    label: '管理员',
    type: 'danger'
  },
  {
    username: 'manager',
    password: '123456',
    label: '经理',
    type: 'warning'
  },
  {
    username: 'user',
    password: '123456',
    label: '普通用户',
    type: 'success'
  }
]

// 方法
const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    await loginFormRef.value.validate()

    loading.value = true

    // 调用真实的登录API
    await userStore.login(loginForm)

    // 记住密码
    if (rememberMe.value) {
      Cookies.set('username', loginForm.username, { expires: 7 })
      Cookies.set('password', loginForm.password, { expires: 7 })
    } else {
      Cookies.remove('username')
      Cookies.remove('password')
    }

    ElMessage.success('登录成功')

    // 跳转到首页
    router.push('/')

  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error('登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}

const quickLogin = (account) => {
  loginForm.username = account.username
  loginForm.password = account.password
  handleLogin()
}

// 忘记密码相关方法
const showForgotPassword = () => {
  forgotPasswordVisible.value = true
}

const handleForgotPasswordClose = () => {
  forgotPasswordFormRef.value?.resetFields()
  Object.assign(forgotPasswordForm, {
    email: '',
    captcha: '',
    newPassword: '',
    confirmPassword: ''
  })
  resetCaptcha()
}

const sendCaptcha = async () => {
  try {
    // 验证邮箱格式
    if (!forgotPasswordForm.email) {
      ElMessage.error('请先输入邮箱')
      return
    }
    
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!emailRegex.test(forgotPasswordForm.email)) {
      ElMessage.error('请输入正确的邮箱格式')
      return
    }

    // 这里应该调用发送验证码的API
    // await sendResetPasswordCaptcha(forgotPasswordForm.email)
    
    // Mock发送验证码
    ElMessage.success('验证码已发送到您的邮箱，请查收')
    
    // 开始倒计时
    startCaptchaCountdown()
    
  } catch (error) {
    ElMessage.error('发送验证码失败，请稍后重试')
  }
}

const startCaptchaCountdown = () => {
  captchaDisabled.value = true
  captchaCountdown.value = 60
  
  const timer = setInterval(() => {
    captchaCountdown.value--
    captchaText.value = `${captchaCountdown.value}s后重发`
    
    if (captchaCountdown.value <= 0) {
      clearInterval(timer)
      resetCaptcha()
    }
  }, 1000)
}

const resetCaptcha = () => {
  captchaDisabled.value = false
  captchaText.value = '发送验证码'
  captchaCountdown.value = 0
}

const handleResetPassword = async () => {
  if (!forgotPasswordFormRef.value) return
  
  try {
    await forgotPasswordFormRef.value.validate()
    
    resetPasswordLoading.value = true
    
    // 这里应该调用重置密码的API
    // await resetPassword(forgotPasswordForm)
    
    // Mock重置密码
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    ElMessage.success('密码重置成功，请使用新密码登录')
    forgotPasswordVisible.value = false
    
    // 自动填充用户名（从邮箱提取）
    const username = forgotPasswordForm.email.split('@')[0]
    loginForm.username = username
    loginForm.password = ''
    
  } catch (error) {
    if (error !== false) {
      ElMessage.error('密码重置失败，请检查验证码是否正确')
    }
  } finally {
    resetPasswordLoading.value = false
  }
}

const loadRememberedCredentials = () => {
  const savedUsername = Cookies.get('username')
  const savedPassword = Cookies.get('password')

  if (savedUsername && savedPassword) {
    loginForm.username = savedUsername
    loginForm.password = savedPassword
    rememberMe.value = true
  }
}

// 生命周期
onMounted(() => {
  loadRememberedCredentials()
})
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;

  .login-box {
    width: 400px;
    padding: 40px;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 16px;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
    backdrop-filter: blur(10px);
    position: relative;
    z-index: 10;

    .login-header {
      text-align: center;
      margin-bottom: 40px;

      .logo {
        margin-bottom: 16px;
      }

      .title {
        font-size: 28px;
        font-weight: 600;
        color: var(--el-text-color-primary);
        margin: 0 0 8px 0;
      }

      .subtitle {
        font-size: 14px;
        color: var(--el-text-color-regular);
        margin: 0;
      }
    }

    .login-form {
      .el-form-item {
        margin-bottom: 24px;

        &:last-child {
          margin-bottom: 0;
        }
      }

      .login-options {
        display: flex;
        justify-content: space-between;
        align-items: center;
        width: 100%;
      }

      .login-button {
        width: 100%;
        height: 48px;
        font-size: 16px;
        font-weight: 500;
      }
    }

    // 验证码输入框样式
    .captcha-input {
      display: flex;
      gap: 12px;
      
      .el-input {
        flex: 1;
      }
      
      .captcha-btn {
        width: 120px;
        white-space: nowrap;
      }
    }

    .quick-login {
      margin-top: 30px;

      .divider-text {
        font-size: 12px;
        color: var(--el-text-color-placeholder);
      }

      .quick-accounts {
        display: flex;
        justify-content: center;
        gap: 12px;
        margin-top: 16px;
      }
    }

    .login-footer {
      text-align: center;
      margin-top: 30px;

      p {
        font-size: 12px;
        color: var(--el-text-color-placeholder);
        margin: 0;
      }
    }
  }

  .background-decoration {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;

    .decoration-circle {
      position: absolute;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.1);
      animation: float 6s ease-in-out infinite;

      &.circle-1 {
        width: 200px;
        height: 200px;
        top: 10%;
        left: 10%;
        animation-delay: 0s;
      }

      &.circle-2 {
        width: 150px;
        height: 150px;
        top: 60%;
        right: 15%;
        animation-delay: 2s;
      }

      &.circle-3 {
        width: 100px;
        height: 100px;
        bottom: 20%;
        left: 20%;
        animation-delay: 4s;
      }
    }
  }
}

@keyframes float {

  0%,
  100% {
    transform: translateY(0px);
  }

  50% {
    transform: translateY(-20px);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .login-container {
    padding: 20px;

    .login-box {
      width: 100%;
      max-width: 360px;
      padding: 30px 20px;

      .login-header {
        .title {
          font-size: 24px;
        }
      }

      .quick-login {
        .quick-accounts {
          flex-direction: column;
          gap: 8px;

          .el-button {
            width: 100%;
          }
        }
      }
    }
  }
}

// 暗色主题适配
@media (prefers-color-scheme: dark) {
  .login-container {
    background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);

    .login-box {
      background: rgba(30, 30, 30, 0.95);
      color: var(--el-text-color-primary);
    }
  }
}
</style>