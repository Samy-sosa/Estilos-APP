package com.example.estilosapp.dto.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceResponse {
    private Long id;
    private Long barbershopId;
    private String barbershopName;
    private String name;
    private BigDecimal price;
    private Integer durationMinutes;
}