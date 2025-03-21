<template>
  <div>
    <svg-icon icon-class="question" @click="goto" />
  </div>
</template>

<script>
export default {
  name: 'YummyDoc',
  data() {
    return {
      url: 'https://github.com/Ymy0721/Patent-Search-System/blob/main/README.md'
    }
  },
  methods: {
    goto() {
      window.open(this.url)
    }
  }
}
</script>
