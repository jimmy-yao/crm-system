import request from '@/utils/request'

// 获取角色列表
export function getRoles(params) {
  return request({
    url: '/roles',
    method: 'get',
    params
  })
}

// 搜索角色
export function searchRoles(keyword) {
  return request({
    url: '/roles/search',
    method: 'get',
    params: { keyword }
  })
}

// 获取角色详情
export function getRole(id) {
  return request({
    url: `/roles/${id}`,
    method: 'get'
  })
}

// 创建角色
export function createRole(data) {
  return request({
    url: '/roles',
    method: 'post',
    data
  })
}

// 更新角色
export function updateRole(id, data) {
  return request({
    url: `/roles/${id}`,
    method: 'put',
    data
  })
}

// 删除角色
export function deleteRole(id) {
  return request({
    url: `/roles/${id}`,
    method: 'delete'
  })
}

// 更新角色状态
export function updateRoleStatus(id, status) {
  return request({
    url: `/roles/${id}/status`,
    method: 'put',
    params: { status }
  })
}

// 为角色分配权限
export function assignPermissions(id, permissionIds) {
  return request({
    url: `/roles/${id}/permissions`,
    method: 'post',
    data: permissionIds
  })
}

// 获取用户的角色
export function getUserRoles(userId) {
  return request({
    url: `/roles/user/${userId}`,
    method: 'get'
  })
}