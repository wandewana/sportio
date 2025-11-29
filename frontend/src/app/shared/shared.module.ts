/**
 * Shared Module
 * 
 * Contains reusable components, directives, and pipes
 * that are used across multiple feature modules.
 */
import { NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { NativeScriptCommonModule } from '@nativescript/angular';
import { NativeScriptFormsModule } from '@nativescript/angular';

// Components
import { PrimaryButtonComponent } from './components/primary-button/primary-button.component';
import { SecondaryButtonComponent } from './components/secondary-button/secondary-button.component';
import { CardComponent } from './components/card/card.component';
import { TopBarComponent } from './components/top-bar/top-bar.component';
import { LoadingSpinnerComponent } from './components/loading-spinner/loading-spinner.component';

const SHARED_COMPONENTS = [
  PrimaryButtonComponent,
  SecondaryButtonComponent,
  CardComponent,
  TopBarComponent,
  LoadingSpinnerComponent
];

@NgModule({
  declarations: [
    ...SHARED_COMPONENTS
  ],
  imports: [
    NativeScriptCommonModule,
    NativeScriptFormsModule
  ],
  exports: [
    NativeScriptCommonModule,
    NativeScriptFormsModule,
    ...SHARED_COMPONENTS
  ],
  schemas: [NO_ERRORS_SCHEMA]
})
export class SharedModule { }

