<template>
  <div class="spinner-wave component">
    <div class="sk-wave">
      <div class="sk-rect sk-rect1" :style="loadingColor"></div>
      <div class="sk-rect sk-rect2" :style="loadingColor"></div>
      <div class="sk-rect sk-rect3" :style="loadingColor"></div>
      <div class="sk-rect sk-rect4" :style="loadingColor"></div>
      <div class="sk-rect sk-rect5" :style="loadingColor"></div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SpinnerWave',
  props: {
    color: {
      type: String,
      default: '#bebebe'
    }
  },
  computed: {
    loadingColor () {
      return {
        'background-color': this.color
      }
    }
  }
}
</script>
