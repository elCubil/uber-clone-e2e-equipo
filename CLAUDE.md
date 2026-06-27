# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Run the server (H2 in-memory, no env vars needed)
./mvnw spring-boot:run

# Build JAR
./mvnw package

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=ClassName

# Run a single test method
./mvnw test -Dtest=ClassName#methodName
```

- Server: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:labdb`, user: `sa`, password: empty)

## Architecture

Spring Boot 3.5 · Java 21 · H2 in-memory · JWT (auth0 java-jwt) · Spring Security 6 · Lombok · SpringDoc OpenAPI

### Package layout

```
utec.cs2031.uber/
├── auth/           # JWT generation/validation, login/register flow, JwtAuthFilter
├── user/
│   ├── domain/     # User entity (implements UserDetails), UserService
│   ├── infrastructure/ # UserRepository
│   └── application/    # UserController
├── trip/
│   ├── domain/     # Trip entity, TripService, DTOs (CreateTripRequest, RateTripRequest, TripResponse), TripStatus enum
│   ├── infrastructure/ # TripRepository
│   └── application/    # TripController
├── exception/      # ResourceNotFoundException, UnauthorizedException, InvalidTripStateException
├── SecurityConfig.java      # Stateless JWT filter chain, CORS (all origins allowed)
├── GlobalExceptionHandler.java  # @RestControllerAdvice — maps exceptions to HTTP status codes
├── DataInitializer.java         # CommandLineRunner that seeds 6 users + 3 trips on startup
└── UberApplication.java
```

### Key design decisions

**Authentication**: Passwords are stored and compared in plain text (lab only — no BCrypt). `AuthService` implements `UserDetailsService`; `JwtAuthFilter` reads `Authorization: Bearer <token>`, validates it, and sets the `SecurityContext`.

**Authorization**: Role strings are `"PASSENGER"` or `"DRIVER"` (stored as plain `String` on `User`, not an enum). Role-based access is enforced manually inside service methods using `AccessDeniedException`, not via `@PreAuthorize` annotations.

**User entity**: `User` is mapped to table `app_user` (to avoid SQL keyword collision). It implements `UserDetails` directly.

**Trip state machine**: `PENDING → IN_PROGRESS → COMPLETED` — transitions enforced in `TripService`. Accepting a trip sets `driver.available = false`; completing sets it back to `true`. Rating recalculates the driver's weighted average in-place.

**Error responses**: All errors return `{"error": "message"}` except `MethodArgumentNotValidException`, which returns `{"fieldName": "message"}`.

**Seed data**: `DataInitializer` creates 3 drivers (carlos, lucia, pedro) and 3 passengers (ana, mario, sofia), all with password `pass123`, plus one trip in each status (COMPLETED, IN_PROGRESS, PENDING).

## API Reference

All endpoints except `/auth/**` require `Authorization: Bearer <token>`.

### Auth (public)

| Method | Path | Body | Response |
|--------|------|------|----------|
| POST | `/auth/register` | `firstName`, `lastName`, `email`, `password` (min 6), `role` | `{ "token": "..." }` |
| POST | `/auth/login` | `email`, `password` | `{ "token": "..." }` |

### Users

| Method | Path | Role | Description |
|--------|------|------|-------------|
| GET | `/users/me` | any | Profile of the authenticated user |
| GET | `/drivers/available` | PASSENGER | List drivers with `available: true` |

### Trips

| Method | Path | Role | Description |
|--------|------|------|-------------|
| POST | `/trips` | PASSENGER | Create trip → `PENDING` (201) |
| GET | `/trips` | PASSENGER | My trip history |
| GET | `/trips/pending` | DRIVER | All PENDING trips |
| GET | `/trips/my` | DRIVER | My accepted/completed trips |
| GET | `/trips/{id}` | any | Trip detail (403 if not a participant) |
| PATCH | `/trips/{id}/accept` | DRIVER | Accept PENDING trip → `IN_PROGRESS`; driver set unavailable |
| PATCH | `/trips/{id}/complete` | DRIVER | Complete IN_PROGRESS trip → `COMPLETED`; driver set available |
| POST | `/trips/{id}/rate` | PASSENGER | Rate completed trip (1–5); updates driver's weighted average |

**Error messages** (400): `"Trip is not available for acceptance"` · `"Driver is not available"` · `"Trip is not in progress"` · `"Trip must be completed before rating"` · `"Trip has already been rated"`

### Trip flow

```
PASSENGER            DRIVER
POST /trips          GET /trips/pending
(PENDING)     →      PATCH /{id}/accept
                     (IN_PROGRESS)
                     PATCH /{id}/complete
(COMPLETED)          (COMPLETED)
POST /{id}/rate
```

## Grading Rubric (20 points)

| # | Screen | Pts | Required endpoints |
|---|---|---|---|
| 1 | Login / Register | 3 | `POST /auth/register` · `POST /auth/login` · `GET /users/me` |
| 2 | Passenger dashboard | 3 | `GET /users/me` · `GET /trips` |
| 3 | Request trip | 2 | `GET /drivers/available` · `POST /trips` |
| 4 | Trip detail (passenger) + rating form + polling | 4 | `GET /trips/{id}` · `POST /trips/{id}/rate` |
| 5 | Driver dashboard | 4 | `GET /users/me` · `GET /trips/pending` · `GET /trips/my` · `PATCH /trips/{id}/accept` |
| 6 | Trip detail (driver) + complete | 2 | `GET /trips/{id}` · `PATCH /trips/{id}/complete` |
| 7 | History with status filter (both roles) | 2 | `GET /trips` · `GET /trips/my` |

All 12 backend endpoints must be consumed. All screens are mandatory.

## Frontend Integration (React + TypeScript)

### Suggested TypeScript types

```typescript
type Role = 'PASSENGER' | 'DRIVER';
type TripStatus = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED';

interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: Role;
  available: boolean;
  rating: number;
}

interface Trip {
  id: number;
  status: TripStatus;
  pickupAddress: string;
  dropoffAddress: string;
  requestedAt: string;        // ISO 8601
  acceptedAt: string | null;
  completedAt: string | null;
  passenger: User;
  driver: User | null;
  passengerRating: number | null;
  ratingComment: string | null;
}
```

### Implementation notes

- Store the JWT in `localStorage`; attach it via an Axios interceptor as `Authorization: Bearer <token>`. Redirect to login on `401`.
- Determine role from `GET /users/me` and render PASSENGER or DRIVER view accordingly.
- Poll `GET /trips/{id}` every 3–5 seconds while status is `PENDING` or `IN_PROGRESS` to simulate real-time tracking.
- Use optional chaining (`?.`) for nullable fields: `driver`, `acceptedAt`, `completedAt`, `passengerRating`.

### Minimum screens

**PASSENGER**: login/register (role selector) · request trip form (pickup + dropoff) · trip list with status badge · trip detail with "Rate" button (shown when COMPLETED and unrated).

**DRIVER**: login/register · pending trips list with "Accept" button · active trip with "Complete" button · completed trip history.
