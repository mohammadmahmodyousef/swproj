# Vehicle Rental Management System (VRMS)

A console-based Vehicle Rental Management System built with Java and Maven. The system allows a manager to authenticate, view available vehicles, create and extend rentals, process vehicle returns, calculate rental charges and late penalties, and send lifecycle email notifications to customers.

The project follows a layered architecture and applies object-oriented principles, polymorphism, repository abstractions, strategy-based pricing, automated testing, mocking, and code-coverage reporting.

---

## Table of Contents

- [Features](#features)
- [Implemented User Stories](#implemented-user-stories)
- [Vehicle Types and Rules](#vehicle-types-and-rules)
- [Business Rules](#business-rules)
- [Architecture](#architecture)
- [Design Patterns](#design-patterns)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Email Configuration](#email-configuration)
- [Running the Application](#running-the-application)
- [Default Login](#default-login)
- [Application Menu](#application-menu)
- [Data Persistence](#data-persistence)
- [Testing](#testing)
- [Code Coverage](#code-coverage)
- [Javadoc](#javadoc)
- [Troubleshooting](#troubleshooting)
- [Security Notes](#security-notes)
- [Known Limitations](#known-limitations)
- [Future Improvements](#future-improvements)
- [Team](#team)
- [License](#license)

---

## Features

### Authentication

- Manager login using stored credentials.
- Invalid username and password handling.
- Manager logout.
- Protected operations require an authenticated manager.

### Vehicle Catalog

- Display available vehicles only.
- Hide rented vehicles from the available catalog.
- Persist vehicle status between application runs.
- Support multiple vehicle types using inheritance and polymorphism.

### Rental Management

- Create a rental record.
- Prevent duplicate rental IDs.
- Prevent double booking of the same vehicle.
- Change the vehicle status from `AVAILABLE` to `RENTED` after a successful rental.
- Enforce valid rental dates.
- Enforce a maximum rental period of 30 days.
- Display active rentals.
- Extend an active rental period.
- Reset reminder flags after an extension so notifications follow the new end date.

### Return and Billing

- Return a rented vehicle.
- Prevent returning the same rental more than once.
- Close the rental record after a successful return.
- Change the vehicle status back to `AVAILABLE`.
- Calculate rental cost using the actual rental duration.
- Apply a daily late-return penalty.
- Support early, on-time, and late returns.
- Display a detailed return summary containing rental cost, penalty, and total cost.

### Email Notifications

- Send a rental confirmation email.
- Send a reminder before the rental end date.
- Send a notification when the rental period ends.
- Prevent duplicate reminder and expiration emails.
- Send an email when the rental period is extended.
- Send a return confirmation for early, on-time, and late returns.
- Include dates, charges, penalties, and totals in return emails.

### Testing and Quality

- Unit tests using JUnit 5.
- Mocking using Mockito.
- Integration-style repository tests using temporary files.
- JaCoCo coverage reports.
- Maven-based build and test workflow.
- Generated Javadoc documentation.

---

## Implemented User Stories

### Sprint 1: Authentication and Vehicle Catalog

- **US1.1 – Manager Login**
  - Valid credentials result in successful login.
  - Invalid credentials produce a clear error message.

- **US1.2 – Manager Logout**
  - The manager can log out.
  - Protected operations require login again.

- **US1.3 – View Available Vehicles**
  - Available vehicles are displayed.
  - Rented vehicles are hidden.

### Sprint 2: Rental Operations

- **US2.1 – Rent a Vehicle**
  - A rental record is created.
  - The selected vehicle becomes rented.

- **US2.2 – Prevent Double Booking**
  - Duplicate rental IDs are rejected.
  - A vehicle with an active rental cannot be rented again.

- **US2.3 – Enforce Rental Duration Limits**
  - Missing dates are rejected.
  - An end date before the start date is rejected.
  - A rental longer than 30 days is rejected.

- **US2.4 – View Active Rentals**
  - Only active rentals are displayed.
  - Rental ID, customer, vehicle, start date, and end date are shown.

- **US2.5 – Extend Rental Period**
  - Only active rentals can be extended.
  - The new end date must be after the current end date.
  - The total rental period cannot exceed 30 days.
  - The updated end date is persisted.
  - The customer receives an extension email.

### Sprint 3: Notifications and Mocking

- **US3.1 – Rental Expiry Reminder**
  - A reminder is generated before the rental expires.
  - The notification service is mocked during unit testing.

- **US3.2 – Rental Expiration Notification**
  - The customer is notified when the rental period ends.

- **US3.3 – Prevent Duplicate Notifications**
  - Reminder and expiration flags are persisted.
  - The same notification is not sent twice.

### Sprint 4: Returns and Billing

- **US4.1 – Return Vehicle**
  - The rental is closed.
  - The vehicle becomes available again.

- **US4.2 – Calculate Rental Cost**
  - Cost is based on the rental duration.
  - A minimum of one rental day is charged.

- **US4.3 – Apply Late Return Penalty**
  - Late days are calculated correctly.
  - The configured daily penalty is applied.

- **US4.4 – Return Confirmation Email**
  - Early, on-time, and late returns produce an email confirmation.
  - The email includes a detailed financial summary.

### Sprint 5: Vehicle Types and Polymorphism

- **US5.1 – Support Multiple Vehicle Types**
  - Car
  - Motorcycle
  - Van
  - Truck
  - Electric Vehicle

- **US5.2 – Apply Type-Specific Rules**
  - Trucks require a special license.
  - Motorcycles require a minimum customer age.
  - Electric vehicles require a minimum battery level.

---

## Vehicle Types and Rules

| Vehicle Type | Rule |
|---|---|
| Car | Uses the default rental validation rules. |
| Motorcycle | Customer must be at least 18 years old. |
| Van | Uses the default rental validation rules. |
| Truck | Customer must have a special truck license. |
| Electric Vehicle | Battery level must be at least 20%. |

The electric-vehicle battery level must always remain between `0` and `100`.

---

## Business Rules

- Rental IDs must be unique.
- The customer email is required.
- Rental start and end dates are required.
- The end date cannot be before the start date.
- The maximum total rental period is 30 days.
- A vehicle must be `AVAILABLE` before it can be rented.
- A vehicle cannot have more than one active rental.
- Only active rentals can be extended.
- An extension date must be after the existing end date.
- An inactive rental cannot be returned again.
- The return date cannot be before the rental start date.
- A same-day return is billed as one day.
- Late penalties apply only when the actual return date is after the scheduled end date.
- Expiry reminders and expiration emails are sent only once unless the rental is extended.

### Current Pricing Configuration

| Charge | Value |
|---|---:|
| Daily rental rate | `$50.00` per day |
| Late-return penalty | `$20.00` per late day |

These values are configured in `Main.java` through strategy implementations and can be changed without modifying the return-service logic.

---

## Architecture

The project uses a layered architecture:

### 1. Presentation Layer

Responsible for receiving user actions and returning readable messages.

Main classes:

- `ManagerLoginController`
- `ManagerLogoutController`
- `VehicleCatalogController`
- `RentalController`
- `RentalReturnController`

### 2. Application / Service Layer

Contains business workflows and coordinates domain objects, repositories, strategies, and notifications.

Main classes:

- `AuthService`
- `VehicleService`
- `RentalService`
- `RentalReturnService`
- `RentalExpiryReminderService`
- `RentalReturnResult`

### 3. Domain Layer

Contains the core business entities and vehicle-specific behavior.

Main classes:

- `Manager`
- `Rental`
- `Vehicle`
- `Car`
- `Motorcycle`
- `Van`
- `Truck`
- `ElectricVehicle`
- `VehicleStatus`

### 4. Persistence Layer

Stores and retrieves managers, vehicles, and rentals using text files.

Main abstractions:

- `ManagerRepository`
- `VehicleRepository`
- `RentalRepository`

Main implementations:

- `FileManagerRepository`
- `FileVehicleRepository`
- `FileRentalRepository`

### 5. Notification Layer

Encapsulates customer notifications and SMTP email delivery.

Main classes:

- `NotificationService`
- `EmailNotificationService`
- `EmailService`

---

## Design Patterns

### Strategy Pattern

The return workflow depends on abstractions instead of fixed calculations:

- `RentalPricingStrategy`
- `DailyRentalPricingStrategy`
- `LateReturnPenaltyStrategy`
- `DailyLateReturnPenaltyStrategy`

This allows new pricing and penalty policies to be added without rewriting `RentalReturnService`.

### Repository Pattern

Services depend on repository interfaces rather than direct file operations. This separates persistence from business logic and makes testing easier.

### Dependency Injection

Repositories, strategies, and notification services are provided through constructors. Tests can therefore replace real dependencies with mocks or temporary-file implementations.

### Polymorphism

All vehicle types inherit from `Vehicle` and override behavior such as:

- `getType()`
- `validateRental(...)`
- `getExtraData()`

### Notification Abstraction

Application services depend on `NotificationService` instead of directly calling SMTP code. `EmailNotificationService` acts as the email implementation.

---

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 17 | Main programming language |
| Maven | Dependency management and build automation |
| JUnit 5 | Unit and integration testing |
| Mockito 5 | Mocking dependencies in tests |
| JaCoCo | Code-coverage reporting |
| Jakarta Mail | SMTP email delivery |
| java-dotenv | Loading email credentials from `.env` |
| Git / GitHub | Version control and collaboration |
| Eclipse | Primary development environment |

---

## Project Structure

```text
vrms/
├── data/
│   ├── managers.txt
│   ├── rentals.txt
│   └── vehicles.txt
├── doc/
│   └── generated Javadoc files
├── src/
│   ├── main/
│   │   └── java/com/vrms/
│   │       ├── Main.java
│   │       ├── application/
│   │       │   ├── strategy/
│   │       │   └── service classes
│   │       ├── bootstrap/
│   │       ├── domain/
│   │       ├── notification/
│   │       ├── persistence/
│   │       └── presentation/
│   └── test/
│       └── java/com/vrms/
│           ├── application/
│           ├── domain/
│           ├── notification/
│           ├── persistence/
│           └── presentation/
├── .env
├── .gitignore
├── pom.xml
└── README.md
```

---

## Prerequisites

Install the following before running the project:

- Java Development Kit 17 or later.
- Apache Maven 3.8 or later.
- Git, when cloning from GitHub.
- A Gmail account with a Google App Password, when email delivery is enabled.

Check the installed versions:

```bash
java -version
mvn -version
git --version
```

---

## Installation

Clone the repository:

```bash
git clone <REPOSITORY_URL>
cd vehicle-rental-management-system
```

Install dependencies and compile the project:

```bash
mvn clean compile
```

Run all tests:

```bash
mvn clean test
```

---

## Email Configuration

Create a `.env` file in the project root:

```env
EMAIL_USERNAME=your_email@gmail.com
EMAIL_PASSWORD=your_16_character_google_app_password
```

Important rules:

- Use a Google **App Password**, not the normal Gmail password.
- Enable two-step verification on the sender account before creating an App Password.
- `EMAIL_USERNAME` must match the authenticated Gmail sender.
- Do not commit `.env` to GitHub.
- Keep `.env` listed in `.gitignore`.

The application uses Gmail SMTP with TLS on port `587`.

When email credentials are missing or invalid, application startup or email delivery may fail with a clear runtime error.

---

## Running the Application

### Eclipse

1. Import the project as an existing Maven project.
2. Wait for Maven dependencies to finish downloading.
3. Open `src/main/java/com/vrms/Main.java`.
4. Select **Run As → Java Application**.

### Command Line

Run using the Maven Exec Plugin without permanently modifying `pom.xml`:

```bash
mvn org.codehaus.mojo:exec-maven-plugin:3.5.0:java -Dexec.mainClass=com.vrms.Main
```

Run the command from the project root so the application can locate `.env` and the `data` directory.

---

## Default Login

The application creates a default manager when it does not already exist:

```text
Username: admin
Password: 1234
```

These credentials are intended for demonstration and academic testing only.

---

## Application Menu

After a successful login, the manager can use the following menu:

```text
1. View available vehicles
2. Rent a vehicle
3. View active rentals
4. Extend rental period
5. Return a vehicle
6. Generate rental notifications
7. Logout
8. Exit
```

### Example Rental Input

```text
Rental ID: R001
Vehicle ID: V001
Customer name: Ali
Customer email: ali@example.com
Customer age: 25
Special truck license: no
Start date: 2026-07-18
End date: 2026-07-25
```

Dates must use the following format:

```text
yyyy-MM-dd
```

---

## Default Vehicle Data

When `data/vehicles.txt` is empty, the application creates these vehicles:

| ID | Type | Name | Model | Initial Status |
|---|---|---|---|---|
| V001 | Car | Toyota Corolla | 2024 | AVAILABLE |
| V002 | Motorcycle | Honda CBR | 2023 | AVAILABLE |
| V003 | Van | Ford Transit | 2024 | AVAILABLE |
| V004 | Truck | Mercedes Actros | 2022 | AVAILABLE |
| V005 | Electric Vehicle | Tesla Model 3 | 2025 | AVAILABLE |

The default electric vehicle is initialized with an `80%` battery level.

---

## Data Persistence

The current version uses UTF-8 text files stored in the `data` directory.

### Manager Format

```text
username|password
```

Example:

```text
admin|1234
```

### Vehicle Format

```text
id|name|model|status|type|extraData
```

Example:

```text
V005|Tesla Model 3|2025|AVAILABLE|Electric Vehicle|80
```

### Rental Format

```text
rentalId|vehicleId|customerName|customerEmail|startDate|endDate|active|expiryReminderSent|expirationEmailSent
```

Example:

```text
R001|V001|Ali|ali@example.com|2026-07-18|2026-07-25|true|false|false
```

Repository save operations update an existing record when the same ID already exists.

---

## Testing

Run the full test suite:

```bash
mvn clean test
```

Run a full verification build:

```bash
mvn clean verify
```

The tests cover areas such as:

- Valid and invalid manager authentication.
- Logout behavior.
- Available-vehicle filtering.
- Vehicle serialization and deserialization.
- Duplicate rental prevention.
- Rental date validation.
- The 30-day rental limit.
- Vehicle-specific validation rules.
- Active-rental filtering.
- Rental extensions.
- Extension email notifications.
- Expiry reminders.
- Expiration notifications.
- Duplicate-notification prevention.
- Early, on-time, and late returns.
- Rental-cost and penalty calculations.
- Return email notifications.
- File repository persistence.
- Email-service delegation using Mockito.

Tests should finish with:

```text
Failures: 0
Errors: 0
BUILD SUCCESS
```

---

## Code Coverage

JaCoCo is configured in `pom.xml`.

Generate a fresh report:

```bash
mvn clean test
```

Open the generated HTML report:

```text
target/site/jacoco/index.html
```

The project coverage target is at least `80%` for production classes.

Eclipse users can also run:

```text
Coverage As → JUnit Test
```

Coverage for files under `src/test/java` is not the project quality target; the important coverage values are for production classes under `src/main/java`.

---

## Javadoc

Generated Javadoc files are stored in:

```text
doc/index.html
```

Regenerate Maven Javadoc after changing source documentation:

```bash
mvn javadoc:javadoc
```

The generated Maven output is normally located at:

```text
target/site/apidocs/index.html
```

---

## Troubleshooting

### Maven reports `MissingMethodInvocation`

This occurs when Mockito `when(...)` or `verify(...)` is used on a real object instead of a mock.

Incorrect:

```java
RentalRepository repository = new FileRentalRepository(path);
when(repository.findAll()).thenReturn(rentals);
```

Correct mock-based test:

```java
RentalRepository repository = mock(RentalRepository.class);
when(repository.findAll()).thenReturn(rentals);
```

For tests that intentionally use `FileRentalRepository`, save real records and do not use Mockito stubbing on that repository.

### Email is delivered to Spam

- Open the email and select **Not spam**.
- Add the sender to the recipient's contacts.
- Ensure the SMTP sender and the `From` address are the same Gmail account.
- Avoid sending many identical test messages in a short period.
- Use a clear sender name and meaningful email content.

### Email authentication fails

- Confirm that two-step verification is enabled.
- Generate a new Google App Password.
- Remove spaces from the App Password before saving it.
- Confirm that `.env` is located in the project root.
- Confirm that the variables are named exactly `EMAIL_USERNAME` and `EMAIL_PASSWORD`.

### Data appears incorrect after repeated tests

The application persists data in `data/*.txt`. Remove only the test records or reset the files before a clean demonstration. Do not delete production data accidentally.

### Eclipse runs an old version of a test

1. Save all files.
2. Select **Project → Clean**.
3. Refresh the Maven project.
4. Remove old coverage sessions.
5. Run `mvn clean test` from the exact project directory.

---

## Security Notes

The current implementation is designed for academic demonstration.

- Manager passwords are stored as plain text in `data/managers.txt`.
- The default password is publicly known.
- `.env` contains sensitive email credentials and must never be committed.
- Google App Passwords must be revoked immediately if exposed.
- Production systems should use password hashing, access roles, secure secret storage, and a database.

Recommended `.gitignore` entries:

```gitignore
.env
target/
*.log
```

---

## Known Limitations

- The application currently uses a console interface.
- Persistence is file-based and is not designed for concurrent access.
- Manager passwords are not hashed.
- The same base daily price is currently used for all vehicle types.
- Email delivery depends on Gmail SMTP and an internet connection.
- Gmail may classify early test messages as spam.
- The application does not currently include customer accounts or online payments.
- Reservation management is not yet implemented.

---

## Future Improvements

- Hash manager passwords securely.
- Add role-based authorization for administrators and employees.
- Replace text files with SQLite or another SQL database.
- Add reservation management and reservation-conflict detection.
- Add type-specific pricing strategies.
- Add a maintenance status and maintenance records.
- Add an explicit Observer event publisher for rental lifecycle events.
- Add a JavaFX or web-based user interface.
- Add CSV and PDF reports.
- Add GitHub Actions for automated build and testing.
- Add SonarQube or SonarCloud analysis.
- Enforce the 80% JaCoCo threshold automatically during `mvn verify`.

---

## Team

Maximum team size: three students.

| Member | Student ID | Responsibilities |
|---|---|---|
| `<Mahmoud Abu Salameh>` | `<12324141>` | `<It was divided equally amongst us, sprints were divided equally to ensure that the code was reviewed by multiple developers.>` |
| `<Yousef Khayat>` | `<12323154>` | `<It was divided equally amongst us, sprints were divided equally to ensure that the code was reviewed by multiple developers.>` |
| `<Mahmoud Zaben>` | `<12326051>` | `<It was divided equally amongst us, sprints were divided equally to ensure that the code was reviewed by multiple developers.>` |

---

## Academic Documentation

Before submission, include or verify the following:

- Complete UML class diagram.
- Javadoc for classes, methods, and fields.
- JaCoCo coverage report.
- Unit tests and Mockito tests.
- Git history with clear focused commits.
- Pull request descriptions.
- AI refactoring report containing:
  - Files refactored with AI assistance.
  - Prompts used.
  - Original code.
  - Refactored code.
  - Reasons for accepting or rejecting each suggestion.

---

## License

This project was developed for academic and educational purposes. No formal open-source license has been added.
