package com.trafficlight.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Trafik optimizasyon request'i")
public class OptimizationRequest {

    @NotNull(message = "Kavşak ID'si belirtilmelidir")
    @Schema(description = "Optimize edilecek kavşak ID'si", example = "1")
    private Long intersectionId;

    @NotNull(message = "Araç sayısı belirtilmelidir")
    @Min(value = 0, message = "Araç sayısı negatif olamaz")
    @Schema(description = "Mevcut araç sayısı", example = "45")
    private Integer vehicleCount;

    @Schema(description = "Ortalama hız (km/h)", example = "25.5")
    private Double averageSpeed;

    @Schema(description = "Manuel kural ID'si (opsiyonel)", example = "1")
    private Long ruleId;
}