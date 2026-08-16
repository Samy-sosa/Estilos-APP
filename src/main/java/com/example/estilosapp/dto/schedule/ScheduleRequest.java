package com.example.estilosapp.dto.schedule;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
public class ScheduleRequest {

    @NotNull(message = "El ID del barbero es obligatorio")
    private Long barberId;

    @NotNull(message = "El día de la semana es obligatorio")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime startTime;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime endTime;

    private LocalTime breakStart;
    private LocalTime breakEnd;
}