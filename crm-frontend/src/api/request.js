import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'

// 创建axios实例
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    
    // 添加认证头
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    
    // 如果有用户名和密码，使用Basic认证
    if (userStore.username && userStore.password) {
      const credentials = btoa(`${userStore.username}:${userStore.password}`)
      config.headers.Authorization = `Basic ${credentials}`
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
    
    // 如果是文件下载，直接返回
    if (response.config.responseType === 'blob') {
      return response
    }
    
    return res
  },
  error => {
    console.error('响应错误:', error)
    
    const { response } = error
    let message = '请求失败'
    
    if (response) {
      switch (response.status) {
        case 400:
          message = response.data?.errorMessage || '请求参数错误'
          break
        case 401: {
          message = '认证失败，请重新登录'
          const userStore = useUserStore()
          userStore.logout()
          router.push('/login')
          break
        }
        case 403:
          message = '权限不足，拒绝访问'
          break
        case 404:
          message = '请求的资源不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = response.data?.errorMessage || `请求失败 (${response.status})`
      }
    } else if (error.code === 'ECONNABORTED') {
      message = '请求超时，请稍后重试'
    } else if (error.message === 'Network Error') {
      message = '网络连接失败，请检查网络'
    }
    
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default service