import { defineStore } from 'pinia'
import { login, getUserInfo } from '@/api/auth'
import { getToken, setToken, removeToken } from '@/utils/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    userInfo: null,
    roles: [],
    permissions: []
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    hasRole: (state) => (role) => state.roles.includes(role),
    hasPermission: (state) => (permission) => state.permissions.includes(permission)
  },

  actions: {
    // 登录
    async login(loginForm) {
      const response = await login(loginForm)
      const { token, userInfo, roles, permissions } = response.data
      
      this.token = token
      this.userInfo = userInfo
      this.roles = roles || []
      this.permissions = permissions || []
      
      setToken(token)
      return response
    },

    // 获取用户信息
    async getUserInfo() {
      const response = await getUserInfo()
      const { userInfo, roles, permissions } = response.data
      
      this.userInfo = userInfo
      this.roles = roles || []
      this.permissions = permissions || []
      
      return response
    },

    // 登出
    logout() {
      this.token = ''
      this.userInfo = null
      this.roles = []
      this.permissions = []
      removeToken()
    },

    // 重置状态
    resetState() {
      this.token = ''
      this.userInfo = null
      this.roles = []
      this.permissions = []
    }
  }
})