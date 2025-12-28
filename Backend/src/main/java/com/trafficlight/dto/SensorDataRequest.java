package com.trafficlight.dto;

import com.trafficlight.entity.TrafficSensor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
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
@Schema(description = "Sensör veri gönderme request'i")
public class SensorDataRequest {

    @NotBlank(message = "Sensör ID'si boş olamaz")
    @Schema(description = "Sensör benzersiz kimliği", example = "SENSOR-001")
    private String sensorId;

    @NotNull(message = "Kavşak ID'si belirtilmelidir")
    @Schema(description = "Sensörün bulunduğu kavşak", example = "1")
    private Long intersectionId;

    @NotNull(message = "Yön belirtilmelidir")
    @Schema(description = "Sensörün izlediği yön", example = "NORTH")
    private TrafficSensor.Direction direction;

    @NotNull(message = "Araç sayısı belirtilmelidir")
    @Min(value = 0, message = "Araç sayısı negatif olamaz")
    @Schema(description = "Tespit edilen araç sayısı", example = "45")
    private Integer vehicleCount;

    @Schema(description = "Ortalama araç hızı (km/h)", example = "35.5")
    private Double averageSpeed;
}