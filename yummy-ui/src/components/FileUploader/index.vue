<template>
  <div class="file-uploader">
    <el-tooltip :content="tooltip || '上传文件'" placement="top">
      <el-upload
        action="#"
        :accept="accept"
        :multiple="false"
        :disabled="disabled"
        :show-file-list="false"
        :http-request="handleHttpRequest"
        :before-upload="beforeUpload"
      >
        <el-button
          type="text"
          icon="el-icon-upload"
          :disabled="disabled"
          class="upload-button"
        ></el-button>
      </el-upload>
    </el-tooltip>
  </div>
</template>

<script>
export default {
  name: 'FileUploader',
  props: {
    accept: {
      type: String,
      default: '*/*'
    },
    buttonText: {
      type: String,
      default: '上传文件'
    },
    buttonType: {
      type: String,
      default: 'default'
    },
    size: {
      type: String,
      default: 'mini'
    },
    icon: {
      type: String,
      default: 'el-icon-upload2'
    },
    disabled: {
      type: Boolean,
      default: false
    },
    tooltip: {
      type: String,
      default: '上传文件(PDF, Word, Excel等)'
    },
    maxSize: {
      type: Number,
      default: 50 // 默认50MB
    }
  },
  data() {
    return {
      previewFile: null
    }
  },
  computed: {
    fileDisplayName() {
      if (!this.previewFile) return '';
      const name = this.previewFile.name;
      return name.length > 20 ? name.substr(0, 17) + '...' : name;
    }
  },
  methods: {
    triggerFileInput() {
      this.$refs.fileInput.click();
    },
    handleFileChange(event) {
      const files = event.target.files;
      if (!files || files.length === 0) return;
      
      const file = files[0];
      
      // 检查文件大小
      if (file.size > this.maxSize * 1024 * 1024) {
        this.$message.error(`文件大小不能超过${this.maxSize}MB`);
        this.$refs.fileInput.value = '';
        return;
      }
      
      this.previewFile = file;
      this.$emit('file-selected', file);
    },
    removeFile() {
      this.previewFile = null;
      this.$refs.fileInput.value = '';
      this.$emit('file-removed');
    },
    formatFileSize(bytes) {
      if (bytes === 0) return '0 B';
      const k = 1024;
      const sizes = ['B', 'KB', 'MB', 'GB'];
      const i = Math.floor(Math.log(bytes) / Math.log(k));
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    },
    beforeUpload(file) {
      // 检查文件大小是否超过限制
      const sizeInMB = file.size / 1024 / 1024;
      if (sizeInMB > this.maxSize) {
        this.$message.error(`文件大小不能超过 ${this.maxSize}MB!`);
        return false;
      }
      return true;
    },
    handleHttpRequest(options) {
      // 不执行实际上传，只是触发文件选择事件
      this.$emit('file-selected', options.file);
    },
    resetUploader() {
      this.$emit('file-removed');
    }
  }
}
</script>

<style scoped>
.file-uploader {
  display: inline-block;
  
  .el-button {
    color: #606266;
    
    &:hover {
      color: #409EFF;
    }
  }
  
  .upload-button {
    font-size: 22px; /* 增大图标尺寸 */
    padding: 8px;
    
    i {
      font-size: 22px; /* 确保图标本身也变大 */
    }
  }
}
.file-preview {
  margin-top: 8px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  max-width: 300px;
}
.file-info {
  display: flex;
  align-items: center;
  overflow: hidden;
}
.file-name {
  margin: 0 5px;
  color: #606266;
  font-size: 12px;
}
.file-size {
  color: #909399;
  font-size: 12px;
}
.remove-file {
  cursor: pointer;
  color: #909399;
}
.remove-file:hover {
  color: #f56c6c;
}
</style>
