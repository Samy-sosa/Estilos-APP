package com.example.estilosapp.controller;

import com.example.estilosapp.dto.barbershop.BarbershopRequest;
import com.example.estilosapp.dto.barbershop.BarbershopResponse;
import com.example.estilosapp.service.BarbershopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/barbershops")
@RequiredArgsConstructor
public class BarbershopController {

    private final BarbershopService barbershopService;

    @GetMapping
    public ResponseEntity<List<BarbershopResponse>> findAll(
            @RequestParam(required = false) String municipality) {
        if (municipality != null && !municipality.isBlank()) {
            return ResponseEntity.ok(barbershopService.findByMunicipality(municipality));
        }
        return ResponseEntity.ok(barbershopService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BarbershopResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(barbershopService.findById(id));
    }

    @PostMapping
    public ResponseEntity<BarbershopResponse> create(@Valid @RequestBody BarbershopRequest request) {
        BarbershopResponse created = barbershopService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BarbershopResponse> update(
            @PathVariable Long id, @Valid @RequestBody BarbershopRequest request) {
        return ResponseEntity.ok(barbershopService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        barbershopService.delete(id);
        return ResponseEntity.noContent().build();
    }
}