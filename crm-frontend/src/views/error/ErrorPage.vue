<template>
  <div class="error-page">
    <div class="error-container">
      <div class="error-code">500</div>
      <div class="error-message">服务器错误</div>
      <div class="error-description">
        抱歉，服务器出现了一些问题，请稍后再试
      </div>
      <div class="error-actions">
        <el-button type="primary" @click="goHome">
          <el-icon><HomeFilled /></el-icon>
          返回首页
        </el-button>
        <el-button @click="refresh">
          <el-icon><Refresh /></el-icon>
          刷新页面
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { HomeFilled, Refresh } from '@element-plus/icons-vue'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const refresh = () => {
  window.location.reload()
}
</script>

<style scoped>
.error-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.error-container {
  text-align: center;
  padding: 60px 40px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  max-width: 500px;
  width: 90%;
}

.error-code {
  font-size: 120px;
  font-weight: bold;
  color: #f56c6c;
  margin-bottom: 20px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.error-message {
  font-size: 28px;
  color: #303133;
  margin-bottom: 15px;
  font-weight: 600;
}

.error-description {
  font-size: 16px;
  color: #606266;
  margin-bottom: 40px;
  line-height: 1.6;
}

.error-actions {
  display: flex;
  justify-content: center;
  gap: 15px;
  flex-wrap: wrap;
}

.error-actions .el-button {
  padding: 12px 24px;
  font-size: 16px;
}

@media (max-width: 768px) {
  .error-container {
    padding: 40px 20px;
  }
  
  .error-code {
    font-size: 80px;
  }
  
  .error-message {
    font-size: 24px;
  }
  
  .error-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .error-actions .el-button {
    width: 200px;
  }
}
</style>