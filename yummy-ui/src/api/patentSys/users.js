import request from '@/utils/request'

// 查询专利用户列表
export function listUsers(query) {
  return request({
    url: '/patentSys/users/list',
    method: 'get',
    params: query
  })
}

// 查询专利用户详细
export function getUsers(userId) {
  return request({
    url: '/patentSys/users/' + userId,
    method: 'get'
  })
}

// 新增专利用户
export function addUsers(data) {
  return request({
    url: '/patentSys/users',
    method: 'post',
    data: data
  })
}

// 修改专利用户
export function updateUsers(data) {
  return request({
    url: '/patentSys/users',
    method: 'put',
    data: data
  })
}

// 删除专利用户
export function delUsers(userId) {
  return request({
    url: '/patentSys/users/' + userId,
    method: 'delete'
  })
}
