package com.trafficlight.controller;

import com.trafficlight.dto.OptimizationRequest;
import com.trafficlight.dto.OptimizationResponse;
import com.trafficlight.dto.SensorDataRequest;
import com.trafficlight.entity.RuleApplication;
import com.trafficlight.entity.TrafficRule;
import com.trafficlight.entity.TrafficSensor;
import com.trafficlight.repository.RuleApplicationRepository;
import com.trafficlight.repository.TrafficRuleRepository;
import com.trafficlight.repository.TrafficSensorRepository;
import com.trafficlight.service.TrafficRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/optimization")
@RequiredArgsConstructor
@Tag(name = "🎯 Traffic Optimization", description = "Kural Tabanlı Trafik Optimizasyon Sistemi")
public class TrafficOptimizationController {

    private final TrafficRuleService ruleService;
    private final TrafficRuleRepository ruleRepository;
    private final RuleApplicationRepository applicationRepository;
    private final TrafficSensorRepository sensorRepository;

    @Operation(
        summary = "🚦 Trafik optimizasyonu uygula",
        description = """
            Belirtilen kavşak için trafik yoğunluğuna göre otomatik optimizasyon yapar.
            
            **Nasıl Çalışır:**
            1. Araç sayısına göre uygun kuralı bulur
            2. Yeşil ışık süresini dinamik olarak ayarlar
            3. Performans metriklerini hesaplar
            4. Uygulama kaydını tutar
            
            **Örnekler:**
            - 45 araç → Yeşil süre 30s'den 45s'ye çıkar (+15s)
            - 10 araç → Yeşil süre 30s'de kalır (değişiklik yok)
            - 60 araç → Yeşil süre 30s'den 55s'ye çıkar (+25s)
            
            **Kurallar:**
            • PEAK_HOUR: Sabah 07:00-09:00, 25+ araç
            • HIGH_DENSITY: 40+ araç (her zaman)
            • NIGHT_MODE: Gece 00:00-06:00, 15- araç
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "✅ Optimizasyon başarılı",
            content = @Content(schema = @Schema(implementation = OptimizationResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "❌ Geçersiz istek"
        )
    })
    @PostMapping("/apply")
    public ResponseEntity<OptimizationResponse> applyOptimization(
            @Valid @RequestBody
            @Parameter(description = "Optimizasyon parametreleri", required = true)
            OptimizationRequest request) {
        
        OptimizationResponse response = ruleService.optimizeTraffic(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "🚀 Hızlı Test - Yoğun Trafik Simülasyonu",
        description = """
            Test amaçlı yoğun trafik simülasyonu.
            
            **Senaryo:**
            - Kavşak-1'de 45 araç tespit edildi
            - Ortalama hız: 25 km/h
            - Sistem otomatik optimizasyon yapar
            - Yeşil süre artırılır
            
            ⚠️ Demo/test için kullanın!
            """
    )
    @PostMapping("/test/high-traffic")
    public ResponseEntity<OptimizationResponse> testHighTraffic() {
        OptimizationRequest request = OptimizationRequest.builder()
                .intersectionId(1L)
                .vehicleCount(45)
                .averageSpeed(25.0)
                .build();
        
        OptimizationResponse response = ruleService.optimizeTraffic(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "🌙 Hızlı Test - Gece Modu Simülasyonu",
        description = "Test amaçlı düşük trafik (gece modu) simülasyonu. Az araç olduğunda yeşil süre kısaltılır."
    )
    @PostMapping("/test/night-mode")
    public ResponseEntity<OptimizationResponse> testNightMode() {
        OptimizationRequest request = OptimizationRequest.builder()
                .intersectionId(1L)
                .vehicleCount(8)
                .averageSpeed(45.0)
                .build();
        
        OptimizationResponse response = ruleService.optimizeTraffic(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "📋 Tüm kuralları listele",
        description = """
            Sistemde tanımlı tüm trafik kurallarını listeler.
            
            **Her kural şunları içerir:**
            - Kural adı ve tipi
            - Öncelik seviyesi
            - Koşullar (araç sayısı, saat aralığı vb.)
            - Yeşil süre ayarlaması
            - Kaç kez uygulandığı
            """
    )
    @GetMapping("/rules")
    public ResponseEntity<List<TrafficRule>> getAllRules() {
        List<TrafficRule> rules = ruleRepository.findAll();
        return ResponseEntity.ok(rules);
    }

    @Operation(
        summary = "✅ Aktif kuralları listele",
        description = "Şu anda aktif olan kuralları öncelik sırasına göre listeler."
    )
    @GetMapping("/rules/active")
    public ResponseEntity<List<TrafficRule>> getActiveRules() {
        List<TrafficRule> rules = ruleRepository.findByActiveTrueOrderByPriorityAsc();
        return ResponseEntity.ok(rules);
    }

    @Operation(
        summary = "🔍 Belirli bir kuralı getir",
        description = "ID'ye göre kural detaylarını getirir."
    )
    @GetMapping("/rules/{ruleId}")
    public ResponseEntity<TrafficRule> getRule(
            @PathVariable @Parameter(description = "Kural ID", example = "1") Long ruleId) {
        
        return ruleRepository.findById(ruleId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "📊 Kural uygulama geçmişi",
        description = """
            Belirli bir kuralın tüm uygulama geçmişini detaylı şekilde getirir.
            
            **İçerik:**
            - Ne zaman uygulandı
            - Hangi kavşakta
            - Kaç araç vardı
            - Yeşil süre nasıl değişti
            - Başarılı/Başarısız
            """
    )
    @GetMapping("/rules/{ruleId}/history")
    public ResponseEntity<List<RuleApplication>> getRuleHistory(
            @PathVariable @Parameter(description = "Kural ID", example = "1") Long ruleId) {
        
        List<RuleApplication> history = 
            applicationRepository.findByRuleIdOrderByAppliedAtDesc(ruleId);
        return ResponseEntity.ok(history);
    }

    @Operation(
        summary = "🏆 En çok uygulanan kurallar",
        description = "Bugün en çok hangi kurallar uygulandı? İstatistik gösterir."
    )
    @GetMapping("/rules/statistics/most-applied")
    public ResponseEntity<List<Object[]>> getMostAppliedRules() {
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        List<Object[]> stats = applicationRepository.findMostAppliedRulesSince(today);
        return ResponseEntity.ok(stats);
    }

    @Operation(
        summary = "📡 Sensör verisi gönder",
        description = """
            Sensörden gelen trafik verisini kaydeder.
            
            **Veri:**
            - Sensör ID
            - Kavşak ve yön
            - Araç sayısı
            - Ortalama hız
            
            Veriler analiz ve raporlama için saklanır.
            """
    )
    @PostMapping("/sensor/data")
    public ResponseEntity<Map<String, Object>> submitSensorData(
            @Valid @RequestBody
            @Parameter(description = "Sensör verisi", required = true)
            SensorDataRequest request) {
        
        TrafficSensor sensor = TrafficSensor.builder()
                .sensorId(request.getSensorId())
                .intersectionId(request.getIntersectionId())
                .direction(request.getDirection())
                .vehicleCount(request.getVehicleCount())
                .averageSpeed(request.getAverageSpeed())
                .build();
        
        TrafficSensor saved = sensorRepository.save(sensor);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "📡 Sensör verisi başarıyla kaydedildi");
        response.put("sensorId", saved.getSensorId());
        response.put("densityLevel", saved.getDensityLevel().getDisplayName());
        response.put("timestamp", saved.getRecordedAt());
        
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "📊 Kavşak sensör verileri",
        description = "Belirli bir kavşağın son sensör verilerini getirir."
    )
    @GetMapping("/sensor/intersection/{intersectionId}")
    public ResponseEntity<List<TrafficSensor>> getIntersectionSensorData(
            @PathVariable @Parameter(description = "Kavşak ID", example = "1") Long intersectionId) {
        
        List<TrafficSensor> data = 
            sensorRepository.findByIntersectionIdAndActiveTrueOrderByRecordedAtDesc(intersectionId);
        return ResponseEntity.ok(data);
    }

    @Operation(
        summary = "📈 Son 1 saatteki sensör verileri",
        description = "Son 1 saat içinde kaydedilen tüm sensör verilerini getirir."
    )
    @GetMapping("/sensor/recent/{intersectionId}")
    public ResponseEntity<List<TrafficSensor>> getRecentSensorData(
            @PathVariable @Parameter(description = "Kavşak ID", example = "1") Long intersectionId) {
        
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<TrafficSensor> data = sensorRepository.findRecentReadings(intersectionId, oneHourAgo);
        return ResponseEntity.ok(data);
    }

    @Operation(
        summary = "⚙️ Varsayılan kuralları oluştur",
        description = """
            Sistem varsayılan kurallarını oluşturur.
            
            **Oluşturulan Kurallar:**
            1. PEAK_HOUR_EXTENSION - Sabah yoğunluğu
            2. HIGH_DENSITY_BOOST - Yüksek yoğunluk
            3. NIGHT_MODE_QUICK - Gece modu
            
            ⚠️ Sadece ilk kurulumda bir kez çalıştırın!
            """
    )
    @PostMapping("/rules/create-defaults")
    public ResponseEntity<Map<String, Object>> createDefaultRules() {
        ruleService.createDefaultRules();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "✅ Varsayılan kurallar oluşturuldu");
        response.put("totalRules", ruleRepository.count());
        
        return ResponseEntity.ok(response);
    }
}