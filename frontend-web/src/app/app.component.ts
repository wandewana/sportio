import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  template: `
    <div class="mobile-frame">
      <div class="mobile-screen">
        <router-outlet></router-outlet>
      </div>
    </div>
  `,
  styles: [`
    .mobile-frame {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
      background: #1a1a2e;
      padding: 20px;
    }
    
    .mobile-screen {
      width: 100%;
      max-width: 390px;
      min-height: 844px;
      background: var(--color-background);
      border-radius: 40px;
      overflow: hidden;
      box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
      position: relative;
    }
    
    @media (max-width: 430px) {
      .mobile-frame {
        padding: 0;
        background: var(--color-background);
      }
      .mobile-screen {
        min-height: 100vh;
        border-radius: 0;
        box-shadow: none;
      }
    }
  `]
})
export class AppComponent {}

