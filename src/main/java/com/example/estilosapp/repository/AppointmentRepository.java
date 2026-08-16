package com.example.estilosapp.repository;

import com.example.estilosapp.entity.Appointment;
import com.example.estilosapp.entity.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByBarbershopId(Long barbershopId);

    List<Appointment> findByClientId(Long clientId);

    // Clave para el algoritmo de slots: citas de un barbero en una fecha,
    // solo las que "ocupan" horario (confirmadas o pendientes de pago)
    @Query("SELECT a FROM Appointment a " +
            "WHERE a.barber.id = :barberId " +
            "AND a.date = :date " +
            "AND a.status IN (com.example.estilosapp.entity.enums.AppointmentStatus.CONFIRMED, " +
            "                 com.example.estilosapp.entity.enums.AppointmentStatus.PENDING_PAYMENT)")
    List<Appointment> findActiveAppointmentsByBarberAndDate(
            @Param("barberId") Long barberId,
            @Param("date") LocalDate date
    );
}