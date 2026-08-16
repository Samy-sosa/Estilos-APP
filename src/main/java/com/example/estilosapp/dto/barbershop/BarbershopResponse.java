package com.example.estilosapp.dto.barbershop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BarbershopResponse {
    private Long id;
    private Long ownerId;
    private String ownerName;
    private String name;
    private String slug;
    private String municipality;
    private String address;
    private BigDecimal lat;
    private BigDecimal lng;
    private Boolean mercadoPagoConnected; // true si ya tiene mp_access_token
    private LocalDateTime createdAt;
}