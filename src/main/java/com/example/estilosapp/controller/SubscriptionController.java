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

    // Mercado Pago llama aquí cuando cambia el estado de una suscripción
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody Map<String, Object> payload) {
        // El payload real de MP trae distintos formatos según el tipo de evento;
        // esto es una implementación mínima para el MVP, la afinamos con pruebas reales
        Object data = payload.get("data");
        if (data instanceof Map<?, ?> dataMap && dataMap.get("id") != null) {
            String preapprovalId = dataMap.get("id").toString();
            // En un webhook real habría que consultar el estado actual vía GET /preapproval/{id}
            // porque el payload de la notificación no siempre trae el status directo.
            // Por ahora dejamos el hook listo para conectar esa consulta.
        }
        return ResponseEntity.ok().build();
    }
}