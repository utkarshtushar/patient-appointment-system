-- =====================================================
-- DATABASE CLEANING SCRIPTS FOR H2 DATABASE
-- =====================================================

-- =====================================================
-- OPTION 1: CLEAN ALL DATA (KEEP TABLES)
-- =====================================================

-- Clean all appointment data
DELETE FROM appointments;

-- Clean all appointment slots
DELETE FROM appointment_slots;

-- Clean all notification queue data
DELETE FROM notification_queue;

-- Clean all doctor schedules
DELETE FROM doctor_schedules;

-- Clean all users (THIS WILL REMOVE ALL USERS!)
DELETE FROM users;

-- Reset auto-increment sequences
ALTER TABLE users ALTER COLUMN id RESTART WITH 1;
ALTER TABLE appointments ALTER COLUMN id RESTART WITH 1;
ALTER TABLE appointment_slots ALTER COLUMN id RESTART WITH 1;
ALTER TABLE notification_queue ALTER COLUMN id RESTART WITH 1;
ALTER TABLE doctor_schedules ALTER COLUMN id RESTART WITH 1;

-- =====================================================
-- OPTION 2: CLEAN ONLY APPOINTMENTS & SLOTS (KEEP USERS)
-- =====================================================

-- Clean appointments and slots but keep users
-- DELETE FROM appointments;
-- DELETE FROM appointment_slots;
-- DELETE FROM notification_queue;
-- DELETE FROM doctor_schedules;

-- Reset sequences for appointments only
-- ALTER TABLE appointments ALTER COLUMN id RESTART WITH 1;
-- ALTER TABLE appointment_slots ALTER COLUMN id RESTART WITH 1;
-- ALTER TABLE notification_queue ALTER COLUMN id RESTART WITH 1;
-- ALTER TABLE doctor_schedules ALTER COLUMN id RESTART WITH 1;

-- =====================================================
-- OPTION 3: CLEAN OLD DATA (KEEP RECENT)
-- =====================================================

-- Delete old appointments (older than 30 days)
-- DELETE FROM appointments WHERE appointment_date < DATEADD('DAY', -30, CURRENT_DATE);

-- Delete old notification queue entries
-- DELETE FROM notification_queue WHERE created_at < DATEADD('DAY', -7, CURRENT_DATE);

-- Delete old unavailable slots
-- DELETE FROM appointment_slots WHERE is_available = false AND appointment_date < CURRENT_DATE;

-- =====================================================
-- OPTION 4: CLEAN SPECIFIC DATA
-- =====================================================

-- Clean only cancelled appointments
-- DELETE FROM appointments WHERE status = 'CANCELLED';

-- Clean only old completed appointments
-- DELETE FROM appointments WHERE status = 'COMPLETED' AND appointment_date < DATEADD('DAY', -30, CURRENT_DATE);

-- Clean unavailable slots for future dates
-- DELETE FROM appointment_slots WHERE is_available = false AND appointment_date >= CURRENT_DATE;

-- =====================================================
-- OPTION 5: NUCLEAR OPTION - DROP AND RECREATE TABLES
-- =====================================================

-- WARNING: THIS WILL DESTROY ALL DATA AND TABLE STRUCTURE!
-- USE ONLY IF YOU WANT TO START COMPLETELY FRESH

-- DROP TABLE IF EXISTS appointments;
-- DROP TABLE IF EXISTS appointment_slots;
-- DROP TABLE IF EXISTS notification_queue;
-- DROP TABLE IF EXISTS doctor_schedules;
-- DROP TABLE IF EXISTS users;

-- Note: After dropping tables, restart the application
-- to let Hibernate recreate the schema

-- =====================================================
-- VERIFICATION QUERIES (RUN AFTER CLEANING)
-- =====================================================

-- Check remaining data counts
SELECT 'users' as table_name, COUNT(*) as count FROM users
UNION ALL
SELECT 'appointments' as table_name, COUNT(*) as count FROM appointments
UNION ALL
SELECT 'appointment_slots' as table_name, COUNT(*) as count FROM appointment_slots
UNION ALL
SELECT 'notification_queue' as table_name, COUNT(*) as count FROM notification_queue
UNION ALL
SELECT 'doctor_schedules' as table_name, COUNT(*) as count FROM doctor_schedules;

-- =====================================================
-- QUICK ACCESS COMMANDS FOR H2 CONSOLE
-- =====================================================

-- To use these commands:
-- 1. Access H2 Console: https://appointment-system-utkarsh.onrender.com/h2-console
-- 2. Login with: jdbc:h2:file:./data/appointment_system (user: sa, password: empty)
-- 3. Copy and paste the desired SQL commands above
-- 4. Execute them one by one or all at once

-- =====================================================
-- SAMPLE DATA CREATION (OPTIONAL)
-- =====================================================

-- Create sample admin user (uncomment if needed)
-- INSERT INTO users (first_name, last_name, email, password, role, created_at)
-- VALUES ('Admin', 'User', 'admin@system.com', '$2a$10$rI3Gw8zF9EoPLj5YnPnHBuZnxWUn0F1QCk8hD2c3L4m5N6o7P8q9R0s', 'ADMIN', CURRENT_TIMESTAMP);

-- Create sample doctor (uncomment if needed)
-- INSERT INTO users (first_name, last_name, email, password, role, specialization, license_number, phone_number, created_at)
-- VALUES ('Dr. John', 'Doe', 'doctor@system.com', '$2a$10$rI3Gw8zF9EoPLj5YnPnHBuZnxWUn0F1QCk8hD2c3L4m5N6o7P8q9R0s', 'DOCTOR', 'General Medicine', 'DOC123456', '+1234567890', CURRENT_TIMESTAMP);

-- Create sample patient (uncomment if needed)
-- INSERT INTO users (first_name, last_name, email, password, role, phone_number, created_at)
-- VALUES ('John', 'Patient', 'patient@system.com', '$2a$10$rI3Gw8zF9EoPLj5YnPnHBuZnxWUn0F1QCk8hD2c3L4m5N6o7P8q9R0s', 'PATIENT', '+1234567891', CURRENT_TIMESTAMP);
