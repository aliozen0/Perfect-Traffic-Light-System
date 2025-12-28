package com.trafficlight.util;

import com.trafficlight.entity.*;
import com.trafficlight.entity.Intersection.IntersectionStatus;
import com.trafficlight.entity.Intersection.IntersectionType;
import com.trafficlight.entity.IntersectionPhase.PhaseType;
import com.trafficlight.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder; // EKLENDİ
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * HAFTA 5 - Data Seeding Script
 * Real-world test data generation for traffic light intersections
 */
@Component
@Profile("dev") // Only run in dev profile
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final IntersectionRepository intersectionRepository;
    private final IntersectionConfigRepository configRepository;
    private final IntersectionMetricRepository metricRepository;
    private final IntersectionPhaseRepository phaseRepository;
    
    // --- YENİ EKLENEN BAĞIMLILIKLAR ---
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    // ----------------------------------
    
    private final Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        
        // --- 1. ADIM: ÖNCE ADMİN KULLANCISINI OLUŞTUR (YENİ) ---
        seedDefaultAdmin();
        // -------------------------------------------------------

        // --- 2. ADIM: SONRA KAVŞAK VERİLERİNİ KONTROL ET ---
        if (intersectionRepository.count() > 0) {
            log.info("Database already contains intersection data. Skipping map seeding.");
            return;
        }

        log.info("Starting data seeding...");
        
        seedIstanbulIntersections();
        seedAnkaraIntersections();
        seedIzmirIntersections();
        
        log.info("Data seeding completed successfully!");
        log.info("Total intersections: {}", intersectionRepository.count());
        log.info("Total metrics: {}", metricRepository.count());
    }

    /**
     * Varsayılan Admin Kullanıcısını Oluştur
     */
    private void seedDefaultAdmin() {
        // Eğer admin kullanıcısı yoksa oluştur
        if (userRepository.findByUsername("admin@traffic.com").isEmpty()) {
            User admin = User.builder()
                    .username("admin@traffic.com")
                    .password(passwordEncoder.encode("password123")) // Şifre şifrelenerek kaydedilmeli
                    .isAdmin(true)
                    .enabled(true)
                    .build();
            
            userRepository.save(admin);
            log.info("✅ Varsayılan Admin Kullanıcısı Oluşturuldu: admin@traffic.com / password123");
        } else {
            log.info("ℹ️ Admin kullanıcısı zaten mevcut.");
        }
    }

    /**
     * Seed Istanbul intersections (30 intersections)
     */
    private void seedIstanbulIntersections() {
        log.info("Seeding Istanbul intersections...");
        
        List<IntersectionData> istanbulData = List.of(
            new IntersectionData("Taksim Meydanı", "IST-TAK-001", "41.0369", "28.9857", "Beyoğlu", "34433"),
            new IntersectionData("Beşiktaş Barbaros Bulvarı", "IST-BES-001", "41.0422", "29.0089", "Beşiktaş", "34353"),
            new IntersectionData("Kadıköy İskele", "IST-KAD-001", "40.9900", "29.0250", "Kadıköy", "34710"),
            new IntersectionData("Mecidiyeköy Metrobüs", "IST-MEC-001", "41.0682", "28.9976", "Şişli", "34394"),
            new IntersectionData("Bağdat Caddesi Kızıltoprak", "IST-BAG-001", "40.9750", "29.0420", "Kadıköy", "34720"),
            new IntersectionData("Levent Metro", "IST-LEV-001", "41.0780", "29.0094", "Beşiktaş", "34330"),
            new IntersectionData("Kozyatağı Kavşağı", "IST-KOZ-001", "40.9750", "29.1150", "Kadıköy", "34742"),
            new IntersectionData("Nişantaşı Rumeli Caddesi", "IST-NIS-001", "41.0464", "28.9941", "Şişli", "34367"),
            new IntersectionData("Ataköy Sahil Yolu", "IST-ATA-001", "40.9782", "28.8646", "Bakırköy", "34158"),
            new IntersectionData("Üsküdar Validebağ", "IST-USK-001", "41.0264", "29.0166", "Üsküdar", "34664"),
            new IntersectionData("Fatih Vatan Caddesi", "IST-FAT-001", "41.0186", "28.9490", "Fatih", "34093"),
            new IntersectionData("Zeytinburnu E-5", "IST-ZEY-001", "41.0031", "28.9027", "Zeytinburnu", "34020"),
            new IntersectionData("Maltepe Sahil Yolu", "IST-MAL-001", "40.9350", "29.1350", "Maltepe", "34844"),
            new IntersectionData("Bakırköy Ataköy", "IST-BAK-001", "40.9800", "28.8700", "Bakırköy", "34156"),
            new IntersectionData("Beylikdüzü Migros", "IST-BEY-001", "41.0050", "28.6480", "Beylikdüzü", "34520"),
            new IntersectionData("Kartal Yakacık", "IST-KAR-001", "40.8980", "29.1850", "Kartal", "34870"),
            new IntersectionData("Bostancı Sahil", "IST-BOS-001", "40.9600", "29.0900", "Kadıköy", "34744"),
            new IntersectionData("Göztepe Metrobüs", "IST-GOZ-001", "40.9850", "29.0700", "Kadıköy", "34730"),
            new IntersectionData("Esenler Otogar", "IST-ESE-001", "41.0420", "28.8820", "Esenler", "34220"),
            new IntersectionData("Maslak Sarıyer", "IST-MAS-001", "41.1150", "29.0200", "Sarıyer", "34398"),
            new IntersectionData("Ortaköy Meydanı", "IST-ORT-001", "41.0550", "29.0275", "Beşiktaş", "34347"),
            new IntersectionData("Yenikapı Metro", "IST-YEN-001", "41.0055", "28.9505", "Fatih", "34096"),
            new IntersectionData("Avcılar E-5", "IST-AVC-001", "41.0205", "28.7200", "Avcılar", "34310"),
            new IntersectionData("Pendik Marina", "IST-PEN-001", "40.8780", "29.2350", "Pendik", "34893"),
            new IntersectionData("Şişli Osmanbey", "IST-SIS-001", "41.0500", "28.9900", "Şişli", "34373"),
            new IntersectionData("Gaziosmanpaşa", "IST-GAZ-001", "41.0650", "28.9050", "Gaziosmanpaşa", "34260"),
            new IntersectionData("Ümraniye Çarşı", "IST-UMR-001", "41.0150", "29.1150", "Ümraniye", "34760"),
            new IntersectionData("Eyüp Sultan", "IST-EYU-001", "41.0550", "28.9350", "Eyüpsultan", "34050"),
            new IntersectionData("Küçükyalı Sahil", "IST-KUC-001", "40.9050", "29.1650", "Maltepe", "34854"),
            new IntersectionData("Sefaköy Metrobüs", "IST-SEF-001", "41.0050", "28.7850", "Küçükçekmece", "34295")
        );

        for (IntersectionData data : istanbulData) {
            Intersection intersection = createIntersection(data, "Istanbul");
            createConfigForIntersection(intersection);
            createPhasesForIntersection(intersection);
            createMetricsForIntersection(intersection, 30); // Last 30 days
        }
        
        log.info("Seeded {} Istanbul intersections", istanbulData.size());
    }

    /**
     * Seed Ankara intersections (15 intersections)
     */
    private void seedAnkaraIntersections() {
        log.info("Seeding Ankara intersections...");
        
        List<IntersectionData> ankaraData = List.of(
            new IntersectionData("Kızılay Meydanı", "ANK-KIZ-001", "39.9208", "32.8541", "Çankaya", "06420"),
            new IntersectionData("Ulus Meydanı", "ANK-ULU-001", "39.9450", "32.8597", "Altındağ", "06050"),
            new IntersectionData("Tunalı Hilmi Caddesi", "ANK-TUN-001", "39.9175", "32.8468", "Çankaya", "06700"),
            new IntersectionData("Atatürk Bulvarı Sıhhiye", "ANK-SIH-001", "39.9350", "32.8550", "Çankaya", "06100"),
            new IntersectionData("Dikmen Caddesi", "ANK-DIK-001", "39.8980", "32.8650", "Çankaya", "06450"),
            new IntersectionData("Keçiören Meydanı", "ANK-KEC-001", "39.9800", "32.8700", "Keçiören", "06290"),
            new IntersectionData("Çayyolu Kızılcaşar", "ANK-CAY-001", "39.9100", "32.7450", "Çankaya", "06810"),
            new IntersectionData("Batıkent Kavşağı", "ANK-BAT-001", "39.9850", "32.7350", "Yenimahalle", "06370"),
            new IntersectionData("Mamak Meydanı", "ANK-MAM-001", "39.9250", "32.9150", "Mamak", "06620"),
            new IntersectionData("Emek Mahallesi", "ANK-EME-001", "39.9050", "32.8280", "Çankaya", "06490"),
            new IntersectionData("Demetevler AŞTİ", "ANK-DEM-001", "39.9650", "32.8150", "Yenimahalle", "06200"),
            new IntersectionData("Bahçelievler Meydanı", "ANK-BAH-001", "39.9450", "32.8350", "Çankaya", "06490"),
            new IntersectionData("Etimesgut Eryaman", "ANK-ETI-001", "39.9550", "32.6850", "Etimesgut", "06794"),
            new IntersectionData("Sincan Yenikent", "ANK-SIN-001", "39.9950", "32.5550", "Sincan", "06930"),
            new IntersectionData("Pursaklar Saray", "ANK-PUR-001", "40.0350", "32.9050", "Pursaklar", "06145")
        );

        for (IntersectionData data : ankaraData) {
            Intersection intersection = createIntersection(data, "Ankara");
            createConfigForIntersection(intersection);
            createPhasesForIntersection(intersection);
            createMetricsForIntersection(intersection, 30);
        }
        
        log.info("Seeded {} Ankara intersections", ankaraData.size());
    }

    /**
     * Seed Izmir intersections (10 intersections)
     */
    private void seedIzmirIntersections() {
        log.info("Seeding Izmir intersections...");
        
        List<IntersectionData> izmirData = List.of(
            new IntersectionData("Konak Meydanı", "IZM-KON-001", "38.4189", "27.1287", "Konak", "35240"),
            new IntersectionData("Alsancak Kordon", "IZM-ALS-001", "38.4350", "27.1450", "Konak", "35220"),
            new IntersectionData("Bornova Meydanı", "IZM-BOR-001", "38.4650", "27.2150", "Bornova", "35040"),
            new IntersectionData("Karşıyaka İskele", "IZM-KAR-001", "38.4550", "27.1050", "Karşıyaka", "35530"),
            new IntersectionData("Buca Şirinyer", "IZM-BUC-001", "38.3850", "27.1850", "Buca", "35390"),
            new IntersectionData("Bayraklı Forum", "IZM-BAY-001", "38.4650", "27.1650", "Bayraklı", "35530"),
            new IntersectionData("Gaziemir Havalimanı", "IZM-GAZ-001", "38.3650", "27.1350", "Gaziemir", "35410"),
            new IntersectionData("Çiğli Ege Üniversite", "IZM-CIG-001", "38.5050", "27.0450", "Çiğli", "35620"),
            new IntersectionData("Balçova Teleferik", "IZM-BAL-001", "38.3850", "27.0550", "Balçova", "35330"),
            new IntersectionData("Narlıdere Sahil", "IZM-NAR-001", "38.4050", "27.0350", "Narlıdere", "35320")
        );

        for (IntersectionData data : izmirData) {
            Intersection intersection = createIntersection(data, "Izmir");
            createConfigForIntersection(intersection);
            createPhasesForIntersection(intersection);
            createMetricsForIntersection(intersection, 30);
        }
        
        log.info("Seeded {} Izmir intersections", izmirData.size());
    }

    /**
     * Create intersection entity
     */
    private Intersection createIntersection(IntersectionData data, String city) {
        Intersection intersection = Intersection.builder()
            .name(data.name)
            .code(data.code)
            .latitude(new BigDecimal(data.latitude))
            .longitude(new BigDecimal(data.longitude))
            .address(data.name + ", " + data.district)
            .city(city)
            .district(data.district)
            .postalCode(data.postalCode)
            .intersectionType(randomIntersectionType())
            .status(randomStatus())
            .lanesCount(random.nextInt(3) + 3) // 3-5 lanes
            .hasPedestrianCrossing(random.nextBoolean())
            .hasVehicleDetection(random.nextBoolean())
            .hasEmergencyOverride(random.nextBoolean())
            .description("Intersection at " + data.name)
            .installationDate(LocalDate.now().minusYears(random.nextInt(10) + 1))
            .lastMaintenanceDate(LocalDate.now().minusMonths(random.nextInt(6)))
            .nextMaintenanceDate(LocalDate.now().plusMonths(random.nextInt(3) + 1))
            .createdBy("seeder")
            .updatedBy("seeder")
            .build();
        
        return intersectionRepository.save(intersection);
    }

    /**
     * Create configuration for intersection
     */
    private void createConfigForIntersection(Intersection intersection) {
        IntersectionConfig config = IntersectionConfig.builder()
            .intersection(intersection)
            .greenLightDuration(random.nextInt(30) + 30) // 30-60 seconds
            .yellowLightDuration(3)
            .redLightDuration(random.nextInt(30) + 30)
            .allRedDuration(2)
            .pedestrianCrossingDuration(random.nextInt(10) + 15) // 15-25 seconds
            .minimumGreenTime(5)
            .maximumGreenTime(120)
            .vehicleDetectionEnabled(intersection.getHasVehicleDetection())
            .pedestrianButtonEnabled(true)
            .emergencyVehiclePriority(intersection.getHasEmergencyOverride())
            .adaptiveTimingEnabled(random.nextBoolean())
            .peakHourModeEnabled(random.nextBoolean())
            .nightModeEnabled(random.nextBoolean())
            .peakMorningStart(LocalTime.of(7, 0))
            .peakMorningEnd(LocalTime.of(10, 0))
            .peakEveningStart(LocalTime.of(17, 0))
            .peakEveningEnd(LocalTime.of(20, 0))
            .nightModeStart(LocalTime.of(23, 0))
            .nightModeEnd(LocalTime.of(6, 0))
            .cycleLength(90)
            .coordinationEnabled(random.nextBoolean())
            .coordinationOffset(random.nextInt(30))
            .configVersion("1.0")
            .isActive(true)
            .effectiveFrom(LocalDate.now().minusMonths(1))
            .createdBy("seeder")
            .build();
        
        configRepository.save(config);
    }

    /**
     * Create phases for intersection
     */
    private void createPhasesForIntersection(Intersection intersection) {
        List<IntersectionPhase> phases = new ArrayList<>();
        
        // Phase 1: North-South vehicle traffic
        phases.add(IntersectionPhase.builder()
            .intersection(intersection)
            .phaseNumber(1)
            .phaseName("North-South Through")
            .phaseType(PhaseType.VEHICLE)
            .allowedDirections(new String[]{"north", "south"})
            .movementType("through")
            .minDuration(15)
            .maxDuration(60)
            .defaultDuration(30)
            .extensionTime(3)
            .priorityLevel(1)
            .isProtected(true)
            .hasPedestrianSignal(false)
            .sequenceOrder(1)
            .canSkip(false)
            .isActive(true)
            .createdBy("seeder")
            .build());
        
        // Phase 2: East-West vehicle traffic
        phases.add(IntersectionPhase.builder()
            .intersection(intersection)
            .phaseNumber(2)
            .phaseName("East-West Through")
            .phaseType(PhaseType.VEHICLE)
            .allowedDirections(new String[]{"east", "west"})
            .movementType("through")
            .minDuration(15)
            .maxDuration(60)
            .defaultDuration(30)
            .extensionTime(3)
            .priorityLevel(1)
            .isProtected(true)
            .hasPedestrianSignal(false)
            .sequenceOrder(2)
            .canSkip(false)
            .isActive(true)
            .createdBy("seeder")
            .build());
        
        // Phase 3: Pedestrian crossing
        if (intersection.getHasPedestrianCrossing()) {
            phases.add(IntersectionPhase.builder()
                .intersection(intersection)
                .phaseNumber(3)
                .phaseName("Pedestrian Crossing")
                .phaseType(PhaseType.PEDESTRIAN)
                .allowedDirections(new String[]{"all"})
                .movementType("crossing")
                .minDuration(10)
                .maxDuration(30)
                .defaultDuration(20)
                .extensionTime(5)
                .priorityLevel(2)
                .isProtected(true)
                .hasPedestrianSignal(true)
                .pedestrianClearanceTime(5)
                .accessiblePedestrianSignal(true)
                .sequenceOrder(3)
                .canSkip(true)
                .isActive(true)
                .createdBy("seeder")
                .build());
        }
        
        // Phase 4: Left turn phase
        phases.add(IntersectionPhase.builder()
            .intersection(intersection)
            .phaseNumber(4)
            .phaseName("Protected Left Turn")
            .phaseType(PhaseType.TURNING)
            .allowedDirections(new String[]{"left"})
            .movementType("left_turn")
            .minDuration(5)
            .maxDuration(20)
            .defaultDuration(10)
            .extensionTime(2)
            .priorityLevel(3)
            .isProtected(true)
            .hasPedestrianSignal(false)
            .sequenceOrder(4)
            .canSkip(true)
            .isActive(true)
            .createdBy("seeder")
            .build());
        
        phaseRepository.saveAll(phases);
    }

    /**
     * Create historical metrics for intersection
     */
    private void createMetricsForIntersection(Intersection intersection, int days) {
        LocalDate today = LocalDate.now();
        
        for (int dayOffset = 0; dayOffset < days; dayOffset++) {
            LocalDate date = today.minusDays(dayOffset);
            
            // Create metrics for peak hours only (to keep data reasonable)
            int[] peakHours = {8, 9, 17, 18, 19};
            
            for (int hour : peakHours) {
                IntersectionMetric metric = IntersectionMetric.builder()
                    .intersection(intersection)
                    .measurementDate(date)
                    .measurementHour(hour)
                    .totalVehicleCount(random.nextInt(500) + 800) // 800-1300 vehicles
                    .carCount(random.nextInt(400) + 600)
                    .truckCount(random.nextInt(50) + 20)
                    .busCount(random.nextInt(30) + 10)
                    .motorcycleCount(random.nextInt(40) + 20)
                    .bicycleCount(random.nextInt(30) + 10)
                    .pedestrianCount(random.nextInt(200) + 100)
                    .averageWaitTime(new BigDecimal(random.nextInt(60) + 30)) // 30-90 seconds
                    .maximumWaitTime(new BigDecimal(random.nextInt(120) + 60))
                    .averageQueueLength(new BigDecimal(random.nextInt(8) + 3))
                    .maximumQueueLength(random.nextInt(15) + 5)
                    .throughput(random.nextInt(400) + 1000) // 1000-1400 vehicles/hour
                    .greenTimeUtilization(new BigDecimal(random.nextInt(30) + 70)) // 70-100%
                    .redLightViolations(random.nextInt(5))
                    .yellowLightViolations(random.nextInt(3))
                    .pedestrianViolations(random.nextInt(2))
                    .accidentsCount(random.nextInt(100) < 5 ? 1 : 0) // 5% chance of accident
                    .nearMissCount(random.nextInt(3))
                    .emergencyVehiclePassages(random.nextInt(3))
                    .systemUptimePercentage(new BigDecimal(random.nextInt(5) + 95)) // 95-100%
                    .malfunctionCount(random.nextInt(100) < 10 ? 1 : 0) // 10% chance
                    .manualOverrideCount(random.nextInt(2))
                    .estimatedCo2Emission(new BigDecimal(random.nextInt(200) + 150))
                    .estimatedFuelConsumption(new BigDecimal(random.nextInt(80) + 50))
                    .dataQualityScore(new BigDecimal("0." + (random.nextInt(20) + 80))) // 0.80-1.00
                    .build();
                
                metricRepository.save(metric);
            }
        }
    }

    /**
     * Random intersection type generator
     */
    private IntersectionType randomIntersectionType() {
        IntersectionType[] types = IntersectionType.values();
        // Weighted random: 70% traffic lights, 20% crossroad, 10% others
        int rand = random.nextInt(100);
        if (rand < 70) return IntersectionType.TRAFFIC_LIGHT;
        if (rand < 90) return IntersectionType.CROSSROAD;
        return types[random.nextInt(types.length)];
    }

    /**
     * Random status generator
     */
    private IntersectionStatus randomStatus() {
        int rand = random.nextInt(100);
        if (rand < 85) return IntersectionStatus.ACTIVE;
        if (rand < 95) return IntersectionStatus.MAINTENANCE;
        return IntersectionStatus.INACTIVE;
    }

    /**
     * Helper class for intersection data
     */
    private static class IntersectionData {
        String name;
        String code;
        String latitude;
        String longitude;
        String district;
        String postalCode;

        IntersectionData(String name, String code, String latitude, String longitude, 
                        String district, String postalCode) {
            this.name = name;
            this.code = code;
            this.latitude = latitude;
            this.longitude = longitude;
            this.district = district;
            this.postalCode = postalCode;
        }
    }
}