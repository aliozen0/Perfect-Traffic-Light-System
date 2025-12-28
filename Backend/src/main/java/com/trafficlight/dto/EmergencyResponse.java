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
@Schema(description = "Acil araç müdahalesi sonuç response'u")
public class EmergencyResponse {

    @Schema(description = "İşlem başarılı mı?", example = "true")
    private Boolean success;

    @Schema(description = "Durum mesajı", example = "🚑 Acil araç tespit edildi ve öncelik verildi")
    private String message;

    @Schema(description = "Acil araç detayları")
    private EmergencyVehicleInfo vehicle;

    @Schema(description = "Yapılan işlemler listesi")
    private List<String> actions;

    @Schema(description = "Etkilenen kavşaklar")
    private List<IntersectionStatus> affectedIntersections;

    @Schema(description = "Etki analizi")
    private ImpactAnalysis impact;

    @Schema(description = "Zaman bilgileri")
    private TimeInfo timeInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmergencyVehicleInfo {
        @Schema(description = "Araç ID", example = "AMB-001")
        private String vehicleId;

        @Schema(description = "Araç tipi", example = "🚑 Ambulans")
        private String type;

        @Schema(description = "Durum", example = "Geçiş Yapıyor")
        private String status;

        @Schema(description = "Kavşak adı", example = "Kavşak-1 (Atatürk Bulvarı)")
        private String location;

        @Schema(description = "Yön", example = "Kuzey")
        private String direction;

        @Schema(description = "Öncelik seviyesi (1=en yüksek)", example = "1")
        private Integer priority;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IntersectionStatus {
        @Schema(description = "Kavşak ID", example = "1")
        private Long intersectionId;

        @Schema(description = "Kavşak adı", example = "Atatürk Bulvarı & Kızılay")
        private String name;

        @Schema(description = "Önceki faz", example = "🔴 KIRMIZI")
        private String previousPhase;

        @Schema(description = "Yeni faz", example = "🟢 YEŞİL")
        private String currentPhase;

        @Schema(description = "Süre (saniye)", example = "60")
        private Integer duration;

        @Schema(description = "Değişiklik nedeni", example = "ACİL DURUM ÖNCELİĞİ")
        private String reason;

        @Schema(description = "Görsel durum", example = "🟢🟢🟢 YEŞİL (Acil)")
        private String visual;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImpactAnalysis {
        @Schema(description = "Etkilenen kavşak sayısı", example = "3")
        private Integer affectedIntersections;

        @Schema(description = "Toplam bekleme süresi (saniye)", example = "45")
        private Integer totalWaitTime;

        @Schema(description = "Tahmini gecikme", example = "Minimal (10-15 saniye)")
        private String estimatedDelay;

        @Schema(description = "Öneri", example = "Normal trafiğe 60 saniye sonra dönülecek")
        private String recommendation;

        @Schema(description = "Trafik akışı etkisi", example = "Düşük - Sadece 1 kavşak yeşil")
        private String trafficFlow;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeInfo {
        @Schema(description = "Tespit zamanı", example = "2025-12-27T16:45:10")
        private LocalDateTime detectedAt;

        @Schema(description = "Tahmini geçiş süresi (saniye)", example = "60")
        private Integer estimatedClearTime;

        @Schema(description = "Normal moda dönüş zamanı", example = "2025-12-27T16:46:10")
        private LocalDateTime resumeNormalAt;
    }
}