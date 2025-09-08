# Patient Appointment System - Complete API Documentation

## 🚀 Production URLs (Customizable)
**Replace `{BASE_URL}` with your actual deployment URL:**
- **Local Development**: `http://localhost:8080`
- **Heroku**: `https://your-app-name.herokuapp.com`
- **Railway**: `https://your-app-name.up.railway.app`
- **Render**: `https://your-app-name.onrender.com`
- **Custom Domain**: `https://your-custom-domain.com`

### Quick Reference URLs
- **Live App**: `{BASE_URL}`
- **Health Check**: `{BASE_URL}/actuator/health`
- **API Base**: `{BASE_URL}/api`

---

## 🏠 Basic Endpoints

### 1. Home Page
```bash
curl -X GET "{BASE_URL}/"
```
**Response**: `"Patient Appointment System is running successfully!"`

### 2. API Test Endpoint
```bash
curl -X GET "{BASE_URL}/api/test"
```
**Response**: `"API endpoints are working!"`

---

## 🔐 Authentication APIs (`/api/auth`)

### 3. User Registration (Patient)
```bash
curl -X POST "{BASE_URL}/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "patient@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "+1234567890",
    "role": "PATIENT"
  }'
```

### 4. User Registration (Doctor) - Complete
```bash
curl -X POST "{BASE_URL}/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "doctor@example.com",
    "password": "doctor123",
    "firstName": "Dr. Sarah",
    "lastName": "Wilson",
    "phoneNumber": "+1234567891",
    "role": "DOCTOR",
    "specialization": "Cardiology",
    "licenseNumber": "MD123456789"
  }'
```
**Required Fields for Doctor:**
- `specialization` (required): Doctor's medical specialization
- `licenseNumber` (required): Medical license number

### 5. User Registration (Admin)
```bash
curl -X POST "{BASE_URL}/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "admin123",
    "firstName": "System",
    "lastName": "Admin",
    "phoneNumber": "+1234567892",
    "role": "ADMIN"
  }'
```

### 6. User Login
```bash
curl -X POST "{BASE_URL}/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "patient@example.com",
    "password": "password123"
  }'
```
**Response**: Returns JWT token and user details
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "email": "patient@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "PATIENT"
  }
}
```

---

## 👥 User Management APIs (`/api/users`)

### Get Users by Role
**Endpoint**: `GET /api/users?role={ROLE}`  
**Authentication**: Required (All roles: PATIENT, DOCTOR, ADMIN)  
**Description**: Get filtered list of users by role

#### Get All Doctors
```bash
curl -X GET "{BASE_URL}/api/users?role=DOCTOR" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json"
```

#### Get All Patients
```bash
curl -X GET "{BASE_URL}/api/users?role=PATIENT" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json"
```

#### Get All Admins
```bash
curl -X GET "{BASE_URL}/api/users?role=ADMIN" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json"
```

**Sample Response**:
```json
[
  {
    "id": 1,
    "firstName": "Dr. John",
    "lastName": "Doe",
    "email": "doctor@example.com",
    "phoneNumber": "+1234567890",
    "role": "DOCTOR",
    "specialization": "Cardiology",
    "licenseNumber": "DOC123456",
    "createdAt": "2025-09-09T12:00:00"
  }
]
```

---

## 📅 Appointment Management APIs (`/api/appointments`)

### 7. Get Available Slots (Public - No Auth)
```bash
curl -X GET "{BASE_URL}/api/public/slots/available?doctorId=1&startDate=2025-09-06&endDate=2025-09-13" \
  -H "Content-Type: application/json"
```
**Query Parameters:**
- `doctorId` (required): ID of the doctor
- `startDate` (required): Start date (YYYY-MM-DD format)
- `endDate` (required): End date (YYYY-MM-DD format)

### 8. Book an Appointment (Patient Auth Required)
```bash
curl -X POST "{BASE_URL}/api/appointments/book" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {PATIENT_JWT_TOKEN}" \
  -d '{
    "slotId": 1,
    "patientNotes": "Regular checkup appointment"
  }'
```

### 9. Get Patient's Appointments (Patient Auth Required)
```bash
curl -X GET "{BASE_URL}/api/appointments/patient/my-appointments" \
  -H "Authorization: Bearer {PATIENT_JWT_TOKEN}"
```

### 10. Get Doctor's Appointments (Doctor Auth Required)
```bash
curl -X GET "{BASE_URL}/api/appointments/doctor/my-appointments" \
  -H "Authorization: Bearer {DOCTOR_JWT_TOKEN}"
```

### 11. Cancel an Appointment (Patient/Doctor Auth Required)
```bash
curl -X PUT "{BASE_URL}/api/appointments/{appointmentId}/cancel" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```
**Path Parameters:**
- `appointmentId`: ID of the appointment to cancel

### 12. Get Specific Appointment Details (Patient/Doctor/Admin Auth Required)
```bash
curl -X GET "{BASE_URL}/api/appointments/{appointmentId}" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```
**Path Parameters:**
- `appointmentId`: ID of the appointment to retrieve

---

## 🏥 Health & Monitoring APIs

### 13. Application Health Check (Public)
```bash
curl -X GET "{BASE_URL}/actuator/health"
```
**Response**: Application and database health status

### 14. Application Information (Public)
```bash
curl -X GET "{BASE_URL}/actuator/info"
```
**Response**: Application metadata and version information

### 15. Application Metrics (Public)
```bash
curl -X GET "{BASE_URL}/actuator/metrics"
```
**Response**: Application performance metrics

---

## 📊 Complete API Summary

| # | Method | Endpoint | Auth Required | Role | Description |
|---|--------|----------|---------------|------|-------------|
| 1 | GET | `/` | No | Public | Home page |
| 2 | GET | `/api/test` | No | Public | API test endpoint |
| 3 | POST | `/api/auth/register` | No | Public | User registration |
| 4 | POST | `/api/auth/login` | No | Public | User login |
| 5 | GET | `/api/public/slots/available` | No | Public | Get available appointment slots |
| 6 | POST | `/api/appointments/book` | Yes | PATIENT | Book an appointment |
| 7 | GET | `/api/appointments/patient/my-appointments` | Yes | PATIENT | Get patient's appointments |
| 8 | GET | `/api/appointments/doctor/my-appointments` | Yes | DOCTOR | Get doctor's appointments |
| 9 | PUT | `/api/appointments/{id}/cancel` | Yes | PATIENT/DOCTOR | Cancel appointment |
| 10 | GET | `/api/appointments/{id}` | Yes | PATIENT/DOCTOR/ADMIN | Get appointment details |
| 11 | GET | `/actuator/health` | No | Public | Health check |
| 12 | GET | `/actuator/info` | No | Public | Application info |
| 13 | GET | `/actuator/metrics` | No | Public | Application metrics |
| 14 | GET | `/api/users` | Yes | PATIENT/DOCTOR/ADMIN | Get users by role |

---

## 🔧 Environment Configuration

### Local Development
```bash
export BASE_URL="http://localhost:8080"
```

### Heroku Deployment
```bash
export BASE_URL="https://your-heroku-app-name.herokuapp.com"
```

### Railway Deployment
```bash
export BASE_URL="https://your-railway-app-name.up.railway.app"
```

### Render Deployment
```bash
export BASE_URL="https://your-app-name.onrender.com"
```

### Custom Domain
```bash
export BASE_URL="https://api.yourdomain.com"
```

---

## 📝 Complete Testing Workflow

### Step 1: Test Basic Connectivity
```bash
# Test home page
curl -X GET "{BASE_URL}/"

# Test API endpoint
curl -X GET "{BASE_URL}/api/test"

# Test health check
curl -X GET "{BASE_URL}/actuator/health"
```

### Step 2: Test Authentication Flow
```bash
# Register a patient
curl -X POST "{BASE_URL}/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"email":"test@patient.com","password":"test123","firstName":"Test","lastName":"Patient","phoneNumber":"+1234567890","role":"PATIENT"}'

# Register a doctor
curl -X POST "{BASE_URL}/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"email":"test@doctor.com","password":"test123","firstName":"Dr. Test","lastName":"Doctor","phoneNumber":"+1234567891","role":"DOCTOR","specialization":"General Medicine"}'

# Login as patient
curl -X POST "{BASE_URL}/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"test@patient.com","password":"test123"}'

# Save the JWT token from response for next steps
```

### Step 3: Test Appointment System
```bash
# Get available slots (no auth needed)
curl -X GET "{BASE_URL}/api/public/slots/available?doctorId=1&startDate=2025-09-06&endDate=2025-09-13"

# Book appointment (use patient JWT token)
curl -X POST "{BASE_URL}/api/appointments/book" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {PATIENT_JWT_TOKEN}" \
  -d '{"slotId":1,"patientNotes":"Regular checkup"}'

# Get patient appointments
curl -X GET "{BASE_URL}/api/appointments/patient/my-appointments" \
  -H "Authorization: Bearer {PATIENT_JWT_TOKEN}"
```

---

## 🔒 Security & Authentication

### JWT Token Usage
- All tokens expire in 24 hours
- Include token in Authorization header: `Authorization: Bearer {JWT_TOKEN}`
- Tokens are role-specific (PATIENT, DOCTOR, ADMIN)

### Role-Based Access Control
- **Public**: Home, Test, Health, Available Slots, Auth endpoints
- **PATIENT**: Book appointments, view own appointments
- **DOCTOR**: View own appointments, cancel appointments
- **ADMIN**: View all appointments, access admin endpoints

### Request Headers
```bash
# For JSON requests
-H "Content-Type: application/json"

# For authenticated requests
-H "Authorization: Bearer {JWT_TOKEN}"
```

---

## 🚨 Error Handling

### Common HTTP Status Codes
- **200 OK**: Request successful
- **400 Bad Request**: Invalid request data
- **401 Unauthorized**: Missing or invalid JWT token
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Server error

### Example Error Responses
```json
{
  "error": "Email already exists",
  "timestamp": "2025-09-05T10:30:00Z"
}
```

---

## 🎯 Quick Start Commands

Replace `{BASE_URL}` with your actual deployment URL and run these commands to test your API:

```bash
# Set your base URL
BASE_URL="https://your-app-name.herokuapp.com"

# Test connectivity
curl -X GET "${BASE_URL}/actuator/health"

# Test public API
curl -X GET "${BASE_URL}/api/public/slots/available?doctorId=1&startDate=2025-09-06&endDate=2025-09-13"

# Register and test authentication
curl -X POST "${BASE_URL}/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@example.com","password":"demo123","firstName":"Demo","lastName":"User","phoneNumber":"+1234567890","role":"PATIENT"}'

# Login and get JWT token
curl -X POST "${BASE_URL}/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@example.com","password":"demo123"}'
```

---

## 🚀 Complete Git & Heroku Deployment Commands

### **Step 1: Initialize Git (if not already done)**
```bash
cd "C:\Utkarsh\Personal\appointmentSystem"
git init
git branch -M main
```

### **Step 2: Add Remote Repository (Optional - if you want to push to GitHub)**
```bash
# Replace 'utkarshtushar' with your GitHub username
git remote add origin https://github.com/utkarshtushar/patient-appointment-system.git
```

### **Step 3: Commit All Changes**
```bash
# Add all files to staging
git add .

# Commit with descriptive message
git commit -m "Production ready: Complete API documentation, Heroku config, and free tier optimization

# Push to GitHub (optional)
git push -u origin main --force
```

### **Step 4: Deploy to Heroku**
```bash
# Login to Heroku
heroku login

# Create Heroku app (replace with your preferred name)
heroku create utkarsh-appointment-system

# Add free PostgreSQL database
heroku addons:create heroku-postgresql:mini

# Set required environment variables
heroku config:set SPRING_PROFILES_ACTIVE=heroku
heroku config:set JWT_SECRET=MySecureJWTSecretKey123456789012345678901234567890

# Add Heroku remote
heroku git:remote -a utkarsh-appointment-system

# Deploy to Heroku
git push heroku main

# Scale web dyno to 1
heroku ps:scale web=1

# Open your live application
heroku open
```

### **Step 5: Verify Deployment**
```bash
# Check if app is running
heroku ps --app utkarsh-appointment-system

# View logs
heroku logs --tail --app utkarsh-appointment-system

# Test your live API
curl -X GET "https://utkarsh-appointment-system.herokuapp.com/actuator/health"
```

### **Step 6: Deploy to Render**
```bash
# Login to Render
render login

# Create a new Web Service
render create web-service

# Follow prompts to set up your service
# - Name: utkarsh-appointment-system
# - Region: Choose closest to you
# - Branch: main
# - Root Directory: Leave blank

# Set environment variables
render env add SPRING_PROFILES_ACTIVE render
render env add JWT_SECRET MySecureJWTSecretKey123456789012345678901234567890

# Deploy the service
git push render main

# Open your live application
render open utkarsh-appointment-system
```
