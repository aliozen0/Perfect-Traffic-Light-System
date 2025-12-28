-- ========================================
-- HAFTA 1: Sample Data for Testing
-- Traffic Light Management System
-- PostgreSQL Sample Data
-- ========================================

-- Sample Intersections
INSERT INTO intersections (name, code, latitude, longitude, address, city, district, postal_code, intersection_type, status, lanes_count, has_pedestrian_crossing, has_vehicle_detection, has_emergency_override, description, installation_date, last_maintenance_date, next_maintenance_date, created_by) VALUES
('Taksim Square Intersection', 'IST-TAK-001', 41.0369, 28.9857, 'Taksim Square, Beyoğlu', 'Istanbul', 'Beyoğlu', '34435', 'traffic_light', 'active', 6, TRUE, TRUE, TRUE, 'Major intersection at Taksim Square with high pedestrian traffic', '2020-01-15', '2024-11-01', '2025-05-01', 'system'),
('Kadıköy Pier Intersection', 'IST-KAD-002', 40.9907, 29.0258, 'Kadıköy Pier, Kadıköy', 'Istanbul', 'Kadıköy', '34710', 'traffic_light', 'active', 4, TRUE, TRUE, FALSE, 'Busy intersection near ferry terminal', '2019-06-20', '2024-10-15', '2025-04-15', 'system'),
('Ankara Kızılay Square', 'ANK-KIZ-001', 39.9189, 32.8540, 'Kızılay Square, Çankaya', 'Ankara', 'Çankaya', '06420', 'traffic_light', 'active', 8, TRUE, TRUE, TRUE, 'Central intersection in Ankara', '2018-03-10', '2024-09-20', '2025-03-20', 'system'),
('İzmir Konak Roundabout', 'IZM-KON-001', 38.4189, 27.1287, 'Konak Square, Konak', 'Izmir', 'Konak', '35250', 'roundabout', 'active', 4, TRUE, FALSE, FALSE, 'Historic roundabout at Konak Square', '2017-05-05', '2024-08-10', '2025-02-10', 'system'),
('Beşiktaş Barbaros Square', 'IST-BES-001', 41.0428, 29.0089, 'Barbaros Boulevard, Beşiktaş', 'Istanbul', 'Beşiktaş', '34349', 'traffic_light', 'maintenance', 6, TRUE, TRUE, TRUE, 'Under maintenance for system upgrade', '2019-09-12', '2024-12-01', '2025-01-15', 'system'),
('Bursa Cumhuriyet Street', 'BUR-CUM-001', 40.1885, 29.0610, 'Cumhuriyet Street, Osmangazi', 'Bursa', 'Osmangazi', '16040', 'traffic_light', 'active', 4, TRUE, FALSE, FALSE, 'Main street intersection', '2021-02-20', '2024-10-05', '2025-04-05', 'system'),
('Antalya Konyaaltı Junction', 'ANT-KON-001', 36.8841, 30.6789, 'Konyaaltı Avenue, Konyaaltı', 'Antalya', 'Konyaaltı', '07050', 'traffic_light', 'active', 6, TRUE, TRUE, FALSE, 'Coastal road intersection', '2020-07-15', '2024-11-20', '2025-05-20', 'system'),
('Adana Ziyapaşa Boulevard', 'ADA-ZIY-001', 36.9913, 35.3304, 'Ziyapaşa Boulevard, Seyhan', 'Adana', 'Seyhan', '01120', 'traffic_light', 'inactive', 4, TRUE, FALSE, FALSE, 'Temporarily inactive due to road construction', '2018-11-08', '2024-07-12', '2024-12-30', 'system');

-- Sample Intersection Configs
INSERT INTO intersection_configs (intersection_id, green_light_duration, yellow_light_duration, red_light_duration, all_red_duration, pedestrian_crossing_duration, minimum_green_time, maximum_green_time, vehicle_detection_enabled, pedestrian_button_enabled, emergency_vehicle_priority, adaptive_timing_enabled, peak_hour_mode_enabled, night_mode_enabled, peak_morning_start, peak_morning_end, peak_evening_start, peak_evening_end, night_mode_start, night_mode_end, cycle_length, coordination_enabled, coordination_offset, config_version, is_active, effective_from, created_by) VALUES
(1, 45, 3, 45, 2, 20, 10, 120, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, '07:00:00', '10:00:00', '17:00:00', '20:00:00', '23:00:00', '06:00:00', 120, TRUE, 0, '2.0', TRUE, '2024-01-01', 'system'),
(2, 35, 3, 35, 2, 15, 8, 90, TRUE, TRUE, FALSE, FALSE, TRUE, TRUE, '07:30:00', '10:00:00', '17:00:00', '19:30:00', '00:00:00', '06:00:00', 90, FALSE, 0, '1.5', TRUE, '2024-01-01', 'system'),
(3, 50, 3, 50, 2, 25, 10, 150, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, '07:00:00', '10:00:00', '17:00:00', '20:00:00', '23:00:00', '06:00:00', 130, TRUE, 15, '2.1', TRUE, '2024-01-01', 'system'),
(4, 30, 3, 30, 2, 12, 5, 60, FALSE, TRUE, FALSE, FALSE, FALSE, TRUE, '08:00:00', '10:00:00', '17:00:00', '19:00:00', '23:00:00', '07:00:00', 80, FALSE, 0, '1.0', TRUE, '2024-01-01', 'system'),
(5, 40, 3, 40, 2, 18, 8, 100, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, '07:00:00', '10:00:00', '17:00:00', '20:00:00', '23:00:00', '06:00:00', 100, TRUE, 10, '2.0', TRUE, '2024-01-01', 'system'),
(6, 30, 3, 30, 2, 15, 7, 80, FALSE, TRUE, FALSE, FALSE, TRUE, TRUE, '07:30:00', '09:30:00', '17:00:00', '19:00:00', '22:00:00', '06:00:00', 85, FALSE, 0, '1.3', TRUE, '2024-01-01', 'system'),
(7, 40, 3, 40, 2, 20, 10, 100, TRUE, TRUE, FALSE, TRUE, TRUE, TRUE, '07:00:00', '10:00:00', '17:00:00', '20:00:00', '00:00:00', '06:00:00', 110, TRUE, 5, '1.8', TRUE, '2024-01-01', 'system'),
(8, 35, 3, 35, 2, 15, 8, 90, FALSE, TRUE, FALSE, FALSE, FALSE, FALSE, '08:00:00', '10:00:00', '17:00:00', '19:00:00', '23:00:00', '06:00:00', 90, FALSE, 0, '1.0', FALSE, '2024-01-01', 'system');

-- Sample Intersection Metrics (Last 7 days for first intersection)
INSERT INTO intersection_metrics (intersection_id, measurement_date, measurement_hour, total_vehicle_count, car_count, truck_count, bus_count, motorcycle_count, bicycle_count, pedestrian_count, average_wait_time, maximum_wait_time, average_queue_length, maximum_queue_length, throughput, green_time_utilization, red_light_violations, yellow_light_violations, pedestrian_violations, accidents_count, near_miss_count, emergency_vehicle_passages, system_uptime_percentage, malfunction_count, manual_override_count, estimated_co2_emission, estimated_fuel_consumption, data_quality_score) VALUES
-- Taksim Square - Recent data
(1, CURRENT_DATE - INTERVAL '1 day', 8, 1250, 950, 80, 45, 125, 50, 320, 45.5, 120.3, 8.5, 15, 1250, 87.5, 3, 2, 5, 0, 1, 2, 99.8, 0, 0, 125.4, 95.2, 0.95),
(1, CURRENT_DATE - INTERVAL '1 day', 9, 1450, 1100, 95, 50, 150, 55, 380, 52.3, 135.7, 9.8, 18, 1450, 89.2, 4, 3, 6, 0, 2, 1, 99.9, 0, 0, 145.2, 110.5, 0.96),
(1, CURRENT_DATE - INTERVAL '1 day', 17, 1680, 1280, 105, 60, 175, 60, 420, 58.7, 145.2, 11.2, 20, 1680, 91.3, 5, 4, 8, 0, 2, 3, 99.7, 0, 0, 168.5, 128.3, 0.94),
(1, CURRENT_DATE - INTERVAL '1 day', 18, 1820, 1385, 110, 65, 190, 70, 460, 62.4, 156.8, 12.5, 22, 1820, 92.5, 6, 5, 9, 0, 3, 2, 99.8, 0, 0, 182.7, 139.1, 0.95),
-- Kadıköy Pier
(2, CURRENT_DATE - INTERVAL '1 day', 8, 980, 750, 60, 35, 95, 40, 280, 38.2, 98.5, 6.8, 12, 980, 85.3, 2, 1, 3, 0, 1, 1, 99.9, 0, 0, 98.3, 74.8, 0.97),
(2, CURRENT_DATE - INTERVAL '1 day', 17, 1120, 860, 70, 40, 105, 45, 310, 42.5, 108.9, 7.5, 14, 1120, 87.1, 3, 2, 4, 0, 1, 2, 99.8, 0, 0, 112.4, 85.6, 0.96),
-- Ankara Kızılay
(3, CURRENT_DATE - INTERVAL '1 day', 8, 1580, 1200, 95, 55, 160, 70, 410, 55.8, 142.6, 10.2, 19, 1580, 90.5, 4, 3, 7, 0, 2, 3, 99.7, 0, 0, 158.6, 120.8, 0.95),
(3, CURRENT_DATE - INTERVAL '1 day', 17, 1750, 1330, 105, 62, 175, 78, 450, 61.2, 155.3, 11.8, 21, 1750, 92.1, 5, 4, 8, 0, 3, 4, 99.6, 0, 0, 175.8, 133.9, 0.94);

-- Sample Intersection Phases for Taksim Square (4-way intersection)
INSERT INTO intersection_phases (intersection_id, phase_number, phase_name, phase_type, allowed_directions, movement_type, min_duration, max_duration, default_duration, extension_time, priority_level, is_protected, is_permissive, has_pedestrian_signal, pedestrian_clearance_time, accessible_pedestrian_signal, sequence_order, can_skip, conflicting_phases, compatible_phases, description, is_active) VALUES
-- Taksim Square Phases
(1, 1, 'North-South Through', 'vehicle', ARRAY['north', 'south'], 'through', 15, 90, 45, 5, 5, TRUE, FALSE, FALSE, 0, FALSE, 1, FALSE, ARRAY[2, 3], ARRAY[4], 'Main north-south traffic flow', TRUE),
(1, 2, 'East-West Through', 'vehicle', ARRAY['east', 'west'], 'through', 15, 90, 45, 5, 5, TRUE, FALSE, FALSE, 0, FALSE, 2, FALSE, ARRAY[1, 3], ARRAY[4], 'Main east-west traffic flow', TRUE),
(1, 3, 'Left Turn Phase', 'turning', ARRAY['north', 'south', 'east', 'west'], 'left_turn', 10, 60, 20, 3, 3, TRUE, FALSE, FALSE, 0, FALSE, 3, TRUE, ARRAY[1, 2, 4], ARRAY[]::integer[], 'Protected left turns from all directions', TRUE),
(1, 4, 'Pedestrian Phase', 'pedestrian', ARRAY['north', 'south', 'east', 'west'], 'through', 15, 45, 25, 0, 8, FALSE, FALSE, TRUE, 5, TRUE, 4, FALSE, ARRAY[3], ARRAY[1, 2], 'All-way pedestrian crossing', TRUE),

-- Kadıköy Pier Phases
(2, 1, 'Main Street Through', 'vehicle', ARRAY['north', 'south'], 'through', 10, 70, 35, 4, 5, TRUE, FALSE, FALSE, 0, FALSE, 1, FALSE, ARRAY[2], ARRAY[3], 'Main street traffic', TRUE),
(2, 2, 'Side Street Through', 'vehicle', ARRAY['east', 'west'], 'through', 10, 60, 30, 4, 4, TRUE, FALSE, FALSE, 0, FALSE, 2, FALSE, ARRAY[1], ARRAY[3], 'Side street traffic', TRUE),
(2, 3, 'Pedestrian Phase', 'pedestrian', ARRAY['north', 'south', 'east', 'west'], 'through', 12, 30, 18, 0, 7, FALSE, FALSE, TRUE, 4, TRUE, 3, FALSE, ARRAY[]::integer[], ARRAY[1, 2], 'Pedestrian crossing', TRUE),

-- Ankara Kızılay Phases (Complex 8-phase system)
(3, 1, 'North Through', 'vehicle', ARRAY['north'], 'through', 15, 100, 50, 5, 5, TRUE, FALSE, FALSE, 0, FALSE, 1, FALSE, ARRAY[2, 3, 4], ARRAY[]::integer[], 'North approach through traffic', TRUE),
(3, 2, 'South Through', 'vehicle', ARRAY['south'], 'through', 15, 100, 50, 5, 5, TRUE, FALSE, FALSE, 0, FALSE, 2, FALSE, ARRAY[1, 3, 4], ARRAY[]::integer[], 'South approach through traffic', TRUE),
(3, 3, 'East Through', 'vehicle', ARRAY['east'], 'through', 15, 100, 50, 5, 5, TRUE, FALSE, FALSE, 0, FALSE, 3, FALSE, ARRAY[1, 2, 4], ARRAY[]::integer[], 'East approach through traffic', TRUE),
(3, 4, 'West Through', 'vehicle', ARRAY['west'], 'through', 15, 100, 50, 5, 5, TRUE, FALSE, FALSE, 0, FALSE, 4, FALSE, ARRAY[1, 2, 3], ARRAY[]::integer[], 'West approach through traffic', TRUE),
(3, 5, 'Pedestrian North-South', 'pedestrian', ARRAY['north', 'south'], 'through', 15, 40, 25, 0, 8, FALSE, FALSE, TRUE, 5, TRUE, 5, FALSE, ARRAY[]::integer[], ARRAY[]::integer[], 'North-South pedestrian crossing', TRUE),
(3, 6, 'Pedestrian East-West', 'pedestrian', ARRAY['east', 'west'], 'through', 15, 40, 25, 0, 8, FALSE, FALSE, TRUE, 5, TRUE, 6, FALSE, ARRAY[]::integer[], ARRAY[]::integer[], 'East-West pedestrian crossing', TRUE);

-- Add more metrics for different hours and dates
INSERT INTO intersection_metrics (intersection_id, measurement_date, measurement_hour, total_vehicle_count, car_count, truck_count, bus_count, motorcycle_count, bicycle_count, pedestrian_count, average_wait_time, maximum_wait_time, average_queue_length, maximum_queue_length, throughput, green_time_utilization, red_light_violations, yellow_light_violations, pedestrian_violations, accidents_count, near_miss_count, emergency_vehicle_passages, system_uptime_percentage, malfunction_count, manual_override_count, estimated_co2_emission, estimated_fuel_consumption, data_quality_score)
SELECT 
    (i.id),
    CURRENT_DATE - (random() * 30)::INTEGER * INTERVAL '1 day',
    (random() * 23)::INTEGER,
    (800 + random() * 1200)::INTEGER,
    (600 + random() * 900)::INTEGER,
    (50 + random() * 100)::INTEGER,
    (30 + random() * 50)::INTEGER,
    (80 + random() * 150)::INTEGER,
    (30 + random() * 80)::INTEGER,
    (200 + random() * 400)::INTEGER,
    (30 + random() * 50)::DECIMAL(10,2),
    (80 + random() * 100)::DECIMAL(10,2),
    (5 + random() * 10)::DECIMAL(10,2),
    (10 + random() * 15)::INTEGER,
    (800 + random() * 1200)::INTEGER,
    (75 + random() * 20)::DECIMAL(5,2),
    (random() * 8)::INTEGER,
    (random() * 5)::INTEGER,
    (random() * 10)::INTEGER,
    0,
    (random() * 4)::INTEGER,
    (random() * 5)::INTEGER,
    (95 + random() * 5)::DECIMAL(5,2),
    0,
    0,
    (80 + random() * 100)::DECIMAL(10,2),
    (60 + random() * 80)::DECIMAL(10,2),
    (0.85 + random() * 0.15)::DECIMAL(3,2)
FROM intersections i
CROSS JOIN generate_series(1, 5) g
WHERE i.status = 'active';

