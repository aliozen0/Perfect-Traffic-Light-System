-- Sprint 4: Traffic Optimization System (Rule Engine + Sensors)
-- V5__create_optimization_system_tables.sql

-- Traffic Rules Table
CREATE TABLE IF NOT EXISTS traffic_rules (
    id BIGSERIAL PRIMARY KEY,
    rule_name VARCHAR(100) NOT NULL UNIQUE,
    rule_type VARCHAR(30) NOT NULL,
    description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    priority INTEGER NOT NULL,
    
    -- Conditions
    min_vehicle_count INTEGER,
    max_vehicle_count INTEGER,
    time_start TIME,
    time_end TIME,
    day_type VARCHAR(20),
    
    -- Actions
    green_duration_adjustment INTEGER,
    base_green_duration INTEGER NOT NULL DEFAULT 30,
    max_green_duration INTEGER NOT NULL DEFAULT 90,
    min_green_duration INTEGER NOT NULL DEFAULT 15,
    
    -- Metadata
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    times_applied BIGINT NOT NULL DEFAULT 0,
    
    CONSTRAINT chk_rule_type CHECK (rule_type IN (
        'PEAK_HOUR', 'HIGH_DENSITY', 'LOW_DENSITY', 
        'NIGHT_MODE', 'WEEKEND', 'WEATHER_BASED', 'CUSTOM'
    )),
    CONSTRAINT chk_day_type CHECK (day_type IN ('WEEKDAY', 'WEEKEND', 'ALL'))
);

-- Rule Applications Table (Logging)
CREATE TABLE IF NOT EXISTS rule_applications (
    id BIGSERIAL PRIMARY KEY,
    rule_id BIGINT NOT NULL,
    rule_name VARCHAR(100),
    intersection_id BIGINT NOT NULL,
    intersection_name VARCHAR(200),
    vehicle_count INTEGER,
    previous_green_duration INTEGER,
    new_green_duration INTEGER,
    adjustment INTEGER,
    reason VARCHAR(1000),
    applied_at TIMESTAMP NOT NULL,
    successful BOOLEAN NOT NULL DEFAULT TRUE,
    error_message VARCHAR(500),
    
    CONSTRAINT fk_rule FOREIGN KEY (rule_id) 
        REFERENCES traffic_rules(id) ON DELETE CASCADE,
    CONSTRAINT fk_rule_intersection FOREIGN KEY (intersection_id) 
        REFERENCES intersections(id) ON DELETE CASCADE
);

-- Traffic Sensors Table
CREATE TABLE IF NOT EXISTS traffic_sensors (
    id BIGSERIAL PRIMARY KEY,
    sensor_id VARCHAR(50) NOT NULL UNIQUE,
    intersection_id BIGINT NOT NULL,
    direction VARCHAR(20) NOT NULL,
    vehicle_count INTEGER NOT NULL,
    average_speed DOUBLE PRECISION,
    density_level VARCHAR(20) NOT NULL,
    recorded_at TIMESTAMP NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    
    CONSTRAINT fk_sensor_intersection FOREIGN KEY (intersection_id) 
        REFERENCES intersections(id) ON DELETE CASCADE,
    CONSTRAINT chk_direction CHECK (direction IN ('NORTH', 'SOUTH', 'EAST', 'WEST')),
    CONSTRAINT chk_density CHECK (density_level IN (
        'LOW', 'MEDIUM', 'HIGH', 'CRITICAL', 'UNKNOWN'
    ))
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_rules_active ON traffic_rules(active);
CREATE INDEX IF NOT EXISTS idx_rules_priority ON traffic_rules(priority);
CREATE INDEX IF NOT EXISTS idx_rules_type ON traffic_rules(rule_type);

CREATE INDEX IF NOT EXISTS idx_applications_rule ON rule_applications(rule_id);
CREATE INDEX IF NOT EXISTS idx_applications_intersection ON rule_applications(intersection_id);
CREATE INDEX IF NOT EXISTS idx_applications_applied_at ON rule_applications(applied_at);

CREATE INDEX IF NOT EXISTS idx_sensors_sensor_id ON traffic_sensors(sensor_id);
CREATE INDEX IF NOT EXISTS idx_sensors_intersection ON traffic_sensors(intersection_id);
CREATE INDEX IF NOT EXISTS idx_sensors_recorded_at ON traffic_sensors(recorded_at);
CREATE INDEX IF NOT EXISTS idx_sensors_density ON traffic_sensors(density_level);

-- Comments
COMMENT ON TABLE traffic_rules IS 'Trafik optimizasyon kuralları';
COMMENT ON TABLE rule_applications IS 'Kural uygulama logları';
COMMENT ON TABLE traffic_sensors IS 'Trafik sensörü verileri';

COMMENT ON COLUMN traffic_rules.rule_type IS 'Kural tipi: PEAK_HOUR, HIGH_DENSITY, NIGHT_MODE vb.';
COMMENT ON COLUMN traffic_rules.priority IS '1=En yüksek öncelik';
COMMENT ON COLUMN traffic_rules.green_duration_adjustment IS 'Yeşil süre ayarlaması (+/- saniye)';

COMMENT ON COLUMN traffic_sensors.density_level IS 'Yoğunluk: LOW, MEDIUM, HIGH, CRITICAL';
COMMENT ON COLUMN traffic_sensors.average_speed IS 'Ortalama araç hızı (km/h)';