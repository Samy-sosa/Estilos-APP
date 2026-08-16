package com.example.estilosapp.service;

import com.example.estilosapp.dto.barbershop.BarbershopRequest;
import com.example.estilosapp.dto.barbershop.BarbershopResponse;
import com.example.estilosapp.entity.Barbershop;
import com.example.estilosapp.entity.User;
import com.example.estilosapp.exception.ResourceNotFoundException;
import com.example.estilosapp.repository.BarbershopRepository;
import com.example.estilosapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BarbershopService {

    private final BarbershopRepository barbershopRepository;
    private final UserRepository userRepository;

    public List<BarbershopResponse> findAll() {
        return barbershopRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public BarbershopResponse findById(Long id) {
        Barbershop barbershop = getEntityOrThrow(id);
        return toResponse(barbershop);
    }

    public List<BarbershopResponse> findByMunicipality(String municipality) {
        return barbershopRepository.findByMunicipalityIgnoreCase(municipality).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public BarbershopResponse create(BarbershopRequest request) {
        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario propietario no encontrado con id: " + request.getOwnerId()));

        if (barbershopRepository.findBySlug(request.getSlug()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una barbería con el slug: " + request.getSlug());
        }

        Barbershop barbershop = Barbershop.builder()
                .owner(owner)
                .name(request.getName())
                .slug(request.getSlug())
                .municipality(request.getMunicipality())
                .address(request.getAddress())
                .lat(request.getLat())
                .lng(request.getLng())
                .build();

        return toResponse(barbershopRepository.save(barbershop));
    }

    public BarbershopResponse update(Long id, BarbershopRequest request) {
        Barbershop barbershop = getEntityOrThrow(id);

        barbershop.setName(request.getName());
        barbershop.setMunicipality(request.getMunicipality());
        barbershop.setAddress(request.getAddress());
        barbershop.setLat(request.getLat());
        barbershop.setLng(request.getLng());
        // El slug y el owner no se actualizan por este endpoint a propósito

        return toResponse(barbershopRepository.save(barbershop));
    }

    public void delete(Long id) {
        Barbershop barbershop = getEntityOrThrow(id);
        barbershopRepository.delete(barbershop);
    }

    private Barbershop getEntityOrThrow(Long id) {
        return barbershopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Barbería no encontrada con id: " + id));
    }

    private BarbershopResponse toResponse(Barbershop b) {
        return BarbershopResponse.builder()
                .id(b.getId())
                .ownerId(b.getOwner().getId())
                .ownerName(b.getOwner().getName())
                .name(b.getName())
                .slug(b.getSlug())
                .municipality(b.getMunicipality())
                .address(b.getAddress())
                .lat(b.getLat())
                .lng(b.getLng())
                .mercadoPagoConnected(b.getMpAccessToken() != null)
                .createdAt(b.getCreatedAt())
                .build();
    }
}