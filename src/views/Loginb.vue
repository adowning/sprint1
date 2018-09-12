<script>
// import Layout from '@layouts/main'
import { authMethods } from '../plugins/helpers'
import { Plugins } from '@capacitor/core';
// import appConfig from '@src/app.config'
import authService from '../services/auth.service'
import { QrcodeReader } from 'vue-qrcode-reader'

export default {
  page: {
    title: 'Log in',
    // meta: [{ name: 'description', content: `Log in to ${appConfig.title}` }],
  },
  // components: {  QrcodeReader },
  data () {
    return {
      username: '',
      password: '',
      authError: null,
      tryingToLogIn: false,
      paused: false,
      loading: false,
      accepted: false,
      isMenu: true,
      isPolicy: false,
      clockedIn: false,
      hasTablet: false,
      tabletAvailable: true
    }
  },
  methods: {
    ...authMethods,
    makeLogin () {
      authService.makeLogin({
        email: this.email,
        password: this.password
      }).then(response => { this.error = '' })
        .then(() => {
          this.$store.dispatch('user/getCurrent')
            .then(() => this.$router.push('profile'))
            .catch(error => console.log(error))
        })
        .catch((error) => {
          console.log('error', error)
          this.error = error.status === 404 ? 'User with same email not found' : error.message
        })
    },
    tryToLogIn (content, location) {
      this.authError = null
      return this.login({
        username: content.username,
        password: content.password
      })
        .then(token => {
          this.tryingToLogIn = false
          // Redirect to the originally requested page, or to the home page
          this.$router.push(this.$route.query.redirectFrom || { name: 'home' })
        })
        .catch(error => {
          this.tryingToLogIn = false
          this.authError = error
          var message = 'fix me up and i cant log in'
          this.event.$emit('alert', message);
        })
    },
    async onDetect (promise) {
      console.log('onDetect')
      this.tryingToLogIn = true
      try {
        const {
          source,       // 'file', 'url' or 'stream'
          imageData,    // raw image data of image/frame
          content,      // decoded String
          location      // QR code coordinates
        } = await promise
        console.log(source + imageData + content + location)
        this.tryingToLogIn(content, location)
        // ...
      } catch (error) {
        if (error.name === 'DropImageFetchError') {
          // drag-and-dropped URL (probably just an <img> element) from different
          // domain without CORS header caused same-origin-policy violation
        } else if (error.name === 'DropImageDecodeError') {
          // drag-and-dropped file is not of type image and can't be decoded
        } else {
          // idk, open an issue ¯\_(ツ)_/¯
        }
      }
    },
    async onDecode (decodedString) {
      console.log(decodedString)
      this.paused = true
    },
    async onInit (promise) {
      // show loading indicator
      console.log("promise")
      var errorText = ''
      try {
        await promise

        // successfully initialized
      } catch (error) {
        if (error.name === 'NotAllowedError') {
          errorText = "user denied camera access permisson"
        } else if (error.name === 'NotFoundError') {
          errorText = 'no suitable camera device installed'
        } else if (error.name === 'NotSupportedError') {
          errorText = ' page is not served over HTTPS (or localhost)'
        } else if (error.name === 'NotReadableError') {
          errorText = ' maybe camera is already in use'
        } else if (error.name === 'OverconstrainedError') {
          errorText = ' passed constraints dont match any camera.'
          // Did you requested the front camera although there is none?
        } else {
          errorText = ' browser might be lacking features (WebRTC, ...)'
        }
      } finally {
        // hide loading indicator
        this.loading = false
      }
      if (errorText.length > 1) {
        var message = {
          color: 'red',
          text: errorText
        }
        this.event.$emit('alert', message);
      }
    }
  }
}
</script>
<template>
  <v-container grid-list-xl>
    <v-layout>
      <v-flex v-if="isPolicy">
        <v-card>
          <v-card-title class="headline grey lighten-2" primary-title>
            Equipment Policy
          </v-card-title>
          <v-card-text>
            By accepting this form, I agree to the following: I am responsible for the equipment or property issued to me; I will use it/them in the manner intended; I will be responsible for any damage done (excluding normal wear and tear); upon separation from the Company, I will return the item(s) issued to me in proper working order (excluding normal wear & tear); I will replace any items issued to me that are damaged or lost at my expense; I authorize a payroll deduction to cover the replacement cost of any item issued to me that is not returned for whatever reason, or is not returned in good working order.
          </v-card-text>
          <v-divider></v-divider>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="primary" flat @click="accepted = true">
              I accept
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-flex>

      <qrcode-reader v-if="accepted && !tryingToLogIn" :video-constraints="{ width: { min: 360, ideal: 680, max: 1920 }, height: { min: 240, ideal: 480, max: 1080 }, facingMode: 'user', audio: false }" @detect="onDetect" @init="onInit" @decode="onDecode" :paused="paused">
      </qrcode-reader>
      <v-flex xs2>
      </v-flex>
      <v-flex class=" mt-5" xs6>

        <v-alert class="white--text text-xs-center mt-5" :value="true" type="info">
          Since the tablet is not checked out to any user, in order to log in you will need to checkout the tablet first. Please note you will be responsible for the equipment until you either transfer to a team member or turn it in at the office.
        </v-alert>
        <div class="text-xs-center mt-5" v-if="isMenu">
          <v-btn v-if="!clockedIn && !tabletAvailable" block color="blue-grey" class="white--text" @click.native="clockIn()">
            Clock In
          </v-btn>
          <v-btn v-if="clockedIn && !hasTablet" block color="blue-grey" class="white--text" @click.native="clockOut()">
            Clock Out
          </v-btn>
          <v-btn v-if="hasTablet" block color="blue-grey" class="white--text" @click.native="checkOutTab()">
            Checkin Tablet
          </v-btn>
          <v-btn v-if="tabletAvailable" block color="blue-grey" class="white--text" @click.native="checkInTab()">
            Checkout Tablet
          </v-btn>
          <v-btn v-if="hasTablet" block color="blue-grey" class="white--text" @click.native="transferTab()">
            Transfer Ownership
          </v-btn>
        </div>
      </v-flex>
      <v-flex xs2>
      </v-flex>
      <v-btn block color="blue-grey" class="white--text" @click.native="makeLogin()">
        Test login
      </v-btn>
    </v-layout>
  </v-container>
</template>

