import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-pill',
  standalone: true,
  imports: [CommonModule],
  template: `
    <span 
      class="pill"
      [class.pill-primary]="variant === 'primary'"
      [class.pill-secondary]="variant === 'secondary'">
      {{ text }}
    </span>
  `,
  styles: [`
    .pill {
      display: inline-flex;
      align-items: center;
      padding: 4px 10px;
      font-size: 11px;
      font-weight: 500;
      border-radius: var(--radius-full);
      background: var(--color-glass);
      color: var(--color-text-secondary);
      border: 1px solid var(--color-border-light);
    }
    
    .pill-primary {
      background: rgba(6, 182, 212, 0.15);
      color: var(--color-primary);
      border-color: rgba(6, 182, 212, 0.3);
    }
    
    .pill-secondary {
      background: rgba(124, 58, 237, 0.15);
      color: var(--color-secondary);
      border-color: rgba(124, 58, 237, 0.3);
    }
  `]
})
export class PillComponent {
  @Input() text = '';
  @Input() variant: 'default' | 'primary' | 'secondary' = 'default';
}

