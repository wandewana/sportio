import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div 
      class="card"
      [class.card-clickable]="clickable"
      [class.card-horizontal]="horizontal">
      <ng-content></ng-content>
    </div>
  `,
  styles: [`
    .card {
      background: var(--gradient-card);
      border: 1px solid var(--color-border-light);
      border-radius: var(--radius-md);
      padding: var(--spacing-lg);
      transition: all 0.2s ease;
    }
    
    .card-clickable {
      cursor: pointer;
      
      &:hover {
        border-color: var(--color-border);
        transform: translateY(-2px);
      }
    }
    
    .card-horizontal {
      display: flex;
      flex-direction: row;
      align-items: center;
      gap: var(--spacing-md);
    }
  `]
})
export class CardComponent {
  @Input() clickable = false;
  @Input() horizontal = false;
}

