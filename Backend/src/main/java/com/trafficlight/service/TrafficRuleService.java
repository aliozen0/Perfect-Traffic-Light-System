package com.trafficlight.service;

import com.trafficlight.dto.OptimizationRequest;
import com.trafficlight.dto.OptimizationResponse;
import com.trafficlight.entity.RuleApplication;
import com.trafficlight.entity.TrafficRule;
import com.trafficlight.entity.TrafficSensor;
import com.trafficlight.repository.RuleApplicationRepository;
import com.trafficlight.repository.TrafficRuleRepository;
import com.trafficlight.repository.TrafficSensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrafficRuleService {

    private final TrafficRuleRepository ruleRepository;
    private final RuleApplicationRepository applicationRepository;
    private final TrafficSensorRepository sensorRepository;

    /**
     * Trafik optimizasyonu uygula
     */
    @Transactional
    public OptimizationResponse optimizeTraffic(OptimizationRequest request) {
        log.info("🎯 Optimizasyon başlatıldı - Kavşak: {}, Araç: {}", 
                 request.getIntersectionId(), request.getVehicleCount());

        // 1. Sensör verisini kaydet (opsiyonel)
        saveSensorData(request);

        // 2. Uygulanabilir kuralları bul
        List<TrafficRule> applicableRules = findApplicableRules(request);

        if (applicableRules.isEmpty()) {
            return buildNoRuleResponse(request);
        }

        // 3. En yüksek öncelikli kuralı uygula
        TrafficRule selectedRule = applicableRules.get(0);

        // 4. Yeşil süreyi hesapla
        int previousDuration = selectedRule.getBaseGreenDuration();
        int newDuration = calculateNewGreenDuration(selectedRule, request.getVehicleCount());

        // 5. Uygulama kaydı oluştur
        RuleApplication application = logRuleApplication(
                selectedRule, request, previousDuration, newDuration);

        // 6. Kural uygulama sayısını artır
        incrementRuleApplicationCount(selectedRule);

        // 7. Response oluştur
        return buildOptimizationResponse(request, applicableRules, selectedRule, 
                                         previousDuration, newDuration);
    }

    /**
     * Sensör verisini kaydet
     */
    private void saveSensorData(OptimizationRequest request) {
        if (request.getAverageSpeed() != null) {
            TrafficSensor sensor = TrafficSensor.builder()
                    .sensorId("AUTO-" + request.getIntersectionId() + "-" + System.currentTimeMillis())
                    .intersectionId(request.getIntersectionId())
                    .direction(TrafficSensor.Direction.NORTH) // Default
                    .vehicleCount(request.getVehicleCount())
                    .averageSpeed(request.getAverageSpeed())
                    .active(true)
                    .build();
            sensorRepository.save(sensor);
        }
    }

    /**
     * Uygulanabilir kuralları bul
     */
    private List<TrafficRule> findApplicableRules(OptimizationRequest request) {
        if (request.getRuleId() != null) {
            // Manuel kural seçimi
            return ruleRepository.findById(request.getRuleId())
                    .map(List::of)
                    .orElse(new ArrayList<>());
        }

        // Otomatik kural seçimi
        List<TrafficRule> allRules = ruleRepository.findByActiveTrueOrderByPriorityAsc();
        List<TrafficRule> applicable = new ArrayList<>();

        LocalTime now = LocalTime.now();

        for (TrafficRule rule : allRules) {
            // Araç sayısı kontrolü
            if (rule.getMinVehicleCount() != null && 
                request.getVehicleCount() < rule.getMinVehicleCount()) {
                continue;
            }
            if (rule.getMaxVehicleCount() != null && 
                request.getVehicleCount() > rule.getMaxVehicleCount()) {
                continue;
            }

            // Zaman kontrolü
            if (rule.getTimeStart() != null && rule.getTimeEnd() != null) {
                if (now.isBefore(rule.getTimeStart()) || now.isAfter(rule.getTimeEnd())) {
                    continue;
                }
            }

            applicable.add(rule);
        }

        return applicable;
    }

    /**
     * Yeni yeşil süreyi hesapla
     */
    private int calculateNewGreenDuration(TrafficRule rule, int vehicleCount) {
        int baseDuration = rule.getBaseGreenDuration();
        int adjustment = rule.getGreenDurationAdjustment() != null ? 
                         rule.getGreenDurationAdjustment() : 0;

        int newDuration = baseDuration + adjustment;

        // Min/Max sınırları
        if (rule.getMinGreenDuration() != null) {
            newDuration = Math.max(newDuration, rule.getMinGreenDuration());
        }
        if (rule.getMaxGreenDuration() != null) {
            newDuration = Math.min(newDuration, rule.getMaxGreenDuration());
        }

        // Dinamik ayarlama (araç sayısına göre)
        if (vehicleCount > 40) {
            newDuration += 10; // Çok yoğun
        } else if (vehicleCount > 25) {
            newDuration += 5;  // Yoğun
        }

        return Math.max(15, Math.min(90, newDuration)); // 15-90 saniye arası
    }

    /**
     * Kural uygulamasını logla
     */
    private RuleApplication logRuleApplication(TrafficRule rule, OptimizationRequest request,
                                               int previousDuration, int newDuration) {
        RuleApplication application = RuleApplication.builder()
                .ruleId(rule.getId())
                .ruleName(rule.getRuleName())
                .intersectionId(request.getIntersectionId())
                .intersectionName("Kavşak-" + request.getIntersectionId())
                .vehicleCount(request.getVehicleCount())
                .previousGreenDuration(previousDuration)
                .newGreenDuration(newDuration)
                .adjustment(newDuration - previousDuration)
                .reason(String.format("%s kuralı uygulandı - Araç sayısı: %d",
                        rule.getRuleType().getDisplayName(), request.getVehicleCount()))
                .successful(true)
                .build();

        return applicationRepository.save(application);
    }

    /**
     * Kural uygulama sayısını artır
     */
    private void incrementRuleApplicationCount(TrafficRule rule) {
        if (rule.getTimesApplied() == null) {
            rule.setTimesApplied(0L);
        }
        rule.setTimesApplied(rule.getTimesApplied() + 1);
        ruleRepository.save(rule);
    }

    /**
     * Kural bulunamadığında response
     */
    private OptimizationResponse buildNoRuleResponse(OptimizationRequest request) {
        return OptimizationResponse.builder()
                .success(false)
                .message("⚠️ Uygulanabilir kural bulunamadı")
                .intersection(OptimizationResponse.IntersectionInfo.builder()
                        .intersectionId(request.getIntersectionId())
                        .name("Kavşak-" + request.getIntersectionId())
                        .vehicleCount(request.getVehicleCount())
                        .build())
                .build();
    }

    /**
     * Optimizasyon response'u oluştur
     */
    private OptimizationResponse buildOptimizationResponse(
            OptimizationRequest request,
            List<TrafficRule> applicableRules,
            TrafficRule selectedRule,
            int previousDuration,
            int newDuration) {

        // Kavşak bilgileri
        String densityLevel = getDensityLevel(request.getVehicleCount());
        OptimizationResponse.IntersectionInfo intersectionInfo = 
            OptimizationResponse.IntersectionInfo.builder()
                .intersectionId(request.getIntersectionId())
                .name("Kavşak-" + request.getIntersectionId() + " (Atatürk Bulvarı)")
                .vehicleCount(request.getVehicleCount())
                .densityLevel(densityLevel)
                .averageSpeed(request.getAverageSpeed() != null ? 
                             request.getAverageSpeed() + " km/h" : "N/A")
                .build();

        // Uygulanan kurallar
        List<OptimizationResponse.AppliedRule> appliedRulesList = new ArrayList<>();
        for (TrafficRule rule : applicableRules) {
            appliedRulesList.add(OptimizationResponse.AppliedRule.builder()
                    .ruleId(rule.getId())
                    .ruleName(rule.getRuleName())
                    .ruleType(rule.getRuleType().getDisplayName())
                    .priority(rule.getPriority())
                    .description(rule.getDescription())
                    .build());
        }

        // Optimizasyon detayları
        int adjustment = newDuration - previousDuration;
        OptimizationResponse.OptimizationDetails details = 
            OptimizationResponse.OptimizationDetails.builder()
                .previousGreenDuration(previousDuration)
                .newGreenDuration(newDuration)
                .adjustment((adjustment >= 0 ? "+" : "") + adjustment + " saniye")
                .reason(String.format("%s - %d araç tespit edildi",
                        selectedRule.getRuleType().getDisplayName(),
                        request.getVehicleCount()))
                .visual(String.format("⏱️  %ds → %ds (%+ds)", 
                        previousDuration, newDuration, adjustment))
                .appliedAt(LocalDateTime.now())
                .build();

        // Performans metrikleri
        OptimizationResponse.PerformanceMetrics performance = 
            calculatePerformance(adjustment, request.getVehicleCount());

        return OptimizationResponse.builder()
                .success(true)
                .message("🎯 Trafik kuralı başarıyla uygulandı: " + selectedRule.getRuleName())
                .intersection(intersectionInfo)
                .appliedRules(appliedRulesList)
                .details(details)
                .performance(performance)
                .build();
    }

    /**
     * Yoğunluk seviyesini belirle
     */
    private String getDensityLevel(int vehicleCount) {
        if (vehicleCount < 10) return "🟢 Düşük (0-9 araç)";
        if (vehicleCount < 30) return "🟡 Orta (10-29 araç)";
        if (vehicleCount < 50) return "🟠 Yüksek (30-49 araç)";
        return "🔴 Kritik (50+ araç)";
    }

    /**
     * Performans metriklerini hesapla
     */
    private OptimizationResponse.PerformanceMetrics calculatePerformance(
            int adjustment, int vehicleCount) {
        
        String waitTimeReduction = adjustment > 0 ? 
                "-" + (adjustment * 2) + "%" : "0%";
        
        String flowImprovement = adjustment > 0 ? 
                "+" + (adjustment * 3) + "%" : "0%";
        
        int efficiency = 50 + (adjustment * 5);
        efficiency = Math.max(0, Math.min(100, efficiency));
        
        String recommendation = adjustment > 0 ? 
                "✅ Optimizasyon başarılı - " + (adjustment * 60 / 4) + " dakika sürdürülmeli" :
                "⚠️ Optimizasyon yapılmadı - Normal mod devam ediyor";

        return OptimizationResponse.PerformanceMetrics.builder()
                .waitTimeReduction(waitTimeReduction)
                .flowImprovement(flowImprovement)
                .efficiencyScore(efficiency + "/100")
                .recommendation(recommendation)
                .build();
    }

    /**
     * Varsayılan kuralları oluştur
     */
    @Transactional
    public void createDefaultRules() {
        if (ruleRepository.count() > 0) {
            log.info("Kurallar zaten mevcut, atlanıyor");
            return;
        }

        log.info("📝 Varsayılan kurallar oluşturuluyor...");

        // 1. Yoğun saat kuralı
        TrafficRule peakHour = TrafficRule.builder()
                .ruleName("PEAK_HOUR_EXTENSION")
                .ruleType(TrafficRule.RuleType.PEAK_HOUR)
                .description("Yoğun saatlerde yeşil süreyi artırır")
                .active(true)
                .priority(1)
                .minVehicleCount(25)
                .timeStart(LocalTime.of(7, 0))
                .timeEnd(LocalTime.of(9, 0))
                .dayType(TrafficRule.DayType.WEEKDAY)
                .baseGreenDuration(30)
                .greenDurationAdjustment(15)
                .maxGreenDuration(60)
                .minGreenDuration(20)
                .build();
        ruleRepository.save(peakHour);

        // 2. Yüksek yoğunluk kuralı
        TrafficRule highDensity = TrafficRule.builder()
                .ruleName("HIGH_DENSITY_BOOST")
                .ruleType(TrafficRule.RuleType.HIGH_DENSITY)
                .description("Araç sayısı 40'ı geçtiğinde süreyi uzat")
                .active(true)
                .priority(2)
                .minVehicleCount(40)
                .dayType(TrafficRule.DayType.ALL)
                .baseGreenDuration(30)
                .greenDurationAdjustment(20)
                .maxGreenDuration(75)
                .minGreenDuration(25)
                .build();
        ruleRepository.save(highDensity);

        // 3. Gece modu
        TrafficRule nightMode = TrafficRule.builder()
                .ruleName("NIGHT_MODE_QUICK")
                .ruleType(TrafficRule.RuleType.NIGHT_MODE)
                .description("Gece saatlerinde kısa süreli geçiş")
                .active(true)
                .priority(3)
                .maxVehicleCount(15)
                .timeStart(LocalTime.of(0, 0))
                .timeEnd(LocalTime.of(6, 0))
                .dayType(TrafficRule.DayType.ALL)
                .baseGreenDuration(30)
                .greenDurationAdjustment(-10)
                .maxGreenDuration(25)
                .minGreenDuration(15)
                .build();
        ruleRepository.save(nightMode);

        log.info("✅ 3 varsayılan kural oluşturuldu");
    }
}