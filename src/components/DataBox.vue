<template>
  <div class="data-box component">
    <SpinnerWave class="spinner-wrapper" v-if="loading"></SpinnerWave>

    <div :class="{'data-box-content': true, 'loading': loading}" v-if="!isEmpty && !error">
      <slot></slot>
    </div>

    <div class="data-box-content is-empty" v-if="isEmpty && !error && !loading">
      <div class="smile">(>_
        <)</div>
          <div class="title">No content!</div>
      </div>

      <div class="data-box-content error" v-if="error">
        <div class="smile">(>_
          <)</div>
            <div class="title">Something wrong here!</div>
            <div class="error-msg">{{ error }}</div>
        </div>
      </div>
</template>
<script type="text/babel">
import SpinnerWave from './SpinnerWave.vue'

export default {
  name: 'DataBox',
  props: {
    loading: {
      type: Boolean,
      default: false
    },
    isEmpty: {
      type: Boolean,
      default: false
    },
    error: {
      type: [String, Boolean],
      default: false
    }
  },
  components: { SpinnerWave }
}
</script>
