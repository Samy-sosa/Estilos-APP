package com.example.estilosapp.dto.service;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServiceRequest {

    @NotNull(message = "El ID de la barbería es obligatorio")
    private Long barbershopId;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal price;

    @NotNull(message = "La duración es obligatoria")
    @Min(value = 5, message = "La duración mínima es de 5 minutos")
    private Integer durationMinutes;
}