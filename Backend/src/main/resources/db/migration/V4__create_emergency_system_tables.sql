-- Sprint 4: Emergency Vehicle Priority System
-- V5__create_emergency_system_tables.sql

-- Emergency Vehicles Table
CREATE TABLE IF NOT EXISTS emergency_vehicles (
    id BIGSERIAL PRIMARY KEY,
    vehicle_id VARCHAR(50) NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    current_intersection_id BIGINT NOT NULL,
    direction VARCHAR(20) NOT NULL,
    detected_at TIMESTAMP NOT NULL,
    cleared_at TIMESTAMP,
    priority_level INTEGER NOT NULL DEFAULT 1,
    notes VARCHAR(500),
    CONSTRAINT fk_intersection FOREIGN KEY (current_intersection_id) 
        REFERENCES intersections(id) ON DELETE CASCADE,
    CONSTRAINT chk_type CHECK (type IN ('AMBULANCE', 'FIRE_TRUCK', 'POLICE', 'MILITARY')),
    CONSTRAINT chk_status CHECK (status IN ('DETECTED', 'IN_PROGRESS', 'CLEARED', 'CANCELLED')),
    CONSTRAINT chk_direction CHECK (direction IN ('NORTH', 'SOUTH', 'EAST', 'WEST')),
    CONSTRAINT chk_priority CHECK (priority_level BETWEEN 1 AND 5)
);

-- Emergency Events Table (Logging)
CREATE TABLE IF NOT EXISTS emergency_events (
    id BIGSERIAL PRIMARY KEY,
    emergency_vehicle_id BIGINT NOT NULL,
    intersection_id BIGINT NOT NULL,
    intersection_name VARCHAR(200),
    event_type VARCHAR(50) NOT NULL,
    description VARCHAR(1000),
    previous_phase VARCHAR(20),
    new_phase VARCHAR(20),
    duration_seconds INTEGER,
    affected_intersections INTEGER,
    total_wait_time INTEGER,
    created_at TIMESTAMP NOT NULL,
    success BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_emergency_vehicle FOREIGN KEY (emergency_vehicle_id) 
        REFERENCES emergency_vehicles(id) ON DELETE CASCADE,
    CONSTRAINT fk_event_intersection FOREIGN KEY (intersection_id) 
        REFERENCES intersections(id) ON DELETE CASCADE,
    CONSTRAINT chk_event_type CHECK (event_type IN (
        'EMERGENCY_DETECTED', 
        'PHASE_CHANGED', 
        'GREEN_LIGHT_ACTIVATED',
        'RED_LIGHT_ACTIVATED',
        'EMERGENCY_CLEARED',
        'NORMAL_OPERATION_RESUMED'
    ))
);

-- Indexes for better performance
CREATE INDEX IF NOT EXISTS idx_emergency_vehicles_status ON emergency_vehicles(status);
CREATE INDEX IF NOT EXISTS idx_emergency_vehicles_intersection ON emergency_vehicles(current_intersection_id);
CREATE INDEX IF NOT EXISTS idx_emergency_vehicles_detected_at ON emergency_vehicles(detected_at);
CREATE INDEX IF NOT EXISTS idx_emergency_events_vehicle ON emergency_events(emergency_vehicle_id);
CREATE INDEX IF NOT EXISTS idx_emergency_events_intersection ON emergency_events(intersection_id);
CREATE INDEX IF NOT EXISTS idx_emergency_events_type ON emergency_events(event_type);
CREATE INDEX IF NOT EXISTS idx_emergency_events_created_at ON emergency_events(created_at);

-- Comments
COMMENT ON TABLE emergency_vehicles IS 'Acil araç takip sistemi - Ambulans, İtfaiye vb.';
COMMENT ON TABLE emergency_events IS 'Acil durum olay kayıtları ve log';

COMMENT ON COLUMN emergency_vehicles.vehicle_id IS 'Aracın benzersiz kimliği (örn: AMB-001)';
COMMENT ON COLUMN emergency_vehicles.type IS 'Araç tipi: AMBULANCE, FIRE_TRUCK, POLICE, MILITARY';
COMMENT ON COLUMN emergency_vehicles.status IS 'Durum: DETECTED, IN_PROGRESS, CLEARED, CANCELLED';
COMMENT ON COLUMN emergency_vehicles.priority_level IS '1=En yüksek öncelik, 5=En düşük öncelik';

COMMENT ON COLUMN emergency_events.event_type IS 'Olay tipi: EMERGENCY_DETECTED, PHASE_CHANGED vb.';
COMMENT ON COLUMN emergency_events.affected_intersections IS 'Etkilenen toplam kavşak sayısı';
COMMENT ON COLUMN emergency_events.total_wait_time IS 'Toplam bekleme süresi (saniye)';