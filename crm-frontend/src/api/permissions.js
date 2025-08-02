import request from '@/utils/request'

// 获取权限列表
export function getPermissions(params) {
  return request({
    url: '/permissions',
    method: 'get',
    params
  })
}

// 获取权限树
export function getPermissionTree() {
  return request({
    url: '/permissions/tree',
    method: 'get'
  })
}

// 搜索权限
export function searchPermissions(keyword) {
  return request({
    url: '/permissions/search',
    method: 'get',
    params: { keyword }
  })
}

// 获取权限详情
export function getPermission(id) {
  return request({
    url: `/permissions/${id}`,
    method: 'get'
  })
}

// 创建权限
export function createPermission(data) {
  return request({
    url: '/permissions',
    method: 'post',
    data
  })
}

// 更新权限
export function updatePermission(id, data) {
  return request({
    url: `/permissions/${id}`,
    method: 'put',
    data
  })
}

// 删除权限
export function deletePermission(id) {
  return request({
    url: `/permissions/${id}`,
    method: 'delete'
  })
}

// 更新权限状态
export function updatePermissionStatus(id, status) {
  return request({
    url: `/permissions/${id}/status`,
    method: 'put',
    params: { status }
  })
}

// 获取用户的权限
export function getUserPermissions(userId) {
  return request({
    url: `/permissions/user/${userId}`,
    method: 'get'
  })
}

// 获取角色的权限
export function getRolePermissions(roleId) {
  return request({
    url: `/permissions/role/${roleId}`,
    method: 'get'
  })
}