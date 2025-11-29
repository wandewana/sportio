/**
 * Top Bar Component
 * 
 * Header navigation bar with title and actions.
 * Matches the .topbar class from mockup components.css
 */
import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-top-bar',
  templateUrl: './top-bar.component.html',
  styleUrls: ['./top-bar.component.scss']
})
export class TopBarComponent {
  /** Main title text */
  @Input() title = '';
  
  /** Subtitle/description text */
  @Input() subtitle = '';
  
  /** Show back navigation button */
  @Input() showBackButton = false;
  
  /** Emits when back button is pressed */
  @Output() backPressed = new EventEmitter<void>();

  onBackTap(): void {
    this.backPressed.emit();
  }
}

