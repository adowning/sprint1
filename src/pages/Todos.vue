<template>
  <v-container fluid>
    <v-slide-y-transition mode="out-in">
      <v-layout column align-center>
        <!-- <div class="todos">
          <input v-model="title" placeholder="New Todo">
          <button @click="$pouch.post('todos', {title: title});title=''">Save Todo</button>
          <div v-for="todo in todos">
            <input v-model="todo.title" @change="$pouch.put('todos', todo)">
            <button @click="$pouch.remove('todos', todo)">Remove</button>
          </div>
        </div> -->

      </v-layout>
    </v-slide-y-transition>
  </v-container>
</template>
<!--<template>
  <div class="todos">
    <input v-model="title" placeholder="New Todo">
    <button @click="$pouch.post('todos', {title: title});title=''">Save Todo</button>
    <div v-for="todo in todos">
      <input v-model="todo.title" @change="$pouch.put('todos', todo)">
      <button @click="$pouch.remove('todos', todo)">Remove</button>
    </div>
  </div>
</template>-->
<script>
export default {
  data () {
    return {
      title: ''
    }
  },
  // VuePouch adds a `pouch` config option to all components.
  pouch: {
    // The simplest usage. queries all documents from the "todos" pouch database and assigns them to the "todos" vue property.
    todos: {/*empty selector*/ }
  },
  created () {
    // Send all documents to the remote database, and stream changes in real-time
    console.log(this.$pouch)
    console.log(this)
    this.$pouch.sync('todos', 'http://0.0.0.0:8082/list').then(this.$pouch.createUser("name", "password"));

  }
}
</script>