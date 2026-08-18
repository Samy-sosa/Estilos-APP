package com.example.estilosapp.dto.mercadopago;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MpPreapprovalResponse {
    private String id;
    private String status;
    private String init_point;
}