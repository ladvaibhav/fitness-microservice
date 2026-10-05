# Fitness Microservices Platform

An event-driven fitness tracking and AI-powered workout recommendation platform built with Spring Boot, Spring Cloud, PostgreSQL, MongoDB, RabbitMQ, and Google Gemini.

---

## Architecture Overview

```text
                  +--------------------------------+
                  |         API Gateway            |
                  |          Port: 8080            |
                  +---------------+----------------+
                                  |
            +---------------------+---------------------+
            |                     |                     |
     /v1/users/**         /v1/activities/**     /v1/recommendations/**
            |                     |                     |
            v                     v                     v
   +-----------------+   +------------------+   +-----------------+
   |   User Service  |   | Activity Service |   |    AI Service   |
   |    Port: 8081   |   |    Port: 8082    |   |    Port: 8084   |
   |  (PostgreSQL)   |   |    (MongoDB)     |   |    (MongoDB)    |
   +--------^--------+   +--------+---------+   +--------^--------+
            |                     |                      |
            +-- WebClient Sync ---+                      |
               Validation                                |
                                  |                      |
                                  v                      |
                        [ RabbitMQ Exchange ]            |
                           fitness.exchange              |
                                  |                      |
                                  v                      |
                          [ activity.queue ] ------------+
                                                Async Event
```

### Core Components
- **Service Discovery (`eureka`):** Netflix Eureka registry running on port `8761`.
- **Centralized Configuration (`configserver`):** Spring Cloud Config Server running on port `8888` serving native classpath configurations.
- **API Gateway (`gateway`):** Reactive Spring Cloud Gateway on port `8080` managing routing and load-balancing.
- **User Service (`userservice`):** Manages user profiles with relational persistence in PostgreSQL.
- **Activity Service (`activityservice`):** Tracks fitness logs in MongoDB, validates user IDs synchronously via WebClient, and publishes events to RabbitMQ.
- **AI Recommendation Service (`aiservice`):** Listens to RabbitMQ activity messages, prompts the Gemini API for performance analysis and safety tips, and stores results in MongoDB.

---

## Services & Port Mapping

| Service | Port | Database / Broker | Description |
| :--- | :--- | :--- | :--- |
| **Eureka Server** | `8761` | None | Service Registry |
| **Config Server** | `8888` | Local Classpath (`native`) | Centralized configurations |
| **API Gateway** | `8080` | None | Dynamic Reverse Proxy (`lb://`) |
| **User Service** | `8081` | PostgreSQL (`fitnessUserdb`) | User identity & validation |
| **Activity Service** | `8082` | MongoDB (`fitnessactivity`) | Workout logging & AMQP publisher |
| **AI Service** | `8084` | MongoDB (`fitnessrecommendation`) | AI recommendations consumer |

---

## Prerequisites

- **Java JDK**: Version 21 or higher
- **Maven**: Version 3.8+ (or use the provided `./mvnw` wrappers)
- **PostgreSQL**: Running on port `5432` with database `fitnessUserdb`
- **MongoDB**: Running on port `27017`
- **RabbitMQ**: Running on port `5672` (default credentials: `guest`/`guest`)
- **Google Gemini API Key**

---

## Environment Setup

Configure the following environment variables before launching `aiservice`:

```bash
export GEMINI_API_KEY="your-gemini-api-key"
export GEMINI_API_URL="[https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=](https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=)"
```

---

## Startup Sequence

Because services register with Eureka and fetch properties from Config Server, start the applications in this exact sequence:

1. **Start Eureka Server**
   ```bash
   cd eureka && ./mvnw spring-boot:run
   ```
   *Dashboard available at: `http://localhost:8761`*

2. **Start Config Server**
   ```bash
   cd ../configserver && ./mvnw spring-boot:run
   ```
   *Verify configs at: `http://localhost:8888/user-service/default`*

3. **Start Core Microservices** (in separate terminals)
   ```bash
   # Terminal 3: User Service
   cd ../userservice && ./mvnw spring-boot:run

   # Terminal 4: Activity Service
   cd ../activityservice && ./mvnw spring-boot:run

   # Terminal 5: AI Service
   cd ../aiservice && ./mvnw spring-boot:run
   ```

4. **Start API Gateway**
   ```bash
   cd ../gateway && ./mvnw spring-boot:run
   ```

---

## API Documentation

All requests should be routed through the API Gateway at `http://localhost:8080`.

### 1. User Service (`/v1/users`)

- **Create User**
    - `POST /v1/users`
    - Body:
      ```json
      {
        "email": "user@example.com",
        "password": "securePassword123",
        "firstName": "Alex",
        "lastName": "Rivera"
      }
      ```
- **Get All Users:** `GET /v1/users/all`
- **Get User Profile:** `GET /v1/users/{userId}`
- **Validate User (Internal):** `GET /v1/users/{userId}/validate`

### 2. Activity Service (`/v1/activities`)

- **Log Activity**
    - `POST /v1/activities`
    - Body:
      ```json
      {
        "userId": "your-user-id",
        "type": "RUNNING",
        "durationMinutes": 45,
        "caloriesBurned": 420,
        "startTime": "2026-03-30T07:30:00",
        "additionalMetrics": {
          "distanceKm": 7.5,
          "avgHeartRate": 152
        }
      }
      ```
  *Allowed Activity Types:* `WALKING`, `RUNNING`, `CYCLING`, `WORKOUT`, `SWIMMING`, `YOGA`, `OTHER`
- **Get All Activities:** `GET /v1/activities/all`
- **Get Activities by Header:** `GET /v1/activities/userId` (Requires `X-User-Id` header)
- **Get Activity by ID:** `GET /v1/activities/id/{id}`

### 3. AI Service (`/v1/recommendations`)

- **Get Recommendations for User:** `GET /v1/recommendation/userId/{userId}`
- **Get Recommendation for Activity:** `GET /v1/recommendation/activityId/{activityId}`

---

## Asynchronous Event Flow

1. When a workout is saved via `POST /v1/activities`, `ActivityService` publishes the payload to `fitness.exchange` using routing key `activity.tracking`.
2. RabbitMQ delivers the payload to `activity.queue`.
3. `ActivityMessageListener` in `aiservice` consumes the message and constructs a structured prompt.
4. The Gemini API response is parsed into:
    - **Performance Analysis:** Overall summary, pace, heart rate, and calories.
    - **Actionable Improvements:** Key targeted training adjustments.
    - **Next Workout Suggestions:** Recommended upcoming sessions.
    - **Safety Guidelines:** Recovery and hydration precautions.
5. The recommendation is saved to MongoDB and linked by `userId` and `activityId`.