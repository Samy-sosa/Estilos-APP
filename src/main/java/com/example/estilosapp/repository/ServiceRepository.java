package com.example.estilosapp.repository;

import com.example.estilosapp.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByBarbershopId(Long barbershopId);
}