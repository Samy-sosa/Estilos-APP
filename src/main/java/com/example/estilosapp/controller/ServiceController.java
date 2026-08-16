package com.example.estilosapp.controller;

import com.example.estilosapp.dto.service.ServiceRequest;
import com.example.estilosapp.dto.service.ServiceResponse;
import com.example.estilosapp.service.ServiceManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceManagementService serviceManagementService;

    @GetMapping
    public ResponseEntity<List<ServiceResponse>> findAll(
            @RequestParam(required = false) Long barbershopId) {
        if (barbershopId != null) {
            return ResponseEntity.ok(serviceManagementService.findByBarbershop(barbershopId));
        }
        return ResponseEntity.ok(serviceManagementService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceManagementService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ServiceResponse> create(@Valid @RequestBody ServiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceManagementService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> update(
            @PathVariable Long id, @Valid @RequestBody ServiceRequest request) {
        return ResponseEntity.ok(serviceManagementService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceManagementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}