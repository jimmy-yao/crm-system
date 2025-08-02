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
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/NotFoundPage.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  NProgress.start()

  const userStore = useUserStore()
  const token = userStore.token

  if (to.meta.requiresAuth !== false && !token) {
    // 需要登录但没有token，跳转到登录页
    next('/login')
  } else if (to.path === '/login' && token) {
    // 已登录用户访问登录页，跳转到首页
    next('/')
  } else {
    // 设置页面标题
    if (to.meta.title) {
      document.title = `${to.meta.title} - CRM管理系统`
    }
    next()
  }
})

router.afterEach(() => {
  NProgress.done()
})

export default router