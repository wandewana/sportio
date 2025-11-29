/**
 * App Routing Module
 * 
 * Configures the main application routes with lazy loading.
 * Follows NativeScript Router patterns.
 * 
 * @see docs/system-design/sportio_ui_specs.json - navigation_flow
 */
import { NgModule } from '@angular/core';
import { Routes } from '@angular/router';
import { NativeScriptRouterModule } from '@nativescript/angular';

const routes: Routes = [
  // Default route - redirect to home
  {
    path: '',
    redirectTo: '/home',
    pathMatch: 'full'
  },
  
  // Home / Session Discovery
  {
    path: 'home',
    loadChildren: () => 
      import('./features/home/home.module').then(m => m.HomeModule)
  },
  
  // Authentication (placeholder for future implementation)
  // {
  //   path: 'auth',
  //   loadChildren: () => 
  //     import('./features/auth/auth.module').then(m => m.AuthModule)
  // },
  
  // Session Management (placeholder for future implementation)
  // {
  //   path: 'sessions',
  //   loadChildren: () => 
  //     import('./features/sessions/sessions.module').then(m => m.SessionsModule)
  // },
  
  // Profile (placeholder for future implementation)
  // {
  //   path: 'profile',
  //   loadChildren: () => 
  //     import('./features/profile/profile.module').then(m => m.ProfileModule)
  // },

  // Catch-all route
  {
    path: '**',
    redirectTo: '/home'
  }
];

@NgModule({
  imports: [
    NativeScriptRouterModule.forRoot(routes, {
      // Enable debugging in development
      // enableTracing: true
    })
  ],
  exports: [NativeScriptRouterModule]
})
export class AppRoutingModule { }

