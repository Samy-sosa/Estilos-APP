package com.example.estilosapp.repository;

import com.example.estilosapp.entity.AppointmentService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentServiceRepository extends JpaRepository<AppointmentService, Long> {
}