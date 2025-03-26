<template>
  <div class="message-container" ref="messageContainer">
    <WelcomeScreen 
      v-if="messages.length === 0" 
      :botAvatar="botAvatar" 
    />
    
    <MessageItem
      v-for="(msg, idx) in messages"
      :key="idx"
      :message="msg"
      :userAvatar="userAvatar"
      :botAvatar="botAvatar"
      :isEditing="editingMessageId === msg.id"
      @regenerate="$emit('regenerate', msg)"
      @edit-start="startEditMessage"
      @edit-cancel="cancelEditMessage"
      @edit-save="saveEditMessage"
    />
    
    <!-- 流式响应区域 - 实时Markdown渲染 -->
    <div v-if="loading || streamingContent" class="message-item assistant">
      <div class="avatar assistant-avatar">
        <img :src="botAvatar" alt="AI" />
      </div>
      
      <div class="assistant-bubble">
        <!-- 思考中的加载状态 -->
        <div v-if="loading && !streamingContent" class="thinking-indicator">
          <span>思考中</span>
          <div class="typing-dots">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </div>
        
        <!-- 流式内容使用MarkdownRenderer实时渲染，使用key强制重新渲染 -->
        <MarkdownRenderer 
          v-if="streamingContent" 
          :content="streamingContent" 
          :key="renderKey"
        />
        
        <!-- 光标只在有内容且不在代码块中时显示 -->
        <span v-if="streamingContent && !isInCodeBlock" class="typing-cursor" :class="{ 'cursor-blink': !isPaused }"></span>
      </div>
      
      <!-- 控制按钮 -->
      <div v-if="streamingContent" class="streaming-controls">
        <el-button 
          size="mini" 
          type="text" 
          :icon="isPaused ? 'el-icon-video-play' : 'el-icon-video-pause'"
          @click="$emit('toggle-pause')" 
          :title="isPaused ? '继续生成' : '暂停生成'">
        </el-button>
        <el-button 
          size="mini" 
          type="text" 
          icon="el-icon-close" 
          @click="$emit('stop-generation')" 
          title="停止生成">
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
import WelcomeScreen from './WelcomeScreen.vue';
import MessageItem from './MessageItem.vue';
import MarkdownRenderer from './MarkdownRenderer.vue';
import { checkCodeBlockStatus } from '@/api/patentSys/chat';

export default {
  name: 'MessageList',
  components: {
    WelcomeScreen,
    MessageItem,
    MarkdownRenderer
  },
  props: {
    messages: {
      type: Array,
      required: true
    },
    userAvatar: {
      type: String,
      required: true
    },
    botAvatar: {
      type: String,
      required: true
    },
    loading: {
      type: Boolean,
      default: false
    },
    streamingContent: {
      type: String,
      default: ''
    },
    isPaused: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      editingMessageId: null,
      renderKey: 0,  // 添加renderKey来控制强制重新渲染
      lastCodeBlockState: false, // 跟踪上一次渲染的代码块状态
    }
  },
  computed: {
    // 判断当前光标是否在代码块中，如果在则不显示光标
    isInCodeBlock() {
      // 使用工具函数检查代码块状态
      return checkCodeBlockStatus(this.streamingContent || '').inProgress;
    },
    
    // 检查是否含有完整的代码块
    hasCompleteCodeBlock() {
      // 使用工具函数检查是否有完整的代码块
      return checkCodeBlockStatus(this.streamingContent || '').complete;
    }
  },
  watch: {
    messages: {
      handler() {
        this.scrollToBottom();
      },
      deep: true
    },
    streamingContent() {
      this.scrollToBottom();
      
      // 检查代码块状态变化
      const currentCodeBlockState = this.hasCompleteCodeBlock;
      if (this.lastCodeBlockState !== currentCodeBlockState) {
        this.renderKey += 1; // 强制重新渲染MarkdownRenderer
        this.lastCodeBlockState = currentCodeBlockState;
      }
    }
  },
  methods: {
    scrollToBottom() {
      this.$nextTick(() => {
        if (this.$refs.messageContainer) {
          this.$refs.messageContainer.scrollTop = this.$refs.messageContainer.scrollHeight;
        }
      });
    },
    startEditMessage(message) {
      this.editingMessageId = message.id;
      this.$emit('edit-start', message);
    },
    cancelEditMessage() {
      this.editingMessageId = null;
      this.$emit('edit-cancel');
    },
    saveEditMessage({ message, newContent }) {
      this.editingMessageId = null;
      this.$emit('edit-save', { message, newContent });
    }
  },
  mounted() {
    this.scrollToBottom();
  }
}
</script>

<style lang="scss" scoped>
/* 思考中状态的样式 */
.thinking-indicator {
  display: flex;
  align-items: center;
  padding: 8px 12px; // 减小内边距，使气泡更紧凑
  
  span {
    font-size: 14px;
    margin-right: 8px;
    color: #666;
  }
}

/* 确保AI头像尺寸与正常消息一致 */
.message-item.assistant .avatar {
  width: 36px;
  height: 36px;
  flex-shrink: 0;
}

/* 确保AI消息气泡样式一致 - 即使只有思考中文字 */
.assistant-bubble {
  background-color: white;
  color: #333;
  padding: 12px 16px;
  border-radius: 18px;
  border-top-left-radius: 4px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.1);
  position: relative;
  min-width: 100px; // 确保短内容时气泡也有最小宽度
}

/* 打字机光标样式 */
.typing-cursor {
  display: inline-block;
  width: 2px;
  height: 1em;
  background-color: #333;
  margin-left: 2px;
  vertical-align: middle;
  position: relative;
}

.cursor-blink {
  animation: cursor-blink 0.7s infinite;
}

@keyframes cursor-blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

.streaming-controls {
  margin-left: 10px;
  visibility: visible;
  opacity: 1;
  transition: opacity 0.3s;
}

/* 打字机加载样式 */
.typing-dots {
  display: flex;
  align-items: center;
}

.typing-dots span {
  width: 6px;
  height: 6px;
  margin: 0 2px;
  background-color: #999;
  border-radius: 50%;
  display: inline-block;
  animation: typing-dot 1.4s infinite ease-in-out both;
}

.typing-dots span:nth-child(1) {
  animation-delay: 0s;
}

.typing-dots span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-dots span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing-dot {
  0%, 80%, 100% { transform: scale(0.5); opacity: 0.5; }
  40% { transform: scale(1); opacity: 1; }
}

/* 确保思考中状态的气泡与默认样式一致 */
.message-item.assistant .assistant-bubble .thinking-indicator {
  min-height: 0; // 覆盖默认的最小高度
  padding: 0; // 移除内边距以适应气泡
  box-sizing: border-box;
}

/* 确保所有状态下AI头像尺寸一致 - 提高选择器优先级 */
.message-item.assistant .avatar,
.message-item.assistant .assistant-avatar,
.message-container .message-item.assistant .avatar {
  width: 36px !important;
  height: 36px !important;
  flex-shrink: 0 !important;
  
  img {
    width: 100% !important;
    height: 100% !important;
    border-radius: 50% !important;
    object-fit: cover !important;
  }
}
</style>
