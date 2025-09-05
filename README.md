# Patient Appointment Booking System

A comprehensive appointment booking system built with Spring Boot, featuring real-time slot management, JWT authentication, and distributed architecture.

## 🚀 Features

- **User Management**: Patient, Doctor, and Admin roles with JWT authentication
- **Appointment Booking**: Real-time slot availability with concurrent booking protection
- **Distributed Architecture**: Redis caching and RabbitMQ messaging
- **Database Management**: H2 file-based database with persistence
- **Security**: JWT-based authentication with role-based access control
- **Monitoring**: Spring Actuator health checks and metrics
- **API Documentation**: RESTful APIs with comprehensive testing guides

## 🛠️ Tech Stack

- **Backend**: Spring Boot 3.1.5, Java 17
- **Database**: H2 (File-based), PostgreSQL ready
- **Caching**: Redis for session management
- **Message Queue**: RabbitMQ for async notifications
- **Security**: Spring Security with JWT
- **Build Tool**: Maven

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Git

## 🚀 Quick Start

1. **Clone the repository**
   ```bash
   git clone https://github.com/utkarshtushar/patient-appointment-system.git
   cd patient-appointment-system
   ```

2. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

3. **Access the application**
   - Application: http://localhost:8080
   - H2 Console: http://localhost:8080/h2-console
   - Health Check: http://localhost:8080/actuator/health

## 📡 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login

### Appointments
- `GET /api/appointments/slots/available` - Get available slots (Public)
- `POST /api/appointments/book` - Book appointment (Auth required)
- `GET /api/appointments/patient/my-appointments` - Get user appointments

### Health & Monitoring
- `GET /actuator/health` - Application health status

## 🧪 Testing

Use the provided curl commands in `API_CURL_COMMANDS.md` for comprehensive testing.

Example:
```bash
# Get available slots
curl -X GET "http://localhost:8080/api/appointments/slots/available?doctorId=1&startDate=2025-09-05&endDate=2025-09-12"
```

## 🏗️ Project Structure

```
src/
├── main/java/com/appointment/system/
│   ├── config/          # Configuration classes
│   ├── controller/      # REST controllers
│   ├── dto/            # Data Transfer Objects
│   ├── entity/         # JPA entities
│   ├── repository/     # Data repositories
│   ├── security/       # Security configuration
│   └── service/        # Business logic
└── resources/
    └── application.properties
```

## 🔐 Security Features

- JWT-based authentication
- Role-based access control (PATIENT, DOCTOR, ADMIN)
- Password encryption with BCrypt
- CORS configuration

## 📊 Database

- **H2 Database**: File-based storage at `./data/appointment_system`
- **Features**: Connection pooling, proper indexing, data persistence

## 👨‍💻 Developer

**Utkarsh Tushar**
- GitHub: [@utkarshtushar](https://github.com/utkarshtushar)
