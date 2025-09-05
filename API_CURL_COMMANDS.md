# Patient Appointment System - Optimized API Testing Guide

## Application Status
✅ **Application**: http://localhost:8080
✅ **H2 Console**: http://localhost:8080/h2-console  
✅ **Database**: jdbc:h2:file:./data/appointment_system (user: sa, password: empty)

---

## 🚀 QUICK START - Sequential Testing Order

### Phase 1: System Setup & Health Check
### Phase 2: User Registration & Authentication  
### Phase 3: Doctor Schedule & Slot Setup
### Phase 4: Patient Appointment Booking Flow
### Phase 5: Doctor Appointment Management
### Phase 6: Administrative Operations

---

## Phase 1: System Setup & Health Check

### 1.1 Health Check
```bash
curl -X GET http://localhost:8080/
```
**Expected Response:** `"Patient Appointment System is running successfully!"`

### 1.2 API Test Endpoint
```bash
curl -X GET http://localhost:8080/api/test
```
**Expected Response:** `"API endpoints are working!"`

---

## Phase 2: User Registration & Authentication

### 2.1 Register Doctor (Primary)
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Dr. John",
    "lastName": "Smith",
    "email": "doctor.smith@hospital.com",
    "password": "password123",
    "phoneNumber": "+1234567890",
    "role": "DOCTOR",
    "specialization": "Cardiology",
    "licenseNumber": "DOC12345"
  }'
```
**Note:** Save the doctor ID from response (usually ID: 1)

### 2.2 Register Patient (Primary)
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "lastName": "Doe",
    "email": "jane.doe@email.com",
    "password": "password123",
    "phoneNumber": "+1987654321",
    "role": "PATIENT"
  }'
```
**Note:** Save the patient ID from response (usually ID: 2)

### 2.3 Register Admin (Optional)
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Admin",
    "lastName": "User",
    "email": "admin@hospital.com",
    "password": "admin123",
    "phoneNumber": "+1111111111",
    "role": "ADMIN"
  }'
```

### 2.4 Login Doctor & Get Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "doctor.smith@hospital.com",
    "password": "password123"
  }'
```
**Action Required:** Copy the JWT token from response and save as `DOCTOR_TOKEN`

### 2.5 Login Patient & Get Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "jane.doe@email.com",
    "password": "password123"
  }'
```
**Action Required:** Copy the JWT token from response and save as `PATIENT_TOKEN`

---

## Phase 3: Doctor Schedule & Slot Setup

### 3.1 Access H2 Database Console (For Verification Only)
1. Open browser: http://localhost:8080/h2-console
2. JDBC URL: `jdbc:h2:file:./data/appointment_system`
3. Username: `sa`
4. Password: (leave empty)
5. Click "Connect"

### 3.2 Verify Automatic Schedule Creation
```sql
-- Check that doctor schedules were automatically created during registration
-- This should show Monday-Friday schedules with proper break times
SELECT 
    id,
    doctor_id,
    day_of_week,
    start_time,
    end_time,
    break_start_time,
    break_end_time,
    slot_duration_minutes,
    is_active
FROM doctor_schedules 
ORDER BY doctor_id, day_of_week;
```
**Expected:** You should see 5 records per doctor (Monday-Friday) with:
- Start time: 09:00:00
- End time: 17:00:00  
- Break start: 12:00:00
- Break end: 13:00:00
- Slot duration: 30 minutes

### 3.3 Verify Automatic Slot Generation
```sql
-- Check that appointment slots were automatically generated (30 days ahead)
SELECT 
    COUNT(*) as total_slots,
    doctor_id,
    MIN(slot_date_time) as first_slot,
    MAX(slot_date_time) as last_slot
FROM appointment_slots 
GROUP BY doctor_id
ORDER BY doctor_id;
```
**Expected:** Each doctor should have multiple slots spanning ~30 days from tomorrow

### 3.3.1 Get All Slots for Specific Doctor
```sql
-- Get all appointment slots for a specific doctor (replace 1 with desired doctor_id)
SELECT 
    id,
    doctor_id,
    slot_date_time,
    is_available,
    is_booked,
    created_at,
    CASE 
        WHEN is_booked = true THEN 'BOOKED'
        WHEN is_available = true THEN 'AVAILABLE'
        ELSE 'UNAVAILABLE'
    END as status
FROM appointment_slots 
WHERE doctor_id = 1
ORDER BY slot_date_time;
```
**Usage:** Replace `doctor_id = 1` with the desired doctor ID (1, 2, 3, etc.)

### 3.3.2 Get Available Slots Only for Specific Doctor
```sql
-- Get only available (bookable) slots for a specific doctor
SELECT 
    id,
    slot_date_time,
    DATE(slot_date_time) as appointment_date,
    TIME(slot_date_time) as appointment_time
FROM appointment_slots 
WHERE doctor_id = 1 
    AND is_available = true 
    AND is_booked = false
ORDER BY slot_date_time
LIMIT 20;
```
**Usage:** Shows first 20 available slots for doctor ID 1

**✅ Note:** Doctor registration now automatically creates:
- **Monday-Friday schedules** (9 AM - 5 PM with lunch break 12-1 PM)
- **30 days of appointment slots** (excluding break times)
- **Ready-to-book availability** immediately after registration

---

## Phase 4: Patient Appointment Booking Flow

### 4.1 Check Available Slots (Public - No Token Required)
```bash
curl -X GET "http://localhost:8080/api/appointments/slots/available?doctorId=1&startDate=2025-09-05&endDate=2025-09-12" \
  -H "Content-Type: application/json"
```
**Note:** This should return the slots created in Phase 3. Pick a slot ID for booking.

### 4.2 Book First Appointment (Patient Token Required)
```bash
curl -X POST http://localhost:8080/api/appointments/book \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_PATIENT_TOKEN" \
  -d '{
    "slotId": 1,
    "patientNotes": "Regular cardiology checkup - first visit"
  }'
```
**Replace:** `YOUR_PATIENT_TOKEN` with actual token from step 2.5

### 4.3 Book Second Appointment (Patient Token Required)
```bash
curl -X POST http://localhost:8080/api/appointments/book \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_PATIENT_TOKEN" \
  -d '{
    "slotId": 3,
    "patientNotes": "Follow-up appointment for test results"
  }'
```

### 4.4 View My Appointments (Patient Token Required)
```bash
curl -X GET http://localhost:8080/api/appointments/patient/my-appointments \
  -H "Authorization: Bearer YOUR_PATIENT_TOKEN"
```
**Expected:** List of patient's booked appointments

### 4.5 Get Specific Appointment Details (Patient Token Required)
```bash
curl -X GET http://localhost:8080/api/appointments/1 \
  -H "Authorization: Bearer YOUR_PATIENT_TOKEN"
```

### 4.6 Cancel an Appointment (Patient Token Required)
```bash
curl -X PUT http://localhost:8080/api/appointments/2/cancel \
  -H "Authorization: Bearer YOUR_PATIENT_TOKEN"
```
**Note:** This cancels appointment ID 2 (the second appointment)

---

## Phase 5: Doctor Appointment Management

### 5.1 View All My Appointments (Doctor Token Required)
```bash
curl -X GET http://localhost:8080/api/appointments/doctor/my-appointments \
  -H "Authorization: Bearer YOUR_DOCTOR_TOKEN"
```
**Replace:** `YOUR_DOCTOR_TOKEN` with actual token from step 2.4

### 5.2 Get Specific Patient Appointment (Doctor Token Required)
```bash
curl -X GET http://localhost:8080/api/appointments/1 \
  -H "Authorization: Bearer YOUR_DOCTOR_TOKEN"
```

### 5.3 Cancel Patient Appointment (Doctor Token Required)
```bash
curl -X PUT http://localhost:8080/api/appointments/1/cancel \
  -H "Authorization: Bearer YOUR_DOCTOR_TOKEN"
```
**Note:** Doctors can cancel any appointment

---

## Phase 6: Administrative Operations

### 6.1 Database Verification Queries
```sql
-- Execute in H2 Console to verify all operations

-- 1. Check all users
SELECT id, first_name, last_name, email, role, is_active FROM users;

-- 2. Check all appointments with details
SELECT 
    a.id as appointment_id,
    a.appointment_date_time,
    a.status,
    a.patient_notes,
    p.first_name as patient_name,
    p.email as patient_email,
    d.first_name as doctor_name,
    d.specialization
FROM appointments a
JOIN users p ON a.patient_id = p.id
JOIN users d ON a.doctor_id = d.id
ORDER BY a.appointment_date_time;

-- 3. Check slot utilization
SELECT 
    slot_date_time,
    is_available,
    is_booked,
    CASE 
        WHEN is_booked = true THEN 'BOOKED'
        WHEN is_available = true THEN 'AVAILABLE'
        ELSE 'UNAVAILABLE'
    END as status
FROM appointment_slots 
ORDER BY slot_date_time;

-- 4. Check notification queue (if any)
SELECT * FROM notification_queue;
```

### 6.2 Clean Reset (if needed)
```sql
-- Execute in H2 Console to reset all data
DELETE FROM notification_queue;
DELETE FROM appointments;
DELETE FROM appointment_slots;
DELETE FROM doctor_schedules;
DELETE FROM users;
```

---

## 🔧 Token Management Helper

### Extract Token from Login Response
When you login, you'll get a response like:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "user": {...}
}
```
Copy the `token` value and use it in subsequent API calls.

### Token Usage Examples
```bash
# Wrong format
-H "Authorization: eyJhbGciOiJIUzI1NiIs..."

# Correct format
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiIs..."
```

---

## 🐛 Troubleshooting Guide

| Error Code | Issue | Solution |
|------------|-------|----------|
| **401 Unauthorized** | Missing/invalid token | Re-login and get fresh token |
| **403 Forbidden** | Wrong role for endpoint | Check if user role matches API requirement |
| **400 Bad Request** | Invalid request body | Verify JSON format and required fields |
| **404 Not Found** | Resource doesn't exist | Check if appointment/slot ID exists |
| **409 Conflict** | Slot already booked | Choose a different available slot |

---

## 📋 Testing Checklist

- [ ] **Phase 1:** Health check endpoints working
- [ ] **Phase 2:** Users registered and tokens obtained
- [ ] **Phase 3:** Doctor schedules and slots created in database
- [ ] **Phase 4:** Patient can view, book, and manage appointments
- [ ] **Phase 5:** Doctor can view and manage patient appointments
- [ ] **Phase 6:** Database verification shows correct data

**Testing Complete!** ✅ Your Patient Appointment System is fully functional.
