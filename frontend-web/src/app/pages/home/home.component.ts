import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonComponent } from '../../components/button/button.component';
import { CardComponent } from '../../components/card/card.component';
import { PillComponent } from '../../components/pill/pill.component';
import { IconButtonComponent } from '../../components/icon-button/icon-button.component';

interface Session {
  id: string;
  sport: string;
  sportIcon: string;
  title: string;
  time: string;
  spotsLeft: number;
  distance: string;
  level: string;
  isIndoor: boolean;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, ButtonComponent, CardComponent, PillComponent, IconButtonComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent {
  welcomeMessage = 'Welcome to Sportio! 🏸';
  subtitle = 'Find your squad, book your game';

  sessions: Session[] = [
    {
      id: '1',
      sport: 'Badminton',
      sportIcon: '🏸',
      title: 'Casual Badminton',
      time: 'Tomorrow • 19:00 • 4 players needed',
      spotsLeft: 2,
      distance: '0.55 km',
      level: 'Intermediate',
      isIndoor: true
    },
    {
      id: '2',
      sport: 'Futsal',
      sportIcon: '⚽',
      title: 'Pickup Futsal',
      time: 'Today • 20:30 • 9/14 joined',
      spotsLeft: 4,
      distance: '1.2 km',
      level: 'All levels',
      isIndoor: true
    },
    {
      id: '3',
      sport: 'Tennis',
      sportIcon: '🎾',
      title: 'Tennis Doubles',
      time: 'Sat • 08:00 • 1 needed',
      spotsLeft: 1,
      distance: '0.4 km',
      level: 'Advanced',
      isIndoor: false
    }
  ];

  onSessionClick(session: Session): void {
    console.log('Session clicked:', session.title);
  }

  onJoinClick(session: Session): void {
    console.log('Join clicked for:', session.id);
  }

  onProposeClick(): void {
    console.log('Propose session clicked');
  }

  onNotificationsClick(): void {
    console.log('Notifications clicked');
  }

  onProfileClick(): void {
    console.log('Profile clicked');
  }
}

