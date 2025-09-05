-- Fix Break Time Slots Issue
-- Delete appointment slots that fall during break time (12:00-13:00)

-- Step 1: Check how many break time slots exist
SELECT
    COUNT(*) as break_time_slots,
    doctor_id
FROM appointment_slots
WHERE FORMATDATETIME(slot_date_time, 'HH:mm:ss') >= '12:00:00'
    AND FORMATDATETIME(slot_date_time, 'HH:mm:ss') < '13:00:00'
GROUP BY doctor_id;

-- Step 2: See the actual break time slots before deletion
SELECT
    id,
    doctor_id,
    slot_date_time,
    FORMATDATETIME(slot_date_time, 'HH:mm:ss') as slot_time,
    is_available,
    is_booked
FROM appointment_slots
WHERE FORMATDATETIME(slot_date_time, 'HH:mm:ss') >= '12:00:00'
    AND FORMATDATETIME(slot_date_time, 'HH:mm:ss') < '13:00:00'
ORDER BY doctor_id, slot_date_time;

-- Step 3: Delete break time slots (ONLY if they are not booked)
DELETE FROM appointment_slots
WHERE FORMATDATETIME(slot_date_time, 'HH:mm:ss') >= '12:00:00'
    AND FORMATDATETIME(slot_date_time, 'HH:mm:ss') < '13:00:00'
    AND is_booked = false;

-- Alternative Step 3 (using HOUR function - simpler approach)
-- DELETE FROM appointment_slots
-- WHERE HOUR(slot_date_time) = 12
--     AND is_booked = false;

-- Step 4: Verify deletion worked
SELECT
    id,
    doctor_id,
    slot_date_time,
    FORMATDATETIME(slot_date_time, 'HH:mm:ss') as slot_time
FROM appointment_slots
WHERE FORMATDATETIME(slot_date_time, 'HH:mm:ss') >= '11:30:00'
    AND FORMATDATETIME(slot_date_time, 'HH:mm:ss') <= '13:30:00'
ORDER BY doctor_id, slot_date_time;

-- Expected result: You should see 11:30 AM, then jump to 1:00 PM (no 12:00 or 12:30)
