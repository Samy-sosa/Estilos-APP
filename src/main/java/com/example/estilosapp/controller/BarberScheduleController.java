package com.example.estilosapp.controller;

import com.example.estilosapp.dto.schedule.ScheduleRequest;
import com.example.estilosapp.dto.schedule.ScheduleResponse;
import com.example.estilosapp.service.BarberScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class BarberScheduleController {

    private final BarberScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<List<ScheduleResponse>> findAll(
            @RequestParam(required = false) Long barberId) {
        if (barberId != null) {
            return ResponseEntity.ok(scheduleService.findByBarber(barberId));
        }
        return ResponseEntity.ok(scheduleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ScheduleResponse> create(@Valid @RequestBody ScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleResponse> update(
            @PathVariable Long id, @Valid @RequestBody ScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        scheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}