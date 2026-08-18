package com.example.estilosapp.dto.subscription;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubscriptionRequest {

    @NotNull(message = "El ID de la barbería es obligatorio")
    private Long barbershopId;

    @NotNull(message = "El ID del plan es obligatorio")
    private Long planId;

    // Email del dueño de la barbería, requerido por Mercado Pago para el checkout de suscripción
    @NotBlank(message = "El email del pagador es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    private String payerEmail;
}