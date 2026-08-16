package com.example.estilosapp.dto.barber;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BarberResponse {
    private Long id;
    private Long barbershopId;
    private String barbershopName;
    private Long userId;
    private String name;
    private Boolean isActive;
}