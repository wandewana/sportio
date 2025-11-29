/**
 * Secondary Button Component
 * 
 * Outline-style button for secondary actions.
 * Matches the .outline-btn class from mockup components.css
 */
import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-secondary-button',
  templateUrl: './secondary-button.component.html',
  styleUrls: ['./secondary-button.component.scss']
})
export class SecondaryButtonComponent {
  /** Button text label */
  @Input() text = 'Button';
  
  /** Disables button interaction when true */
  @Input() disabled = false;
  
  /** Emits when button is pressed */
  @Output() buttonPressed = new EventEmitter<void>();

  onTap(): void {
    if (!this.disabled) {
      this.buttonPressed.emit();
    }
  }
}

