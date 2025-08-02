<template>
  <div class="app-layout">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside :width="sidebarWidth" class="sidebar-container">
        <div class="sidebar-header">
          <div class="logo" :class="{ collapsed: isCollapsed }">
            <el-icon :size="32" color="#409EFF">
              <Management />
            </el-icon>
            <span v-show="!isCollapsed" class="logo-text">CRM系统</span>
          </div>
        </div>
        
        <el-scrollbar class="sidebar-scrollbar">
          <el-menu
            :default-active="activeMenu"
            :collapse="isCollapsed"
            :unique-opened="true"
            router
            class="sidebar-menu"
          >
            <template v-for="item in menuList" :key="item.path">
              <!-- 单级菜单 -->
              <el-menu-item
                v-if="!item.children"
                :index="item.path"
                @click="handleMenuClick(item)"
              >
                <el-icon>
                  <component :is="item.icon" />
                </el-icon>
                <template #title>{{ item.title }}</template>
              </el-menu-item>
              
              <!-- 多级菜单 -->
              <el-sub-menu v-else :index="item.path">
                <template #title>
                  <el-icon>
                    <component :is="item.icon" />
                  </el-icon>
                  <span>{{ item.title }}</span>
                </template>
                <el-menu-item
                  v-for="child in item.children"
                  :key="child.path"
                  :index="child.path"
                  @click="handleMenuClick(child)"
                >
                  <el-icon>
                    <component :is="child.icon" />
                  </el-icon>
                  <template #title>{{ child.title }}</template>
                </el-menu-item>
              </el-sub-menu>
            </template>
          </el-menu>
        </el-scrollbar>
      </el-aside>

      <!-- 主内容区 -->
      <el-container>
        <!-- 顶部导航栏 -->
        <el-header class="header-container">
          <div class="header-left">
            <el-button
              type="text"
              :icon="isCollapsed ? Expand : Fold"
              @click="toggleSidebar"
              class="collapse-btn"
            />
            <el-breadcrumb separator="/" class="breadcrumb">
              <el-breadcrumb-item
                v-for="item in breadcrumbList"
                :key="item.path"
                :to="item.path"
              >
                {{ item.title }}
              </el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          
          <div class="header-right">
            <!-- 全屏切换 -->
            <el-tooltip :content="isFullscreen ? '退出全屏' : '全屏'" placement="bottom">
              <el-button
                type="text"
                :icon="FullScreen"
                @click="toggleFullscreen"
                class="header-btn"
              />
            </el-tooltip>
            
            <!-- 主题切换 -->
            <el-tooltip content="主题切换" placement="bottom">
              <el-button
                type="text"
                :icon="isDark ? Sunny : Moon"
                @click="toggleTheme"
                class="header-btn"
              />
            </el-tooltip>
            
            <!-- 用户菜单 -->
            <el-dropdown @command="handleUserCommand" class="user-dropdown">
              <div class="user-info">
                <el-avatar :size="32" class="user-avatar">
                  {{ userStore.userInfo?.realName?.charAt(0) || 'U' }}
                </el-avatar>
                <span class="user-name">{{ userStore.userInfo?.realName || '用户' }}</span>
                <el-icon class="dropdown-icon">
                  <ArrowDown />
                </el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    个人中心
                  </el-dropdown-item>
                  <el-dropdown-item command="settings">
                    <el-icon><Setting /></el-icon>
                    系统设置
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 标签页导航 -->
        <div class="tabs-container" v-if="showTabs">
          <el-tabs
            v-model="activeTab"
            type="card"
            closable
            @tab-remove="removeTab"
            @tab-click="handleTabClick"
            class="route-tabs"
          >
            <el-tab-pane
              v-for="tab in tabList"
              :key="tab.path"
              :label="tab.title"
              :name="tab.path"
              :closable="tab.path !== '/'"
            />
          </el-tabs>
        </div>

        <!-- 主要内容 -->
        <el-main class="main-container">
          <router-view v-slot="{ Component, route }">
            <transition name="fade-transform" mode="out-in">
              <keep-alive :include="cachedViews">
                <component :is="Component" :key="route.path" />
              </keep-alive>
            </transition>
          </router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, markRaw } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Management,
  Fold,
  Expand,
  FullScreen,
  Sunny,
  Moon,
  User,
  Setting,
  SwitchButton,
  ArrowDown,
  House,
  UserFilled,
  Avatar,
  Key
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const appStore = useAppStore()

// 响应式数据
const isCollapsed = ref(false)
const isFullscreen = ref(false)
const isDark = ref(false)
const showTabs = ref(true)
const activeTab = ref('/')
const tabList = ref([
  { path: '/', title: '首页', closable: false }
])
const cachedViews = ref(['Dashboard'])

// 计算属性
const sidebarWidth = computed(() => isCollapsed.value ? '64px' : '200px')
const activeMenu = computed(() => route.path)

const breadcrumbList = computed(() => {
  const matched = route.matched.filter(item => item.meta && item.meta.title)
  return matched.map(item => ({
    path: item.path,
    title: item.meta.title
  }))
})

// 菜单配置
const menuList = ref([
  {
    path: '/',
    title: '首页',
    icon: markRaw(House)
  },
  {
    path: '/customers',
    title: '客户管理',
    icon: markRaw(UserFilled)
  },
  {
    path: '/system',
    title: '系统管理',
    icon: markRaw(Setting),
    children: [
      {
        path: '/users',
        title: '用户管理',
        icon: markRaw(User)
      },
      {
        path: '/roles',
        title: '角色管理',
        icon: markRaw(Avatar)
      },
      {
        path: '/permissions',
        title: '权限管理',
        icon: markRaw(Key)
      }
    ]
  }
])

// 方法
const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
  appStore.setSidebarCollapsed(isCollapsed.value)
}

const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
    isFullscreen.value = true
  } else {
    document.exitFullscreen()
    isFullscreen.value = false
  }
}

const toggleTheme = () => {
  isDark.value = !isDark.value
  document.documentElement.classList.toggle('dark', isDark.value)
  appStore.setDarkMode(isDark.value)
}

const handleMenuClick = (menuItem) => {
  // 添加到标签页
  addTab({
    path: menuItem.path,
    title: menuItem.title
  })
}

const addTab = (tab) => {
  const existingTab = tabList.value.find(item => item.path === tab.path)
  if (!existingTab) {
    tabList.value.push(tab)
  }
  activeTab.value = tab.path
}

const removeTab = (targetPath) => {
  const tabs = tabList.value
  let activePath = activeTab.value
  
  if (activePath === targetPath) {
    tabs.forEach((tab, index) => {
      if (tab.path === targetPath) {
        const nextTab = tabs[index + 1] || tabs[index - 1]
        if (nextTab) {
          activePath = nextTab.path
        }
      }
    })
  }
  
  activeTab.value = activePath
  tabList.value = tabs.filter(tab => tab.path !== targetPath)
  
  if (activePath !== route.path) {
    router.push(activePath)
  }
}

const handleTabClick = (tab) => {
  router.push(tab.props.name)
}

const handleUserCommand = async (command) => {
  switch (command) {
    case 'profile':
      ElMessage.info('个人中心功能开发中...')
      break
    case 'settings':
      ElMessage.info('系统设置功能开发中...')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await userStore.logout()
        router.push('/login')
        ElMessage.success('退出登录成功')
      } catch (error) {
        // 用户取消
      }
      break
  }
}

// 监听路由变化
watch(route, (newRoute) => {
  // 更新活跃标签
  activeTab.value = newRoute.path
  
  // 自动添加标签页
  if (newRoute.meta && newRoute.meta.title) {
    addTab({
      path: newRoute.path,
      title: newRoute.meta.title
    })
  }
}, { immediate: true })

// 监听全屏状态变化
const handleFullscreenChange = () => {
  isFullscreen.value = !!document.fullscreenElement
}

// 生命周期
onMounted(() => {
  // 恢复应用状态
  isCollapsed.value = appStore.sidebarCollapsed
  isDark.value = appStore.darkMode
  
  // 应用主题
  document.documentElement.classList.toggle('dark', isDark.value)
  
  // 监听全屏事件
  document.addEventListener('fullscreenchange', handleFullscreenChange)
})
</script>

<style lang="scss" scoped>
.app-layout {
  height: 100vh;
  
  .el-container {
    height: 100%;
  }
  
  .sidebar-container {
    background: var(--el-bg-color);
    border-right: 1px solid var(--el-border-color-light);
    transition: width 0.3s;
    
    .sidebar-header {
      height: 60px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-bottom: 1px solid var(--el-border-color-lighter);
      
      .logo {
        display: flex;
        align-items: center;
        transition: all 0.3s;
        
        &.collapsed {
          justify-content: center;
        }
        
        .logo-text {
          margin-left: 12px;
          font-size: 18px;
          font-weight: 600;
          color: var(--el-text-color-primary);
        }
      }
    }
    
    .sidebar-scrollbar {
      height: calc(100% - 60px);
      
      .sidebar-menu {
        border-right: none;
        
        :deep(.el-menu-item),
        :deep(.el-sub-menu__title) {
          height: 48px;
          line-height: 48px;
          
          &:hover {
            background-color: var(--el-color-primary-light-9);
          }
          
          &.is-active {
            background-color: var(--el-color-primary-light-8);
            color: var(--el-color-primary);
            
            &::before {
              content: '';
              position: absolute;
              right: 0;
              top: 0;
              bottom: 0;
              width: 3px;
              background-color: var(--el-color-primary);
            }
          }
        }
      }
    }
  }
  
  .header-container {
    background: var(--el-bg-color);
    border-bottom: 1px solid var(--el-border-color-light);
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 20px;
    
    .header-left {
      display: flex;
      align-items: center;
      
      .collapse-btn {
        margin-right: 20px;
        font-size: 18px;
      }
      
      .breadcrumb {
        :deep(.el-breadcrumb__item) {
          .el-breadcrumb__inner {
            color: var(--el-text-color-regular);
            
            &:hover {
              color: var(--el-color-primary);
            }
          }
          
          &:last-child {
            .el-breadcrumb__inner {
              color: var(--el-text-color-primary);
              font-weight: 500;
            }
          }
        }
      }
    }
    
    .header-right {
      display: flex;
      align-items: center;
      gap: 12px;
      
      .header-btn {
        font-size: 18px;
        
        &:hover {
          color: var(--el-color-primary);
        }
      }
      
      .user-dropdown {
        .user-info {
          display: flex;
          align-items: center;
          cursor: pointer;
          padding: 8px 12px;
          border-radius: 6px;
          transition: background-color 0.3s;
          
          &:hover {
            background-color: var(--el-fill-color-light);
          }
          
          .user-avatar {
            background-color: var(--el-color-primary);
          }
          
          .user-name {
            margin: 0 8px;
            font-weight: 500;
          }
          
          .dropdown-icon {
            font-size: 12px;
            transition: transform 0.3s;
          }
        }
      }
    }
  }
  
  .tabs-container {
    background: var(--el-bg-color);
    border-bottom: 1px solid var(--el-border-color-light);
    padding: 0 20px;
    
    .route-tabs {
      :deep(.el-tabs__header) {
        margin: 0;
        
        .el-tabs__nav {
          border: none;
        }
        
        .el-tabs__item {
          border: 1px solid var(--el-border-color-light);
          border-bottom: none;
          margin-right: 4px;
          
          &.is-active {
            background-color: var(--el-color-primary-light-9);
            color: var(--el-color-primary);
            border-color: var(--el-color-primary-light-7);
          }
        }
      }
    }
  }
  
  .main-container {
    background: var(--el-bg-color-page);
    overflow: auto;
  }
}

// 页面切换动画
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}

// 响应式设计
@media (max-width: 768px) {
  .app-layout {
    .sidebar-container {
      position: fixed;
      top: 0;
      left: 0;
      z-index: 1000;
      height: 100vh;
    }
    
    .header-container {
      .header-left {
        .breadcrumb {
          display: none;
        }
      }
      
      .header-right {
        .user-info {
          .user-name {
            display: none;
          }
        }
      }
    }
    
    .tabs-container {
      display: none;
    }
  }
}
</style>