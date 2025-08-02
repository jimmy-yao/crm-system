import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  // 侧边栏状态
  const sidebarCollapsed = ref(false)
  
  // 主题模式
  const darkMode = ref(false)
  
  // 设备类型
  const device = ref('desktop') // desktop, tablet, mobile
  
  // 页面加载状态
  const loading = ref(false)
  
  // 标签页设置
  const showTabs = ref(true)
  const cachedViews = ref(['Dashboard'])
  
  // 应用设置
  const settings = ref({
    title: 'CRM管理系统',
    logo: '',
    fixedHeader: true,
    showLogo: true,
    showBreadcrumb: true,
    showTags: true,
    showFooter: false,
    menuAccordion: true,
    enableTransition: true,
    transitionName: 'fade-transform'
  })

  // Actions
  const setSidebarCollapsed = (collapsed) => {
    sidebarCollapsed.value = collapsed
    // 可以在这里添加本地存储逻辑
    localStorage.setItem('sidebarCollapsed', JSON.stringify(collapsed))
  }

  const setDarkMode = (dark) => {
    darkMode.value = dark
    // 保存到本地存储
    localStorage.setItem('darkMode', JSON.stringify(dark))
    
    // 应用主题类
    if (dark) {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
  }

  const setDevice = (deviceType) => {
    device.value = deviceType
  }

  const setLoading = (loadingState) => {
    loading.value = loadingState
  }

  const toggleSidebar = () => {
    setSidebarCollapsed(!sidebarCollapsed.value)
  }

  const toggleDarkMode = () => {
    setDarkMode(!darkMode.value)
  }

  const addCachedView = (viewName) => {
    if (!cachedViews.value.includes(viewName)) {
      cachedViews.value.push(viewName)
    }
  }

  const removeCachedView = (viewName) => {
    const index = cachedViews.value.indexOf(viewName)
    if (index > -1) {
      cachedViews.value.splice(index, 1)
    }
  }

  const clearCachedViews = () => {
    cachedViews.value = []
  }

  const updateSettings = (newSettings) => {
    settings.value = { ...settings.value, ...newSettings }
    // 保存到本地存储
    localStorage.setItem('appSettings', JSON.stringify(settings.value))
  }

  // 初始化应用状态（从本地存储恢复）
  const initializeApp = () => {
    try {
      // 恢复侧边栏状态
      const savedSidebarState = localStorage.getItem('sidebarCollapsed')
      if (savedSidebarState !== null) {
        sidebarCollapsed.value = JSON.parse(savedSidebarState)
      }

      // 恢复主题模式
      const savedDarkMode = localStorage.getItem('darkMode')
      if (savedDarkMode !== null) {
        const isDark = JSON.parse(savedDarkMode)
        setDarkMode(isDark)
      }

      // 恢复应用设置
      const savedSettings = localStorage.getItem('appSettings')
      if (savedSettings) {
        settings.value = { ...settings.value, ...JSON.parse(savedSettings) }
      }

      // 检测设备类型
      const checkDevice = () => {
        const width = window.innerWidth
        if (width < 768) {
          setDevice('mobile')
        } else if (width < 1024) {
          setDevice('tablet')
        } else {
          setDevice('desktop')
        }
      }

      checkDevice()
      window.addEventListener('resize', checkDevice)

    } catch (error) {
      console.error('初始化应用状态失败:', error)
    }
  }

  // 重置应用状态
  const resetApp = () => {
    sidebarCollapsed.value = false
    darkMode.value = false
    device.value = 'desktop'
    loading.value = false
    showTabs.value = true
    cachedViews.value = ['Dashboard']
    
    // 清除本地存储
    localStorage.removeItem('sidebarCollapsed')
    localStorage.removeItem('darkMode')
    localStorage.removeItem('appSettings')
  }

  return {
    // State
    sidebarCollapsed,
    darkMode,
    device,
    loading,
    showTabs,
    cachedViews,
    settings,
    
    // Actions
    setSidebarCollapsed,
    setDarkMode,
    setDevice,
    setLoading,
    toggleSidebar,
    toggleDarkMode,
    addCachedView,
    removeCachedView,
    clearCachedViews,
    updateSettings,
    initializeApp,
    resetApp
  }
})