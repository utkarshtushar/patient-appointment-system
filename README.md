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
- `GET /api/public/slots/available` - Get available slots (Public - Legacy)
- `GET /api/appointments/slots` - Get available slots for patients (Auth required)
- `GET /api/appointments/slots/my-slots` - Get doctor's own slots (Doctor auth required)
- `GET /api/appointments/slots/admin` - Get slots by admin (Admin auth required)
- `POST /api/appointments/book` - Book appointment (Patient auth required)
- `GET /api/appointments/patient/my-appointments` - Get patient appointments
- `GET /api/appointments/doctor/my-appointments` - Get doctor appointments
- `PUT /api/appointments/{id}/cancel` - Cancel appointment

### User Management
- `GET /api/users?role={ROLE}` - Get users by role (Auth required)

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

- JWT-based authentication with role-based access control
- **Patient Role**: Can view available slots and book appointments
- **Doctor Role**: Can view their own complete schedule (available and booked slots)
- **Admin Role**: Can view any doctor's schedule or any patient's appointments
- Password encryption with BCrypt
- CORS configuration
- Secure endpoint protection

## 🔍 Slot Viewing System

### Role-Based Slot Access
- **Patients**: View available slots for specific doctors to book appointments
- **Doctors**: View all their own slots (both available and booked) without needing to specify an ID
- **Admins**: View any doctor's complete schedule OR any patient's appointments (mutually exclusive)

### API Examples
```bash
# Patient viewing available slots for doctor
curl -X GET "http://localhost:8080/api/appointments/slots?doctorId=1&startDate=2025-09-10&endDate=2025-09-15" \
  -H "Authorization: Bearer PATIENT_JWT_TOKEN"

# Doctor viewing their own slots
curl -X GET "http://localhost:8080/api/appointments/slots/my-slots?startDate=2025-09-10&endDate=2025-09-15" \
  -H "Authorization: Bearer DOCTOR_JWT_TOKEN"

# Admin viewing doctor's slots
curl -X GET "http://localhost:8080/api/appointments/slots/admin?doctorId=1&startDate=2025-09-10&endDate=2025-09-15" \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN"

# Admin viewing patient's appointments
curl -X GET "http://localhost:8080/api/appointments/slots/admin?patientId=2&startDate=2025-09-10&endDate=2025-09-15" \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN"
```

## 📊 Database

- **H2 Database**: File-based storage at `./data/appointment_system`
- **Features**: Connection pooling, proper indexing, data persistence
- **Enhanced Queries**: Support for role-based slot viewing and patient appointment tracking

## 🚀 Deployment

### Production Ready Configurations
- **Render.com**: Optimized for free tier deployment
- **Heroku**: PostgreSQL integration ready
- **Docker**: Containerized deployment support

### Environment Profiles
- `application.properties` - Local development
- `application-production.properties` - Production deployment

## 📚 Documentation Files

- `README.md` - Project overview and setup guide
- `FRONTEND_API_DOCUMENTATION.md` - Complete API documentation for frontend developers
- `API_CURL_COMMANDS.md` - Ready-to-use curl commands for all endpoints
- `API_Testing_Guide.txt` - Comprehensive testing workflow
- `PRODUCTION_API_VALIDATION.md` - Production validation checklist

## 🎯 Key Features

### Slot Management System
- **Multi-role Access**: Different views for patients, doctors, and admins
- **Real-time Availability**: Live slot status updates
- **Secure Access**: Role-based permissions with JWT authentication
- **Date Range Filtering**: Flexible date-based slot queries

### Appointment Workflow
1. **Patient**: Search available slots → Book appointment → View appointments
2. **Doctor**: View complete schedule → Manage appointments → Cancel if needed
3. **Admin**: Monitor all slots and appointments → Manage system-wide data

## 👨‍💻 Developer

**Utkarsh Tushar**
- GitHub: [@utkarshtushar](https://github.com/utkarshtushar)

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📞 Support

For support, email utkarshtushar@example.com or create an issue in the GitHub repository.
