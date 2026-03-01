-- ============================================================
-- instructor_profiles — public-facing instructor info
-- ============================================================
CREATE TABLE instructor_profiles (
    id                  UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID          NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    first_name          VARCHAR(100)  NOT NULL,
    last_name           VARCHAR(100)  NOT NULL,
    bio                 TEXT,
    photo_url           VARCHAR(500),
    phone_number        VARCHAR(20),
    rate_per_hour       NUMERIC(8,2),
    years_experience    INT,
    license_number      VARCHAR(50),
    service_radius_km   INT           NOT NULL DEFAULT 10,
    -- Approximate center of where instructor operates
    latitude            NUMERIC(10,7),
    longitude           NUMERIC(10,7),
    city                VARCHAR(100),
    postcode            VARCHAR(20),
    is_profile_complete BOOLEAN       NOT NULL DEFAULT false,
    created_at          TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_instructor_user_id  ON instructor_profiles(user_id);
CREATE INDEX idx_instructor_location ON instructor_profiles(latitude, longitude);
CREATE INDEX idx_instructor_postcode ON instructor_profiles(postcode);

CREATE TRIGGER set_instructor_profiles_updated_at
    BEFORE UPDATE ON instructor_profiles
    FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();

-- ============================================================
-- learner_profiles — learner-specific info
-- ============================================================
CREATE TABLE learner_profiles (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id       UUID         NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100) NOT NULL,
    phone_number  VARCHAR(20),
    date_of_birth DATE,
    postcode      VARCHAR(20),
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_learner_user_id ON learner_profiles(user_id);

CREATE TRIGGER set_learner_profiles_updated_at
    BEFORE UPDATE ON learner_profiles
    FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();
