<template>
  <div class="input-area">
    <!-- 输入工具栏 -->
    <div class="input-toolbar">
      <FileUploader 
        class="tool-button"
        accept=".pdf,.doc,.docx,.txt,.xlsx,.ppt,.pptx" 
        :disabled="loading"
        tooltip="上传文件以供AI分析"
        @file-selected="onFileSelected"
        @file-removed="onFileRemoved"
      />
      <div class="web-search-toggle">
        <el-tooltip content="启用联网搜索" placement="top">
          <el-switch
            v-model="webSearch"
            active-color="#13ce66"
            inactive-color="#909399"
            @change="toggleWebSearch"
            style="margin-left: 10px;"
          ></el-switch>
        </el-tooltip>
      </div>
      <span v-if="file" class="file-indicator">
        <i class="el-icon-document"></i> {{ fileName }}
        <i class="el-icon-close" @click="removeFile"></i>
      </span>
    </div>
    
    <!-- 简化的消息输入区域 -->
    <div class="message-input-wrapper">
      <el-input
        class="message-input"
        type="textarea"
        :rows="3"
        v-model="message"
        :placeholder="isEditing ? '编辑消息...' : '输入问题...'"
        resize="none"
        @keydown.native.ctrl.enter.prevent="sendMessage"
        ref="messageInput"
      ></el-input>
      
      <div v-if="isEditing" class="editing-actions">
        <el-button size="mini" @click="$emit('cancel-edit')">取消</el-button>
        <el-button size="mini" type="primary" @click="sendMessage">更新</el-button>
      </div>
      <el-button
        v-else 
        type="primary" 
        :icon="loading ? 'el-icon-loading' : 'el-icon-s-promotion'"
        @click="loading ? stopGeneration() : sendMessage()" 
        :disabled="!loading && (!message.trim() && !file)"
        class="send-btn"
        circle
      ></el-button>
    </div>
    
    <div class="input-tip">按 Ctrl + Enter 发送</div>
  </div>
</template>

<script>
import FileUploader from '@/components/FileUploader';

export default {
  name: 'MessageInput',
  components: {
    FileUploader
  },
  props: {
    loading: {
      type: Boolean,
      default: false
    },
    selectedFile: {
      type: Object,
      default: null
    },
    editingContent: {
      type: String,
      default: ''
    },
    isEditing: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      message: '',
      file: this.selectedFile,
      webSearch: false
    }
  },
  computed: {
    fileName() {
      if (!this.file) return '';
      const name = this.file.name;
      return name.length > 20 ? name.substring(0, 17) + '...' : name;
    }
  },
  watch: {
    selectedFile(newVal) {
      this.file = newVal;
    },
    editingContent(newVal) {
      if (this.isEditing) {
        this.message = newVal;
        this.$nextTick(() => {
          this.focus();
        });
      }
    }
  },
  methods: {
    sendMessage() {
      if ((!this.message.trim() && !this.file) || this.loading) return;
      
      const messageContent = this.message.trim();
      const fileData = this.file;
      const webSearchEnabled = this.webSearch;
      
      // 先清空输入框，再发送消息
      this.message = '';
      
      this.$emit('send', {
        message: messageContent,
        file: fileData,
        webSearch: webSearchEnabled
      });
    },
    
    // 添加停止生成的方法
    stopGeneration() {
      this.$emit('stop-generation');
    },
    
    onFileSelected(file) {
      this.file = file;
      this.$emit('file-selected', file);
    },
    
    onFileRemoved() {
      this.removeFile();
    },
    
    removeFile() {
      this.file = null;
      this.$emit('file-removed');
    },
    
    toggleWebSearch(enabled) {
      this.webSearch = enabled;
      this.$emit('toggle-web-search', enabled);
    },
    
    focus() {
      this.$refs.messageInput.focus();
    }
  }
}
</script>
