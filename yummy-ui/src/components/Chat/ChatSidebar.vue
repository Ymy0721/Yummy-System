<template>
  <div class="sidebar" :class="{ collapsed: collapsed }">
    <!-- 收起/展开侧边栏按钮 -->
    <div class="toggle-sidebar" @click="$emit('toggle')">
      <i :class="collapsed ? 'el-icon-d-arrow-right' : 'el-icon-d-arrow-left'"></i>
    </div>

    <div v-if="!collapsed" class="sidebar-content">
      <div class="new-chat" @click="$emit('create-chat')">
        <i class="el-icon-plus"></i> 新建对话
      </div>
      
      <!-- 模型选择器 -->
      <div class="model-selector">
        <el-select v-model="currentModel" placeholder="选择AI模型" size="small" @change="modelChanged">
          <el-option
            v-for="item in modelOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id">
          </el-option>
        </el-select>
      </div>
      
      <div class="history-list">
        <div 
          v-for="(item, index) in chatHistory" 
          :key="index" 
          class="history-item"
          :class="{ active: currentChatId === item.id }"
          @click="$emit('switch-chat', item.id)">
          <div class="title">{{ item.title || '新对话' }}</div>
          <div class="actions">
            <i class="el-icon-edit" @click.stop="$emit('rename-chat', item)"></i>
            <i class="el-icon-delete" @click.stop="$emit('delete-chat', item.id)"></i>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ChatSidebar',
  props: {
    collapsed: {
      type: Boolean,
      default: false
    },
    chatHistory: {
      type: Array,
      required: true
    },
    currentChatId: {
      type: [String, Number], // 修改为接受字符串或数字类型
      default: null
    },
    modelOptions: {
      type: Array,
      required: true
    },
    selectedModel: {
      type: [String, Number], // 同时修改这个以保持一致
      default: null
    }
  },
  data() {
    return {
      currentModel: this.selectedModel
    }
  },
  watch: {
    selectedModel(newVal) {
      this.currentModel = newVal;
    }
  },
  methods: {
    modelChanged(value) {
      this.$emit('change-model', value);
    }
  }
}
</script>
