import { NativeScriptConfig } from '@nativescript/core';

export default {
  id: 'com.sportio.app',
  appPath: 'src',
  appResourcesPath: 'App_Resources',
  android: {
    v8Flags: '--expose_gc',
    markingMode: 'none',
    codeCache: true
  },
  ios: {
    discardUncaughtJsExceptions: true
  }
} as NativeScriptConfig;

