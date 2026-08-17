package com.example.estilosapp.controller;

import com.example.estilosapp.dto.mercadopago.MpConnectStatusResponse;
import com.example.estilosapp.service.MercadoPagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mercadopago")
@RequiredArgsConstructor
public class MercadoPagoController {

    private final MercadoPagoService mercadoPagoService;

    // El frontend llama esto para saber si la barbería ya está conectada,
    // y si no, obtiene la URL a la que debe mandar al usuario.
    @GetMapping("/status/{barbershopId}")
    public ResponseEntity<MpConnectStatusResponse> getStatus(@PathVariable Long barbershopId) {
        return ResponseEntity.ok(mercadoPagoService.getStatus(barbershopId));
    }

    // Mercado Pago redirige aquí después de que el barbero autoriza.
    @GetMapping("/callback")
    public ResponseEntity<String> callback(
            @RequestParam String code,
            @RequestParam String state) {
        mercadoPagoService.handleCallback(code, state);
        // En producción esto debería redirigir a una pantalla de éxito en tu app/web,
        // por ahora regresamos un mensaje simple para confirmar en pruebas.
        return ResponseEntity.ok("Cuenta de Mercado Pago conectada exitosamente. Ya puedes cerrar esta ventana.");
    }

    @PostMapping("/disconnect/{barbershopId}")
    public ResponseEntity<Void> disconnect(@PathVariable Long barbershopId) {
        mercadoPagoService.disconnect(barbershopId);
        return ResponseEntity.noContent().build();
    }
}