/**
 * Home Screen Component
 * 
 * Main screen displaying nearby sessions and discovery features.
 * This is a "hello-world" implementation demonstrating the architecture.
 * 
 * @see docs/mockup/index.html - "home" screen
 * @see docs/system-design/sportio_ui_specs.json - HomeScreen specification
 */
import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';

import { AppState, selectIsLoading } from '../../../../state';
import { NavigationService } from '../../../../core';

// Mock session data for hello-world demo
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
  selector: 'app-home-screen',
  templateUrl: './home-screen.component.html',
  styleUrls: ['./home-screen.component.scss']
})
export class HomeScreenComponent implements OnInit {
  /** Loading state from store */
  isLoading$: Observable<boolean>;
  
  /** Welcome message */
  welcomeMessage = 'Welcome to Sportio! 🏸';
  
  /** Subtitle */
  subtitle = 'Find your squad, book your game';
  
  /** Mock sessions for demo */
  sessions: Session[] = [
    {
      id: '1',
      sport: 'Badminton',
      sportIcon: '🏸',
      title: 'Casual Badminton • 2 spots left',
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
      title: 'Pickup Futsal • Need 4 more',
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
      title: 'Tennis Doubles • Find Partner',
      time: 'Sat • 08:00 • 1 needed',
      spotsLeft: 1,
      distance: '0.4 km',
      level: 'Advanced',
      isIndoor: false
    }
  ];

  constructor(
    private store: Store<{ app: AppState }>,
    private navigationService: NavigationService
  ) {
    this.isLoading$ = this.store.select(selectIsLoading);
  }

  ngOnInit(): void {
    console.log('HomeScreen initialized - Sportio Hello World!');
  }

  /**
   * Handle session card tap
   */
  onSessionTap(session: Session): void {
    console.log('Session tapped:', session.title);
    // TODO: Navigate to session detail
  }

  /**
   * Handle join button tap
   */
  onJoinTap(session: Session): void {
    console.log('Join tapped for session:', session.id);
    // TODO: Join session flow
  }

  /**
   * Handle propose session button
   */
  onProposeTap(): void {
    console.log('Propose session tapped');
    // TODO: Navigate to propose session
  }

  /**
   * Handle notifications button
   */
  onNotificationsTap(): void {
    console.log('Notifications tapped');
    // TODO: Navigate to notifications
  }

  /**
   * Handle profile button
   */
  onProfileTap(): void {
    console.log('Profile tapped');
    // TODO: Navigate to profile
  }
}

