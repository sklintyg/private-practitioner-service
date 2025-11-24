# Private Practitioner Service

A Spring Boot service for managing private practitioner registrations and authentication for the
Webcert platform.

## Overview

The Private Practitioner Service (formerly Privatläkarportal) provides registration, authentication,
and management capabilities for private practitioners who need access to Webcert. It integrates with
HSA (Health and Social Care Administration) and manages practitioner credentials, subscriptions, and
notifications.

## Key Features

- **Private Practitioner Registration**: Handles registration workflow for private practitioners
- **HSA Integration**: Fetches and validates practitioner credentials from Socialstyrelsen (Swedish
  National Board of Health and Welfare)
- **Email Notifications**: Sends status notifications (approved, rejected, pending, removed)
- **Scheduled HSA Updates**: Automatic credential updates via scheduled jobs
- **Internal API**: Provides endpoints for integration with Webcert, Rehabstöd and Customer Termination
- **Database Management**: Uses Liquibase for database migrations

## Technology Stack

- **Java 21**
- **Spring Boot 3.x**
- **Gradle** - Build automation
- **MySQL** - Primary database (H2 for development)
- **Redis** - Distributed locks
- **Liquibase** - Database migration management
- **Docker** - Containerization support

## Prerequisites

- Java Development Kit (JDK) 21
- Gradle (wrapper included)
- MySQL database (or H2 for local development)
- Redis server
- Docker (optional, for containerized deployment)

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/sklintyg/privatlakarportal.git
cd privatlakarportal
```

### Build the Application

Build the application using the Gradle wrapper:

```bash
./gradlew clean build
```

### Run Locally

#### Using Gradle

```bash
./gradlew bootRun
```

#### Using Java

```bash
java -jar app/build/libs/app.jar
```

### Configuration

The application can be configured via `application.properties` or environment variables. Key
configuration properties:

#### Database Configuration

```properties
database.name=privatlakarportal
database.server=<db-host>
database.password=<username>
database.username=<password>
```

#### Redis Configuration

```properties
spring.data.redis.host=<redis-host>
spring.data.redis.password=<password>
spring.data.redis.port=6379
```

#### Mail Configuration

```properties
mail.host=<smtp-host>
mail.port=25
mail.from=<sender-email>
mail.admin=<admin-email>
```

#### API Ports

- Internal API: `server.port=8081`

### Development Setup

For local development with stubbed data, refer to the configuration in:

- `app/src/main/resources/application.properties`
- `devops/dev/config/application-dev.properties`

## Integration with Webcert

The Private Practitioner Service is designed to work alongside Webcert. To integrate with a local
Webcert instance:

### Prerequisites

1. Both services must be running
2. Test data must be consistent across both services

### Test Users

| PNR           | name                | case                                                            | route             | action                           |
|---------------|---------------------|-----------------------------------------------------------------|-------------------|----------------------------------|
| 191212121212  | Tolvan Tolvanson    | Har inget avtal, inget pp-konto och inga hosp-uppgifter         | /info-inget-avtal | No actions available             |
| 197705232382  | Frida Kranstege     | Har inget avtal, men har pp-konto och hosp-uppgifter            | /info             | Can only manage pp-account       |
| 199608112380  | Petra Privat        | Har avtal, hosp-uppgifter men inte pp-konto                     | /register         | Can only register for pp-account |
| 195212222318  | Foris Dorisson1     | Har avtal, pp-konto men inte hosp-uppgifter                     | /info             | Can only manage pp-account       |
| 197309069289  | Nina Maria Greger   | Har avtal, pp-konto och hosp-uppgifter, inte läkarlegitimation  | /info             | Can only manage pp-account       |
| 195711092642  | Amanda Tigersson    | Har avtal, pp-konto och hosp-uppgifter, återkallad legitimation | /info             | Can only manage pp-account       |
| 199001092387  | Maj Pärsson         | Har avtal, pp-konto och hosp-uppgifter, läkarlegitimation       | /search           | Can do all actions               |


### Configuration Steps

1. Start the Private Practitioner Service (default ports: 18070)

2. Configure Webcert by setting these properties in `webcert/webcert-dev.properties`:

```properties
privatepractitionerservice.base.url=http://localhost:18070/internalapi/privatepractitioner
```
3. Start Webcert with spring profile `private-practitioner-service-active`

4. Access Webcert at `https://wc.localtest.me/welcome.html`

5. Log in as "Maj Pärsson (Privatläkare, Godkänd)"

6. User validation will be performed against the local Private Practitioner Service

## Project Structure

```
private-practitioner-service/
├── app/                          # Main application module
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── se/inera/intyg/privatepractitionerservice/
│   │   │   │       ├── application/          # Application layer
│   │   │   │       └── infrastructure/       # Infrastructure layer
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       ├── changelog/               # Liquibase migrations
│   │   │       └── bootstrap-*/             # Bootstrap data
│   │   └── test/                            # Test sources
│   └── build.gradle
├── devops/                       # DevOps configurations
│   └── dev/                     # Development environment config
├── gradle/                       # Gradle wrapper files
├── http/                         # HTTP request examples
├── build.gradle                  # Root build configuration
├── Dockerfile                    # Docker configuration
└── README.md
```

## API Documentation

The service exposes an internal API interface:

### Internal API (Port 18070)

- `/internalapi/*` - Internal endpoints for Webcert, Rehabstöd and Customer Termation integrations
- Registration endpoints
- Practitioner information retrieval
- Status and health checks

For detailed API testing, see the `http/internal-api.http` file.

## Database Management

The service uses Liquibase for database schema management. Changelog files are located in:

- `app/src/main/resources/changelog/`

## Testing

Run all tests:

```bash
./gradlew test
```

View test reports:

```bash
./gradlew testReport
```

Test coverage (Jacoco):

```bash
./gradlew jacocoTestReport
```

## Docker

Build Docker image:

```bash
docker build -t private-practitioner-service .
```

The Dockerfile is configured to use the built JAR from `app/build/libs/app.jar`.

## Scheduled Jobs

The service includes scheduled tasks:

- **HOSP Update Job**: `privatlakarportal.hospupdate.cron=0 0 0 1/5 * *`
    - Runs every 5 days at midnight
    - Updates practitioner credentials from HSA

## Monitoring and Health Checks

Health check endpoint available (check deployment configuration for exact URL).

The service includes:

- Spring Boot Actuator endpoints
- Custom health indicators
- Monitoring integration

## Security

The service implements:

- Secure SMTP email delivery
- Redis authentication

## Email Notifications

The service sends automated email notifications for:

- **Approved**: Practitioner approved and can use Webcert
- **Rejected**: Access denied due to missing credentials or suspension
- **Pending**: Credentials not yet available from Socialstyrelsen
- **Removed**: Registration removed after multiple failed validation attempts
- **Admin Alerts**: HSA ID allocation warnings

## Contributing

Follow the project's coding standards and include tests for new features.

## Further Documentation

For more detailed setup and deployment instructions, refer to:

- [DevOps Documentation](https://github.com/sklintyg/devops/tree/release/2021-1/develop/README.md)
- Project wiki (if available)

## License

Copyright (C) 2025 Inera AB (http://www.inera.se)

Private Practitioner Service is free software: you can redistribute it and/or modify it under the
terms of the GNU Affero General Public License as published by the Free Software Foundation, either
version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero
General Public License for more details.

For the full license text, see [LICENSE](LICENSE).

## Support

For issues and questions, please contact Inera AB or refer to the project's issue tracker. 
