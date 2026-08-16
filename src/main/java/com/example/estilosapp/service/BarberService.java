package com.example.estilosapp.service;

import com.example.estilosapp.dto.barber.BarberRequest;
import com.example.estilosapp.dto.barber.BarberResponse;
import com.example.estilosapp.entity.Barber;
import com.example.estilosapp.entity.Barbershop;
import com.example.estilosapp.entity.User;
import com.example.estilosapp.exception.ResourceNotFoundException;
import com.example.estilosapp.repository.BarberRepository;
import com.example.estilosapp.repository.BarbershopRepository;
import com.example.estilosapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BarberService {

    private final BarberRepository barberRepository;
    private final BarbershopRepository barbershopRepository;
    private final UserRepository userRepository;

    public List<BarberResponse> findAll() {
        return barberRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public BarberResponse findById(Long id) {
        return toResponse(getEntityOrThrow(id));
    }

    public List<BarberResponse> findActiveByBarbershop(Long barbershopId) {
        return barberRepository.findByBarbershopIdAndIsActiveTrue(barbershopId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public BarberResponse create(BarberRequest request) {
        Barbershop barbershop = barbershopRepository.findById(request.getBarbershopId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Barbería no encontrada con id: " + request.getBarbershopId()));

        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Usuario no encontrado con id: " + request.getUserId()));
        }

        Barber barber = Barber.builder()
                .barbershop(barbershop)
                .user(user)
                .name(request.getName())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        return toResponse(barberRepository.save(barber));
    }

    public BarberResponse update(Long id, BarberRequest request) {
        Barber barber = getEntityOrThrow(id);

        barber.setName(request.getName());
        if (request.getIsActive() != null) {
            barber.setIsActive(request.getIsActive());
        }

        return toResponse(barberRepository.save(barber));
    }

    public void delete(Long id) {
        barberRepository.delete(getEntityOrThrow(id));
    }

    private Barber getEntityOrThrow(Long id) {
        return barberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Barbero no encontrado con id: " + id));
    }

    private BarberResponse toResponse(Barber b) {
        return BarberResponse.builder()
                .id(b.getId())
                .barbershopId(b.getBarbershop().getId())
                .barbershopName(b.getBarbershop().getName())
                .userId(b.getUser() != null ? b.getUser().getId() : null)
                .name(b.getName())
                .isActive(b.getIsActive())
                .build();
    }
}