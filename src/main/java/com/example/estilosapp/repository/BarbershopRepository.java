package com.example.estilosapp.repository;

import com.example.estilosapp.entity.Barbershop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BarbershopRepository extends JpaRepository<Barbershop, Long> {
    Optional<Barbershop> findBySlug(String slug);
    List<Barbershop> findByMunicipalityIgnoreCase(String municipality);
    List<Barbershop> findByOwnerId(Long ownerId);
}