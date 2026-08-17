package com.example.estilosapp.dto.mercadopago;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MpConnectStatusResponse {
    private Long barbershopId;
    private Boolean connected;
    private String authorizationUrl; // solo si connected = false
}