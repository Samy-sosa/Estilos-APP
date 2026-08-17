package com.example.estilosapp.dto.mercadopago;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MpTokenResponse {
    private String access_token;
    private String token_type;
    private Integer expires_in;
    private String scope;
    private Long user_id;
    private String refresh_token;
    private Long public_key;
}