<template>
  <v-app>
    <v-content>
      <router-view/>
    </v-content>
  </v-app>
</template>

<script>
import * as Database from './plugins/database';

export default {
  name: 'App',
  data () {
    return {
      clipped: false,
      drawer: true,
      fixed: false,
      items: [{
        icon: 'bubble_chart',
        title: 'Inspire'
      }],
      miniVariant: false,
      right: true,
      rightDrawer: false,
      title: 'Vuetify.js'
    }
  },
  created () {



    // var createDatabase = function(callback) {
    //  db = new PouchDB("badgescanner");
    //  var ddoc = {
    //    _id: '_design/query',
    //    views: {
    //      byts: {
    //        map: function (doc) {
    //          if (typeof doc.ts != "number") {
    //            doc.ts = 0;
    //          }
    //          emit(doc.ts,null);
    //        }.toString()
    //      }
    //    }
    //  };

    //  db.put(ddoc).then(function (data) {
    //    callback(null, data);
    //  }).catch(function (err) {
    //    callback(null, null);
    //  });
    // }

    // this.$pouch.sync('complaints', 'https://3fa6559f-dddc-4829-9483-15226b5b832d-bluemix:afabb0d15ff8f7118c400a70f6fdf77cd5c2455c537e47d7672c3a62bfc2fd91@3fa6559f-dddc-4829-9483-15226b5b832d-bluemix.cloudant.com/complaints', {
    //   filter: '_selector',
    //   selector: {
    //     type: 'complaint',
    //     assignee: 'test'
    //   }
    // })
  }
}
</script>
