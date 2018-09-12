/**
 * import and init global plugins
 */

import Vue from 'vue'
import AsyncComputed from 'vue-async-computed'

import globalEventBus from '../plugins/globalEventBus'
// import './database'
import PouchDB from 'pouchdb-browser'
PouchDB.plugin(require('pouchdb-find'));
PouchDB.plugin(require('pouchdb-live-find'));
PouchDB.plugin(require('pouchdb-authentication'));
Vue.use(require('vue-pouch'), {
  pouch: PouchDB, // optional if `PouchDB` is available on the global object
  defaultDB: 'http://0.0.0.0:8082/' // the database to use if none is specified in the pouch setting of the vue component
})

Vue.use(globalEventBus)
Vue.use(AsyncComputed)