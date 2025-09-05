-- SQL Query to Add Break Times to Existing Doctor Schedules

-- Update all existing doctor schedules that have NULL break times
-- This will set lunch break from 12:00 PM to 1:00 PM

UPDATE doctor_schedules
SET
    break_start_time = '12:00:00',
    break_end_time = '13:00:00',
    updated_at = CURRENT_TIMESTAMP
WHERE
    break_start_time IS NULL
    AND break_end_time IS NULL;

-- Verify the update
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

-- Optional: Check how many records were updated
-- SELECT COUNT(*) as updated_schedules
-- FROM doctor_schedules
-- WHERE break_start_time = '12:00:00' AND break_end_time = '13:00:00';
