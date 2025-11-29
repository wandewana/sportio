/**
 * App Component
 * 
 * Root component that serves as the application shell.
 * Provides the main navigation structure with Frame.
 */
import { Component, OnInit } from '@angular/core';
import { Application } from '@nativescript/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  
  constructor() {}

  ngOnInit(): void {
    // Apply dark theme by default (matching Sportio design system)
    if (Application.android) {
      // Android-specific initialization
    } else if (Application.ios) {
      // iOS-specific initialization
    }
  }
}

