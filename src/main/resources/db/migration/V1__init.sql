-- Inicijalna šema baze za HabitFlow

CREATE TABLE users (
    id                 VARCHAR(36) PRIMARY KEY,
    email              VARCHAR(255) UNIQUE,
    password_hash      VARCHAR(255),
    display_name       VARCHAR(255),
    identity_statement VARCHAR(500),
    created_at         BIGINT
);

CREATE TABLE habits (
    id                     VARCHAR(36) PRIMARY KEY,
    user_id                VARCHAR(36) NOT NULL REFERENCES users(id),
    name                   VARCHAR(255),
    category               VARCHAR(255),
    type                   VARCHAR(20),
    frequency_type         VARCHAR(30),
    days_of_week           VARCHAR(50),
    target_count           INTEGER,
    reminder_time          VARCHAR(10),
    cue_text               VARCHAR(500),
    stacked_after_habit_id VARCHAR(36),
    is_archived            BOOLEAN DEFAULT FALSE,
    created_at             BIGINT,
    updated_at             BIGINT
);

CREATE TABLE habit_entries (
    id         VARCHAR(36) PRIMARY KEY,
    habit_id   VARCHAR(36) NOT NULL REFERENCES habits(id),
    date       VARCHAR(10) NOT NULL,
    status     VARCHAR(20),
    value      INTEGER,
    updated_at BIGINT,
    CONSTRAINT uq_habit_date UNIQUE (habit_id, date)
);

CREATE TABLE recommendations (
    id           VARCHAR(36) PRIMARY KEY,
    user_id      VARCHAR(36) NOT NULL REFERENCES users(id),
    habit_id     VARCHAR(36),
    type         VARCHAR(30),
    message      VARCHAR(500),
    created_at   BIGINT,
    is_dismissed BOOLEAN DEFAULT FALSE
);

CREATE TABLE achievements (
    id          VARCHAR(36) PRIMARY KEY,
    user_id     VARCHAR(36) NOT NULL REFERENCES users(id),
    type        VARCHAR(30),
    unlocked_at BIGINT
);

CREATE INDEX idx_habits_user ON habits(user_id);
CREATE INDEX idx_entries_habit ON habit_entries(habit_id);
CREATE INDEX idx_habits_updated ON habits(updated_at);
CREATE INDEX idx_entries_updated ON habit_entries(updated_at);
