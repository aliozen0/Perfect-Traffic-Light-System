package com.trafficlight.controller;

import com.trafficlight.dto.EmergencyRequest;
import com.trafficlight.dto.EmergencyResponse;
import com.trafficlight.entity.EmergencyEvent;
import com.trafficlight.entity.EmergencyVehicle;
import com.trafficlight.service.EmergencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emergency")
@RequiredArgsConstructor
@Tag(name = "🚨 Emergency System", description = "Acil Araç Öncelik Sistemi - Ambulans, İtfaiye, Polis")
public class EmergencyController {

    private final EmergencyService emergencyService;

    @Operation(
        summary = "🚑 Acil araç tespit et ve öncelik ver",
        description = """
            Acil araç (ambulans, itfaiye, polis) tespit edildiğinde bu endpoint çağrılır.
            Sistem otomatik olarak:
            • Ana kavşağı YEŞIL yapar (60 saniye)
            • Diğer kavşakları GÜVENLİK için KIRMIZI yapar
            • Trafik akışını optimize eder
            • Detaylı log kaydı tutar
            
            **Örnek Senaryo:**
            Ambulans Kavşak-1'e geldiğinde:
            - Kavşak-1: 🟢 YEŞİL (60s)
            - Kavşak-2: 🔴 KIRMIZI (güvenlik)
            - Kavşak-3: 🔴 KIRMIZI (güvenlik)
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "✅ Acil araç başarıyla tespit edildi ve öncelik verildi",
            content = @Content(schema = @Schema(implementation = EmergencyResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "❌ Geçersiz istek - Zorunlu alanlar eksik"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "❌ Sunucu hatası"
        )
    })
    @PostMapping("/trigger")
    public ResponseEntity<EmergencyResponse> triggerEmergency(
            @Valid @RequestBody 
            @Parameter(description = "Acil araç bilgileri", required = true)
            EmergencyRequest request) {
        
        EmergencyResponse response = emergencyService.triggerEmergency(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "✅ Acil durumu sonlandır",
        description = """
            Acil araç kavşaktan geçtiğinde bu endpoint çağrılır.
            Sistem:
            • Acil aracı "GEÇTİ" olarak işaretler
            • Tüm kavşakları normal moda döndürür
            • Sonlandırma kaydı oluşturur
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "✅ Acil durum başarıyla sonlandırıldı"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "❌ Acil araç bulunamadı"
        )
    })
    @PostMapping("/clear/{vehicleId}")
    public ResponseEntity<EmergencyResponse> clearEmergency(
            @PathVariable 
            @Parameter(description = "Acil araç ID'si", example = "1")
            Long vehicleId) {
        
        EmergencyResponse response = emergencyService.clearEmergency(vehicleId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "📋 Aktif acil durumları listele",
        description = """
            Şu anda aktif olan tüm acil durumları listeler.
            Durum: DETECTED veya IN_PROGRESS
            
            **Kullanım:**
            - Dashboard'da aktif durumları göster
            - Çakışan acil durumları tespit et
            - Önceliklendirme yap
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "✅ Aktif acil durumlar listesi"
        )
    })
    @GetMapping("/active")
    public ResponseEntity<List<EmergencyVehicle>> getActiveEmergencies() {
        List<EmergencyVehicle> emergencies = emergencyService.getActiveEmergencies();
        return ResponseEntity.ok(emergencies);
    }

    @Operation(
        summary = "🔍 Belirli kavşaktaki acil durumları getir",
        description = """
            Belirli bir kavşakta tespit edilen tüm acil durumları listeler.
            
            **Kullanım:**
            - Kavşak bazlı istatistik
            - Hangi kavşakta kaç acil durum var?
            - Sık kullanılan güzergahları tespit et
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "✅ Kavşak acil durumları listesi"
        )
    })
    @GetMapping("/intersection/{intersectionId}")
    public ResponseEntity<List<EmergencyVehicle>> getEmergenciesByIntersection(
            @PathVariable 
            @Parameter(description = "Kavşak ID'si", example = "1")
            Long intersectionId) {
        
        List<EmergencyVehicle> emergencies = 
            emergencyService.getEmergenciesByIntersection(intersectionId);
        return ResponseEntity.ok(emergencies);
    }

    @Operation(
        summary = "📊 Acil durum geçmişini getir",
        description = """
            Belirli bir acil aracın tüm olay geçmişini detaylı şekilde getirir.
            
            **İçerik:**
            - Tespit zamanı
            - Faz değişiklikleri
            - Hangi kavşaklar etkilendi
            - Ne kadar süre geçti
            - Başarılı/Başarısız
            
            **Kullanım:**
            - Acil araç rotası analizi
            - Performans ölçümü
            - Raporlama
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "✅ Acil durum geçmişi"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "❌ Acil araç bulunamadı"
        )
    })
    @GetMapping("/history/{vehicleId}")
    public ResponseEntity<List<EmergencyEvent>> getEmergencyHistory(
            @PathVariable 
            @Parameter(description = "Acil araç ID'si", example = "1")
            Long vehicleId) {
        
        List<EmergencyEvent> history = emergencyService.getEmergencyHistory(vehicleId);
        return ResponseEntity.ok(history);
    }

    @Operation(
        summary = "🚀 Hızlı Test - Ambulans Simülasyonu",
        description = """
            Test amaçlı hızlı ambulans simülasyonu.
            Otomatik olarak:
            - AMB-TEST-001 ID'li ambulans oluşturur
            - Kavşak-1'de tespit eder
            - Kuzey yönünden geliyor olarak işaretler
            - Acil durumu tetikler
            
            ⚠️ Sadece test/demo için kullanın!
            """
    )
    @PostMapping("/test/ambulance")
    public ResponseEntity<EmergencyResponse> testAmbulance() {
        EmergencyRequest testRequest = EmergencyRequest.builder()
                .vehicleId("AMB-TEST-001")
                .type(EmergencyVehicle.VehicleType.AMBULANCE)
                .intersectionId(1L)
                .direction(EmergencyVehicle.Direction.NORTH)
                .notes("Test simülasyonu - Demo amaçlı")
                .build();
        
        EmergencyResponse response = emergencyService.triggerEmergency(testRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "🔥 Hızlı Test - İtfaiye Simülasyonu",
        description = "Test amaçlı itfaiye aracı simülasyonu. Kavşak-2'de tespit edilir."
    )
    @PostMapping("/test/firetruck")
    public ResponseEntity<EmergencyResponse> testFireTruck() {
        EmergencyRequest testRequest = EmergencyRequest.builder()
                .vehicleId("FIRE-TEST-001")
                .type(EmergencyVehicle.VehicleType.FIRE_TRUCK)
                .intersectionId(2L)
                .direction(EmergencyVehicle.Direction.SOUTH)
                .notes("Test simülasyonu - İtfaiye demo")
                .build();
        
        EmergencyResponse response = emergencyService.triggerEmergency(testRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "🚓 Hızlı Test - Polis Simülasyonu",
        description = "Test amaçlı polis aracı simülasyonu. Kavşak-3'te tespit edilir."
    )
    @PostMapping("/test/police")
    public ResponseEntity<EmergencyResponse> testPolice() {
        EmergencyRequest testRequest = EmergencyRequest.builder()
                .vehicleId("POLICE-TEST-001")
                .type(EmergencyVehicle.VehicleType.POLICE)
                .intersectionId(3L)
                .direction(EmergencyVehicle.Direction.EAST)
                .notes("Test simülasyonu - Polis demo")
                .build();
        
        EmergencyResponse response = emergencyService.triggerEmergency(testRequest);
        return ResponseEntity.ok(response);
    }
}