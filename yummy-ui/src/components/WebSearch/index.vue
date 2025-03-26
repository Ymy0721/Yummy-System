<template>
  <div class="web-search">
    <el-tooltip :content="isEnabled ? '已启用联网搜索' : '点击启用联网搜索'" placement="top">
      <el-button 
        :type="isEnabled ? 'success' : 'default'"
        size="mini"
        :icon="isEnabled ? 'el-icon-connection' : 'el-icon-search'"
        @click="toggleWebSearch"
        circle
      ></el-button>
    </el-tooltip>
  </div>
</template>

<script>
export default {
  name: 'WebSearch',
  props: {
    value: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      isEnabled: this.value
    }
  },
  watch: {
    value(newVal) {
      this.isEnabled = newVal;
    }
  },
  methods: {
    toggleWebSearch() {
      this.isEnabled = !this.isEnabled;
      this.$emit('input', this.isEnabled);
      this.$emit('change', this.isEnabled);
    }
  }
}
</script>

<style scoped>
.web-search {
  display: inline-block;
  margin-left: 6px;
}
</style>
