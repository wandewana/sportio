/**
 * Card Component
 * 
 * Reusable card container with Sportio styling.
 * Matches the .card class from mockup components.css
 */
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss']
})
export class CardComponent {
  /** Optional horizontal layout mode */
  @Input() horizontal = false;
  
  /** Optional padding override */
  @Input() padding: 'sm' | 'md' | 'lg' = 'md';
}

