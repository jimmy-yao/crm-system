<template>
  <div class="test-page">
    <h1>测试页面</h1>
    <div class="auth-info">
      <h3>认证状态</h3>
      <p>Token: {{ userStore.token ? '有' : '无' }}</p>
      <p>用户名: {{ userStore.credentials.username || '无' }}</p>
      <p>密码: {{ userStore.credentials.password ? '有' : '无' }}</p>
      <p>用户信息: {{ userStore.userInfo ? userStore.userInfo.realName : '无' }}</p>
    </div>
    
    <div class="actions">
      <button @click="checkAuth">检查认证状态</button>
      <button @click="testAPI">测试API调用</button>
      <button @click="clearAuth">清除认证</button>
    </div>
    
    <div class="result" v-if="result">
      <h3>结果</h3>
      <pre>{{ result }}</pre>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { getCustomers } from '@/api/customers'

const userStore = useUserStore()
const result = ref('')

const checkAuth = () => {
  const authState = {
    token: userStore.token,
    credentials: userStore.credentials,
    userInfo: userStore.userInfo,
    sessionStorage: sessionStorage.getItem('crm_credentials'),
    cookieToken: document.cookie.includes('crm-token')
  }
  result.value = JSON.stringify(authState, null, 2)
}

const testAPI = async () => {
  try {
    const response = await getCustomers()
    result.value = 'API调用成功: ' + JSON.stringify(response.data, null, 2)
  } catch (error) {
    result.value = 'API调用失败: ' + error.message
  }
}

const clearAuth = () => {
  userStore.logout()
  result.value = '认证已清除'
}
</script>

<style scoped>
.test-page {
  padding: 20px;
}

.auth-info, .actions, .result {
  margin: 20px 0;
  padding: 15px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  margin: 5px;
  padding: 8px 16px;
}

pre {
  background: #f5f5f5;
  padding: 10px;
  overflow: auto;
}
</style>