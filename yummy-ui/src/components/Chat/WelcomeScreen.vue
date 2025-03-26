<template>
  <div class="welcome-screen">
    <div class="welcome-logo">
      <img :src="botAvatar" alt="AI助手">
    </div>
    <h2>欢迎使用AI助手</h2>
    <p>您可以向我询问任何问题，我会尽力提供帮助。</p>
  </div>
</template>

<script>
export default {
  name: 'WelcomeScreen',
  props: {
    botAvatar: {
      type: String,
      required: true
    }
  }
}
</script>
