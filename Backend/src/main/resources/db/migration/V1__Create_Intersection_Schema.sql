-- ========================================
-- HAFTA 1: Database Schema Design
-- Traffic Light Management System
-- PostgreSQL Database Schema
-- ========================================

-- ========================================
-- Enable PostGIS Extensions for Location Features
-- ========================================
CREATE EXTENSION IF NOT EXISTS cube;
CREATE EXTENSION IF NOT EXISTS earthdistance;

-- ========================================
-- Table 1: intersections (Ana Tablo)
-- ========================================
CREATE TABLE intersections (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(100) UNIQUE NOT NULL,
    
    -- Konum Bilgileri
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    address TEXT,
    city VARCHAR(100) NOT NULL,
    district VARCHAR(100),
    postal_code VARCHAR(20),
    
    -- Kesişim Türü
    intersection_type VARCHAR(50) NOT NULL CHECK (intersection_type IN ('traffic_light', 'roundabout', 'crossroad', 'pedestrian_crossing')),
    
    -- Status Bilgisi
    status VARCHAR(50) NOT NULL DEFAULT 'active' CHECK (status IN ('active', 'inactive', 'maintenance', 'under_construction')),
    
    -- Konfigürasyon Referansları
    lanes_count INTEGER DEFAULT 4,
    has_pedestrian_crossing BOOLEAN DEFAULT TRUE,
    has_vehicle_detection BOOLEAN DEFAULT FALSE,
    has_emergency_override BOOLEAN DEFAULT FALSE,
    
    -- Metadata
    description TEXT,
    installation_date DATE,
    last_maintenance_date DATE,
    next_maintenance_date DATE,
    
    -- Audit Fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 0
);

-- ========================================
-- Table 2: intersection_configs (Konfigürasyon)
-- ========================================
CREATE TABLE intersection_configs (
    id BIGSERIAL PRIMARY KEY,
    intersection_id BIGINT NOT NULL,
    
    -- Timing Configuration
    green_light_duration INTEGER NOT NULL DEFAULT 30, -- seconds
    yellow_light_duration INTEGER NOT NULL DEFAULT 3, -- seconds
    red_light_duration INTEGER NOT NULL DEFAULT 30, -- seconds
    all_red_duration INTEGER NOT NULL DEFAULT 2, -- seconds (safety clearance)
    
    -- Advanced Configuration
    pedestrian_crossing_duration INTEGER DEFAULT 15,
    minimum_green_time INTEGER DEFAULT 5,
    maximum_green_time INTEGER DEFAULT 120,
    
    -- Detection System Config
    vehicle_detection_enabled BOOLEAN DEFAULT FALSE,
    pedestrian_button_enabled BOOLEAN DEFAULT TRUE,
    emergency_vehicle_priority BOOLEAN DEFAULT FALSE,
    
    -- Adaptive Traffic Control
    adaptive_timing_enabled BOOLEAN DEFAULT FALSE,
    peak_hour_mode_enabled BOOLEAN DEFAULT FALSE,
    night_mode_enabled BOOLEAN DEFAULT FALSE,
    
    -- Time-based Configuration
    peak_morning_start TIME DEFAULT '07:00:00',
    peak_morning_end TIME DEFAULT '10:00:00',
    peak_evening_start TIME DEFAULT '17:00:00',
    peak_evening_end TIME DEFAULT '20:00:00',
    night_mode_start TIME DEFAULT '23:00:00',
    night_mode_end TIME DEFAULT '06:00:00',
    
    -- System Configuration
    cycle_length INTEGER DEFAULT 90, -- total cycle time in seconds
    coordination_enabled BOOLEAN DEFAULT FALSE,
    coordination_offset INTEGER DEFAULT 0,
    
    -- Metadata
    config_version VARCHAR(20) DEFAULT '1.0',
    is_active BOOLEAN DEFAULT TRUE,
    effective_from DATE,
    effective_until DATE,
    
    -- Audit Fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    
    -- Foreign Key
    CONSTRAINT fk_intersection_config 
        FOREIGN KEY (intersection_id) 
        REFERENCES intersections(id) 
        ON DELETE CASCADE
);

-- ========================================
-- Table 3: intersection_metrics (KPI Metrikleri)
-- ========================================
CREATE TABLE intersection_metrics (
    id BIGSERIAL PRIMARY KEY,
    intersection_id BIGINT NOT NULL,
    
    -- Time Period
    measurement_date DATE NOT NULL,
    measurement_hour INTEGER CHECK (measurement_hour BETWEEN 0 AND 23),
    
    -- Traffic Volume Metrics
    total_vehicle_count INTEGER DEFAULT 0,
    car_count INTEGER DEFAULT 0,
    truck_count INTEGER DEFAULT 0,
    bus_count INTEGER DEFAULT 0,
    motorcycle_count INTEGER DEFAULT 0,
    bicycle_count INTEGER DEFAULT 0,
    pedestrian_count INTEGER DEFAULT 0,
    
    -- Performance Metrics
    average_wait_time DECIMAL(10, 2), -- seconds
    maximum_wait_time DECIMAL(10, 2), -- seconds
    average_queue_length DECIMAL(10, 2),
    maximum_queue_length INTEGER,
    throughput INTEGER, -- vehicles per hour
    
    -- Efficiency Metrics
    green_time_utilization DECIMAL(5, 2), -- percentage
    red_light_violations INTEGER DEFAULT 0,
    yellow_light_violations INTEGER DEFAULT 0,
    pedestrian_violations INTEGER DEFAULT 0,
    
    -- Incident Metrics
    accidents_count INTEGER DEFAULT 0,
    near_miss_count INTEGER DEFAULT 0,
    emergency_vehicle_passages INTEGER DEFAULT 0,
    
    -- System Performance
    system_uptime_percentage DECIMAL(5, 2),
    malfunction_count INTEGER DEFAULT 0,
    manual_override_count INTEGER DEFAULT 0,
    
    -- Environmental Metrics
    estimated_co2_emission DECIMAL(10, 2), -- kg
    estimated_fuel_consumption DECIMAL(10, 2), -- liters
    
    -- Metadata
    data_quality_score DECIMAL(3, 2) CHECK (data_quality_score BETWEEN 0 AND 1),
    notes TEXT,
    
    -- Audit Fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign Key
    CONSTRAINT fk_intersection_metric 
        FOREIGN KEY (intersection_id) 
        REFERENCES intersections(id) 
        ON DELETE CASCADE,
    
    -- Unique constraint to prevent duplicate metrics
    CONSTRAINT unique_metric_period 
        UNIQUE (intersection_id, measurement_date, measurement_hour)
);

-- ========================================
-- Table 4: intersection_phases (Faz Bilgileri)
-- ========================================
CREATE TABLE intersection_phases (
    id BIGSERIAL PRIMARY KEY,
    intersection_id BIGINT NOT NULL,
    
    -- Phase Information
    phase_number INTEGER NOT NULL CHECK (phase_number > 0),
    phase_name VARCHAR(100) NOT NULL,
    phase_type VARCHAR(50) NOT NULL CHECK (phase_type IN ('vehicle', 'pedestrian', 'mixed', 'turning', 'protected')),
    
    -- Direction Configuration
    allowed_directions TEXT[], -- Array: ['north', 'south', 'east', 'west']
    movement_type VARCHAR(50), -- 'through', 'left_turn', 'right_turn', 'u_turn'
    
    -- Timing
    min_duration INTEGER NOT NULL DEFAULT 5, -- seconds
    max_duration INTEGER NOT NULL DEFAULT 120, -- seconds
    default_duration INTEGER NOT NULL DEFAULT 30, -- seconds
    extension_time INTEGER DEFAULT 3, -- seconds for vehicle detection
    
    -- Phase Priority
    priority_level INTEGER DEFAULT 1 CHECK (priority_level BETWEEN 1 AND 10),
    is_protected BOOLEAN DEFAULT FALSE,
    is_permissive BOOLEAN DEFAULT FALSE,
    
    -- Pedestrian Phase Specific
    has_pedestrian_signal BOOLEAN DEFAULT FALSE,
    pedestrian_clearance_time INTEGER DEFAULT 0,
    accessible_pedestrian_signal BOOLEAN DEFAULT FALSE,
    
    -- Sequence Configuration
    sequence_order INTEGER NOT NULL,
    next_phase_id BIGINT,
    can_skip BOOLEAN DEFAULT FALSE,
    
    -- Conflict Management
    conflicting_phases INTEGER[], -- Array of phase numbers that conflict
    compatible_phases INTEGER[], -- Array of phase numbers that can run simultaneously
    
    -- Metadata
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    
    -- Audit Fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign Keys
    CONSTRAINT fk_intersection_phase 
        FOREIGN KEY (intersection_id) 
        REFERENCES intersections(id) 
        ON DELETE CASCADE,
    
    CONSTRAINT fk_next_phase 
        FOREIGN KEY (next_phase_id) 
        REFERENCES intersection_phases(id) 
        ON DELETE SET NULL,
    
    -- Unique constraint for phase number per intersection
    CONSTRAINT unique_phase_number 
        UNIQUE (intersection_id, phase_number)
);

-- ========================================
-- INDEXES (Performance Optimization)
-- ========================================

-- Index 1: Location-based queries (HAFTA 4 - findNearby optimization)
CREATE INDEX idx_intersections_location 
    ON intersections USING GIST (
        ll_to_earth(latitude, longitude)
    );

-- Alternative spatial index if PostGIS is not available
CREATE INDEX idx_intersections_lat_lng 
    ON intersections (latitude, longitude);

-- Index 2: Status and City filters (HAFTA 4 - findByCity, findByStatus)
CREATE INDEX idx_intersections_city_status 
    ON intersections (city, status);

-- Index 3: Intersection Type filter
CREATE INDEX idx_intersections_type 
    ON intersections (intersection_type);

-- Index 4: Metrics date range queries (HAFTA 3 - time-range filtering)
CREATE INDEX idx_metrics_date_hour 
    ON intersection_metrics (intersection_id, measurement_date, measurement_hour);

-- Index 5: Config lookup optimization
CREATE INDEX idx_configs_intersection_active 
    ON intersection_configs (intersection_id, is_active);

-- Index 6: Phase sequence optimization
CREATE INDEX idx_phases_sequence 
    ON intersection_phases (intersection_id, sequence_order);

-- Index 7: Created/Updated timestamp indexes for audit queries
CREATE INDEX idx_intersections_created_at 
    ON intersections (created_at DESC);

CREATE INDEX idx_intersections_updated_at 
    ON intersections (updated_at DESC);

-- ========================================
-- FUNCTIONS & TRIGGERS
-- ========================================

-- Function to update the updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Triggers for automatic updated_at
CREATE TRIGGER update_intersections_updated_at
    BEFORE UPDATE ON intersections
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_intersection_configs_updated_at
    BEFORE UPDATE ON intersection_configs
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_intersection_metrics_updated_at
    BEFORE UPDATE ON intersection_metrics
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_intersection_phases_updated_at
    BEFORE UPDATE ON intersection_phases
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ========================================
-- COMMENTS (Documentation)
-- ========================================

COMMENT ON TABLE intersections IS 'Main table storing traffic light intersection information';
COMMENT ON TABLE intersection_configs IS 'Configuration parameters for each intersection';
COMMENT ON TABLE intersection_metrics IS 'Performance metrics and KPIs for intersections';
COMMENT ON TABLE intersection_phases IS 'Traffic light phase definitions and sequences';

COMMENT ON COLUMN intersections.intersection_type IS 'Type: traffic_light, roundabout, crossroad, pedestrian_crossing';
COMMENT ON COLUMN intersections.status IS 'Status: active, inactive, maintenance, under_construction';
COMMENT ON COLUMN intersection_metrics.data_quality_score IS 'Data quality score from 0.0 to 1.0';

