/**
 * Core Module
 * 
 * Contains singleton services and providers that should be
 * instantiated only once throughout the application.
 */
import { NgModule, Optional, SkipSelf } from '@angular/core';
import { HTTP_INTERCEPTORS } from '@angular/common/http';

// Services
import { ApiService } from './services/api.service';
import { NavigationService } from './services/navigation.service';

@NgModule({
  providers: [
    ApiService,
    NavigationService
  ]
})
export class CoreModule {
  /**
   * Prevents reimporting the CoreModule.
   * Only the AppModule should import this.
   */
  constructor(@Optional() @SkipSelf() parentModule: CoreModule) {
    if (parentModule) {
      throw new Error(
        'CoreModule is already loaded. Import it only in AppModule.'
      );
    }
  }
}

