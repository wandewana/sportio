/**
 * Sportio Mobile App - Entry Point
 * 
 * This is the main entry point for the NativeScript Angular application.
 * It bootstraps the AppModule to start the application.
 */
import { platformNativeScript, runNativeScriptAngularApp } from '@nativescript/angular';
import { AppModule } from './app/app.module';

runNativeScriptAngularApp({
  appModuleBootstrap: () => platformNativeScript().bootstrapModule(AppModule),
});

