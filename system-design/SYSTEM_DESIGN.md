# Sportio Backend System Design Document

## Technology Stack
- **Backend Framework**: Spring WebFlux (Reactive)
- **Database**: PostgreSQL with R2DBC
- **Real-time**: WebSocket with Spring WebFlux
- **Message Queue**: Redis Pub/Sub for real-time events
- **Cache**: Redis for session/geo-queries
- **Payment**: Stripe API
- **Push Notifications**: FCM/APNs
- **Email**: SendGrid

---

## 1. Entity-Relationship Diagram (ERD)

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                    SPORTIO ERD                                          │
└─────────────────────────────────────────────────────────────────────────────────────────┘

┌──────────────────┐       ┌──────────────────┐       ┌──────────────────┐
│      users       │       │     sessions     │       │      venues      │
├──────────────────┤       ├──────────────────┤       ├──────────────────┤
│ id (PK)          │──┐    │ id (PK)          │    ┌──│ id (PK)          │
│ email (UNIQUE)   │  │    │ host_id (FK)     │────┘  │ name             │
│ password_hash    │  │    │ sport_type       │       │ description      │
│ full_name        │  │    │ title            │       │ address          │
│ avatar_url       │  │    │ description      │       │ latitude         │
│ avatar_initials  │  │    │ date             │       │ longitude        │
│ skill_level      │  │    │ time_start       │       │ type (INDOOR/OUT)│
│ games_played     │  │    │ time_end         │       │ amenities[]      │
│ bio              │  │    │ players_needed   │       │ sports_available │
│ member_since     │  └───>│ visibility       │       │ rating           │
│ created_at       │       │ status           │       │ review_count     │
│ updated_at       │       │ latitude         │       │ price_per_hour   │
└──────────────────┘       │ longitude        │       │ operating_hours  │
         │                 │ created_at       │       │ contact_phone    │
         │                 └──────────────────┘       │ photos[]         │
         │                          │                 └──────────────────┘
         │                          │                          │
         │    ┌─────────────────────┼──────────────────────────┘
         │    │                     │
         ▼    ▼                     ▼
┌──────────────────┐       ┌──────────────────┐       ┌──────────────────┐
│ session_players  │       │     bookings     │       │   venue_slots    │
├──────────────────┤       ├──────────────────┤       ├──────────────────┤
│ id (PK)          │       │ id (PK)          │       │ id (PK)          │
│ session_id (FK)  │       │ session_id (FK)  │       │ venue_id (FK)    │
│ user_id (FK)     │       │ venue_id (FK)    │       │ date             │
│ is_host          │       │ date             │       │ time_start       │
│ status (JOINED/  │       │ time_start       │       │ time_end         │
│   MAYBE/LEFT)    │       │ time_end         │       │ is_available     │
│ joined_at        │       │ total_cost       │       │ booking_id (FK)  │
│ payment_status   │       │ cost_per_person  │       └──────────────────┘
│ payment_amount   │       │ split_payment    │
│ paid_at          │       │ status           │
└──────────────────┘       │ created_at       │
                           └──────────────────┘
         │                          │
         │                          │
         ▼                          ▼
┌──────────────────┐       ┌──────────────────┐       ┌──────────────────┐
│     payments     │       │     messages     │       │  notifications   │
├──────────────────┤       ├──────────────────┤       ├──────────────────┤
│ id (PK)          │       │ id (PK)          │       │ id (PK)          │
│ booking_id (FK)  │       │ session_id (FK)  │       │ user_id (FK)     │
│ user_id (FK)     │       │ sender_id (FK)   │       │ type             │
│ amount           │       │ text             │       │ icon             │
│ currency         │       │ is_system        │       │ title            │
│ stripe_payment_id│       │ created_at       │       │ body             │
│ status           │       └──────────────────┘       │ action_type      │
│ created_at       │                                  │ action_url       │
└──────────────────┘                                  │ is_read          │
                                                      │ created_at       │
┌──────────────────┐       ┌──────────────────┐       └──────────────────┘
│     reports      │       │     devices      │
├──────────────────┤       ├──────────────────┤       ┌──────────────────┐
│ id (PK)          │       │ id (PK)          │       │  refresh_tokens  │
│ reporter_id (FK) │       │ user_id (FK)     │       ├──────────────────┤
│ reported_user_id │       │ device_token     │       │ id (PK)          │
│ session_id (FK)  │       │ platform         │       │ user_id (FK)     │
│ reason_id        │       │ app_version      │       │ token_hash       │
│ description      │       │ created_at       │       │ expires_at       │
│ status           │       │ updated_at       │       │ created_at       │
│ created_at       │       └──────────────────┘       └──────────────────┘
│ resolved_at      │
└──────────────────┘       ┌──────────────────┐
                           │ session_invites  │
┌──────────────────┐       ├──────────────────┤
│notification_prefs│       │ id (PK)          │
├──────────────────┤       │ session_id (FK)  │
│ user_id (PK, FK) │       │ inviter_id (FK)  │
│ push_enabled     │       │ invitee_id (FK)  │
│ session_invites  │       │ message          │
│ player_joined    │       │ status           │
│ chat_messages    │       │ created_at       │
│ payment_reminders│       └──────────────────┘
│ session_reminders│
│ marketing        │       ┌──────────────────┐
└──────────────────┘       │  payment_methods │
                           ├──────────────────┤
┌──────────────────┐       │ id (PK)          │
│   sport_types    │       │ user_id (FK)     │
├──────────────────┤       │ stripe_pm_id     │
│ id (PK)          │       │ type             │
│ name             │       │ last4            │
│ icon             │       │ brand            │
│ default_players  │       │ exp_month        │
└──────────────────┘       │ exp_year         │
                           │ is_default       │
                           └──────────────────┘
```

### Enum Definitions
```sql
-- Session Status
CREATE TYPE session_status AS ENUM ('pending', 'open', 'booked', 'completed', 'cancelled');

-- Player Status
CREATE TYPE player_status AS ENUM ('joined', 'maybe', 'left');

-- Payment Status
CREATE TYPE payment_status AS ENUM ('pending', 'processing', 'success', 'failed', 'refunded');

-- Booking Status
CREATE TYPE booking_status AS ENUM ('pending', 'confirmed', 'cancelled', 'completed');

-- Skill Level
CREATE TYPE skill_level AS ENUM ('Beginner', 'Intermediate', 'Advanced', 'All levels');

-- Venue Type
CREATE TYPE venue_type AS ENUM ('Indoor', 'Outdoor');

-- Report Status
CREATE TYPE report_status AS ENUM ('submitted', 'under_review', 'resolved', 'dismissed');

-- Visibility
CREATE TYPE visibility_type AS ENUM ('public', 'private');

-- Notification Type
CREATE TYPE notification_type AS ENUM (
  'session_invite', 'player_joined', 'player_left', 'booking_confirmed',
  'payment_reminder', 'payment_received', 'session_reminder', 'slot_opened',
  'chat_message', 'session_cancelled', 'report_update'
);
```

---

## 2. System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                              SPORTIO SYSTEM ARCHITECTURE                                │
└─────────────────────────────────────────────────────────────────────────────────────────┘

                                    ┌─────────────────┐
                                    │   Mobile Apps   │
                                    │  (iOS/Android)  │
                                    └────────┬────────┘
                                             │
                              ┌──────────────┼──────────────┐
                              │ HTTPS/WSS    │              │
                              ▼              ▼              ▼
                    ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
                    │ API Gateway │  │  WebSocket  │  │    CDN      │
                    │   (Kong)    │  │   Gateway   │  │ (CloudFront)│
                    └──────┬──────┘  └──────┬──────┘  └─────────────┘
                           │                │
                           │    ┌───────────┘
                           ▼    ▼
┌──────────────────────────────────────────────────────────────────────────────────────┐
│                              SPRING WEBFLUX APPLICATION                              │
│  ┌────────────────────────────────────────────────────────────────────────────────┐  │
│  │                              CONTROLLER LAYER                                   │  │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ │  │
│  │  │   Auth   │ │ Session  │ │ Booking  │ │   Chat   │ │  Profile │ │  Venue   │ │  │
│  │  │Controller│ │Controller│ │Controller│ │Controller│ │Controller│ │Controller│ │  │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘ │  │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐                                        │  │
│  │  │Notificat.│ │  Report  │ │  Payment │                                        │  │
│  │  │Controller│ │Controller│ │Controller│                                        │  │
│  │  └──────────┘ └──────────┘ └──────────┘                                        │  │
│  └────────────────────────────────────────────────────────────────────────────────┘  │
│  ┌────────────────────────────────────────────────────────────────────────────────┐  │
│  │                               SERVICE LAYER                                     │  │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ │  │
│  │  │   Auth   │ │ Session  │ │ Booking  │ │   Chat   │ │  User    │ │  Venue   │ │  │
│  │  │ Service  │ │ Service  │ │ Service  │ │ Service  │ │ Service  │ │ Service  │ │  │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘ │  │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐              │  │
│  │  │Notificat.│ │  Report  │ │ Payment  │ │   Geo    │ │ AutoMatch│              │  │
│  │  │ Service  │ │ Service  │ │ Service  │ │ Service  │ │ Service  │              │  │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘              │  │
│  └────────────────────────────────────────────────────────────────────────────────┘  │
│  ┌────────────────────────────────────────────────────────────────────────────────┐  │
│  │                            REPOSITORY LAYER (R2DBC)                             │  │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ │  │
│  │  │  User    │ │ Session  │ │ Booking  │ │ Message  │ │  Venue   │ │ Payment  │ │  │
│  │  │Repository│ │Repository│ │Repository│ │Repository│ │Repository│ │Repository│ │  │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘ │  │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐                           │  │
│  │  │Notificat.│ │  Report  │ │  Device  │ │  Invite  │                           │  │
│  │  │Repository│ │Repository│ │Repository│ │Repository│                           │  │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘                           │  │
│  └────────────────────────────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────────────────────────┘
           │              │              │              │              │
           ▼              ▼              ▼              ▼              ▼
    ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐
    │ PostgreSQL │ │   Redis    │ │   Stripe   │ │  FCM/APNs  │ │  SendGrid  │
    │   (R2DBC)  │ │Cache/PubSub│ │  Payments  │ │    Push    │ │   Email    │
    └────────────┘ └────────────┘ └────────────┘ └────────────┘ └────────────┘
```

---

## 3. Component Design

### 3.1 Controller Layer (API Endpoints)

| Controller | Base Path | Endpoints | Description |
|------------|-----------|-----------|-------------|
| **AuthController** | `/api/v1/auth` | POST `/login`, POST `/register`, POST `/logout`, POST `/forgot-password`, POST `/refresh-token` | Authentication & token management |
| **SessionController** | `/api/v1/sessions` | GET `/nearby`, GET `/search`, GET `/{id}`, POST `/`, PUT `/{id}`, DELETE `/{id}`, POST `/{id}/join`, POST `/{id}/leave`, POST `/{id}/maybe`, POST `/{id}/lock` | Session CRUD & player actions |
| **LobbyController** | `/api/v1/sessions/{id}/lobby` | GET `/`, GET `/auto-match`, POST `/invite` | Lobby management & invites |
| **VenueController** | `/api/v1/venues` | GET `/`, GET `/{id}`, GET `/{id}/slots` | Venue discovery & availability |
| **BookingController** | `/api/v1/bookings` | POST `/`, GET `/{id}`, POST `/{id}/confirm`, POST `/{id}/cancel`, GET `/{id}/payment-status` | Booking lifecycle |
| **PaymentController** | `/api/v1/payments` | POST `/`, GET `/{id}`, POST `/request`, GET `/methods`, POST `/methods` | Payment processing |
| **ChatController** | `/api/v1/sessions/{id}/chat` | GET `/messages`, POST `/messages` | Chat history (REST fallback) |
| **ProfileController** | `/api/v1/users` | GET `/me`, PUT `/me`, GET `/{id}`, GET `/{id}/stats`, GET `/{id}/history` | User profile management |
| **NotificationController** | `/api/v1/notifications` | GET `/`, PUT `/{id}/read`, PUT `/read-all`, POST `/device`, DELETE `/device/{id}`, GET `/settings`, PUT `/settings` | Notification management |
| **ReportController** | `/api/v1/reports` | GET `/reasons`, POST `/` | Report submission |
| **MapController** | `/api/v1/map` | GET `/pins`, GET `/clusters` | Map view data |

## 4. Data Flow Diagrams

### 4.1 Session Discovery Flow
```
┌─────────┐    GET /sessions/nearby         ┌────────────────┐
│  Client │ ─────────────────────────────▶  │ SessionController│
└─────────┘   {lat, lng, radius, sport}     └───────┬────────┘
                                                    │
                                                    ▼
                                            ┌────────────────┐
                                            │ SessionService │
                                            └───────┬────────┘
                                                    │
                          ┌─────────────────────────┼─────────────────────────┐
                          ▼                         ▼                         ▼
                  ┌──────────────┐          ┌──────────────┐          ┌──────────────┐
                  │  GeoService  │          │ SessionRepo  │          │  Redis Cache │
                  │ (PostGIS)    │          │   (R2DBC)    │          │  (Geo Query) │
                  └──────────────┘          └──────────────┘          └──────────────┘
                          │                         │                         │
                          └─────────────────────────┼─────────────────────────┘
                                                    ▼
                                          ┌──────────────────┐
                                          │ Flux<SessionDTO> │
                                          │ (Enriched with   │
                                          │  player count,   │
                                          │  distance, etc.) │
                                          └──────────────────┘
```

### 4.2 Booking Flow (Critical Path)
```
┌──────────────────────────────────────────────────────────────────────────────────────┐
│                              BOOKING FLOW STATE MACHINE                              │
└──────────────────────────────────────────────────────────────────────────────────────┘

    ┌─────────┐     Host clicks      ┌─────────────┐    Select venue    ┌─────────────┐
    │ Session │ ──────────────────▶  │   Venue     │ ─────────────────▶ │   Booking   │
    │  OPEN   │     "Lock & Book"    │  Selection  │                    │ Time Select │
    └─────────┘                      └─────────────┘                    └──────┬──────┘
                                                                               │
                                                                    Confirm & Lock
                                                                               │
                                                                               ▼
    ┌─────────────────────────────────────────────────────────────────────────────────┐
    │                            BOOKING CONFIRMATION                                  │
    │  ┌─────────────────────────────────────────────────────────────────────────┐    │
    │  │ 1. Create Booking record (status: PENDING)                              │    │
    │  │ 2. Reserve Venue Slot (optimistic lock)                                 │    │
    │  │ 3. Update Session status to BOOKED                                      │    │
    │  │ 4. Create Payment records for each player (split)                       │    │
    │  │ 5. Send notifications to all joined players                             │    │
    │  │ 6. Trigger payment request via Stripe                                   │    │
    │  └─────────────────────────────────────────────────────────────────────────┘    │
    └─────────────────────────────────────────────────────────────────────────────────┘
                                           │
                      ┌────────────────────┼────────────────────┐
                      ▼                    ▼                    ▼
              ┌──────────────┐     ┌──────────────┐     ┌──────────────┐
              │   Payment    │     │   Payment    │     │   Payment    │
              │   Player 1   │     │   Player 2   │     │   Player N   │
              │   PENDING    │     │   PENDING    │     │   PENDING    │
              └──────┬───────┘     └──────┬───────┘     └──────┬───────┘
                     │                    │                    │
                     ▼                    ▼                    ▼
              ┌──────────────┐     ┌──────────────┐     ┌──────────────┐
              │   Stripe     │     │   Stripe     │     │   Stripe     │
              │   Checkout   │     │   Checkout   │     │   Checkout   │
              └──────┬───────┘     └──────┬───────┘     └──────┬───────┘
                     │                    │                    │
                     └────────────────────┼────────────────────┘
                                          ▼
                              ┌───────────────────────┐
                              │   Stripe Webhook      │
                              │   payment.succeeded   │
                              └───────────┬───────────┘
                                          │
                                          ▼
                              ┌───────────────────────┐
                              │ Update Payment Status │
                              │ Check: All Paid?      │
                              │ If yes: CONFIRMED     │
                              └───────────────────────┘
```


### 4.3 Real-time Chat & WebSocket Flow
```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                              WEBSOCKET ARCHITECTURE                                      │
└─────────────────────────────────────────────────────────────────────────────────────────┘

┌─────────┐         WSS /ws/session/{sessionId}          ┌───────────────────┐
│ Client  │ ◀──────────────────────────────────────────▶ │ WebSocket Handler │
│   A     │                                              │ (Spring WebFlux)  │
└─────────┘                                              └─────────┬─────────┘
                                                                   │
┌─────────┐         WSS /ws/session/{sessionId}                    │
│ Client  │ ◀──────────────────────────────────────────────────────┤
│   B     │                                              ┌─────────▼─────────┐
└─────────┘                                              │   Redis Pub/Sub   │
                                                         │ Channel: session: │
┌─────────┐         WSS /ws/session/{sessionId}          │    {sessionId}    │
│ Client  │ ◀──────────────────────────────────────────────────────┤
│   C     │                                              └─────────┬─────────┘
└─────────┘                                                        │
                                                         ┌─────────▼─────────┐
                                                         │  Message Types:   │
                                                         │  - chat.message   │
                                                         │  - player.joined  │
                                                         │  - player.left    │
                                                         │  - session.locked │
                                                         │  - booking.update │
                                                         │  - payment.status │
                                                         └───────────────────┘
```

**WebSocket Event Types:**
```json
{
  "events": [
    {"type": "chat.message", "payload": {"senderId": "uuid", "text": "string", "timestamp": "ISO8601"}},
    {"type": "chat.typing", "payload": {"userId": "uuid", "isTyping": true}},
    {"type": "player.joined", "payload": {"userId": "uuid", "name": "string", "avatarInitials": "AB"}},
    {"type": "player.left", "payload": {"userId": "uuid", "name": "string"}},
    {"type": "player.maybe", "payload": {"userId": "uuid", "name": "string"}},
    {"type": "session.locked", "payload": {"lockedBy": "uuid", "venueId": "uuid"}},
    {"type": "session.cancelled", "payload": {"reason": "string"}},
    {"type": "booking.confirmed", "payload": {"venueId": "uuid", "timeSlot": "19:00-20:00", "totalCost": 32.00}},
    {"type": "payment.updated", "payload": {"paidCount": 2, "totalCount": 4, "userId": "uuid"}},
    {"type": "payment.reminder", "payload": {"amount": 8.00, "deadline": "ISO8601"}}
  ]
}
```

### 4.4 Notification Flow
```
┌───────────────────┐     ┌───────────────────┐     ┌───────────────────┐
│   Event Source    │────▶│NotificationService│────▶│  Notification DB  │
│ (Session/Booking/ │     │                   │     │     (Store)       │
│  Payment/Chat)    │     └─────────┬─────────┘     └───────────────────┘
└───────────────────┘               │
                                    │
                    ┌───────────────┼───────────────┐
                    ▼               ▼               ▼
            ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
            │  In-App      │ │  FCM (Android)│ │ APNs (iOS)  │
            │  WebSocket   │ │  Push Service │ │ Push Service│
            └──────────────┘ └──────────────┘ └──────────────┘

Notification Types & Triggers:
┌──────────────────────┬────────────────────────────────────────────────────────┐
│ Event                │ Notification                                           │
├──────────────────────┼────────────────────────────────────────────────────────┤
│ Player joins session │ → Host receives "John joined your Badminton session"   │
│ Session locked       │ → All players: "Session locked, venue booking started" │
│ Booking confirmed    │ → All players: "Court booked at BlueCourt 19:00"       │
│ Payment required     │ → Player: "Pay $8 for Badminton session"               │
│ Payment received     │ → Host: "John paid $8 (3/4 paid)"                       │
│ Session reminder     │ → All: "Badminton in 2 hours at BlueCourt"             │
│ Slot opened nearby   │ → User: "A slot opened at BlueCourt 20:00 — 0.8 km"    │
│ Chat message         │ → Other players: "New message in session chat"         │
│ Session cancelled    │ → All players: "Session cancelled by host"             │
└──────────────────────┴────────────────────────────────────────────────────────┘
```

---

## 5. API Implementation Mapping

### 5.1 Authentication (API001)

| Endpoint | Method | Controller | Service Method | Repository | Notes |
|----------|--------|------------|----------------|------------|-------|
| `/auth/login` | POST | AuthController | authenticate(email, password) | UserRepository.findByEmail | Returns JWT access + refresh tokens |
| `/auth/register` | POST | AuthController | register(RegisterDTO) | UserRepository.save | Validates email uniqueness |
| `/auth/logout` | POST | AuthController | logout(refreshToken) | RefreshTokenRepository.delete | Invalidates refresh token |
| `/auth/forgot-password` | POST | AuthController | forgotPassword(email) | UserRepository.findByEmail | Sends email via SendGrid |
| `/auth/refresh-token` | POST | AuthController | refreshToken(token) | RefreshTokenRepository.findByToken | Issues new access token |

### 5.2 Session Management (API002-007)

| Endpoint | Method | Controller | Service Method | Validation |
|----------|--------|------------|----------------|------------|
| `/sessions/nearby` | GET | SessionController | findNearby(lat, lng, radius, sport) | Requires location |
| `/sessions/search` | GET | SessionController | search(query, filters) | Optional filters |
| `/sessions` | POST | SessionController | createSession(CreateSessionDTO) | Auth required, validates sport/time |
| `/sessions/{id}` | GET | SessionController | getSessionDetail(id, userId) | Returns player list, host info |
| `/sessions/{id}/join` | POST | SessionController | joinSession(id, userId) | Not full, not already joined |
| `/sessions/{id}/leave` | POST | SessionController | leaveSession(id, userId) | Must be joined, not host |
| `/sessions/{id}/maybe` | POST | SessionController | markMaybe(id, userId) | Updates player status |
| `/sessions/{id}/lock` | POST | SessionController | lockSession(id, hostId) | Host only, has joined players |
| `/sessions/{id}/lobby` | GET | LobbyController | getLobbyDetails(id) | Returns players, suggestions |
| `/sessions/{id}/lobby/auto-match` | GET | LobbyController | getAutoMatchSuggestions(id) | Skill-based matching |
| `/sessions/{id}/lobby/invite` | POST | LobbyController | invitePlayer(sessionId, inviteeId) | Host only |

### 5.3 Venue & Booking (API008-010)

| Endpoint | Method | Controller | Service Method | Business Logic |
|----------|--------|------------|----------------|----------------|
| `/venues` | GET | VenueController | findNearby(lat, lng, sport, playerCount) | Filters by sport, calculates price/person |
| `/venues/{id}` | GET | VenueController | getVenueDetail(id) | Full venue info, amenities |
| `/venues/{id}/slots` | GET | VenueController | getAvailableSlots(id, date) | Real-time availability check |
| `/bookings` | POST | BookingController | createBooking(CreateBookingDTO) | Validates slot, calculates split |
| `/bookings/{id}` | GET | BookingController | getBookingDetail(id) | Payment status per player |
| `/bookings/{id}/confirm` | POST | BookingController | confirmBooking(id) | Reserves slot, triggers payments |
| `/bookings/{id}/cancel` | POST | BookingController | cancelBooking(id, reason) | Refund logic if applicable |
| `/bookings/{id}/payment-status` | GET | BookingController | getPaymentStatus(id) | X of Y paid |

### 5.4 Chat (API011)

| Endpoint | Method | Controller | Service Method | Real-time |
|----------|--------|------------|----------------|-----------|
| `/sessions/{id}/chat/messages` | GET | ChatController | getMessages(sessionId, page, size) | Paginated history |
| `/sessions/{id}/chat/messages` | POST | ChatController | sendMessage(sessionId, text) | Also broadcasts via WebSocket |
| `WS /ws/session/{id}` | WebSocket | ChatWebSocketHandler | handleMessage() | Real-time bidirectional |

### 5.5 Profile & Notifications (API012-013)

| Endpoint | Method | Controller | Service Method |
|----------|--------|------------|----------------|
| `/users/me` | GET | ProfileController | getCurrentUser() |
| `/users/me` | PUT | ProfileController | updateProfile(UpdateProfileDTO) |
| `/users/{id}` | GET | ProfileController | getUserProfile(id) |
| `/users/{id}/stats` | GET | ProfileController | getUserStats(id) |
| `/users/{id}/history` | GET | ProfileController | getMatchHistory(id) |
| `/notifications` | GET | NotificationController | getNotifications(page, size) |
| `/notifications/{id}/read` | PUT | NotificationController | markAsRead(id) |
| `/notifications/read-all` | PUT | NotificationController | markAllAsRead() |
| `/notifications/device` | POST | NotificationController | registerDevice(token, platform) |
| `/notifications/settings` | GET/PUT | NotificationController | getSettings() / updateSettings() |

### 5.6 Reports (API014)

| Endpoint | Method | Controller | Service Method |
|----------|--------|------------|----------------|
| `/reports/reasons` | GET | ReportController | getReportReasons() |
| `/reports` | POST | ReportController | submitReport(ReportDTO) |

---

## 6. Security & Cross-Cutting Concerns

### 6.1 JWT Authentication Flow
```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           JWT AUTHENTICATION                                │
└─────────────────────────────────────────────────────────────────────────────┘

                           ┌───────────────────┐
    Login Request ────────▶│  AuthController   │
    {email, password}      └─────────┬─────────┘
                                     │
                                     ▼
                           ┌───────────────────┐
                           │   AuthService     │
                           │ - Validate creds  │
                           │ - Generate tokens │
                           └─────────┬─────────┘
                                     │
                    ┌────────────────┴────────────────┐
                    ▼                                 ▼
          ┌─────────────────┐               ┌─────────────────┐
          │  Access Token   │               │  Refresh Token  │
          │  (15 min TTL)   │               │  (7 day TTL)    │
          │  JWT signed     │               │  Stored in DB   │
          └─────────────────┘               └─────────────────┘
                    │
                    ▼
    ┌─────────────────────────────────────────────────────────────────────┐
    │                    SUBSEQUENT API REQUESTS                          │
    │  Authorization: Bearer <access_token>                               │
    │                                                                     │
    │  ┌───────────────┐     ┌───────────────┐     ┌───────────────┐      │
    │  │  JWT Filter   │────▶│ Token Valid?  │────▶│  Set Security │      │
    │  │  (WebFilter)  │     │  Verify sig   │     │    Context    │      │
    │  └───────────────┘     └───────────────┘     └───────────────┘      │
    └─────────────────────────────────────────────────────────────────────┘
```

### 6.2 Error Handling

```java
// Global exception handler for WebFlux
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SessionNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleSessionNotFound(SessionNotFoundException ex) {
        return Mono.just(ResponseEntity.status(404)
            .body(new ErrorResponse("SESSION_NOT_FOUND", ex.getMessage())));
    }

    @ExceptionHandler(SessionFullException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleSessionFull(SessionFullException ex) {
        return Mono.just(ResponseEntity.status(409)
            .body(new ErrorResponse("SESSION_FULL", "All spots are filled")));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUnauthorized(UnauthorizedException ex) {
        return Mono.just(ResponseEntity.status(403)
            .body(new ErrorResponse("FORBIDDEN", ex.getMessage())));
    }

    @ExceptionHandler(SlotAlreadyBookedException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleSlotConflict(SlotAlreadyBookedException ex) {
        return Mono.just(ResponseEntity.status(409)
            .body(new ErrorResponse("SLOT_UNAVAILABLE", "Time slot was just booked")));
    }
}
```

### 6.3 Concurrency Handling (TC016-14: Concurrent Booking)

```java
// Optimistic locking for venue slot reservation
@Service
public class BookingService {

    @Transactional
    public Mono<Booking> confirmBooking(UUID bookingId, UUID hostId) {
        return bookingRepository.findById(bookingId)
            .flatMap(booking -> {
                // Optimistic lock on venue slot
                return venueSlotRepository.reserveSlotWithLock(
                    booking.getVenueId(),
                    booking.getDate(),
                    booking.getTimeStart(),
                    booking.getId()
                )
                .onErrorResume(OptimisticLockingFailureException.class,
                    ex -> Mono.error(new SlotAlreadyBookedException()))
                .then(Mono.just(booking));
            })
            .flatMap(booking -> {
                booking.setStatus(BookingStatus.CONFIRMED);
                return bookingRepository.save(booking);
            })
            .flatMap(this::triggerPaymentsForAllPlayers)
            .flatMap(this::sendBookingConfirmationNotifications);
    }
}
```

---

## 7. Database Indexes & Performance

```sql
-- Geospatial index for session/venue proximity queries
CREATE INDEX idx_sessions_location ON sessions USING GIST (
    ST_MakePoint(longitude, latitude)::geography
);
CREATE INDEX idx_venues_location ON venues USING GIST (
    ST_MakePoint(longitude, latitude)::geography
);

-- Session discovery queries
CREATE INDEX idx_sessions_status_date ON sessions (status, date) WHERE status = 'open';
CREATE INDEX idx_sessions_sport_status ON sessions (sport_type, status);
CREATE INDEX idx_sessions_host ON sessions (host_id);

-- Player lookups
CREATE INDEX idx_session_players_session ON session_players (session_id);
CREATE INDEX idx_session_players_user ON session_players (user_id);
CREATE INDEX idx_session_players_session_status ON session_players (session_id, status);

-- Booking queries
CREATE INDEX idx_bookings_session ON bookings (session_id);
CREATE INDEX idx_bookings_venue_date ON bookings (venue_id, date);
CREATE INDEX idx_venue_slots_availability ON venue_slots (venue_id, date, is_available);

-- Chat message ordering
CREATE INDEX idx_messages_session_created ON messages (session_id, created_at DESC);

-- Notification queries
CREATE INDEX idx_notifications_user_read ON notifications (user_id, is_read, created_at DESC);

-- Auth
CREATE UNIQUE INDEX idx_users_email ON users (email);
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens (user_id);
CREATE INDEX idx_devices_user ON devices (user_id);
```

---

## 8. Configuration (application.yml structure)

```yaml
spring:
  r2dbc:
    url: r2dbc:postgresql://${DB_HOST}:5432/${DB_NAME}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
    pool:
      initial-size: 10
      max-size: 50
      max-idle-time: 30m

  data:
    redis:
      host: ${REDIS_HOST}
      port: 6379

  security:
    jwt:
      secret: ${JWT_SECRET}
      access-token-expiration: 900000  # 15 min
      refresh-token-expiration: 604800000  # 7 days

stripe:
  api-key: ${STRIPE_API_KEY}
  webhook-secret: ${STRIPE_WEBHOOK_SECRET}

firebase:
  credentials-path: ${FIREBASE_CREDENTIALS_PATH}

sendgrid:
  api-key: ${SENDGRID_API_KEY}
  from-email: noreply@sportio.app

geo:
  default-radius-km: 10
  max-radius-km: 50
```

---

## 9. Key Business Rules (from Test Cases)

| Rule ID | Description | Implementation |
|---------|-------------|----------------|
| BR-001 | Session requires at least 2 players to lock | Validate in `lockSession()` |
| BR-002 | Only host can lock/cancel session | Check `hostId == currentUserId` |
| BR-003 | Cannot join full session | Check `playerCount < playersNeeded` |
| BR-004 | Cannot join same session twice | Check `existsBySessionIdAndUserId` |
| BR-005 | Payment splits equally among players | `totalCost / playerCount` |
| BR-006 | Venue slot can only be booked once | Optimistic locking on `venue_slots` |
| BR-007 | Session auto-cancels if no payments in 24h | Scheduled job |
| BR-008 | Past sessions cannot be joined | Check `session.date >= today` |
| BR-009 | Host cannot leave, only cancel | Block leave for host, show cancel option |
| BR-010 | Report requires reason selection | Validate `reasonId != null` |

