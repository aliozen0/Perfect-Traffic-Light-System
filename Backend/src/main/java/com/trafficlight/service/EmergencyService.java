package com.trafficlight.service;

import com.trafficlight.dto.EmergencyRequest;
import com.trafficlight.dto.EmergencyResponse;
import com.trafficlight.entity.EmergencyEvent;
import com.trafficlight.entity.EmergencyVehicle;
import com.trafficlight.repository.EmergencyEventRepository;
import com.trafficlight.repository.EmergencyVehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmergencyService {

    private final EmergencyVehicleRepository emergencyVehicleRepository;
    private final EmergencyEventRepository emergencyEventRepository;

    /**
     * Acil araç tespit edildiğinde tetiklenir
     */
    @Transactional
    public EmergencyResponse triggerEmergency(EmergencyRequest request) {
        log.info("🚨 Acil araç tespit edildi: {} - Kavşak: {}", 
                 request.getVehicleId(), request.getIntersectionId());

        // 1. Acil aracı kaydet
        EmergencyVehicle vehicle = createEmergencyVehicle(request);
        
        // 2. Event log'u oluştur
        EmergencyEvent detectionEvent = createDetectionEvent(vehicle);
        
        // 3. Kavşak fazlarını değiştir (yeşil yap)
        List<EmergencyResponse.IntersectionStatus> intersections = 
            changeIntersectionPhases(vehicle);
        
        // 4. Etki analizi yap
        EmergencyResponse.ImpactAnalysis impact = analyzeImpact(vehicle, intersections);
        
        // 5. Yapılan işlemleri listele
        List<String> actions = buildActionsList(vehicle, intersections);
        
        // 6. Response oluştur
        return buildResponse(vehicle, intersections, impact, actions);
    }

    /**
     * Acil aracı kaydet
     */
    private EmergencyVehicle createEmergencyVehicle(EmergencyRequest request) {
        EmergencyVehicle vehicle = EmergencyVehicle.builder()
                .vehicleId(request.getVehicleId())
                .type(request.getType())
                .status(EmergencyVehicle.EmergencyStatus.DETECTED)
                .currentIntersectionId(request.getIntersectionId())
                .direction(request.getDirection())
                .notes(request.getNotes())
                .build();
        
        return emergencyVehicleRepository.save(vehicle);
    }

    /**
     * Tespit event'i oluştur
     */
    private EmergencyEvent createDetectionEvent(EmergencyVehicle vehicle) {
        EmergencyEvent event = EmergencyEvent.builder()
                .emergencyVehicleId(vehicle.getId())
                .intersectionId(vehicle.getCurrentIntersectionId())
                .intersectionName("Kavşak-" + vehicle.getCurrentIntersectionId())
                .eventType(EmergencyEvent.EventType.EMERGENCY_DETECTED)
                .description(String.format("%s tespit edildi - %s yönünden geliyor",
                        vehicle.getType().getDisplayName(),
                        vehicle.getDirection().getDisplayName()))
                .success(true)
                .build();
        
        return emergencyEventRepository.save(event);
    }

    /**
     * Kavşak fazlarını değiştir
     */
    private List<EmergencyResponse.IntersectionStatus> changeIntersectionPhases(EmergencyVehicle vehicle) {
        List<EmergencyResponse.IntersectionStatus> statuses = new ArrayList<>();
        
        // Ana kavşağı yeşil yap
        EmergencyResponse.IntersectionStatus mainIntersection = 
            EmergencyResponse.IntersectionStatus.builder()
                .intersectionId(vehicle.getCurrentIntersectionId())
                .name("Kavşak-" + vehicle.getCurrentIntersectionId() + " (Atatürk Bulvarı)")
                .previousPhase("🔴 KIRMIZI")
                .currentPhase("🟢 YEŞİL")
                .duration(60)
                .reason("ACİL DURUM ÖNCELİĞİ")
                .visual("🟢🟢🟢 YEŞİL (Acil)")
                .build();
        statuses.add(mainIntersection);
        
        // Event log
        logPhaseChange(vehicle, mainIntersection);
        
        // Diğer kavşakları kırmızı yap (örnek)
        for (long i = 1; i <= 3; i++) {
            if (i != vehicle.getCurrentIntersectionId()) {
                EmergencyResponse.IntersectionStatus otherIntersection = 
                    EmergencyResponse.IntersectionStatus.builder()
                        .intersectionId(i)
                        .name("Kavşak-" + i)
                        .previousPhase("🟢 YEŞİL")
                        .currentPhase("🔴 KIRMIZI")
                        .duration(60)
                        .reason("GÜVENLİK PROTOKOLÜ")
                        .visual("🔴🔴🔴 KIRMIZI (Güvenlik)")
                        .build();
                statuses.add(otherIntersection);
                
                // Event log
                logPhaseChange(vehicle, otherIntersection);
            }
        }
        
        return statuses;
    }

    /**
     * Faz değişikliğini logla
     */
    private void logPhaseChange(EmergencyVehicle vehicle, 
                                 EmergencyResponse.IntersectionStatus status) {
        EmergencyEvent event = EmergencyEvent.builder()
                .emergencyVehicleId(vehicle.getId())
                .intersectionId(status.getIntersectionId())
                .intersectionName(status.getName())
                .eventType(status.getCurrentPhase().contains("YEŞİL") ? 
                          EmergencyEvent.EventType.GREEN_LIGHT_ACTIVATED :
                          EmergencyEvent.EventType.RED_LIGHT_ACTIVATED)
                .description(String.format("%s → %s (%s)",
                        status.getPreviousPhase(),
                        status.getCurrentPhase(),
                        status.getReason()))
                .previousPhase(status.getPreviousPhase())
                .newPhase(status.getCurrentPhase())
                .durationSeconds(status.getDuration())
                .success(true)
                .build();
        
        emergencyEventRepository.save(event);
    }

    /**
     * Etki analizi yap
     */
    private EmergencyResponse.ImpactAnalysis analyzeImpact(
            EmergencyVehicle vehicle,
            List<EmergencyResponse.IntersectionStatus> intersections) {
        
        int totalWaitTime = intersections.stream()
                .filter(i -> i.getCurrentPhase().contains("KIRMIZI"))
                .mapToInt(EmergencyResponse.IntersectionStatus::getDuration)
                .sum();
        
        return EmergencyResponse.ImpactAnalysis.builder()
                .affectedIntersections(intersections.size())
                .totalWaitTime(totalWaitTime)
                .estimatedDelay("Minimal (10-15 saniye)")
                .recommendation("Normal trafiğe 60 saniye sonra dönülecek")
                .trafficFlow("Düşük - Sadece 1 kavşak yeşil, diğerleri güvenlik için kırmızı")
                .build();
    }

    /**
     * Yapılan işlemleri listele
     */
    private List<String> buildActionsList(
            EmergencyVehicle vehicle,
            List<EmergencyResponse.IntersectionStatus> intersections) {
        
        List<String> actions = new ArrayList<>();
        
        for (EmergencyResponse.IntersectionStatus status : intersections) {
            if (status.getIntersectionId().equals(vehicle.getCurrentIntersectionId())) {
                actions.add(String.format("✅ %s: Anında yeşile çevrildi (%d saniye)",
                        status.getName(), status.getDuration()));
            } else {
                actions.add(String.format("🔴 %s: Güvenlik için kırmızıya alındı",
                        status.getName()));
            }
        }
        
        actions.add("⏰ Diğer tüm kavşaklar beklemeye alındı");
        
        return actions;
    }

    /**
     * Response oluştur
     */
    private EmergencyResponse buildResponse(
            EmergencyVehicle vehicle,
            List<EmergencyResponse.IntersectionStatus> intersections,
            EmergencyResponse.ImpactAnalysis impact,
            List<String> actions) {
        
        EmergencyResponse.EmergencyVehicleInfo vehicleInfo = 
            EmergencyResponse.EmergencyVehicleInfo.builder()
                .vehicleId(vehicle.getVehicleId())
                .type(vehicle.getType().getDisplayName())
                .status(vehicle.getStatus().getDisplayName())
                .location("Kavşak-" + vehicle.getCurrentIntersectionId() + " (Atatürk Bulvarı)")
                .direction(vehicle.getDirection().getDisplayName())
                .priority(vehicle.getPriorityLevel())
                .build();
        
        EmergencyResponse.TimeInfo timeInfo = 
            EmergencyResponse.TimeInfo.builder()
                .detectedAt(vehicle.getDetectedAt())
                .estimatedClearTime(60)
                .resumeNormalAt(vehicle.getDetectedAt().plusSeconds(60))
                .build();
        
        return EmergencyResponse.builder()
                .success(true)
                .message("🚑 Acil araç tespit edildi ve öncelik verildi")
                .vehicle(vehicleInfo)
                .actions(actions)
                .affectedIntersections(intersections)
                .impact(impact)
                .timeInfo(timeInfo)
                .build();
    }

    /**
     * Acil durumu sonlandır
     */
    @Transactional
    public EmergencyResponse clearEmergency(Long vehicleId) {
        EmergencyVehicle vehicle = emergencyVehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Acil araç bulunamadı: " + vehicleId));
        
        vehicle.setStatus(EmergencyVehicle.EmergencyStatus.CLEARED);
        vehicle.setClearedAt(LocalDateTime.now());
        emergencyVehicleRepository.save(vehicle);
        
        // Event log
        EmergencyEvent clearEvent = EmergencyEvent.builder()
                .emergencyVehicleId(vehicle.getId())
                .intersectionId(vehicle.getCurrentIntersectionId())
                .intersectionName("Kavşak-" + vehicle.getCurrentIntersectionId())
                .eventType(EmergencyEvent.EventType.EMERGENCY_CLEARED)
                .description("Acil araç kavşaktan geçti, normal moda dönülüyor")
                .success(true)
                .build();
        emergencyEventRepository.save(clearEvent);
        
        log.info("✅ Acil durum sonlandırıldı: {}", vehicle.getVehicleId());
        
        return EmergencyResponse.builder()
                .success(true)
                .message("✅ Acil araç geçti, kavşaklar normal moda döndü")
                .build();
    }

    /**
     * Aktif acil durumları listele
     */
    public List<EmergencyVehicle> getActiveEmergencies() {
        return emergencyVehicleRepository.findActiveEmergencies();
    }

    /**
     * Belirli bir kavşaktaki acil durumları getir
     */
    public List<EmergencyVehicle> getEmergenciesByIntersection(Long intersectionId) {
        return emergencyVehicleRepository.findByCurrentIntersectionId(intersectionId);
    }

    /**
     * Acil durum geçmişini getir
     */
    public List<EmergencyEvent> getEmergencyHistory(Long vehicleId) {
        return emergencyEventRepository.findByEmergencyVehicleIdOrderByCreatedAtDesc(vehicleId);
    }
}