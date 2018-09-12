/**
 * import and init global mixins
 */

import Vue from 'vue'

import currentUser from '../mixins/currentUser'
import deviceInfo from '../mixins/deviceInfo'
import jumpTo from '../mixins/jumpTo'
import formatDateTime from '../mixins/formatDateTime'
import test from '../mixins/test'

Vue.mixin(currentUser)
Vue.mixin(deviceInfo)
Vue.mixin(jumpTo)
Vue.mixin(formatDateTime)
Vue.mixin(test)