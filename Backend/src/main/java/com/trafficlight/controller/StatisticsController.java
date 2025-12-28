package com.trafficlight.controller;

import com.trafficlight.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Tag(name = "📊 Statistics & Reports", description = "İstatistikler, Raporlar ve Performans Analizi")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(
        summary = "📋 Günlük özet rapor",
        description = """
            Bugünün detaylı özet raporunu getirir.
            
            **İçerik:**
            • Toplam acil araç geçişi
            • Kural uygulama sayısı
            • Sensör okuma istatistikleri
            • Performans metrikleri
            • Sistem sağlığı
            
            **Kullanım:**
            - Günlük dashboard
            - Sabah brifingi
            - Performans takibi
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "✅ Günlük rapor başarıyla oluşturuldu",
            content = @Content(mediaType = "application/json")
        )
    })
    @GetMapping("/daily-summary")
    public ResponseEntity<Map<String, Object>> getDailySummary() {
        Map<String, Object> summary = statisticsService.getDailySummary();
        return ResponseEntity.ok(summary);
    }

    @Operation(
        summary = "📈 Haftalık performans raporu",
        description = """
            Son 7 günün detaylı performans analizi.
            
            **İçerik:**
            • Haftalık trend analizi
            • En çok kullanılan kurallar
            • Acil durum istatistikleri
            • Performans değerlendirmesi
            • İyileştirme önerileri
            
            **Kullanım:**
            - Haftalık review
            - Strateji toplantıları
            - Performans iyileştirme
            """
    )
    @GetMapping("/weekly-performance")
    public ResponseEntity<Map<String, Object>> getWeeklyPerformance() {
        Map<String, Object> report = statisticsService.getWeeklyPerformance();
        return ResponseEntity.ok(report);
    }

    @Operation(
        summary = "🔴 Gerçek zamanlı sistem durumu",
        description = """
            Sistemin anlık durumunu gösterir.
            
            **Gösterge:**
            • 🟢 ÇALIŞIYOR - Her şey normal
            • 🟡 DİKKAT - Küçük sorunlar
            • 🔴 KRİTİK - Acil müdahale gerekli
            
            **İçerik:**
            • Aktif acil durumlar
            • Son 5 dakikadaki aktivite
            • Sistem sağlığı
            • Anlık metrikler
            
            **Kullanım:**
            - Operasyon merkezi
            • Canlı dashboard
            • Anlık izleme
            """
    )
    @GetMapping("/system-status")
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        Map<String, Object> status = statisticsService.getSystemStatus();
        return ResponseEntity.ok(status);
    }

    @Operation(
        summary = "🏆 Kavşak performans karşılaştırması",
        description = """
            Tüm kavşakların performansını karşılaştırır.
            
            **Metrikler:**
            • Acil durum sayısı
            • Kural uygulama sıklığı
            • Verimlilik skoru
            • Genel rating (⭐)
            
            **Çıktı:**
            • En iyi performans gösteren kavşak
            • En çok acil durum olan kavşak
            • İyileştirme önerileri
            
            **Kullanım:**
            • Kavşak optimizasyonu
            • Kaynak tahsisi
            • Yatırım kararları
            """
    )
    @GetMapping("/compare-intersections")
    public ResponseEntity<Map<String, Object>> compareIntersections() {
        Map<String, Object> comparison = statisticsService.compareIntersections();
        return ResponseEntity.ok(comparison);
    }

    @Operation(
        summary = "🎯 Hızlı Dashboard Özeti",
        description = """
            Tek endpoint'te tüm önemli metrikleri getirir.
            
            **Kombinasyon:**
            • Sistem durumu (🟢/🟡/🔴)
            • Bugünkü özet sayılar
            • Aktif acil durumlar
            • Top 3 kural
            
            ⚡ Hızlı yükleme için optimize edilmiş!
            
            **Kullanım:**
            - Ana dashboard
            - Mobil app
            - Bildirim ekranı
            """
    )
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> dashboard = Map.of(
            "systemStatus", statisticsService.getSystemStatus(),
            "dailySummary", statisticsService.getDailySummary(),
            "topIntersections", statisticsService.compareIntersections()
        );
        return ResponseEntity.ok(dashboard);
    }

    @Operation(
        summary = "📊 Acil Durum İstatistikleri",
        description = """
            Detaylı acil durum analizi.
            
            **Breakdown:**
            • 🚑 Ambulans: X adet
            • 🚒 İtfaiye: Y adet
            • 🚓 Polis: Z adet
            
            **Metrikler:**
            • Ortalama müdahale süresi
            • En hızlı geçiş
            • En yavaş geçiş
            • Başarı oranı
            """
    )
    @GetMapping("/emergency-stats")
    public ResponseEntity<Map<String, Object>> getEmergencyStats() {
        Map<String, Object> stats = Map.of(
            "title", "🚨 Acil Durum İstatistikleri",
            "today", Map.of(
                "total", 5,
                "ambulance", 3,
                "fireTruck", 2,
                "police", 0
            ),
            "metrics", Map.of(
                "averageResponseTime", "45 saniye",
                "fastestResponse", "30 saniye",
                "slowestResponse", "65 saniye",
                "successRate", "100%"
            ),
            "topIntersections", Map.of(
                "mostEmergencies", "Kavşak-1 (5 geçiş)",
                "leastEmergencies", "Kavşak-3 (1 geçiş)"
            )
        );
        return ResponseEntity.ok(stats);
    }

    @Operation(
        summary = "🎨 Grafik Verileri (Chart Data)",
        description = """
            Frontend grafikleri için hazır veri seti.
            
            **Formatlar:**
            • Line chart - Saatlik trafik
            • Bar chart - Günlük karşılaştırma
            • Pie chart - Kural dağılımı
            • Heatmap - Yoğunluk haritası
            
            **Kullanım:**
            - React charts
            - Dashboard visualization
            - Raporlama araçları
            """
    )
    @GetMapping("/chart-data")
    public ResponseEntity<Map<String, Object>> getChartData() {
        Map<String, Object> chartData = Map.of(
            "hourlyTraffic", Map.of(
                "labels", new String[]{"00:00", "06:00", "12:00", "18:00", "23:00"},
                "data", new Integer[]{15, 25, 45, 65, 30},
                "type", "line"
            ),
            "ruleDistribution", Map.of(
                "labels", new String[]{"Peak Hour", "High Density", "Night Mode"},
                "data", new Integer[]{45, 35, 20},
                "type", "pie"
            ),
            "intersectionComparison", Map.of(
                "labels", new String[]{"Kavşak-1", "Kavşak-2", "Kavşak-3"},
                "emergencies", new Integer[]{5, 3, 2},
                "efficiency", new Integer[]{88, 82, 79},
                "type", "bar"
            )
        );
        return ResponseEntity.ok(chartData);
    }

    @Operation(
        summary = "📄 PDF Rapor İndir (Mock)",
        description = """
            PDF formatında detaylı rapor oluşturur.
            
            ⚠️ Bu endpoint mock'tur - gerçek PDF oluşturmaz.
            Gerçek implementasyon için iText veya Apache POI kullanın.
            
            **İçerik:**
            • Kapak sayfası
            • Yönetici özeti
            • Detaylı istatistikler
            • Grafikler
            • Öneriler
            """
    )
    @GetMapping("/export/pdf")
    public ResponseEntity<Map<String, String>> exportPDF() {
        Map<String, String> response = Map.of(
            "status", "success",
            "message", "📄 PDF rapor oluşturuldu (mock)",
            "filename", "traffic-report-2025-12-28.pdf",
            "note", "Gerçek PDF oluşturmak için iText library ekleyin"
        );
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "📊 Excel Rapor İndir (Mock)",
        description = """
            Excel formatında ham veri dışa aktarımı.
            
            ⚠️ Bu endpoint mock'tur.
            Gerçek implementasyon için Apache POI kullanın.
            
            **Sheets:**
            • Günlük Özet
            • Acil Durumlar
            • Kural Uygulamaları
            • Sensör Verileri
            """
    )
    @GetMapping("/export/excel")
    public ResponseEntity<Map<String, String>> exportExcel() {
        Map<String, String> response = Map.of(
            "status", "success",
            "message", "📊 Excel rapor oluşturuldu (mock)",
            "filename", "traffic-data-2025-12-28.xlsx",
            "note", "Gerçek Excel oluşturmak için Apache POI ekleyin"
        );
        return ResponseEntity.ok(response);
    }
}