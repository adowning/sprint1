import '@babel/polyfill'
import Vue from 'vue'
import './plugins/vuetify'
import App from './App.vue'
import router from './router/index'
import store from './store/index'
import Backendless from 'backendless'
import setGlobalHelpers from './global.helpers'
import './mixins'
import './plugins'
import 'rxjs';

var APP_ID = 'BBC4C6A3-E181-C607-FF00-173E36FE4800';
var API_KEY = '8A0F3B0C-01AF-54D5-FF77-9C17D8293900';

setGlobalHelpers()

Backendless.initApp(APP_ID, API_KEY);


Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')