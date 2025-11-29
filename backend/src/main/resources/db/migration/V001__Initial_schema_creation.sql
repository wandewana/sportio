-- Initial schema creation for Sportio backend
-- Creates core user and session management tables

-- Enable PostGIS extension for geospatial queries
CREATE EXTENSION IF NOT EXISTS postgis;

-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(500),
    avatar_initials VARCHAR(10),
    skill_level VARCHAR(50) CHECK (skill_level IN ('Beginner', 'Intermediate', 'Advanced', 'All levels')),
    games_played INTEGER DEFAULT 0,
    bio TEXT,
    member_since TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sessions table
CREATE TABLE sessions (
    id BIGSERIAL PRIMARY KEY,
    host_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    sport_type VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    date DATE NOT NULL,
    time_start TIME NOT NULL,
    time_end TIME NOT NULL,
    players_needed INTEGER NOT NULL CHECK (players_needed > 0),
    visibility VARCHAR(20) CHECK (visibility IN ('public', 'private')) DEFAULT 'public',
    status VARCHAR(20) CHECK (status IN ('pending', 'open', 'booked', 'completed', 'cancelled')) DEFAULT 'open',
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Session players junction table
CREATE TABLE session_players (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL REFERENCES sessions(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    is_host BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) CHECK (status IN ('joined', 'maybe', 'left')) DEFAULT 'joined',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_status VARCHAR(20) CHECK (payment_status IN ('pending', 'processing', 'success', 'failed', 'refunded')),
    payment_amount DECIMAL(10, 2),
    paid_at TIMESTAMP,
    UNIQUE(session_id, user_id)
);

-- Create indexes for performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_sessions_host ON sessions(host_id);
CREATE INDEX idx_sessions_status_date ON sessions(status, date);
CREATE INDEX idx_sessions_sport_status ON sessions(sport_type, status);
CREATE INDEX idx_session_players_session ON session_players(session_id);
CREATE INDEX idx_session_players_user ON session_players(user_id);

-- Create geospatial indexes for location-based queries
CREATE INDEX idx_sessions_location ON sessions USING GIST (ST_MakePoint(longitude, latitude)::geography);