import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getToken } from '@/utils/auth'
import router from '@/router'

// 创建axios实例
const service = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      // 使用Basic Auth方式，这里需要根据后端实际情况调整
      const userStore = useUserStore()
      if (userStore.userInfo) {
        // 假设用户名存储在userInfo中，密码使用token（简化处理）
        const credentials = btoa(`${userStore.userInfo.username}:${token.split('_')[0] || 'admin123'}`)
        config.headers['Authorization'] = `Basic ${credentials}`
      }
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果返回的状态码不是200，说明接口有问题
    if (response.status !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    
    return response
  },
  error => {
    console.error('响应错误:', error)
    
    let message = '请求失败'
    
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 401: {
          message = '登录已过期，请重新登录'
          // 清除token并跳转到登录页
          const userStore = useUserStore()
          userStore.logout()
          router.push('/login')
          break
        }
        case 403:
          message = '没有权限访问该资源'
          break
        case 404:
          message = '请求的资源不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = data?.errorMessage || data?.message || `请求失败 (${status})`
      }
    } else if (error.code === 'ECONNABORTED') {
      message = '请求超时'
    } else if (error.message === 'Network Error') {
      message = '网络连接失败'
    }
    
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default service