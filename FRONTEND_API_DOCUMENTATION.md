# Patient Appointment System - API Documentation for Frontend

**Base URL:** `https://appointment-system-utkarsh.onrender.com`

## Authentication
All protected endpoints require a JWT token in the Authorization header:
```
Authorization: Bearer <jwt_token>
```

---

## 🔐 Authentication APIs

### 1. User Login
**Endpoint:** `POST /api/auth/login`  
**Access:** Public  
**Description:** Authenticate user and get JWT token

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "user@example.com",
    "phoneNumber": "+1234567890",
    "role": "PATIENT",
    "specialization": null,
    "licenseNumber": null,
    "createdAt": "2025-09-07T12:00:00"
  }
}
```

**Error Response (400 Bad Request):**
```json
{
  "error": "Invalid credentials"
}
```

### 2. User Registration
**Endpoint:** `POST /api/auth/register`  
**Access:** Public  
**Description:** Register a new user (Patient/Doctor)

**Request Body for Patient:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "patient@example.com",
  "password": "password123",
  "phoneNumber": "+1234567890",
  "role": "PATIENT"
}
```

**Request Body for Doctor:**
```json
{
  "firstName": "Dr. Jane",
  "lastName": "Smith",
  "email": "doctor@example.com",
  "password": "password123",
  "phoneNumber": "+1234567890",
  "role": "DOCTOR",
  "specialization": "Cardiology",
  "licenseNumber": "DOC123456"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "patient@example.com",
  "phoneNumber": "+1234567890",
  "role": "PATIENT",
  "specialization": null,
  "licenseNumber": null,
  "createdAt": "2025-09-07T12:00:00"
}
```

**Error Response (400 Bad Request):**
```json
{
  "error": "Email already exists"
}
```

---

## 👥 User Management APIs

### 11. Get Users by Role
**Endpoint:** `GET /api/users?role={ROLE}`  
**Access:** PATIENT, DOCTOR, ADMIN  
**Authentication:** Required  
**Description:** Get filtered list of users by role

**Query Parameters:**
- `role` (required): User role (DOCTOR, PATIENT, ADMIN)

**Example Requests:**
```bash
# Get all doctors
GET /api/users?role=DOCTOR

# Get all patients
GET /api/users?role=PATIENT

# Get all admins
GET /api/users?role=ADMIN
```

**Response (200 OK):**
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
  },
  {
    "id": 2,
    "firstName": "Dr. Jane",
    "lastName": "Smith",
    "email": "doctor2@example.com",
    "phoneNumber": "+1234567891",
    "role": "DOCTOR",
    "specialization": "General Medicine",
    "licenseNumber": "DOC789123",
    "createdAt": "2025-09-09T12:15:00"
  }
]
```

**Error Response (400 Bad Request):**
```json
{
  "error": "Invalid role parameter"
}
```

**Error Response (401 Unauthorized):**
```json
{
  "error": "Authentication required"
}
```

---

## 📅 Appointment APIs

### 3. Get Available Slots
**Endpoint:** `GET /api/public/slots/available`  
**Access:** Public  
**Description:** Get available appointment slots for a doctor

**Query Parameters:**
- `doctorId` (required): Doctor's ID
- `startDate` (required): Start date in YYYY-MM-DD format
- `endDate` (required): End date in YYYY-MM-DD format

**Example Request:**
```
GET /api/public/slots/available?doctorId=1&startDate=2025-09-10&endDate=2025-09-15
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "doctorId": 1,
    "appointmentDate": "2025-09-10",
    "startTime": "09:00:00",
    "endTime": "09:30:00",
    "isAvailable": true,
    "createdAt": "2025-09-07T12:00:00"
  },
  {
    "id": 2,
    "doctorId": 1,
    "appointmentDate": "2025-09-10",
    "startTime": "09:30:00",
    "endTime": "10:00:00",
    "isAvailable": true,
    "createdAt": "2025-09-07T12:00:00"
  }
]
```

### 4. Book Appointment
**Endpoint:** `POST /api/appointments/book`  
**Access:** PATIENT only  
**Authentication:** Required  
**Description:** Book an appointment slot

**Request Body:**
```json
{
  "slotId": 1,
  "patientNotes": "Regular checkup appointment"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "patientId": 2,
  "doctorId": 1,
  "slotId": 1,
  "appointmentDate": "2025-09-10",
  "startTime": "09:00:00",
  "endTime": "09:30:00",
  "status": "SCHEDULED",
  "patientNotes": "Regular checkup appointment",
  "doctorNotes": null,
  "createdAt": "2025-09-07T12:00:00",
  "updatedAt": "2025-09-07T12:00:00"
}
```

**Error Response (400 Bad Request):**
```json
{
  "error": "Slot is not available"
}
```

### 5. Get Patient's Appointments
**Endpoint:** `GET /api/appointments/patient/my-appointments`  
**Access:** PATIENT only  
**Authentication:** Required  
**Description:** Get all appointments for the logged-in patient

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "patientId": 2,
    "doctorId": 1,
    "slotId": 1,
    "appointmentDate": "2025-09-10",
    "startTime": "09:00:00",
    "endTime": "09:30:00",
    "status": "SCHEDULED",
    "patientNotes": "Regular checkup",
    "doctorNotes": null,
    "createdAt": "2025-09-07T12:00:00",
    "updatedAt": "2025-09-07T12:00:00"
  }
]
```

### 6. Get Doctor's Appointments
**Endpoint:** `GET /api/appointments/doctor/my-appointments`  
**Access:** DOCTOR only  
**Authentication:** Required  
**Description:** Get all appointments for the logged-in doctor

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "patientId": 2,
    "doctorId": 1,
    "slotId": 1,
    "appointmentDate": "2025-09-10",
    "startTime": "09:00:00",
    "endTime": "09:30:00",
    "status": "SCHEDULED",
    "patientNotes": "Regular checkup",
    "doctorNotes": "Patient seems healthy",
    "createdAt": "2025-09-07T12:00:00",
    "updatedAt": "2025-09-07T12:00:00"
  }
]
```

### 7. Get Specific Appointment
**Endpoint:** `GET /api/appointments/{appointmentId}`  
**Access:** PATIENT, DOCTOR, ADMIN  
**Authentication:** Required  
**Description:** Get details of a specific appointment

**Path Parameters:**
- `appointmentId`: ID of the appointment

**Response (200 OK):**
```json
{
  "id": 1,
  "patientId": 2,
  "doctorId": 1,
  "slotId": 1,
  "appointmentDate": "2025-09-10",
  "startTime": "09:00:00",
  "endTime": "09:30:00",
  "status": "SCHEDULED",
  "patientNotes": "Regular checkup",
  "doctorNotes": null,
  "createdAt": "2025-09-07T12:00:00",
  "updatedAt": "2025-09-07T12:00:00"
}
```

**Error Response (404 Not Found):**
```json
{
  "error": "Appointment not found"
}
```

### 8. Cancel Appointment
**Endpoint:** `PUT /api/appointments/{appointmentId}/cancel`  
**Access:** PATIENT, DOCTOR  
**Authentication:** Required  
**Description:** Cancel an existing appointment

**Path Parameters:**
- `appointmentId`: ID of the appointment to cancel

**Response (200 OK):**
```json
{
  "id": 1,
  "patientId": 2,
  "doctorId": 1,
  "slotId": 1,
  "appointmentDate": "2025-09-10",
  "startTime": "09:00:00",
  "endTime": "09:30:00",
  "status": "CANCELLED",
  "patientNotes": "Regular checkup",
  "doctorNotes": null,
  "createdAt": "2025-09-07T12:00:00",
  "updatedAt": "2025-09-07T12:30:00"
}
```

---

## 🧪 System Health APIs

### 9. Health Check
**Endpoint:** `GET /`  
**Access:** Public  
**Description:** Check if the system is running

**Response (200 OK):**
```
Patient Appointment System is running successfully!
```

### 10. API Test
**Endpoint:** `GET /api/test`  
**Access:** Public  
**Description:** Test API endpoints

**Response (200 OK):**
```
API endpoints are working!
```

---

## 📋 Data Models

### User Roles
```javascript
const USER_ROLES = {
  PATIENT: "PATIENT",
  DOCTOR: "DOCTOR",
  ADMIN: "ADMIN"
};
```

### Appointment Status
```javascript
const APPOINTMENT_STATUS = {
  SCHEDULED: "SCHEDULED",
  COMPLETED: "COMPLETED",
  CANCELLED: "CANCELLED",
  NO_SHOW: "NO_SHOW"
};
```

### User Object Structure
```javascript
{
  id: Number,
  firstName: String,
  lastName: String,
  email: String,
  phoneNumber: String,
  role: String, // PATIENT, DOCTOR, ADMIN
  specialization: String, // Only for doctors
  licenseNumber: String, // Only for doctors
  createdAt: String // ISO datetime
}
```

### Appointment Object Structure
```javascript
{
  id: Number,
  patientId: Number,
  doctorId: Number,
  slotId: Number,
  appointmentDate: String, // YYYY-MM-DD
  startTime: String, // HH:mm:ss
  endTime: String, // HH:mm:ss
  status: String, // SCHEDULED, COMPLETED, CANCELLED, NO_SHOW
  patientNotes: String,
  doctorNotes: String,
  createdAt: String, // ISO datetime
  updatedAt: String // ISO datetime
}
```

### Appointment Slot Object Structure
```javascript
{
  id: Number,
  doctorId: Number,
  appointmentDate: String, // YYYY-MM-DD
  startTime: String, // HH:mm:ss
  endTime: String, // HH:mm:ss
  isAvailable: Boolean,
  createdAt: String // ISO datetime
}
```

---

## 🔒 Authentication Flow

### 1. User Registration/Login
1. Call `/api/auth/register` or `/api/auth/login`
2. Store the JWT token from response
3. Include token in all subsequent API calls

### 2. Making Authenticated Requests
```javascript
// Example using fetch
const response = await fetch('/api/appointments/patient/my-appointments', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${jwtToken}`,
    'Content-Type': 'application/json'
  }
});
```

### 3. Token Validation
- Tokens are validated on every protected endpoint
- If token is invalid/expired, you'll receive a 401 Unauthorized response
- Frontend should redirect to login page on 401 errors

---

## 🚨 Error Handling

### Common HTTP Status Codes
- **200 OK**: Request successful
- **400 Bad Request**: Invalid request data
- **401 Unauthorized**: Authentication required or token invalid
- **403 Forbidden**: Access denied (insufficient permissions)
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Server error

### Error Response Format
```json
{
  "error": "Error message describing what went wrong"
}
```

---

## 📱 Frontend Integration Tips

### 1. State Management
- Store JWT token in localStorage or sessionStorage
- Store user profile data in global state (Redux/Context)
- Clear token and user data on logout

### 2. API Client Setup
```javascript
// Example axios setup
const apiClient = axios.create({
  baseURL: 'https://appointment-system-utkarsh.onrender.com',
  headers: {
    'Content-Type': 'application/json'
  }
});

// Add token to requests
apiClient.interceptors.request.use(config => {
  const token = localStorage.getItem('jwt_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

### 3. Route Protection
- Implement route guards based on user roles
- Redirect unauthenticated users to login
- Show appropriate UI based on user role (PATIENT/DOCTOR)

### 4. Date/Time Handling
- All dates are in YYYY-MM-DD format
- All times are in HH:mm:ss format
- Convert to local timezone for display if needed

---

## 🔄 Typical User Flows

### Patient Flow
1. Register/Login → Get JWT token
2. Search for available slots → `GET /api/public/slots/available`
3. Book appointment → `POST /api/appointments/book`
4. View appointments → `GET /api/appointments/patient/my-appointments`
5. Cancel if needed → `PUT /api/appointments/{id}/cancel`

### Doctor Flow
1. Register/Login → Get JWT token
2. View appointments → `GET /api/appointments/doctor/my-appointments`
3. View specific appointment details → `GET /api/appointments/{id}`
4. Cancel if needed → `PUT /api/appointments/{id}/cancel`

This documentation provides all the APIs your frontend team needs to build the complete patient appointment system interface.
