# Passly

A mobile platform connecting driving lesson learners with certified instructors.

Instructors manage their profile, availability, and student roster. Learners discover instructors near them and book driving sessions directly from the app.

---

## Features

### For Instructors
- Create and manage a public profile (bio, photo, pricing, vehicle type, transmission)
- Set weekly availability and block out exceptions
- View and manage incoming booking requests
- Track their student roster

### For Learners
- Search and filter instructors by location, transmission, price, and rating
- View instructor profiles and availability
- Book, reschedule, or cancel driving sessions

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Mobile | React Native (Expo 52), TypeScript, NativeWind |
| Backend | Spring Boot 3.3, Java 21, Spring Security |
| Auth | JWT (access + refresh tokens) |
| Database | PostgreSQL 16 with Flyway migrations |
| Storage | Cloudinary (profile photos) |
| State | Zustand |
| Forms | React Hook Form + Zod |

---

## Project Structure

```
passly/
├── backend/          # Spring Boot REST API
│   ├── src/main/java/com/passly/
│   │   ├── auth/         # JWT auth, login, register
│   │   ├── user/         # Core user entity
│   │   ├── instructor/   # Instructor profiles, availability, search
│   │   ├── learner/      # Learner profiles
│   │   ├── booking/      # Session booking management
│   │   └── common/       # Security config, exception handling, storage
│   └── src/main/resources/db/migration/  # Flyway SQL migrations
├── mobile/           # React Native (Expo) app
│   ├── app/          # Expo Router screens
│   ├── src/api/      # API client layer
│   └── src/store/    # Zustand state stores
├── docker-compose.yml       # Local dev (PostgreSQL only)
└── docker-compose.prod.yml  # Production (full stack)
```

---

## Getting Started

### Prerequisites

- Java 21
- Maven 3.9+
- Node.js 20+
- Docker & Docker Compose

### 1. Clone the repo

```bash
git clone https://github.com/your-username/passly.git
cd passly
```

### 2. Set up environment variables

```bash
cp .env.example .env
# Edit .env with your values
```

### 3. Start the database

```bash
docker-compose up -d
```

### 4. Start the backend

```bash
cd backend
./mvnw spring-boot:run
```

The API runs on `http://localhost:8080`.
Swagger UI: `http://localhost:8080/swagger-ui`

### 5. Start the mobile app

```bash
cd mobile
npm install
npx expo start
```

---

## Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `DB_USERNAME` | PostgreSQL username | Yes |
| `DB_PASSWORD` | PostgreSQL password | Yes |
| `JWT_SECRET` | 256-bit secret for signing JWT tokens | Yes |
| `CLOUDINARY_CLOUD_NAME` | Cloudinary cloud name | Yes |
| `CLOUDINARY_API_KEY` | Cloudinary API key | Yes |
| `CLOUDINARY_API_SECRET` | Cloudinary API secret | Yes |
| `CORS_ALLOWED_ORIGINS` | Comma-separated allowed origins (prod only) | Prod |
| `SPRING_PROFILES_ACTIVE` | `dev` or `prod` | Yes |

Generate a secure JWT secret:
```bash
openssl rand -hex 32
```

---

## API Overview

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/register` | Public | Register a new user |
| POST | `/api/auth/login` | Public | Login, returns JWT |
| POST | `/api/auth/refresh` | Public | Refresh access token |
| GET | `/api/instructors` | Public | Search instructors |
| GET | `/api/instructors/{id}` | Public | Get instructor profile |
| PUT | `/api/instructors/me` | Instructor | Update own profile |
| POST | `/api/instructors/me/availability` | Instructor | Set availability |
| GET | `/api/bookings` | Authenticated | List bookings |
| POST | `/api/bookings` | Learner | Book a session |
| PATCH | `/api/bookings/{id}/status` | Instructor | Accept/reject booking |

Full interactive docs available at `/swagger-ui` when running in dev mode.

---

## Production Deployment

Use `docker-compose.prod.yml` with all required environment variables set:

```bash
docker-compose -f docker-compose.prod.yml up -d
```

Ensure `SPRING_PROFILES_ACTIVE=prod` — this disables Swagger and verbose logging.

---

## Database Migrations

Managed by Flyway. Migrations run automatically on startup.

| Version | Description |
|---------|-------------|
| V1 | Create users table |
| V2 | Create instructor and learner profiles |
| V3 | Create availability slots and exceptions |
| V4 | Create bookings table |
