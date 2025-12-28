package com.trafficlight.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Trafik optimizasyon sonucu")
public class OptimizationResponse {

    @Schema(description = "İşlem başarılı mı?", example = "true")
    private Boolean success;

    @Schema(description = "Durum mesajı", example = "🎯 Trafik kuralı başarıyla uygulandı")
    private String message;

    @Schema(description = "Kavşak bilgileri")
    private IntersectionInfo intersection;

    @Schema(description = "Uygulanan kurallar")
    private List<AppliedRule> appliedRules;

    @Schema(description = "Optimizasyon detayları")
    private OptimizationDetails details;

    @Schema(description = "Performans metriği")
    private PerformanceMetrics performance;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IntersectionInfo {
        @Schema(description = "Kavşak ID", example = "1")
        private Long intersectionId;

        @Schema(description = "Kavşak adı", example = "Kavşak-1 (Atatürk Bulvarı)")
        private String name;

        @Schema(description = "Mevcut araç sayısı", example = "45")
        private Integer vehicleCount;

        @Schema(description = "Yoğunluk seviyesi", example = "🟠 Yüksek")
        private String densityLevel;

        @Schema(description = "Ortalama hız", example = "25.5 km/h")
        private String averageSpeed;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppliedRule {
        @Schema(description = "Kural ID", example = "1")
        private Long ruleId;

        @Schema(description = "Kural adı", example = "PEAK_HOUR_EXTENSION")
        private String ruleName;

        @Schema(description = "Kural tipi", example = "⏰ Yoğun Saat")
        private String ruleType;

        @Schema(description = "Öncelik", example = "1")
        private Integer priority;

        @Schema(description = "Açıklama", example = "Yoğun saatlerde yeşil süreyi artırır")
        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptimizationDetails {
        @Schema(description = "Önceki yeşil süre (saniye)", example = "30")
        private Integer previousGreenDuration;

        @Schema(description = "Yeni yeşil süre (saniye)", example = "45")
        private Integer newGreenDuration;

        @Schema(description = "Ayarlama (saniye)", example = "+15")
        private String adjustment;

        @Schema(description = "Değişiklik nedeni", example = "Yüksek araç yoğunluğu tespit edildi")
        private String reason;

        @Schema(description = "Görsel gösterim", example = "⏱️ 30s → 45s (+15s)")
        private String visual;

        @Schema(description = "Uygulama zamanı")
        private LocalDateTime appliedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformanceMetrics {
        @Schema(description = "Tahmini bekleme süresi azalması", example = "-20%")
        private String waitTimeReduction;

        @Schema(description = "Trafik akışı iyileşmesi", example = "+15%")
        private String flowImprovement;

        @Schema(description = "Verimlilik skoru", example = "85/100")
        private String efficiencyScore;

        @Schema(description = "Öneri", example = "Optimizasyon başarılı - 15 dakika sürdürülmeli")
        private String recommendation;
    }
}