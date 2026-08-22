package com.example.estilosapp.controller;

import com.example.estilosapp.dto.subscription.SubscriptionRequest;
import com.example.estilosapp.dto.subscription.SubscriptionResponse;
import com.example.estilosapp.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<SubscriptionResponse> subscribe(@Valid @RequestBody SubscriptionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subscriptionService.subscribe(request));
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionResponse>> findByBarbershop(@RequestParam Long barbershopId) {
        return ResponseEntity.ok(subscriptionService.findByBarbershop(barbershopId));
    }

    // Mercado Pago llama aquí cuando cambia el estado de una suscripción.
    // El payload solo trae el tipo de evento y el ID del recurso afectado.
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody Map<String, Object> payload) {
        Object type = payload.get("type");
        Object data = payload.get("data");

        if ("subscription_preapproval".equals(type) && data instanceof Map<?, ?> dataMap) {
            Object id = dataMap.get("id");
            if (id != null) {
                subscriptionService.refreshFromMercadoPago(id.toString());
            }
        }

        // Siempre respondemos 200 rápido; si no, Mercado Pago reintenta la notificación
        return ResponseEntity.ok().build();
    }
}