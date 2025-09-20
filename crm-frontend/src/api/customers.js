import request from '@/utils/request'

// 获取客户列表
export function getCustomers(page = 1, size = 10, params = {}) {
  return request({
    url: '/customers',
    method: 'get',
    params: { page, size, ...params }
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

// 搜索客户
export function searchCustomers(keyword, page = 1, size = 10) {
  return request({
    url: '/customers/search',
    method: 'get',
    params: { keyword, page, size }
  })
}