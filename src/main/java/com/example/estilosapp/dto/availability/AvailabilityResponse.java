package com.example.estilosapp.dto.availability;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityResponse {
    private Long barberId;
    private LocalDate date;
    private Integer totalDurationMinutes;
    private List<Slot> availableSlots;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Slot {
        private LocalTime startTime;
        private LocalTime endTime;
    }
}