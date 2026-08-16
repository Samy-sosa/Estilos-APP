package com.example.estilosapp.dto.appointment;

import com.example.estilosapp.entity.enums.PaymentMethod;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class AppointmentRequest {

    @NotNull(message = "El ID de la barbería es obligatorio")
    private Long barbershopId;

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clientId;

    @NotNull(message = "El ID del barbero es obligatorio")
    private Long barberId;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate date;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime startTime;

    @NotEmpty(message = "Debe seleccionar al menos un servicio")
    private List<Long> serviceIds;

    @NotNull(message = "El método de pago es obligatorio")
    private PaymentMethod paymentMethod;
}