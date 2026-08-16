package com.example.estilosapp.controller;

import com.example.estilosapp.dto.barber.BarberRequest;
import com.example.estilosapp.dto.barber.BarberResponse;
import com.example.estilosapp.service.BarberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/barbers")
@RequiredArgsConstructor
public class BarberController {

    private final BarberService barberService;

    @GetMapping
    public ResponseEntity<List<BarberResponse>> findAll(
            @RequestParam(required = false) Long barbershopId) {
        if (barbershopId != null) {
            return ResponseEntity.ok(barberService.findActiveByBarbershop(barbershopId));
        }
        return ResponseEntity.ok(barberService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BarberResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(barberService.findById(id));
    }

    @PostMapping
    public ResponseEntity<BarberResponse> create(@Valid @RequestBody BarberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(barberService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BarberResponse> update(
            @PathVariable Long id, @Valid @RequestBody BarberRequest request) {
        return ResponseEntity.ok(barberService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        barberService.delete(id);
        return ResponseEntity.noContent().build();
    }
}