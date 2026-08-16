package com.example.estilosapp.dto.barbershop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BarbershopRequest {

    @NotNull(message = "El ID del propietario es obligatorio")
    private Long ownerId;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El slug es obligatorio")
    private String slug;

    @NotBlank(message = "El municipio es obligatorio")
    private String municipality;

    private String address;
    private BigDecimal lat;
    private BigDecimal lng;
}