<template>
  <div>
    <svg-icon icon-class="github" @click="goto" />
  </div>
</template>

<script>
export default {
  name: 'YummyGit',
  data() {
    return {
      url: 'https://github.com/Ymy0721/Yummy-System'
    }
  },
  methods: {
    goto() {
      window.open(this.url)
    }
  }
}
</script>
