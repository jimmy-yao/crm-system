import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'

// 创建axios实例
const service = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    
    console.log('请求拦截器 - URL:', config.url)
    console.log('请求拦截器 - Token状态:', {
      hasToken: !!userStore.token
    })
    
    // 如果有token，使用Bearer认证
    if (userStore.token) {
      config.headers['Authorization'] = `Bearer ${userStore.token}`
      console.log('请求拦截器 - 已添加Bearer认证头')
    } else {
      console.log('请求拦截器 - 缺少token')
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
    console.log('响应错误详情:', {
      url: error.config?.url,
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data
    })
    
    let message = '请求失败'
    
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 401: {
          message = '登录已过期，请重新登录'
          console.log('401错误 - 触发登录跳转')
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