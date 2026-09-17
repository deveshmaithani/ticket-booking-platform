# 🎟️ Ticket Booking Platform

A production-style **event ticket booking system** built to solve a real distributed-systems problem: **preventing double-booking of the same seat under high concurrency**. Built with Spring Boot and backed by Redis, Kafka, and RabbitMQ — each chosen deliberately for the specific messaging pattern it solves best.

**🔗 Live demo:** `http://<your-ec2-ip>:8080` *(currently stopped)*
**📦 Repo:** `https://github.com/deveshmaithani/ticket-booking-platform`

---

## Why this project exists

This project was built specifically to close that gap — the core problem it solves (two people trying to book the same seat at the same instant) is a well-known, genuinely hard distributed systems problem, and the solution here is provably correct, not just assumed to work.

---

## Architecture

```
                         ┌─────────────┐
                         │   Client    │
                         │ (Postman/   │
                         │  Browser)   │
                         └──────┬──────┘
                                │ REST + JWT
                                ▼
                    ┌───────────────────────┐
                    │   Spring Boot API      │
                    │  (Auth, Venues,        │
                    │   Events, Seats,       │
                    │   Bookings)            │
                    └───┬───────┬───────┬────┘
                        │       │       │
            ┌───────────┘       │       └───────────┐
            ▼                   ▼                   ▼
     ┌─────────────┐    ┌──────────────┐    ┌──────────────┐
     │  PostgreSQL  │    │    Redis     │    │    Kafka     │
     │ (source of   │    │ (distributed │    │ (booking-    │
     │  truth)      │    │  seat lock)  │    │  confirmed   │
     └──────────────┘    └──────────────┘    │  event topic)│
                                              └──────┬───────┘
                                                     ▼
                                          ┌──────────────────┐
                                          │ Analytics/logging │
                                          │    consumer       │
                                          └──────────────────┘

            Booking confirmed also publishes to:
                                                     ▼
                                          ┌──────────────────┐
                                          │    RabbitMQ       │
                                          │ (email task queue,│
                                          │  retry + DLQ)      │
                                          └──────────────────┘
```

**Why two different message brokers?** They solve fundamentally different problems:
- **Kafka** — broadcasting an event to any number of independent downstream consumers (analytics, customer care, inventory sync). Events persist and can be replayed; multiple consumer groups can read independently.
- **RabbitMQ** — reliably executing exactly one task (send this one email), with automatic retry and a dead-letter queue for messages that permanently fail.

---

## Tech stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot, Spring Security, Spring Data JPA |
| Database | PostgreSQL |
| Caching / Locking | Redis (distributed lock via `SETNX` + TTL) |
| Event streaming | Apache Kafka (KRaft mode, no Zookeeper) |
| Task queue | RabbitMQ (with dead-letter queue + retry policy) |
| Auth | JWT (stateless, BCrypt password hashing) |
| Containerization | Docker, Docker Compose |
| Deployment | AWS EC2 (Ubuntu, t3.small) |
| Load testing | k6 |

---

## The core problem: concurrency-safe seat booking

Without protection, two simultaneous booking requests for the same seat can both pass a "is this seat free?" check before either one commits — resulting in a double-booking.

**Solution — two layers of protection:**
1. **Redis distributed lock** (`SET seat:{eventId}:{seatId} {userId} NX EX 300`) — an atomic check-and-set that guarantees only one request can ever acquire the lock for a given seat. Losing requests fail fast with a clear `409 Conflict`.
2. **Database unique constraint** on `(event_id, seat_id)` in the `bookings` table — a final safety net even if the lock layer were somehow bypassed.

### Proof: load-tested with k6

20 concurrent virtual users fired simultaneous booking requests at the **same seat**:

```
✓ booking succeeded (200):  1 / 20
✓ booking rejected (409):  19 / 20
```

**Result: exactly 1 booking succeeded, 19 were correctly rejected — zero double-bookings, even under real concurrent load.**

---

## Getting started (local)

### Prerequisites
- Docker Desktop
- Java 17 (only needed if running outside Docker)
- Maven (or use the included `mvnw` wrapper)

### Run everything with one command
```bash
git clone https://github.com/<your-username>/ticket-booking-platform.git
cd ticket-booking-platform
docker compose up -d --build
```

This spins up: PostgreSQL, Redis, Kafka, RabbitMQ, and the Spring Boot app — fully networked together.

The app will be available at `http://localhost:8080`.
RabbitMQ's management dashboard: `http://localhost:15672` (guest/guest).

### Running without Docker (app only)
```bash
docker compose up -d postgres redis kafka rabbitmq
./mvnw clean spring-boot:run
```

---

## API overview

All endpoints except `/api/auth/**` require a JWT in the `Authorization: Bearer <token>` header.

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Create a new user, returns JWT |
| POST | `/api/auth/login` | Authenticate, returns JWT |
| GET / POST | `/api/venues` | List / create venues |
| GET / POST | `/api/events` | List / create events |
| GET / POST | `/api/seats` | Seat availability for an event / create seats |
| POST | `/api/bookings?eventId=&seatId=` | Book a seat (Redis-locked, race-condition safe) |

---

## Project structure
```
src/main/java/com/devesh/ticketbooking/
├── config/          # Security, Kafka, RabbitMQ configuration
├── controller/       # REST endpoints
├── service/          # Business logic (booking, locking, auth)
├── repository/       # Spring Data JPA repositories
├── entity/            # JPA entities
├── dto/               # Request/response DTOs
├── event/              # Kafka & RabbitMQ event producers/consumers
├── exception/           # Global exception handling
└── security/             # JWT filter, user details service
```

---

## Deployment

Deployed on an **AWS EC2 (t3.small, Ubuntu 22.04)** instance, fully containerized via Docker Compose — the exact same `docker-compose.yml` used locally runs in production, with environment variables overriding hostnames for container-to-container networking.

```bash
# On the server, after cloning the repo:
docker compose up -d --build
```

---

## What I'd add next
- WebSocket/SSE support for real-time booking status updates to the frontend
- Dynamic pricing via a Kafka consumer that recalculates price as seats fill up
- A waitlist queue using RabbitMQ for sold-out events
- Flyway/Liquibase for proper schema migrations (currently using `ddl-auto=update` for local development speed)
- A minimal React frontend to demo the booking flow visually

---

## Author
**Devesh Maithani** — [GitHub](https://github.com/deveshmaithani) 
