-- ============================================================
-- availability_slots — recurring weekly schedule
-- e.g. "Every Monday 09:00–17:00"
-- ============================================================
CREATE TABLE availability_slots (
    id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    instructor_id UUID        NOT NULL REFERENCES instructor_profiles(id) ON DELETE CASCADE,
    day_of_week   VARCHAR(10) NOT NULL CHECK (day_of_week IN (
                      'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY',
                      'FRIDAY', 'SATURDAY', 'SUNDAY'
                  )),
    start_time    TIME        NOT NULL,
    end_time      TIME        NOT NULL,
    is_active     BOOLEAN     NOT NULL DEFAULT true,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_slot_times CHECK (end_time > start_time)
);

CREATE INDEX idx_slots_instructor ON availability_slots(instructor_id);
-- Prevent duplicate slot for the same instructor + day + start_time
CREATE UNIQUE INDEX idx_slots_unique
    ON availability_slots(instructor_id, day_of_week, start_time);

-- ============================================================
-- availability_exceptions — specific blocked dates (holidays etc.)
-- Overrides the recurring weekly slots for that day entirely
-- ============================================================
CREATE TABLE availability_exceptions (
    id             UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    instructor_id  UUID        NOT NULL REFERENCES instructor_profiles(id) ON DELETE CASCADE,
    exception_date DATE        NOT NULL,
    reason         VARCHAR(255),
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_exception_per_day UNIQUE (instructor_id, exception_date)
);

CREATE INDEX idx_exceptions_instructor ON availability_exceptions(instructor_id);
CREATE INDEX idx_exceptions_date       ON availability_exceptions(exception_date);
