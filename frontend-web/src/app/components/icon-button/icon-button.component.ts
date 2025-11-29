import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-icon-button',
  standalone: true,
  imports: [CommonModule],
  template: `
    <button 
      class="icon-btn"
      [class.icon-btn-filled]="variant === 'filled'"
      [title]="label"
      (click)="onClick()">
      {{ icon }}
    </button>
  `,
  styles: [`
    .icon-btn {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 40px;
      height: 40px;
      font-size: 18px;
      background: transparent;
      border: 1px solid var(--color-border);
      border-radius: var(--radius-sm);
      cursor: pointer;
      transition: all 0.2s ease;
      
      &:hover {
        background: var(--color-glass);
        border-color: var(--color-text-secondary);
      }
    }
    
    .icon-btn-filled {
      background: var(--color-glass);
      border: none;
    }
  `]
})
export class IconButtonComponent {
  @Input() icon = '';
  @Input() label = '';
  @Input() variant: 'outline' | 'filled' = 'outline';
  @Output() pressed = new EventEmitter<void>();

  onClick(): void {
    this.pressed.emit();
  }
}

