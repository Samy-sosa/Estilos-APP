package com.example.estilosapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "mercadopago")
public class MercadoPagoProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String platformAccessToken;
}