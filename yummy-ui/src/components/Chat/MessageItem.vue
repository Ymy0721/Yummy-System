<template>
  <div class="message-item" :class="message.role">
    <!-- AI消息 -->
    <template v-if="message.role === 'assistant'">
      <div class="avatar assistant-avatar">
        <img :src="botAvatar" alt="AI">
      </div>
      <div class="bubble assistant-bubble">
        <MarkdownRenderer :content="message.content" />
        <div class="message-actions ai-actions">
          <el-tooltip content="复制" placement="top">
            <i class="el-icon-document-copy" @click="copyMessage"></i>
          </el-tooltip>
          <el-tooltip content="重新生成" placement="top">
            <i class="el-icon-refresh" @click="regenerateMessage"></i>
          </el-tooltip>
        </div>
      </div>
    </template>
    
    <!-- 用户消息 - 使用MarkdownRenderer显示公式 -->
    <template v-else>
      <!-- 固定布局容器 -->
      <div class="user-message-container">
        <!-- 头像固定在右侧 -->
        <div class="avatar user-avatar">
          <img :src="userAvatar" alt="您">
        </div>
        
        <!-- 气泡确保不会与头像重叠 -->
        <div class="bubble-wrapper">
          <!-- 编辑模式 - 直接在气泡内编辑 -->
          <div v-if="localIsEditing" class="bubble user-bubble editing">
            <div class="edit-mode">
              <el-input
                type="textarea"
                v-model="editContent"
                rows="3"
                resize="none"
                autofocus
                class="bubble-edit-input"
              ></el-input>
              <div class="edit-actions">
                <el-button size="mini" @click="cancelEdit">取消</el-button>
                <el-button size="mini" type="primary" @click="saveEdit">确定</el-button>
              </div>
            </div>
          </div>
          
          <!-- 显示模式 - 使用MarkdownRenderer渲染用户消息中的公式 -->
          <div v-else class="bubble user-bubble">
            <MarkdownRenderer :content="message.content" />
            <div class="message-actions user-actions">
              <el-tooltip content="编辑" placement="top">
                <i class="el-icon-edit" @click="startEdit"></i>
              </el-tooltip>
              <el-tooltip content="复制" placement="top">
                <i class="el-icon-document-copy" @click="copyMessage"></i>
              </el-tooltip>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script>
import MarkdownRenderer from './MarkdownRenderer.vue';

export default {
  name: 'MessageItem',
  components: {
    MarkdownRenderer
  },
  props: {
    message: {
      type: Object,
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
    isEditing: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      editContent: this.message.content,
      localIsEditing: this.isEditing
    }
  },
  watch: {
    isEditing(val) {
      this.localIsEditing = val;
      if (val) {
        this.editContent = this.message.content;
      }
    }
  },
  methods: {
    copyMessage() {
      navigator.clipboard.writeText(this.message.content)
        .then(() => {
          this.$message.success('已复制到剪贴板');
        })
        .catch(err => {
          this.$message.error('复制失败');
          console.error('复制失败:', err);
        });
    },
    
    startEdit() {
      this.localIsEditing = true;
      this.editContent = this.message.content;
      this.$emit('edit-start', this.message);
    },
    
    cancelEdit() {
      this.localIsEditing = false;
      this.$emit('edit-cancel');
    },
    
    saveEdit() {
      this.$emit('edit-save', { 
        message: this.message, 
        newContent: this.editContent 
      });
      this.localIsEditing = false;
    },
    
    regenerateMessage() {
      this.$emit('regenerate', this.message);
    }
  }
}
</script>

<style lang="scss" scoped>
/* 消息项基本样式 */
.message-item {
  position: relative;
  margin-bottom: 30px;
  width: 100%;
  
  /* AI消息布局 - 使用flexbox */
  &.assistant {
    display: flex;
    
    .assistant-avatar {
      margin-right: 12px;
    }
    
    .assistant-bubble {
      max-width: calc(100% - 60px);
    }
  }
  
  /* 用户消息布局 - 使用固定布局 */
  .user-message-container {
    position: relative;
    width: 100%;
    display: block;
    min-height: 40px;
    padding-right: 45px; /* 减少右侧padding，让气泡更靠近头像 */
    
    /* 用户头像固定在右侧 */
    .user-avatar {
      position: absolute;
      top: 0;
      right: 0;
      width: 36px;
      height: 36px;
    }
    
    /* 气泡包装器确保气泡不会占据头像空间 */
    .bubble-wrapper {
      width: 100%;
      padding-right: 8px; /* 减少内边距，使气泡更靠近头像 */
      box-sizing: border-box;
      
      .user-bubble {
        float: right; /* 气泡靠右对齐 */
        max-width: 70%;
        text-align: left;
      }
    }
  }
  
  /* 通用气泡样式 */
  .bubble {
    position: relative;
    padding: 8px 12px; /* 减小padding使单行文本的气泡高度更合适 */
    border-radius: 18px;
    word-break: break-word;
    box-shadow: 0 1px 4px rgba(0,0,0,0.1);
    line-height: 1.4; /* 减小行高 */
    
    &.user-bubble {
      background-color: #1e88e5;
      color: white;
      border-top-right-radius: 4px;
      margin-right: 0; /* 移除右侧边距 */
      
      &.editing {
        background-color: white;
        color: #333;
        border: 1px solid #1e88e5; /* 减小border宽度 */
        
        .edit-mode {
          width: 100%;
          
          .el-textarea__inner {
            border: none;
            background: transparent;
            color: #333;
            padding: 0;
            line-height: 1.4; /* 保持一致的行高 */
            
            &:focus {
              box-shadow: none;
            }
          }

          .bubble-edit-input {
            width: 100%;
            margin-bottom: 8px;
          }
          
          .edit-actions {
            display: flex;
            justify-content: flex-end;
            margin-top: 8px;
            gap: 8px;
          }
        }
      }
    }
    
    &.assistant-bubble {
      background-color: white;
      color: #333;
      border-top-left-radius: 4px;
      margin-left: 8px; /* 减少AI气泡与头像的间距 */
      padding: 8px 12px; /* 保持与用户气泡一致的内边距 */
    }
  }
  
  /* 头像样式 */
  .avatar {
    width: 36px;
    height: 36px;
    flex-shrink: 0;
    
    img {
      width: 100%;
      height: 100%;
      border-radius: 50%;
      object-fit: cover;
      border: 2px solid #fff;
      box-shadow: 0 1px 3px rgba(0,0,0,0.1);
    }
  }
  
  /* 消息操作按钮 */
  .message-actions {
    position: absolute;
    display: flex;
    background-color: white;
    border-radius: 4px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.15);
    padding: 6px 10px;
    z-index: 10;
    bottom: -36px;
    
    &.ai-actions {
      left: 0; 
    }
    
    &.user-actions {
      right: 0;
    }
    
    i {
      cursor: pointer;
      font-size: 16px;
      margin: 0 6px;
      color: #606060;
      
      &:hover {
        color: #1e88e5;
      }
    }
  }
}
</style>
