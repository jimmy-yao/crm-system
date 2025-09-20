import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

// 配置NProgress
NProgress.configure({ showSpinner: false })

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/UserLogin.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/AppLayout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/DashboardView.vue'),
        meta: { title: '仪表盘', icon: 'Odometer', requiresAuth: true }
      },
      {
        path: 'customers',
        name: 'Customers',
        component: () => import('@/views/customers/CustomerManagement.vue'),
        meta: { title: '客户管理', icon: 'User', requiresAuth: true }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/users/UserManagement.vue'),
        meta: { title: '用户管理', icon: 'UserFilled', requiresAuth: true }
      },
      {
        path: 'roles',
        name: 'Roles',
        component: () => import('@/views/roles/RoleManagement.vue'),
        meta: { title: '角色管理', icon: 'Avatar', requiresAuth: true }
      },
      {
        path: 'permissions',
        name: 'Permissions',
        component: () => import('@/views/permissions/PermissionManagement.vue'),
        meta: { title: '权限管理', icon: 'Key', requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/UserProfile.vue'),
        meta: { title: '个人中心', icon: 'Setting', requiresAuth: true }
      },
      {
        path: 'test',
        name: 'Test',
        component: () => import('@/views/test/TestPage.vue'),
        meta: { title: '测试页面', requiresAuth: true }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/NotFoundPage.vue'),
    meta: { title: '页面不存在', requiresAuth: false }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置路由守卫
router.beforeEach(async (to, from, next) => {
  NProgress.start()

  const userStore = useUserStore()
  const hasToken = userStore.token

  if (hasToken) {
    // 如果已登录
    if (to.path === '/login') {
      // 如果要去登录页，则重定向到首页
      next({ path: '/' })
    } else {
      // 如果要去其他页面
      const hasUserInfo = userStore.userInfo
      if (hasUserInfo) {
        // 如果已有用户信息，则直接放行
        next()
      } else {
        // 如果没有用户信息（例如刷新页面），则去获取
        try {
          await userStore.getUserInfo()
          // 获取成功后，重新进入导航，使用replace避免历史记录中出现循环
          next({ ...to, replace: true })
        } catch (error) {
          // 如果获取用户信息失败（例如token失效），则登出并重定向到登录页
          console.error('获取用户信息失败:', error)
          userStore.logout()
          next('/login')
        }
      }
    }
  } else {
    // 如果未登录
    if (to.meta.requiresAuth) {
      // 如果目标页面需要认证，则重定向到登录页
      next('/login')
    } else {
      // 如果目标页面不需要认证，则直接放行
      next()
    }
  }
})

router.afterEach((to) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - CRM管理系统`
  }
  NProgress.done()
})

export default router
