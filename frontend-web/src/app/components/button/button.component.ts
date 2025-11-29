import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-button',
  standalone: true,
  imports: [CommonModule],
  template: `
    <button 
      [class]="'btn btn-' + variant"
      [class.btn-full]="fullWidth"
      [class.btn-loading]="loading"
      [disabled]="disabled || loading"
      (click)="onClick()">
      <span class="btn-icon" *ngIf="icon">{{ icon }}</span>
      <span class="btn-text">{{ text }}</span>
      <span class="btn-loader" *ngIf="loading"></span>
    </button>
  `,
  styles: [`
    .btn {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      padding: 10px 20px;
      font-size: 13px;
      font-weight: 600;
      font-family: inherit;
      border: none;
      border-radius: var(--radius-sm);
      cursor: pointer;
      transition: all 0.2s ease;
      white-space: nowrap;
      
      &:disabled {
        opacity: 0.5;
        cursor: not-allowed;
      }
    }
    
    .btn-primary {
      background: var(--gradient-primary);
      color: white;
      
      &:hover:not(:disabled) {
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(6, 182, 212, 0.3);
      }
    }
    
    .btn-secondary {
      background: transparent;
      color: var(--color-text-primary);
      border: 1px solid var(--color-border);
      
      &:hover:not(:disabled) {
        background: var(--color-glass);
        border-color: var(--color-text-secondary);
      }
    }
    
    .btn-text {
      background: transparent;
      color: var(--color-primary);
      padding: 8px 12px;
      
      &:hover:not(:disabled) {
        background: var(--color-glass);
      }
    }
    
    .btn-full {
      width: 100%;
    }
    
    .btn-icon {
      font-size: 16px;
    }
    
    .btn-loading .btn-text {
      opacity: 0;
    }
    
    .btn-loader {
      position: absolute;
      width: 16px;
      height: 16px;
      border: 2px solid transparent;
      border-top-color: currentColor;
      border-radius: 50%;
      animation: spin 0.8s linear infinite;
    }
    
    @keyframes spin {
      to { transform: rotate(360deg); }
    }
  `]
})
export class ButtonComponent {
  @Input() text = 'Button';
  @Input() variant: 'primary' | 'secondary' | 'text' = 'primary';
  @Input() icon = '';
  @Input() fullWidth = false;
  @Input() loading = false;
  @Input() disabled = false;
  @Output() pressed = new EventEmitter<void>();

  onClick(): void {
    if (!this.disabled && !this.loading) {
      this.pressed.emit();
    }
  }
}

