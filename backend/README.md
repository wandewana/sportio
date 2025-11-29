# Sportio Backend

A reactive Spring WebFlux backend for the Sportio sports matching platform.

## Technology Stack

- **Framework**: Spring Boot 3.2.0 with WebFlux (Reactive)
- **Database**: PostgreSQL with R2DBC (Reactive)
- **Cache**: Redis
- **Build Tool**: Maven
- **Java Version**: 17
- **Migration Tool**: Flyway

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 14+
- Redis 6+

## Setup Instructions

### 1. Database Setup

```sql
-- Create development database
CREATE DATABASE sportio_dev;

-- Create test database
CREATE DATABASE sportio_test;

-- Create user
CREATE USER sportio_user WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE sportio_dev TO sportio_user;
GRANT ALL PRIVILEGES ON DATABASE sportio_test TO sportio_user;
```

### 2. Environment Variables

Create a `.env` file in the backend directory:

```bash
DB_HOST=localhost
DB_NAME=sportio_dev
DB_USER=sportio_user
DB_PASSWORD=password
REDIS_HOST=localhost
JWT_SECRET=your-jwt-secret-key-here-minimum-256-bits
```

### 3. Build and Run

```bash
# Build the project
mvn clean compile

# Run the application
mvn spring-boot:run

# Run tests
mvn test
```

## API Endpoints

### Health Check
- `GET /api/v1/health` - Service health status

### User Management
- `GET /api/v1/users` - Get all users
- `GET /api/v1/users/{id}` - Get user by ID
- `GET /api/v1/users/email/{email}` - Get user by email
- `POST /api/v1/users` - Create new user

## Testing Strategy

This project follows an integration and end-to-end testing approach:

- **Integration Tests**: Tests complete request-response flow through real endpoints with real database
- **No Unit Tests**: Unit tests are avoided as they provide little value for reactive Spring WebFlux applications
- **Real Database**: All tests use a dedicated test database configured in `application-test.yml`
- **No Internal Mocking**: Only external third-party APIs are mocked, internal components use real implementations

## Architecture

The application follows a reactive three-tier architecture:

1. **Controller Layer**: API endpoints handling HTTP requests
2. **Service Layer**: Business logic and orchestration
3. **Repository Layer**: Data access using R2DBC

## Database Migrations

Database schema is managed using Flyway migrations:
- Migration files are in `src/main/resources/db/migration/`
- Migrations run automatically on application startup
- Test database is cleaned and re-migrated before each test run

## Reactive Programming

The application uses Project Reactor for reactive programming:
- `Mono<T>` for single results
- `Flux<T>` for multiple results
- Non-blocking I/O operations
- Backpressure handling