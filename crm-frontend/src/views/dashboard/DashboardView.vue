<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :xs="24" :sm="12" :md="6" v-for="stat in stats" :key="stat.key">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" :style="{ backgroundColor: stat.color }">
              <el-icon :size="24">
                <component :is="stat.icon" />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </div>
          <div class="stat-trend" :class="stat.trend > 0 ? 'positive' : 'negative'">
            <el-icon>
              <ArrowUp v-if="stat.trend > 0" />
              <ArrowDown v-else />
            </el-icon>
            <span>{{ Math.abs(stat.trend) }}%</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-section">
      <el-col :xs="24" :lg="16">
        <el-card title="客户增长趋势" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>客户增长趋势</span>
              <el-button-group size="small">
                <el-button 
                  v-for="period in periods" 
                  :key="period.value"
                  :type="selectedPeriod === period.value ? 'primary' : ''"
                  @click="selectedPeriod = period.value"
                >
                  {{ period.label }}
                </el-button>
              </el-button-group>
            </div>
          </template>
          <div ref="customerChart" class="chart-container"></div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :lg="8">
        <el-card title="客户分布" shadow="hover">
          <div ref="distributionChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近活动 -->
    <el-row :gutter="20" class="activity-section">
      <el-col :xs="24" :lg="12">
        <el-card title="最近客户" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最近客户</span>
              <el-button type="text" @click="$router.push('/customers')">
                查看全部
              </el-button>
            </div>
          </template>
          <div class="recent-customers">
            <div 
              v-for="customer in recentCustomers" 
              :key="customer.id"
              class="customer-item"
              @click="viewCustomer(customer.id)"
            >
              <el-avatar :size="40" class="customer-avatar">
                {{ customer.name.charAt(0) }}
              </el-avatar>
              <div class="customer-info">
                <div class="customer-name">{{ customer.name }}</div>
                <div class="customer-meta">
                  <span>{{ customer.phone }}</span>
                  <span class="divider">|</span>
                  <span>{{ formatDate(customer.createdTime) }}</span>
                </div>
              </div>
              <el-tag :type="getCustomerStatus(customer)" size="small">
                {{ getCustomerStatusText(customer) }}
              </el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :lg="12">
        <el-card title="系统通知" shadow="hover">
          <div class="notifications">
            <div 
              v-for="notification in notifications" 
              :key="notification.id"
              class="notification-item"
            >
              <div class="notification-icon" :class="notification.type">
                <el-icon>
                  <component :is="notification.icon" />
                </el-icon>
              </div>
              <div class="notification-content">
                <div class="notification-title">{{ notification.title }}</div>
                <div class="notification-time">{{ formatDate(notification.time) }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  User, 
  UserFilled, 
  TrendCharts, 
  DataAnalysis,
  ArrowUp,
  ArrowDown,
  Bell,
  Warning,
  SuccessFilled
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getCustomers } from '@/api/customers'
import { formatDate } from '@/utils/date'

const router = useRouter()

// 响应式数据
const stats = ref([
  {
    key: 'totalCustomers',
    label: '总客户数',
    value: 0,
    icon: markRaw(User),
    color: '#409EFF',
    trend: 12.5
  },
  {
    key: 'newCustomers',
    label: '本月新增',
    value: 0,
    icon: markRaw(UserFilled),
    color: '#67C23A',
    trend: 8.2
  },
  {
    key: 'activeCustomers',
    label: '活跃客户',
    value: 0,
    icon: markRaw(TrendCharts),
    color: '#E6A23C',
    trend: -2.1
  },
  {
    key: 'conversion',
    label: '转化率',
    value: '0%',
    icon: markRaw(DataAnalysis),
    color: '#F56C6C',
    trend: 5.8
  }
])

const periods = ref([
  { label: '7天', value: '7d' },
  { label: '30天', value: '30d' },
  { label: '90天', value: '90d' }
])

const selectedPeriod = ref('30d')
const recentCustomers = ref([])
const notifications = ref([
  {
    id: 1,
    title: '系统维护通知',
    time: new Date(),
    type: 'warning',
    icon: markRaw(Warning)
  },
  {
    id: 2,
    title: '数据备份完成',
    time: new Date(Date.now() - 2 * 60 * 60 * 1000),
    type: 'success',
    icon: markRaw(SuccessFilled)
  },
  {
    id: 3,
    title: '新用户注册',
    time: new Date(Date.now() - 4 * 60 * 60 * 1000),
    type: 'info',
    icon: markRaw(Bell)
  }
])

// 图表引用
const customerChart = ref(null)
const distributionChart = ref(null)

// 方法
const loadDashboardData = async () => {
  try {
    // 加载客户统计数据
    const response = await getCustomers()
    const customers = response.data || []
    
    // 更新统计数据
    stats.value[0].value = customers.length
    stats.value[1].value = customers.filter(c => {
      const createdDate = new Date(c.createdTime)
      const now = new Date()
      const thisMonth = new Date(now.getFullYear(), now.getMonth(), 1)
      return createdDate >= thisMonth
    }).length
    
    // 获取最近客户
    recentCustomers.value = customers
      .sort((a, b) => new Date(b.createdTime) - new Date(a.createdTime))
      .slice(0, 5)
    
  } catch (error) {
    console.error('加载仪表盘数据失败:', error)
    ElMessage.error('加载数据失败')
  }
}

const initCustomerChart = () => {
  if (!customerChart.value) return
  
  const chart = echarts.init(customerChart.value)
  
  const option = {
    title: {
      show: false
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '新增客户',
        type: 'line',
        stack: 'Total',
        smooth: true,
        lineStyle: {
          color: '#409EFF'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
          ])
        },
        data: [12, 18, 25, 32, 28, 35, 42, 38, 45, 52, 48, 55]
      }
    ]
  }
  
  chart.setOption(option)
  
  // 响应式调整
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

const initDistributionChart = () => {
  if (!distributionChart.value) return
  
  const chart = echarts.init(distributionChart.value)
  
  const option = {
    title: {
      show: false
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '客户分布',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '18',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: 335, name: '重要客户', itemStyle: { color: '#409EFF' } },
          { value: 310, name: '普通客户', itemStyle: { color: '#67C23A' } },
          { value: 234, name: '潜在客户', itemStyle: { color: '#E6A23C' } },
          { value: 135, name: '流失客户', itemStyle: { color: '#F56C6C' } }
        ]
      }
    ]
  }
  
  chart.setOption(option)
  
  // 响应式调整
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

const viewCustomer = (id) => {
  router.push(`/customers/${id}`)
}

const getCustomerStatus = (customer) => {
  // 根据客户创建时间判断状态
  const days = Math.floor((new Date() - new Date(customer.createdTime)) / (1000 * 60 * 60 * 24))
  if (days <= 7) return 'success'
  if (days <= 30) return 'warning'
  return ''
}

const getCustomerStatusText = (customer) => {
  const days = Math.floor((new Date() - new Date(customer.createdTime)) / (1000 * 60 * 60 * 24))
  if (days <= 7) return '新客户'
  if (days <= 30) return '活跃'
  return '普通'
}

// 生命周期
onMounted(async () => {
  await loadDashboardData()
  
  nextTick(() => {
    initCustomerChart()
    initDistributionChart()
  })
})
</script>

<style lang="scss" scoped>
.dashboard {
  padding: 20px;
  
  .stats-cards {
    margin-bottom: 20px;
    
    .stat-card {
      .stat-content {
        display: flex;
        align-items: center;
        margin-bottom: 10px;
        
        .stat-icon {
          width: 50px;
          height: 50px;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          color: white;
          margin-right: 15px;
        }
        
        .stat-info {
          flex: 1;
          
          .stat-value {
            font-size: 24px;
            font-weight: bold;
            color: var(--el-text-color-primary);
            line-height: 1;
          }
          
          .stat-label {
            font-size: 14px;
            color: var(--el-text-color-regular);
            margin-top: 4px;
          }
        }
      }
      
      .stat-trend {
        display: flex;
        align-items: center;
        font-size: 12px;
        
        &.positive {
          color: var(--el-color-success);
        }
        
        &.negative {
          color: var(--el-color-danger);
        }
        
        .el-icon {
          margin-right: 4px;
        }
      }
    }
  }
  
  .charts-section {
    margin-bottom: 20px;
    
    .chart-container {
      height: 300px;
      width: 100%;
    }
  }
  
  .activity-section {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    .recent-customers {
      .customer-item {
        display: flex;
        align-items: center;
        padding: 12px 0;
        border-bottom: 1px solid var(--el-border-color-lighter);
        cursor: pointer;
        transition: background-color 0.3s;
        
        &:hover {
          background-color: var(--el-fill-color-lighter);
          margin: 0 -12px;
          padding: 12px;
          border-radius: 4px;
        }
        
        &:last-child {
          border-bottom: none;
        }
        
        .customer-avatar {
          margin-right: 12px;
          background-color: var(--el-color-primary);
        }
        
        .customer-info {
          flex: 1;
          
          .customer-name {
            font-weight: 500;
            color: var(--el-text-color-primary);
            margin-bottom: 4px;
          }
          
          .customer-meta {
            font-size: 12px;
            color: var(--el-text-color-regular);
            
            .divider {
              margin: 0 8px;
            }
          }
        }
      }
    }
    
    .notifications {
      .notification-item {
        display: flex;
        align-items: center;
        padding: 12px 0;
        border-bottom: 1px solid var(--el-border-color-lighter);
        
        &:last-child {
          border-bottom: none;
        }
        
        .notification-icon {
          width: 32px;
          height: 32px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 12px;
          
          &.warning {
            background-color: var(--el-color-warning-light-9);
            color: var(--el-color-warning);
          }
          
          &.success {
            background-color: var(--el-color-success-light-9);
            color: var(--el-color-success);
          }
          
          &.info {
            background-color: var(--el-color-info-light-9);
            color: var(--el-color-info);
          }
        }
        
        .notification-content {
          flex: 1;
          
          .notification-title {
            font-weight: 500;
            color: var(--el-text-color-primary);
            margin-bottom: 4px;
          }
          
          .notification-time {
            font-size: 12px;
            color: var(--el-text-color-regular);
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .dashboard {
    padding: 10px;
    
    .stats-cards {
      .stat-card {
        margin-bottom: 10px;
      }
    }
    
    .charts-section,
    .activity-section {
      .el-col {
        margin-bottom: 20px;
      }
    }
  }
}
</style>