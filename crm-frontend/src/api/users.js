import request from '@/utils/request'

// 获取用户列表
export function getUsers(params) {
  return request({
    url: '/users',
    method: 'get',
    params
  })
}

// 搜索用户
export function searchUsers(keyword) {
  return request({
    url: '/users/search',
    method: 'get',
    params: { keyword }
  })
}

// 获取用户详情
export function getUser(id) {
  return request({
    url: `/users/${id}`,
    method: 'get'
  })
}

// 创建用户
export function createUser(data) {
  return request({
    url: '/users',
    method: 'post',
    data
  })
}

// 更新用户
export function updateUser(id, data) {
  return request({
    url: `/users/${id}`,
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/users/${id}`,
    method: 'delete'
  })
}

// 更新用户状态
export function updateUserStatus(id, status) {
  return request({
    url: `/users/${id}/status`,
    method: 'put',
    params: { status }
  })
}

// 为用户分配角色
export function assignRoles(id, roleIds) {
  return request({
    url: `/users/${id}/roles`,
    method: 'post',
    data: roleIds
  })
}

// 修改密码
export function changePassword(id, oldPassword, newPassword) {
  return request({
    url: `/users/${id}/password`,
    method: 'put',
    params: { oldPassword, newPassword }
  })
}