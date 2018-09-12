import {
  Plugins
} from '@capacitor/core';
const {
  Device
} = Plugins;


export default {
  asyncComputed: {
    async $deviceInfo() {
      const $deviceInfo = await Device.getInfo()
      return await $deviceInfo
    }
  }
}