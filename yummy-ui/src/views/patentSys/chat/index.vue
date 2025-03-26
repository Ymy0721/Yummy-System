<template>
  <div class="chat-container">
    <!-- 左侧历史会话侧边栏 -->
    <ChatSidebar
      :collapsed="sidebarCollapsed"
      :chatHistory="chatHistory"
      :currentChatId="String(currentChatId)"
      :modelOptions="modelOptions"
      :selectedModel="selectedModel"
      @toggle="toggleSidebar"
      @create-chat="createNewChat"
      @switch-chat="switchChat"
      @rename-chat="renameChat"
      @delete-chat="deleteChat"
      @change-model="changeModel"
    />
    
    <!-- 右侧聊天区域 -->
    <div class="chat-main">
      <!-- 聊天消息列表 -->
      <MessageList
        :messages="currentMessages"
        :userAvatar="userAvatar"
        :botAvatar="botAvatar"
        :loading="loading"
        :streamingContent="streamingContent"
        :isPaused="generationPaused"
        @regenerate="regenerateMessage"
        @edit-start="startEditMessage"
        @edit-cancel="cancelEditMessage"
        @edit-save="saveEditMessage"
        @toggle-pause="toggleGenerationPause"
        @stop-generation="stopGeneration"
      />
      
      <!-- 输入区域 -->
      <MessageInput
        :loading="loading"
        :selectedFile="selectedFile"
        :editing-content="editingMessage ? editingMessage.content : ''"
        :is-editing="!!editingMessage"
        @send="handleSendMessage"
        @file-selected="handleFileSelected"
        @file-removed="removeFile"
        @toggle-web-search="toggleWebSearch"
        @cancel-edit="cancelEdit"
        @stop-generation="stopGeneration"
      />
    </div>
  </div>
</template>

<script>
import { getChatHistory, getChatDetail, createChat, updateChat, deleteChat, sendMessage, uploadChatFile, sendMessageWithTypingEffect } from '@/api/patentSys/chat'
import { getModelOptions } from '@/api/patentSys/aimodel'
import { mapGetters } from 'vuex'

// 导入自定义组件
import ChatSidebar from '@/components/Chat/ChatSidebar'
import MessageList from '@/components/Chat/MessageList'
import MessageInput from '@/components/Chat/MessageInput'

export default {
  name: 'AiChat',
  components: {
    ChatSidebar,
    MessageList,
    MessageInput
  },
  computed: {
    ...mapGetters([
      'avatar' // 从Vuex获取头像
    ]),
    userAvatar() {
      return this.avatar || require('@/assets/images/profile.jpg') // 使用Vuex中的头像，如果没有则使用默认
    }
  },
  data() {
    return {
      chatHistory: [],
      currentChatId: null,
      currentMessages: [],
      loading: false,
      botAvatar: require('@/assets/images/chatbot.png'), // 只保留机器人头像
      modelOptions: [],
      selectedModel: null,
      sidebarCollapsed: false,
      selectedFile: null,
      streamingContent: '',  // 流式内容
      streamController: null, // 用于取消流式请求
      fileUploadInfo: null,
      generationPaused: false,
      editingMessage: null // 正在编辑的消息
    }
  },
  created() {
    this.loadModelOptions()
    this.loadChatHistory()
    
    // 从本地存储恢复侧边栏状态
    const savedState = localStorage.getItem('chat_sidebar_collapsed');
    if (savedState !== null) {
      this.sidebarCollapsed = savedState === 'true';
    }
  },
  methods: {
    async loadModelOptions() {
      try {
        const res = await getModelOptions()
        this.modelOptions = res.data
        if (this.modelOptions.length > 0) {
          // 默认选择默认模型或第一个模型
          this.selectedModel = this.modelOptions.find(m => m.isDefault === '1')?.id || this.modelOptions[0].id
        }
      } catch (error) {
        this.$message.error('获取模型列表失败')
        console.error(error)
      }
    },
    
    changeModel(modelId) {
      this.selectedModel = modelId;
      this.$message.info('已切换AI模型')
    },
    
    toggleSidebar() {
      this.sidebarCollapsed = !this.sidebarCollapsed;
      // 保存状态到本地存储
      localStorage.setItem('chat_sidebar_collapsed', this.sidebarCollapsed.toString());
    },
    
    async handleFileSelected(file) {
      this.selectedFile = file;
      
      try {
        // 上传文件
        const formData = new FormData();
        formData.append('file', file);
        
        const res = await uploadChatFile(formData);
        if (res.code === 200) {
          this.fileUploadInfo = res.data;
          this.$message.success('文件已准备好上传');
        } else {
          this.$message.error('文件准备失败: ' + res.msg);
          this.selectedFile = null;
        }
      } catch (error) {
        this.$message.error('上传文件时出错');
        console.error(error);
        this.selectedFile = null;
      }
    },
    
    removeFile() {
      this.selectedFile = null;
      this.fileUploadInfo = null;
    },
    
    toggleWebSearch(enabled) {
      this.$message.info(`已${enabled ? '启用' : '禁用'}联网搜索功能`);
    },
    
    async loadChatHistory() {
      try {
        const res = await getChatHistory()
        this.chatHistory = res.data
        if (this.chatHistory.length > 0 && !this.currentChatId) {
          this.switchChat(this.chatHistory[0].id)
        }
      } catch (error) {
        this.$message.error('获取聊天历史失败')
        console.error(error)
      }
    },
    
    async switchChat(chatId) {
      this.currentChatId = chatId
      try {
        const res = await getChatDetail(chatId)
        this.currentMessages = res.data
      } catch (error) {
        this.$message.error('获取聊天详情失败')
        console.error(error)
      }
    },
    
    async createNewChat() {
      try {
        const res = await createChat()
        this.chatHistory.unshift(res.data)
        this.switchChat(res.data.id)
      } catch (error) {
        this.$message.error('创建新会话失败')
        console.error(error)
      }
    },
    
    async renameChat(chat) {
      this.$prompt('请输入会话名称', '重命名会话', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValue: chat.title || '新对话'
      }).then(async ({ value }) => {
        try {
          await updateChat(chat.id, { title: value })
          const index = this.chatHistory.findIndex(item => item.id === chat.id)
          if (index !== -1) {
            this.chatHistory[index].title = value
          }
        } catch (error) {
          this.$message.error('重命名失败')
          console.error(error)
        }
      }).catch(() => {})
    },
    
    async deleteChat(chatId) {
      this.$confirm('确定要删除这个会话吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deleteChat(chatId)
          const index = this.chatHistory.findIndex(item => item.id === chatId)
          if (index !== -1) {
            this.chatHistory.splice(index, 1)
          }
          
          if (this.currentChatId === chatId) {
            if (this.chatHistory.length > 0) {
              this.switchChat(this.chatHistory[0].id)
            } else {
              this.currentChatId = null
              this.currentMessages = []
            }
          }
        } catch (error) {
          this.$message.error('删除失败')
          console.error(error)
        }
      }).catch(() => {})
    },
    
    async handleSendMessage({ message, file, webSearch }) {
      // 如果是编辑模式，则更新消息
      if (this.editingMessage) {
        const index = this.currentMessages.findIndex(m => m === this.editingMessage);
        if (index !== -1) {
          this.currentMessages[index].content = message;
          // 清除编辑状态
          this.editingMessage = null;
          return;
        }
      }
      
      // 常规发送消息逻辑...
      if ((!message.trim() && !file) || this.loading) return
      
      const userMessage = message.trim();
      
      // 如果没有当前会话，创建一个新会话
      if (!this.currentChatId) {
        await this.createNewChat();
      }
      
      // 构建发送内容
      let content = userMessage;
      let hasAttachment = false;
      
      // 处理文件上传信息 - 添加fileId引用格式
      if (file && this.fileUploadInfo) {
        content += `\n\n[文件: fileId:${this.fileUploadInfo.fileId}]`;
        hasAttachment = true;
      }
      
      // 处理网络搜索状态 - 添加特定标记
      if (webSearch) {
        content += '\n\n[请使用网络搜索获取最新信息]';
      }
      
      // 添加用户消息到当前会话
      this.currentMessages.push({
        role: 'user',
        content: content,
        createTime: new Date()
      });
      
      this.loading = true;
      this.streamingContent = ''; // 清空之前的流式内容
      
      try {
        // 构造请求数据
        const requestData = {
          chatId: this.currentChatId,
          content: content,
          modelId: this.selectedModel
        };
        
        // 如果有文件，处理文件
        if (hasAttachment) {
          this.$message.info('文件已添加到消息中');
          this.selectedFile = null;
          this.fileUploadInfo = null;
        }
        
        // 使用统一的流式API调用
        await this.callStreamAPI(requestData);
      } catch (error) {
        this.$message.error('发送消息失败');
        console.error(error);
      } finally {
        this.loading = false;
        this.streamController = null;
      }
    },
    
    // 添加缺失的 cancelEdit 方法
    cancelEdit() {
      this.editingMessage = null;
    },
    
    // 开始编辑消息
    startEditMessage(message) {
      if (message.role !== 'user') return;
      this.editingMessage = message;
    },
    
    // 取消编辑
    cancelEditMessage() {
      this.editingMessage = null;
    },
    
    // 保存编辑后的消息
    saveEditMessage({ message, newContent }) {
      const index = this.currentMessages.findIndex(m => m === message);
      if (index !== -1) {
        this.currentMessages[index].content = newContent;
        // 如果需要，可以在这里调用API保存到后端
      }
      this.editingMessage = null;
    },
    
    // 重新生成AI回复
    async regenerateMessage(message) {
      if (message.role !== 'assistant' || this.loading) return;
      
      // 找到这条消息之前的用户消息
      const index = this.currentMessages.findIndex(m => m === message || m.id === message.id);
      if (index <= 0) return;
      
      let previousUserMessage = null;
      // 向前查找最近的用户消息
      for (let i = index - 1; i >= 0; i--) {
        if (this.currentMessages[i].role === 'user') {
          previousUserMessage = this.currentMessages[i];
          break;
        }
      }
      
      if (!previousUserMessage) return;
      
      // 移除当前消息和之后的所有消息（可能有多轮对话）
      this.currentMessages.splice(index);
      
      // 重新发送前一条用户消息以获取新回复
      this.loading = true;
      this.streamingContent = '';
      
      try {
        // 使用前一条用户消息重新调用API
        const requestData = {
          chatId: this.currentChatId,
          content: previousUserMessage.content,
          modelId: this.selectedModel,
          regenerate: true // 标记为重新生成
        };
        
        // 调用流式API
        await this.callStreamAPI(requestData);
      } catch (error) {
        this.$message.error('重新生成回复失败');
        console.error(error);
      } finally {
        this.loading = false;
      }
    },
    
    // 暂停/继续生成
    toggleGenerationPause() {
      this.generationPaused = !this.generationPaused;
      
      // 在前端模拟暂停/继续
      // 注意：由于我们是纯前端模拟打字效果，这里可能需要更复杂的实现
      // 但为了简单起见，我们只更新状态
      if (this.streamController && this.streamController.pause) {
        if (this.generationPaused) {
          this.streamController.pause();
        } else {
          this.streamController.resume();
        }
      }
    },
    
    // 停止生成
    stopGeneration() {
      if (this.streamController) {
        this.streamController.abort();
        this.streamController = null;
      }
      
      // 如果有流式内容，保存为完整消息
      if (this.streamingContent) {
        this.currentMessages.push({
          role: 'assistant',
          content: this.streamingContent,
          createTime: new Date()
        });
        this.streamingContent = '';
      }
      
      this.loading = false;
    },
    
    async callStreamAPI(requestData) {
      // 取消之前的请求（如果有）
      if (this.streamController) {
        this.streamController.abort();
      }
      
      // 创建新的AbortController
      this.streamController = new AbortController();
      this.generationPaused = false;
      
      try {
        // 确保requestData包含所有必要的字段
        if (!requestData.chatId) {
          throw new Error("请求中缺少chatId");
        }
        
        // 添加额外信息到控制台以便调试
        console.log("开始模拟流式请求:", {
          chatId: requestData.chatId,
          contentLength: requestData.content ? requestData.content.length : 0,
          model: requestData.modelId
        });
        
        // 重置流式内容
        this.streamingContent = '';
        
        // 调用模拟流式API
        await sendMessageWithTypingEffect(requestData, {
          signal: this.streamController.signal,
          onMessage: (char) => {
            // 添加字符，实现打字效果
            this.streamingContent += char;
          },
          onError: (error) => {
            console.error("API错误:", error);
            this.$message.error(`请求失败: ${error.message}`);
            
            // 如果发生错误，将当前流式内容添加到消息列表
            if (this.streamingContent) {
              this.currentMessages.push({
                id: Date.now().toString(),
                role: 'assistant',
                content: this.streamingContent + "\n\n[发生错误，生成中断]",
                createTime: new Date()
              });
              this.streamingContent = '';
            } else {
              this.currentMessages.push({
                id: Date.now().toString(),
                role: 'assistant',
                content: "请求失败: " + error.message,
                createTime: new Date()
              });
            }
          },
          onComplete: async () => {
            console.log("模拟流式请求完成");
            // 传输完成后，将最终内容添加到消息列表
            if (this.streamingContent) {
              this.currentMessages.push({
                id: Date.now().toString(),
                role: 'assistant',
                content: this.streamingContent,
                createTime: new Date()
              });
              
              // 如果不是重新生成，则更新聊天标题
              if (!requestData.regenerate) {
                await this.updateChatTitle(requestData.content);
              }
              
              // 清空流式内容
              this.streamingContent = '';
            }
          }
        });
      } catch (error) {
        // 忽略AbortError（用户取消）
        if (error.name !== 'AbortError') {
          console.error("API错误:", error);
          this.$message.error(`请求失败: ${error.message}`);
          throw error;
        }
      }
    },
    
    // 更新聊天标题
    async updateChatTitle(userMessage) {
      // 如果是新会话且没有标题，将第一条消息作为标题
      const currentChat = this.chatHistory.find(c => c.id === this.currentChatId);
      if (currentChat && !currentChat.title) {
        currentChat.title = userMessage.length > 20 ? userMessage.substring(0, 20) + '...' : userMessage;
        await updateChat(this.currentChatId, { title: currentChat.title });
      }
    }
  }
}
</script>

<style lang="scss">
@import '@/assets/styles/chat/index.scss';

// 添加重置样式，确保不受其他样式影响
.chat-container {
  * {
    box-sizing: border-box;
  }
  
  /* 以下是强制固定用户头像在最右侧的样式 */
  .message-item.user .user-message-container {
    position: relative !important;
    width: 100% !important;
    display: block !important;
    min-height: 40px !important;
    padding-right: 45px !important;
    
    .user-avatar {
      position: absolute !important;
      top: 0 !important;
      right: 0 !important;
      width: 36px !important;
      height: 36px !important;
      z-index: 2 !important;
      margin: 0 !important;
    }
    
    .bubble-wrapper {
      width: 100% !important;
      padding-right: 8px !important;
      
      .user-bubble {
        float: right !important;
        max-width: 70% !important;
        margin: 0 !important;
      }
    }
  }
  
  /* 确保按钮始终显示 */
  .message-actions {
    display: flex !important;
    opacity: 1 !important;
    visibility: visible !important;
  }
  
  /* 确保用户区域和AI区域都有足够的底部间距 */
  .message-item {
    margin-bottom: 40px !important;
    
    &:after {
      content: "";
      display: table;
      clear: both;
    }
  }
  
  /* 终极修复：强制用户消息头像在最右侧 */
  .message-item.user {
    display: flex !important;
    flex-direction: row-reverse !important;
    
    .user-avatar {
      order: 999 !important; /* 使用极高的order值确保它始终在最后 */
      margin-left: 8px !important; /* 减少左边距 */
    }
    
    .user-bubble, .editing {
      order: 1 !important;
      margin-right: 8px !important; /* 减少右边距 */
    }
  }
  
  /* 确保AI消息结构正确 */
  .message-item.assistant {
    display: flex !important;
    flex-direction: row !important;
    
    .assistant-avatar {
      order: 1 !important;
      margin-right: 8px !important; /* 减少右边距 */
    }
    
    .assistant-bubble {
      order: 2 !important;
    }
  }
  
  /* 编辑模式样式覆盖 - 保持位置不变 */
  .user-bubble.editing {
    background-color: white !important;
    color: #333 !important;
    border: 1.5px solid #1e88e5 !important;
    box-shadow: 0 1px 4px rgba(0,0,0,0.1) !important;
    position: relative !important;
    
    .edit-mode {
      .el-textarea__inner {
        background-color: transparent !important;
        border: none !important;
        padding: 0 !important;
        color: #333 !important;
      }
    }
  }
}
</style>
