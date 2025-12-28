package com.trafficlight.dto;

import com.trafficlight.entity.EmergencyVehicle;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Acil araç tespiti için request")
public class EmergencyRequest {

    @NotBlank(message = "Araç ID'si boş olamaz")
    @Schema(description = "Acil aracın benzersiz kimliği", example = "AMB-001")
    private String vehicleId;

    @NotNull(message = "Araç tipi belirtilmelidir")
    @Schema(description = "Acil araç tipi", example = "AMBULANCE")
    private EmergencyVehicle.VehicleType type;

    @NotNull(message = "Kavşak ID'si belirtilmelidir")
    @Schema(description = "Aracın bulunduğu kavşak ID'si", example = "1")
    private Long intersectionId;

    @NotNull(message = "Yön belirtilmelidir")
    @Schema(description = "Aracın geldiği yön", example = "NORTH")
    private EmergencyVehicle.Direction direction;

    @Schema(description = "Ek notlar", example = "Kritik durum - hızlı geçiş gerekli")
    private String notes;
}