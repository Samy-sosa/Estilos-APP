package com.example.estilosapp.service;

import com.example.estilosapp.config.MercadoPagoProperties;
import com.example.estilosapp.dto.mercadopago.MpConnectStatusResponse;
import com.example.estilosapp.dto.mercadopago.MpTokenResponse;
import com.example.estilosapp.entity.Barbershop;
import com.example.estilosapp.exception.ResourceNotFoundException;
import com.example.estilosapp.repository.BarbershopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MercadoPagoService {

    private static final String AUTH_BASE_URL = "https://auth.mercadopago.com.mx/authorization";
    private static final String TOKEN_URL = "https://api.mercadopago.com/oauth/token";

    private final MercadoPagoProperties mpProperties;
    private final BarbershopRepository barbershopRepository;
    private final RestClient restClient = RestClient.create();

    /**
     * Genera la URL a la que debe ser redirigido el dueño de la barbería
     * para autorizar la conexión de su cuenta de Mercado Pago.
     * El "state" lleva el barbershopId para poder identificarlo en el callback.
     */
    public String buildAuthorizationUrl(Long barbershopId) {
        String state = String.valueOf(barbershopId);
        String encodedRedirect = URLEncoder.encode(mpProperties.getRedirectUri(), StandardCharsets.UTF_8);

        return AUTH_BASE_URL +
                "?client_id=" + mpProperties.getClientId() +
                "&response_type=code" +
                "&platform_id=mp" +
                "&state=" + state +
                "&redirect_uri=" + encodedRedirect;
    }

    public MpConnectStatusResponse getStatus(Long barbershopId) {
        Barbershop barbershop = getBarbershopOrThrow(barbershopId);
        boolean connected = barbershop.getMpAccessToken() != null;

        return MpConnectStatusResponse.builder()
                .barbershopId(barbershopId)
                .connected(connected)
                .authorizationUrl(connected ? null : buildAuthorizationUrl(barbershopId))
                .build();
    }

    /**
     * Recibe el "code" que Mercado Pago manda al callback, lo intercambia
     * por access_token + refresh_token, y los guarda en la barbería identificada por "state".
     */
    public void handleCallback(String code, String state) {
        Long barbershopId = Long.valueOf(state);
        Barbershop barbershop = getBarbershopOrThrow(barbershopId);

        Map<String, Object> body = new HashMap<>();
        body.put("client_id", mpProperties.getClientId());
        body.put("client_secret", mpProperties.getClientSecret());
        body.put("grant_type", "authorization_code");
        body.put("code", code);
        body.put("redirect_uri", mpProperties.getRedirectUri());

        MpTokenResponse response = restClient.post()
                .uri(TOKEN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(MpTokenResponse.class);

        if (response == null || response.getAccess_token() == null) {
            throw new IllegalStateException("No se pudo obtener el access_token de Mercado Pago");
        }

        barbershop.setMpAccessToken(response.getAccess_token());
        barbershop.setMpRefreshToken(response.getRefresh_token());
        barbershopRepository.save(barbershop);
    }

    /**
     * Refresca el access_token usando el refresh_token guardado.
     * Los access_token de MP expiran, así que esto se llama antes de cada cobro
     * si detectamos que el token ya venció (o se puede llamar proactivamente).
     */
    public void refreshToken(Long barbershopId) {
        Barbershop barbershop = getBarbershopOrThrow(barbershopId);

        if (barbershop.getMpRefreshToken() == null) {
            throw new IllegalStateException("Esta barbería no tiene una conexión de Mercado Pago activa");
        }

        Map<String, Object> body = new HashMap<>();
        body.put("client_id", mpProperties.getClientId());
        body.put("client_secret", mpProperties.getClientSecret());
        body.put("grant_type", "refresh_token");
        body.put("refresh_token", barbershop.getMpRefreshToken());

        MpTokenResponse response = restClient.post()
                .uri(TOKEN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(MpTokenResponse.class);

        if (response == null || response.getAccess_token() == null) {
            throw new IllegalStateException("No se pudo refrescar el token de Mercado Pago");
        }

        barbershop.setMpAccessToken(response.getAccess_token());
        barbershop.setMpRefreshToken(response.getRefresh_token());
        barbershopRepository.save(barbershop);
    }

    public void disconnect(Long barbershopId) {
        Barbershop barbershop = getBarbershopOrThrow(barbershopId);
        barbershop.setMpAccessToken(null);
        barbershop.setMpRefreshToken(null);
        barbershopRepository.save(barbershop);
    }

    private Barbershop getBarbershopOrThrow(Long id) {
        return barbershopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Barbería no encontrada con id: " + id));
    }
}