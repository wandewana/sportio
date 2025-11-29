/**
 * Primary Button Component
 * 
 * Main action button with Sportio accent styling.
 * Matches the .btn class from mockup components.css
 */
import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-primary-button',
  templateUrl: './primary-button.component.html',
  styleUrls: ['./primary-button.component.scss']
})
export class PrimaryButtonComponent {
  /** Button text label */
  @Input() text = 'Button';
  
  /** Shows loading indicator when true */
  @Input() loading = false;
  
  /** Disables button interaction when true */
  @Input() disabled = false;
  
  /** Optional icon to display before text */
  @Input() icon = '';
  
  /** Emits when button is pressed */
  @Output() buttonPressed = new EventEmitter<void>();

  onTap(): void {
    if (!this.disabled && !this.loading) {
      this.buttonPressed.emit();
    }
  }
}

