/**
 * Loading Spinner Component
 * 
 * Shows a loading indicator with optional message.
 */
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-loading-spinner',
  templateUrl: './loading-spinner.component.html',
  styleUrls: ['./loading-spinner.component.scss']
})
export class LoadingSpinnerComponent {
  /** Optional loading message */
  @Input() message = 'Loading...';
  
  /** Size variant */
  @Input() size: 'small' | 'medium' | 'large' = 'medium';
}

