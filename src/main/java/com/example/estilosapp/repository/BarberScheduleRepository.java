package com.example.estilosapp.repository;

import com.example.estilosapp.entity.BarberSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface BarberScheduleRepository extends JpaRepository<BarberSchedule, Long> {
    List<BarberSchedule> findByBarberId(Long barberId);
    Optional<BarberSchedule> findByBarberIdAndDayOfWeek(Long barberId, DayOfWeek dayOfWeek);
}