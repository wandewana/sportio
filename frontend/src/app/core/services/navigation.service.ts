/**
 * Navigation Service
 * 
 * Provides navigation utilities for the NativeScript router.
 * Centralizes navigation logic and history management.
 */
import { Injectable } from '@angular/core';
import { RouterExtensions } from '@nativescript/angular';

@Injectable({
  providedIn: 'root'
})
export class NavigationService {
  constructor(private router: RouterExtensions) {}

  /**
   * Navigate to a route
   */
  navigate(path: string[], options?: NavigateOptions): void {
    this.router.navigate(path, {
      animated: options?.animated ?? true,
      transition: options?.transition ?? {
        name: 'slide',
        duration: 200,
        curve: 'ease'
      },
      clearHistory: options?.clearHistory ?? false
    });
  }

  /**
   * Navigate back
   */
  back(): void {
    if (this.router.canGoBack()) {
      this.router.back();
    }
  }

  /**
   * Navigate to home and clear history
   */
  navigateToHome(): void {
    this.navigate(['/home'], { clearHistory: true });
  }

  /**
   * Navigate to login and clear history
   */
  navigateToLogin(): void {
    this.navigate(['/auth/login'], { clearHistory: true });
  }

  /**
   * Check if can navigate back
   */
  canGoBack(): boolean {
    return this.router.canGoBack();
  }
}

export interface NavigateOptions {
  animated?: boolean;
  clearHistory?: boolean;
  transition?: {
    name: string;
    duration: number;
    curve: string;
  };
}

