// require('app-module-path').addPath(__dirname);

import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
// import bridge from './bridge'
// const {
//   bridge
// } = require('electron').remote.require('./bridge');
// import '../main-html'
// Disable drag and drop of links inside application (which would
// open it as if the whole app was a browser)
document.addEventListener('dragover', event => event.preventDefault());
document.addEventListener('drop', event => event.preventDefault());

// Disable middle-click (which would open a new browser window, but we don't want this)
document.addEventListener('auxclick', event => event.preventDefault());

// Each link (rendered as a button or list item) has its own custom click event
// so disable the default. In particular this will disable Ctrl+Clicking a link
// which would open a new browser window.
document.addEventListener('click', (event) => event.preventDefault());


Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')