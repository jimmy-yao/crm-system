import request from '@/utils/request'

// 获取客户列表
export function getCustomers(params) {
  return request({
    url: '/customers',
    method: 'get',
    params
  })
}

// 搜索客户
export function searchCustomers(keyword) {
  return request({
    url: '/customers/search',
    method: 'get',
    params: { keyword }
  })
}

// 获取客户详情
export function getCustomer(id) {
  return request({
    url: `/customers/${id}`,
    method: 'get'
  })
}

// 创建客户
export function createCustomer(data) {
  return request({
    url: '/customers',
    method: 'post',
    data
  })
}

// 更新客户
export function updateCustomer(id, data) {
  return request({
    url: `/customers/${id}`,
    method: 'put',
    data
  })
}

// 删除客户
export function deleteCustomer(id) {
  return request({
    url: `/customers/${id}`,
    method: 'delete'
  })
}