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
      url: 'https://github.com/Ymy0721/Patent-Search-System'
    }
  },
  methods: {
    goto() {
      window.open(this.url)
    }
  }
}
</script>
