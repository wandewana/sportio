# Sportio Mobile Frontend

A NativeScript Angular mobile application for the Sportio sports matching platform.

## Technology Stack

- **Framework**: Angular 17 with NativeScript
- **State Management**: NgRx Store
- **Styling**: SCSS with custom design system
- **Platform**: iOS and Android

## Project Structure

```
frontend/
├── src/
│   ├── app/
│   │   ├── core/               # Singleton services, guards, interceptors
│   │   │   └── services/       # API, Navigation, Auth services
│   │   ├── shared/             # Reusable components, directives, pipes
│   │   │   └── components/     # UI components (buttons, cards, etc.)
│   │   ├── features/           # Feature modules (lazy loaded)
│   │   │   └── home/           # Home/Discovery feature
│   │   ├── state/              # NgRx store configuration
│   │   ├── app.module.ts       # Root module
│   │   ├── app.component.ts    # Root component
│   │   └── app-routing.module.ts
│   ├── styles/                 # Global SCSS files
│   │   ├── _variables.scss     # Design tokens
│   │   ├── _mixins.scss        # Reusable patterns
│   │   └── main.scss           # Global styles
│   └── environments/           # Environment configs
├── package.json
├── tsconfig.json
├── angular.json
└── nativescript.config.ts
```

## Design System

The app follows the Sportio design system defined in:
- `docs/system-design/sportio_ui_specs.json`
- `docs/mockup/css/variables.css`

### Colors
- Primary: `#06b6d4` (Cyan accent)
- Secondary: `#7c3aed` (Purple)
- Background: `#0f1724` (Dark)
- Surface: `#0b1220` (Card background)

### Typography
- Font: Inter (system fallback)
- Sizes: 12px (small) to 24px (h1)

## Getting Started

### Prerequisites

- Node.js 18+
- NativeScript CLI: `npm install -g nativescript`
- Android Studio (for Android) or Xcode (for iOS)

### Installation

```bash
cd frontend
npm install
```

### Running the App

```bash
# Android
ns run android

# iOS
ns run ios
```

### Development

```bash
# Run tests
npm test

# Lint
npm run lint
```

## Architecture Patterns

### Feature Modules
Each feature is a lazy-loaded module with its own routing:
- `HomeModule` - Session discovery
- `AuthModule` - Authentication (planned)
- `SessionsModule` - Session management (planned)
- `ProfileModule` - User profile (planned)

### State Management
NgRx Store is used for global state:
- App state (loading, auth status, errors)
- Feature-specific stores

### Component Structure
Components follow Angular conventions:
- `.component.ts` - Logic
- `.component.html` - Template
- `.component.scss` - Styles

## Reference

- [NativeScript Angular Docs](https://docs.nativescript.org/angular/)
- [NgRx Documentation](https://ngrx.io/)
- [Sportio UI Specs](../docs/system-design/sportio_ui_specs.json)
- [UI Mockups](../docs/mockup/)

