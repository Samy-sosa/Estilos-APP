package com.example.estilosapp.repository;

import com.example.estilosapp.entity.Barber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BarberRepository extends JpaRepository<Barber, Long> {
    List<Barber> findByBarbershopIdAndIsActiveTrue(Long barbershopId);
}