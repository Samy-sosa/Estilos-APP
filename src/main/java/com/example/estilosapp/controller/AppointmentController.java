package com.example.estilosapp.controller;

import com.example.estilosapp.dto.appointment.AppointmentRequest;
import com.example.estilosapp.dto.appointment.AppointmentResponse;
import com.example.estilosapp.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponse> create(@Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> findAll(
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) Long barbershopId) {
        if (clientId != null) {
            return ResponseEntity.ok(appointmentService.findByClient(clientId));
        }
        if (barbershopId != null) {
            return ResponseEntity.ok(appointmentService.findByBarbershop(barbershopId));
        }
        throw new IllegalArgumentException("Debes especificar clientId o barbershopId");
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.cancel(id));
    }
}