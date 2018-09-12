import {
  Plugins
} from '@capacitor/core';
const {
  Device
} = Plugins;

export default {
  asyncComputed: {
    async $test() {
      return await Device.getInfo()
    }
  }
}