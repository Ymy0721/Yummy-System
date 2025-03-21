import request from '@/utils/request'

// 查询专利数据列表
export function listPatents(query) {
  return request({
    url: '/patentSys/patents/list',
    method: 'get',
    params: query
  })
}

// 统计专利时间数量
export function getPatentCountByTime(){
  return request({
    url: '/patentSys/patents/getPatentCountByTime',
    method: 'get',
  })
}

// 统计专利地区数量
export function getPatentCountByRegion(){
  return request({
    url: '/patentSys/patents/getPatentCountByRegion',
    method: 'get',
  })
}

// 统计代理机构专利数量
export function getPatentCountByAgency(){
  return request({
    url: '/patentSys/patents/getPatentCountByAgency',
    method: 'get',
  })
}

// 统计申请人专利数量
export function getPatentCountByApplicant(){
  return request({
    url: '/patentSys/patents/getPatentCountByApplicant',
    method: 'get',
  })
}

// 统计发明人专利数量
export function getPatentCountByInventor(){
  return request({
    url: '/patentSys/patents/getPatentCountByInventor',
    method: 'get',
  })
}

// 统计发明人类型数量
export function getPatentCountByType(){
  return request({
    url: '/patentSys/patents/getPatentCountByType',
    method: 'get',
  })
}

// 统计专利标题词频
export function getPatentTitleWordCount(){
  return request({
    url: '/patentSys/patents/getPatentTitleWordCount',
    method: 'get',
  })
}

// 返回专利实体关系
export function getEntitiesWithRelationship(){
  return request({
    url: '/patentSys/patents/getEntitiesWithRelationship',
    method: 'get',
  })
}

// 查询专利数据详细
export function getPatents(patentId) {
  return request({
    url: '/patentSys/patents/' + patentId,
    method: 'get'
  })
}

// 新增专利数据
export function addPatents(data) {
  return request({
    url: '/patentSys/patents',
    method: 'post',
    data: data
  })
}

// 修改专利数据
export function updatePatents(data) {
  return request({
    url: '/patentSys/patents',
    method: 'put',
    data: data
  })
}

// 删除专利数据
export function delPatents(patentId) {
  return request({
    url: '/patentSys/patents/' + patentId,
    method: 'delete'
  })
}
