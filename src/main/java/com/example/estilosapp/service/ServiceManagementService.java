package com.example.estilosapp.service;

import com.example.estilosapp.dto.service.ServiceRequest;
import com.example.estilosapp.dto.service.ServiceResponse;
import com.example.estilosapp.entity.Barbershop;
import com.example.estilosapp.exception.ResourceNotFoundException;
import com.example.estilosapp.repository.BarbershopRepository;
import com.example.estilosapp.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceManagementService {

    private final ServiceRepository serviceRepository;
    private final BarbershopRepository barbershopRepository;

    public List<ServiceResponse> findAll() {
        return serviceRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ServiceResponse findById(Long id) {
        return toResponse(getEntityOrThrow(id));
    }

    public List<ServiceResponse> findByBarbershop(Long barbershopId) {
        return serviceRepository.findByBarbershopId(barbershopId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ServiceResponse create(ServiceRequest request) {
        Barbershop barbershop = barbershopRepository.findById(request.getBarbershopId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Barbería no encontrada con id: " + request.getBarbershopId()));

        com.example.estilosapp.entity.Service service = com.example.estilosapp.entity.Service.builder()
                .barbershop(barbershop)
                .name(request.getName())
                .price(request.getPrice())
                .durationMinutes(request.getDurationMinutes())
                .build();

        return toResponse(serviceRepository.save(service));
    }

    public ServiceResponse update(Long id, ServiceRequest request) {
        com.example.estilosapp.entity.Service service = getEntityOrThrow(id);

        service.setName(request.getName());
        service.setPrice(request.getPrice());
        service.setDurationMinutes(request.getDurationMinutes());

        return toResponse(serviceRepository.save(service));
    }

    public void delete(Long id) {
        serviceRepository.delete(getEntityOrThrow(id));
    }

    private com.example.estilosapp.entity.Service getEntityOrThrow(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con id: " + id));
    }

    private ServiceResponse toResponse(com.example.estilosapp.entity.Service s) {
        return ServiceResponse.builder()
                .id(s.getId())
                .barbershopId(s.getBarbershop().getId())
                .barbershopName(s.getBarbershop().getName())
                .name(s.getName())
                .price(s.getPrice())
                .durationMinutes(s.getDurationMinutes())
                .build();
    }
}