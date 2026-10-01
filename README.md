# Booking App Backend

> A Spring Boot REST API for managing event listings, user bookings, and administrative access with JWT authentication.

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4-6db33f?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-26-007396?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8-4479a1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![JWT](https://img.shields.io/badge/JWT-000000?logo=jsonwebtokens&logoColor=white)](https://jwt.io/)
[![Maven](https://img.shields.io/badge/Maven-Wrapper-c71a36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-0f766e.svg)](#license)

Event Booking Backend is a RESTful API built with Spring Boot, Spring Security, and JPA. It provides endpoints for user authentication, event listing management, booking reservations, and administrative user control. The application uses JWT tokens for stateless authentication and BCrypt for password hashing.

The API is the companion to the **Booking App Frontend**, a React client that runs separately at `http://localhost:5173`.

> ⚠️ **Never commit secrets.** Administrator credentials, JWT secrets, database passwords, and `.env` files must stay out of the repository. See [Security](#security) before publishing the project.

## Contents

- [API coverage](#api-coverage)
- [Features](#features)
- [Roles and access](#roles-and-access)
- [Quick start](#quick-start)
- [Using the API](#using-the-api)
- [API endpoints](#api-endpoints)
- [Database](#database)
- [Configuration](#configuration)
- [Project structure](#project-structure)
- [Technology](#technology)
- [Development commands](#development-commands)
- [Testing](#testing)
- [Security](#security)
- [Troubleshooting](#troubleshooting)
- [Complete command reference](#complete-command-reference)
- [Limitations](#limitations)
- [Contributing](#contributing)
- [License](#license)

## API coverage

The API has **15 endpoints** in **4 groups**, served by 4 controllers.

| Group          | Controller          | Base path      | Endpoints | Access                                         |
| -------------- | ------------------- | -------------- | :-------: | ---------------------------------------------- |
| Authentication | `AuthController`    | `/auth`        |     2     | Public                                         |
| Listings       | `ListingController` | `/listings`    |     5     | Reads are public; writes need a signed-in user |
| Bookings       | `BookingController` | `/bookings`    |     3     | Authenticated                                  |
| Admin Users    | `UserController`    | `/admin/users` |     4     | Admin only                                     |

The service runs at `http://localhost:8080` and stores its data in a MySQL database named `booking_db`.

### Seeded sample listings

The backend seeds **6 sample listings** when the listings table is empty.

| Title                   | Category   | Price  | Date       | Slots |
| ----------------------- | ---------- | ------ | ---------- | :---: |
| React Masterclass       | workshop   | 49.99  | 2026-09-05 |  30   |
| Tech Conference 2026    | conference | 199.99 | 2026-09-15 |  100  |
| Jazz Night Live         | concert    | 35.00  | 2026-09-08 |  50   |
| Python for Data Science | workshop   | 59.99  | 2026-09-12 |  25   |
| Startup Pitch Night     | conference | 0.00   | 2026-09-20 |  80   |
| Acoustic Sunset Session | concert    | 25.00  | 2026-09-03 |  40   |

## Features

| Area            | Capabilities                                                                        |
| --------------- | ----------------------------------------------------------------------------------- |
| Auth            | User registration, login, and JWT token generation                                  |
| Listings        | CRUD operations for event listings with category filtering                          |
| Bookings        | Create bookings, view user-specific bookings, and cancel reservations               |
| Admin           | Manage users, update roles, and oversee system data                                 |
| Security        | Role-based access control, CORS configuration, and input validation                 |
| Data            | Automatic database seeding with sample events and an admin account                  |
| Slot tracking   | Reduces a listing's available slots on booking and returns the slot on cancellation |
| Duplicate guard | Allows each user at most one booking per listing                                    |
| Stateless auth  | Signs HS256 tokens that carry the user's email and role, with a configurable expiry |
| Error handling  | Returns validation errors as a field-to-message map and other errors as plain text  |

## Roles and access

| Role  | Access                                                       |
| ----- | ------------------------------------------------------------ |
| User  | Browse listings, book events, view/cancel own bookings       |
| Admin | All user capabilities plus manage users, roles, and listings |

The application can create an administrator account during startup using credentials supplied through environment variables.

**Do not commit administrator credentials or secrets to the repository.**

Sample listings are also seeded if the database is empty.

### Endpoint access rules

| Endpoints                                | Access                    |
| ---------------------------------------- | ------------------------- |
| `/auth/**`                               | Public                    |
| `GET /listings` and `GET /listings/{id}` | Public                    |
| `/admin/**`                              | Admin only                |
| Every other endpoint                     | Authenticated (valid JWT) |

The JWT carries the user's email as its subject and the role as a claim. Registration always creates a `USER`, and admins can create users with either role.

## Quick start

### Requirements

- Java 26, as set by `java.version` in `pom.xml`
- Maven 3.6+ or Maven Wrapper
- MySQL 8 or compatible MySQL server

Earlier documentation listed Java 17 or newer. `pom.xml` sets `java.version` to 26, so build with JDK 26, or lower that value to the JDK you have if the dependencies support it.

The Maven Wrapper downloads Maven 3.9.16 on first use, so a separate Maven install is optional.

### Clone the repository

```bash
git clone https://github.com/saravanansaranraj27/booking-app-backend.git
cd booking-app-backend
```

The React client lives in its own repository, [booking-app-frontend](https://github.com/saravanansaranraj27/booking-app-frontend), which is cloned and run separately.

### Create the database

The application is configured to create the database automatically if it does not exist.

Ensure that your MySQL server is running before starting the application.

### Configure environment variables

Create a local `.env` file in the project root.

Example:

```env
JWT_SECRET=your-long-random-jwt-secret
JWT_EXPIRATION=86400000
ADMIN_EMAIL=your-admin-email
ADMIN_PASSWORD=your-admin-password
```

Do not commit `.env` to Git.

The application loads these values when it starts and uses them for JWT configuration and administrator account creation. `JWT_EXPIRATION` is in milliseconds, so `86400000` is 24 hours.

### Configure the database

Update the database settings in `src/main/resources/application.properties` or configure them using environment variables.

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/booking_db?createDatabaseIfNotExist=true
spring.datasource.username=YOUR_DATABASE_USERNAME
spring.datasource.password=YOUR_DATABASE_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

server.port=8080

jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION}

admin.email=${ADMIN_EMAIL}
admin.password=${ADMIN_PASSWORD}
```

**Never commit real database passwords, JWT secrets, or administrator credentials to the repository.**

### Run the application

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

On macOS/Linux:

```sh
./mvnw spring-boot:run
```

Or, if Maven is installed:

```sh
mvn spring-boot:run
```

The API runs at:

```text
http://localhost:8080
```

On the first run, the application creates the configured administrator account and seeds sample listings if the database is empty.

### Verify the tools

```sh
java -version
```

## Using the API

### Workflow

1. Start MySQL, then start the backend. The log prints `Admin created` and `Seeded 6 listings` on the first run
2. Register a user with `POST /auth/register`, or log in with `POST /auth/login`. Both return a `token`, a `role`, and a `name`
3. Send the token in the `Authorization: Bearer <token>` header on every protected request
4. Browse listings with `GET /listings`, optionally filtered with `?category=workshop`, `?category=conference`, or `?category=concert`
5. Book an event with `POST /bookings` and the `listingId`, then list your bookings with `GET /bookings/my`
6. Cancel a booking with `DELETE /bookings/{id}`. Only the booking's owner can cancel it
7. Log in with the configured administrator account to manage users under `/admin/users`

### Sign in

```sh
curl -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d '{"email":"your-admin-email","password":"your-admin-password"}'
```

The response has the form `{ "token": "...", "role": "ADMIN", "name": "Admin" }`.

## API endpoints

The backend runs at `http://localhost:8080`. Request and response bodies are JSON.

### Authentication

| Method | Endpoint         | Description                   | Request Body      | Response       |
| ------ | ---------------- | ----------------------------- | ----------------- | -------------- |
| POST   | `/auth/register` | Register a new user           | `RegisterRequest` | `AuthResponse` |
| POST   | `/auth/login`    | Authenticate an existing user | `AuthRequest`     | `AuthResponse` |

### Listings

| Method | Endpoint         | Description                                            | Access        |
| ------ | ---------------- | ------------------------------------------------------ | ------------- |
| GET    | `/listings`      | Retrieve all listings with optional category filtering | Public        |
| GET    | `/listings/{id}` | Retrieve one listing                                   | Public        |
| POST   | `/listings`      | Create a listing                                       | Authenticated |
| PUT    | `/listings/{id}` | Update a listing                                       | Authenticated |
| DELETE | `/listings/{id}` | Delete a listing                                       | Authenticated |

The category filter uses the `?category` query parameter and ignores letter case.

### Bookings

| Method | Endpoint         | Description                      | Access                     |
| ------ | ---------------- | -------------------------------- | -------------------------- |
| POST   | `/bookings`      | Book a listing                   | Authenticated              |
| GET    | `/bookings/my`   | Retrieve current user's bookings | Authenticated              |
| DELETE | `/bookings/{id}` | Cancel a booking                 | Authenticated (owner only) |

A booking response contains the booking `id`, the `user` (without the email and password), the `listing`, the `status`, and `createdAt`.

### Admin Users

| Method | Endpoint                 | Description        | Access     |
| ------ | ------------------------ | ------------------ | ---------- |
| GET    | `/admin/users`           | Retrieve all users | Admin only |
| POST   | `/admin/users`           | Create a new user  | Admin only |
| PUT    | `/admin/users/{id}/role` | Update user role   | Admin only |
| DELETE | `/admin/users/{id}`      | Delete a user      | Admin only |

### Request and response bodies

| Body                | Fields                                                                                        |
| ------------------- | --------------------------------------------------------------------------------------------- |
| `RegisterRequest`   | `name` (required), `email` (valid email format), `password` (required, at least 6 characters) |
| `AuthRequest`       | `email`, `password`                                                                           |
| `AuthResponse`      | `token`, `role`, `name`                                                                       |
| `BookingRequest`    | `listingId` (required)                                                                        |
| `UpdateRoleRequest` | `role` (required, `USER` or `ADMIN`)                                                          |
| `UserResponse`      | `id`, `name`, `email`, `role`                                                                 |
| Listing             | `title`, `description` (up to 1000 characters), `category`, `price`, `date`, `slotsAvailable` |
| Create user         | `name`, `email`, `password`, `role` (sent to `POST /admin/users`)                             |

### Errors

| Situation                                                 | Response                                          |
| --------------------------------------------------------- | ------------------------------------------------- |
| Invalid login credentials                                 | `401` with `Invalid credentials`                  |
| Request body fails validation                             | `400` with a map of field names to messages       |
| Application errors, such as an unknown listing or booking | `400` with the error message as plain text        |
| Registering an email that already exists                  | `400` with `Email already registered`             |
| Booking a listing the user has already booked             | `400` with `You have already booked this listing` |
| Booking a listing with no slots left                      | `400` with `No slots available for this event`    |
| Cancelling another user's booking                         | `400` with `Not authorized`                       |

## Database

The backend creates three tables on startup through Hibernate, using `spring.jpa.hibernate.ddl-auto=update`.

| Table      | Columns                                                                              |
| ---------- | ------------------------------------------------------------------------------------ |
| `users`    | `id`, `name`, `email` (unique), `password` (BCrypt hash), `role` (`USER` or `ADMIN`) |
| `listings` | `id`, `title`, `description`, `category`, `price`, `date`, `slots_available`         |
| `bookings` | `id`, `user_id`, `listing_id`, `status` (`CONFIRMED` or `CANCELLED`), `created_at`   |

The `bookings` table has a unique constraint on `listing_id` and `user_id`, so each user has at most one booking per listing. The password is never returned in API responses.

## Configuration

### Environment variables

The backend loads these variables from `.env` in the project root when it starts, if the file exists:

| Variable         | Purpose                                                  |
| ---------------- | -------------------------------------------------------- |
| `JWT_SECRET`     | Secret used to sign JWT tokens                           |
| `JWT_EXPIRATION` | JWT expiration time in milliseconds                      |
| `ADMIN_EMAIL`    | Email of the administrator account created at startup    |
| `ADMIN_PASSWORD` | Password of the administrator account created at startup |

The `.env` file is optional. If it is missing, the same values must be available as system environment variables.

### Application Properties

| Property                        | Description                                                  |
| ------------------------------- | ------------------------------------------------------------ |
| `spring.datasource.url`         | MySQL database connection URL                                |
| `spring.datasource.username`    | MySQL username                                               |
| `spring.datasource.password`    | MySQL password                                               |
| `spring.jpa.hibernate.ddl-auto` | Hibernate schema generation strategy                         |
| `spring.jpa.show-sql`           | Whether SQL statements are logged                            |
| `server.port`                   | Application server port                                      |
| `jwt.secret`                    | Secret used to sign JWT tokens                               |
| `jwt.expiration`                | JWT expiration time in milliseconds                          |
| `admin.email`                   | Administrator email loaded from environment configuration    |
| `admin.password`                | Administrator password loaded from environment configuration |

### CORS Configuration

CORS is configured to allow requests from:

```text
http://localhost:5173
```

This is the default Vite development server URL.

Modify `CorsConfig.java` and `SecurityConfig.java` if your frontend runs on a different origin.

All methods and headers are allowed from that origin, with credentials, and preflight responses are cached for 3600 seconds.

## Project structure

```text
booking-app-backend/                                    # Spring Boot backend project root
├── .mvn/                                               # Maven Wrapper files
│   └── wrapper/                                        # Wrapper configuration
│       └── maven-wrapper.properties                    # Maven Wrapper settings
├── src/                                                # Application source and tests
│   ├── main/                                           # Application code and resources
│   │   ├── java/com/booking/                           # Java source packages
│   │   │   ├── BookingApplication.java                 # Entry point, loads .env, starts Spring Boot
│   │   │   ├── config/                                 # Spring configuration
│   │   │   │   ├── CorsConfig.java                     # CORS filter for the frontend origin
│   │   │   │   ├── DataSeeder.java                     # Seeds the admin account and sample listings
│   │   │   │   └── SecurityConfig.java                 # Security rules, CORS source, BCrypt encoder
│   │   │   ├── controller/                             # REST controllers
│   │   │   │   ├── AuthController.java                 # /auth: register and login
│   │   │   │   ├── BookingController.java              # /bookings: create, list own, cancel
│   │   │   │   ├── ListingController.java              # /listings: list, get, create, update, delete
│   │   │   │   └── UserController.java                 # /admin/users: list, create, role, delete
│   │   │   ├── dto/                                    # Request and response objects
│   │   │   │   ├── AuthRequest.java                    # Login request: email and password
│   │   │   │   ├── AuthResponse.java                   # Login and register response: token, role, and name
│   │   │   │   ├── BookingRequest.java                 # Booking request: the listing ID
│   │   │   │   ├── RegisterRequest.java                # Registration request with validation rules
│   │   │   │   ├── UpdateRoleRequest.java              # Role change request body
│   │   │   │   └── UserResponse.java                   # User data returned by admin endpoints, without the password
│   │   │   ├── exception/                              # Error handling
│   │   │   │   └── GlobalExceptionHandler.java         # Maps errors to 400 responses
│   │   │   ├── model/                                  # JPA entities
│   │   │   │   ├── Booking.java                        # Booking entity with status and creation time
│   │   │   │   ├── Listing.java                        # Event listing entity
│   │   │   │   └── User.java                           # User entity with USER and ADMIN roles
│   │   │   ├── repository/                             # Spring Data JPA repositories
│   │   │   │   ├── BookingRepository.java              # Booking queries, including per-user and duplicate checks
│   │   │   │   ├── ListingRepository.java              # Listing queries, including the category filter
│   │   │   │   └── UserRepository.java                 # User queries, including lookup by email
│   │   │   ├── security/                               # JWT authentication
│   │   │   │   ├── JwtFilter.java                      # Reads the Bearer token on each request
│   │   │   │   └── JwtUtil.java                        # Creates and validates JWT tokens
│   │   │   └── service/                                # Business logic
│   │   │       ├── BookingService.java                 # Booking rules: slots, duplicates, and cancellation
│   │   │       ├── ListingService.java                 # Listing CRUD and category filtering
│   │   │       ├── UserDetailsServiceImpl.java         # Loads users by email for Spring Security
│   │   │       └── UserService.java                    # Admin user management and role changes
│   │   └── resources/                                  # Application resources
│   │       └── application.properties                  # Database, JWT, and admin settings
│   └── test/                                           # Tests
│       └── java/com/example/demo/                      # Test package
│           └── BookingAppBackendApplicationTests.java  # Context load test
├── .env                                                # JWT and admin settings (not committed)
├── .gitattributes                                      # Line-ending rules for mvnw and .cmd files
├── .gitignore                                          # Excludes target, IDE files, and .env
├── HELP.md                                             # Spring Boot reference links
├── mvnw                                                # Maven Wrapper for macOS and Linux
├── mvnw.cmd                                            # Maven Wrapper for Windows
├── pom.xml                                             # Maven build and dependencies
└── README.md                                           # Project documentation
```

The folder also contains Eclipse project files (`.classpath`, `.project`, `.factorypath`, and `.settings/`), which `.gitignore` excludes, and a `target/` build output folder.

## Technology

- [Spring Boot](https://spring.io/projects/spring-boot) 4.1 — application framework
- [Spring Security](https://spring.io/projects/spring-security) — authentication and role-based access
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa) — repositories
- [Hibernate](https://hibernate.org/) — object-relational mapping
- [MySQL](https://www.mysql.com/) — database, through the MySQL Connector/J driver
- [JJWT](https://github.com/jwtk/jjwt) 0.11.5 — JWT creation and validation
- [Lombok](https://projectlombok.org/) — reduces model boilerplate
- Bean Validation — request validation
- BCrypt — password hashing
- [dotenv-java](https://github.com/cdimascio/dotenv-java) 3.0 — loads the `.env` file
- [Maven](https://maven.apache.org/) — build tool, with the Maven Wrapper

## Development commands

### Run the application

```sh
./mvnw spring-boot:run
```

### Package the application

```sh
./mvnw package
```

### Run tests

```sh
./mvnw test
```

### Clean the build

```sh
./mvnw clean
```

### Check the Java version

```sh
java -version
```

## Testing

The project includes **1 test class** with **1 test case**.

| Test file                                | Coverage                                        |
| ---------------------------------------- | ----------------------------------------------- |
| `BookingAppBackendApplicationTests.java` | `contextLoads`, which starts the Spring context |

Run the tests with:

```sh
./mvnw test
```

The test class is in the package `com.example.demo`, while the application class is in `com.booking`. Spring Boot looks for the application class in the test's package and its parents, so if the test reports that it cannot find a `@SpringBootConfiguration`, move the test into `com.booking`. The context test also needs a reachable MySQL server and the environment variables described in [Configuration](#configuration).

## Security

Sensitive configuration should always be supplied through environment variables or another secure configuration mechanism.

Never commit the following to source control:

- `.env` files
- Database passwords
- JWT secrets
- Administrator passwords
- API keys
- Private certificates or keys

If a secret has already been pushed to a public repository, treat it as compromised and rotate it immediately.

## Troubleshooting

### Java version or compiler error

`pom.xml` sets `java.version` to 26, so the build needs a JDK 26. Check your version:

```sh
java -version
```

Install JDK 26, or lower `java.version` in `pom.xml` to the JDK you have.

### `Could not resolve placeholder 'JWT_SECRET'`

The `.env` file is missing, or it is not in the folder where you start the backend. Create it in the project root with the four variables, or set them in the system environment.

### `WeakKeyException` or a JWT key error on startup

The HS256 signing key must be at least 32 characters long. Use a longer `JWT_SECRET`.

### `Access denied for user` or `Communications link failure`

Check that MySQL is running, and that `spring.datasource.url`, `spring.datasource.username`, and `spring.datasource.password` in `application.properties` are correct.

### Port 8080 is already in use

Change `server.port` in `application.properties`. Then update the base URL in the frontend to match.

### CORS errors in the browser

The frontend must run at `http://localhost:5173`. Modify `CorsConfig.java` and `SecurityConfig.java` if it runs on a different origin.

### The administrator account was not created

The account is created at startup only when no user with `ADMIN_EMAIL` exists. Check that `.env` is loaded, and look for the `Admin created` line in the log.

### `You have already booked this listing` after cancelling

Cancelling marks the booking as `CANCELLED` and keeps the row. Each user can have only one booking per listing, so the same event cannot be booked again by the same user. See [Limitations](#limitations).

### A request is rejected after a long time

The token has expired, which depends on `JWT_EXPIRATION`. Log in again to get a new token.

## Complete command reference

### Clone the repository

```sh
git clone https://github.com/saravanansaranraj27/booking-app-backend.git
cd booking-app-backend
```

### Run, package, and test

```powershell
.\mvnw.cmd spring-boot:run
```

On macOS/Linux, or with Maven installed:

```sh
./mvnw spring-boot:run
mvn spring-boot:run
./mvnw package
./mvnw test
./mvnw clean
java -version
```

## Limitations

- Database tables are created automatically using `ddl-auto=update`, but the MySQL server must be running.
- JWT secrets should be strong random values in production.
- CORS is currently configured for `http://localhost:5173`.
- Automated test coverage is currently limited.
- Slot availability checking should use appropriate concurrency controls for high-volume production systems.
- Email validation is basic; production applications may require email verification.
- The listing create, update, and delete endpoints accept any signed-in user. Only the `/admin/**` endpoints are limited to admins.
- Listing request bodies have no validation rules, so the API accepts missing fields.
- Cancelling a booking keeps the row with the `CANCELLED` status. The same user cannot book that listing again, and cancelling a booking that is already cancelled returns another slot to the event.
- The sample listings are dated in September 2026.
- The backend test class is in a different package from the application class.

## Contributing

Bug reports, improvements, and pull requests are welcome.

Please ensure code follows Spring Boot conventions and includes appropriate validation.

```sh
./mvnw test
./mvnw package
```

## License

This project is licensed under the MIT License.
