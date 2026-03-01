-- ============================================================
-- bookings — a confirmed or pending session between instructor + learner
-- ============================================================
CREATE TABLE bookings (
    id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    instructor_id UUID        NOT NULL REFERENCES instructor_profiles(id),
    learner_id    UUID        NOT NULL REFERENCES learner_profiles(id),
    session_date  DATE        NOT NULL,
    start_time    TIME        NOT NULL,
    end_time      TIME        NOT NULL,
    status        VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (
                      status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED', 'NO_SHOW')
                  ),
    notes         TEXT,
    -- Snapshot of the price at booking time (rate may change later)
    total_amount  NUMERIC(8,2),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_booking_times CHECK (end_time > start_time)
);

CREATE INDEX idx_bookings_instructor ON bookings(instructor_id);
CREATE INDEX idx_bookings_learner    ON bookings(learner_id);
CREATE INDEX idx_bookings_date       ON bookings(session_date);
CREATE INDEX idx_bookings_status     ON bookings(status);

-- Partial unique index: same instructor cannot have two active bookings
-- at the same date + start_time (CANCELLED bookings are excluded)
CREATE UNIQUE INDEX idx_bookings_no_overlap
    ON bookings(instructor_id, session_date, start_time)
    WHERE status NOT IN ('CANCELLED');

CREATE TRIGGER set_bookings_updated_at
    BEFORE UPDATE ON bookings
    FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();
