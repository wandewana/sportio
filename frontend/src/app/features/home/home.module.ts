/**
 * Home Feature Module
 * 
 * Contains the home screen and session discovery components.
 * Lazy loaded for optimal startup performance.
 */
import { NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { NativeScriptRouterModule } from '@nativescript/angular';
import { Routes } from '@angular/router';

import { SharedModule } from '../../shared/shared.module';
import { HomeScreenComponent } from './screens/home-screen/home-screen.component';

const routes: Routes = [
  {
    path: '',
    component: HomeScreenComponent
  }
];

@NgModule({
  declarations: [
    HomeScreenComponent
  ],
  imports: [
    SharedModule,
    NativeScriptRouterModule.forChild(routes)
  ],
  schemas: [NO_ERRORS_SCHEMA]
})
export class HomeModule { }

