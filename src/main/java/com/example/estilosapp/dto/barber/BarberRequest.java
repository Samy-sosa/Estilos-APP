package com.example.estilosapp.dto.barber;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BarberRequest {

    @NotNull(message = "El ID de la barbería es obligatorio")
    private Long barbershopId;

    private Long userId; // opcional: si el barbero también tiene cuenta de login

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private Boolean isActive;
}