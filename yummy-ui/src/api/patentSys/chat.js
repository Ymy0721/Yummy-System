import request from '@/utils/request'
import axios from 'axios'
import { getToken } from '@/utils/auth'
import { processStreamingText, checkCodeBlockStatus } from '@/api/patentSys/tool/streamProcessor'

// 获取聊天历史列表
export function getChatHistory() {
  return request({
    url: '/patentSys/chat/history',
    method: 'get'
  })
}

// 获取聊天详情
export function getChatDetail(chatId) {
  return request({
    url: '/patentSys/chat/detail/' + chatId,
    method: 'get'
  })
}

// 创建新的聊天会话
export function createChat() {
  return request({
    url: '/patentSys/chat/create',
    method: 'post'
  })
}

// 更新聊天会话信息
export function updateChat(chatId, data) {
  return request({
    url: '/patentSys/chat/update/' + chatId,
    method: 'put',
    data: data
  })
}

// 删除聊天会话
export function deleteChat(chatId) {
  return request({
    url: '/patentSys/chat/delete/' + chatId,
    method: 'delete'
  })
}

// 发送聊天消息 - 普通方式
export function sendMessage(data) {
  return request({
    url: '/patentSys/chat/send',
    method: 'post',
    data: data,
    // 增加超时时间为2分钟
    timeout: 120000
  })
}

// 模拟流式响应 (优化代码块处理)
export function sendMessageWithTypingEffect(data, options = {}) {
  const { onMessage, onError, onComplete, signal } = options;
  
  return new Promise((resolve, reject) => {
    // 直接调用普通的消息发送API
    sendMessage(data)
      .then(response => {
        if (response.code !== 200) {
          if (onError) onError(new Error(response.msg || '请求失败'));
          reject(new Error(response.msg || '请求失败'));
          return;
        }
        
        // 获取完整的AI回复
        const fullResponse = response.data.content;
        
        if (!fullResponse) {
          if (onComplete) onComplete();
          resolve(response);
          return;
        }
        
        // 使用改进的流式处理器处理文本
        processStreamingText(fullResponse, {
          onOutput: (chunk) => {
            if (onMessage) onMessage(chunk);
          },
          onComplete: () => {
            if (onComplete) onComplete();
            resolve(response);
          },
          onAbort: () => {
            reject(new DOMException('Aborted', 'AbortError'));
          },
          signal: signal,
          // 配置较慢的速度
          speedOptions: {
            baseDelay: 40, // 增加基础延迟
            randomness: 20  // 增加随机性
          }
        });
      })
      .catch(error => {
        if (onError) onError(error);
        reject(error);
      });
  });
}

// 上传文件
export function uploadChatFile(formData) {
  return request({
    url: '/patentSys/chat/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    timeout: 60000 // 文件上传也给予更长的超时时间
  })
}

// 导出检查代码块状态的工具函数，方便其他组件使用
export { checkCodeBlockStatus }
