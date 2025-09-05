-- Clean Database - Delete All Data
-- Execute these commands in H2 Console: http://localhost:8080/h2-console
-- JDBC URL: jdbc:h2:file:./data/appointment_system

-- Step 1: Check current data before deletion
SELECT 'Users' as table_name, COUNT(*) as count FROM users
UNION ALL
SELECT 'Doctor Schedules' as table_name, COUNT(*) as count FROM doctor_schedules
UNION ALL
SELECT 'Appointment Slots' as table_name, COUNT(*) as count FROM appointment_slots
UNION ALL
SELECT 'Appointments' as table_name, COUNT(*) as count FROM appointments
UNION ALL
SELECT 'Notification Queue' as table_name, COUNT(*) as count FROM notification_queue;

-- Step 2: Delete all data (in correct order due to foreign key constraints)

-- Delete notification queue entries first
DELETE FROM notification_queue;

-- Delete appointments
DELETE FROM appointments;

-- Delete appointment slots
DELETE FROM appointment_slots;

-- Delete doctor schedules
DELETE FROM doctor_schedules;

-- Delete all users (doctors, patients, admins)
DELETE FROM users;

-- Step 3: Verify database is clean
SELECT 'Users' as table_name, COUNT(*) as remaining_count FROM users
UNION ALL
SELECT 'Doctor Schedules' as table_name, COUNT(*) as remaining_count FROM doctor_schedules
UNION ALL
SELECT 'Appointment Slots' as table_name, COUNT(*) as remaining_count FROM appointment_slots
UNION ALL
SELECT 'Appointments' as table_name, COUNT(*) as remaining_count FROM appointments
UNION ALL
SELECT 'Notification Queue' as table_name, COUNT(*) as remaining_count FROM notification_queue;

-- Expected result: All counts should be 0

-- Step 4: Reset auto-increment sequences (optional)
ALTER TABLE users ALTER COLUMN id RESTART WITH 1;
ALTER TABLE doctor_schedules ALTER COLUMN id RESTART WITH 1;
ALTER TABLE appointment_slots ALTER COLUMN id RESTART WITH 1;
ALTER TABLE appointments ALTER COLUMN id RESTART WITH 1;
ALTER TABLE notification_queue ALTER COLUMN id RESTART WITH 1;

-- Database is now completely clean and ready for fresh data!
