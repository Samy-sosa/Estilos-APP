package com.example.estilosapp.service;

import com.example.estilosapp.dto.availability.AvailabilityResponse;
import com.example.estilosapp.entity.Appointment;
import com.example.estilosapp.entity.BarberSchedule;
import com.example.estilosapp.entity.Service;
import com.example.estilosapp.exception.ResourceNotFoundException;
import com.example.estilosapp.repository.AppointmentRepository;
import com.example.estilosapp.repository.BarberScheduleRepository;
import com.example.estilosapp.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AvailabilityService {

    private final BarberScheduleRepository scheduleRepository;
    private final AppointmentRepository appointmentRepository;
    private final ServiceRepository serviceRepository;

    public AvailabilityResponse calculateAvailability(Long barberId, LocalDate date, List<Long> serviceIds) {

        // 1. Obtener la jornada del barbero para ese día de la semana
        BarberSchedule schedule = scheduleRepository
                .findByBarberIdAndDayOfWeek(barberId, date.getDayOfWeek())
                .orElse(null);

        // Si el barbero no trabaja ese día, no hay slots disponibles
        if (schedule == null) {
            return AvailabilityResponse.builder()
                    .barberId(barberId)
                    .date(date)
                    .totalDurationMinutes(0)
                    .availableSlots(List.of())
                    .build();
        }

        // 2. Duración total de los servicios seleccionados
        List<Service> services = serviceRepository.findAllById(serviceIds);
        if (services.size() != serviceIds.size()) {
            throw new ResourceNotFoundException("Uno o más servicios no existen");
        }
        int totalDuration = services.stream()
                .mapToInt(Service::getDurationMinutes)
                .sum();

        if (totalDuration <= 0) {
            throw new IllegalArgumentException("La duración total de los servicios debe ser mayor a 0");
        }

        // 3. Citas activas del barbero en esa fecha (ocupan horario)
        List<Appointment> busyAppointments =
                appointmentRepository.findActiveAppointmentsByBarberAndDate(barberId, date);

        // 4. Generar bloques del tamaño del servicio y descartar colisiones
        List<AvailabilityResponse.Slot> availableSlots = new ArrayList<>();
        LocalTime cursor = schedule.getStartTime();

        while (true) {
            LocalTime blockEnd = cursor.plusMinutes(totalDuration);

            // Si el bloque se pasa de la hora de salida, terminamos
            if (blockEnd.isAfter(schedule.getEndTime())) {
                break;
            }

            boolean collidesWithBreak = overlapsBreak(cursor, blockEnd, schedule);
            boolean collidesWithAppointment = overlapsAnyAppointment(cursor, blockEnd, busyAppointments);

            if (!collidesWithBreak && !collidesWithAppointment) {
                availableSlots.add(AvailabilityResponse.Slot.builder()
                        .startTime(cursor)
                        .endTime(blockEnd)
                        .build());
            }

            cursor = blockEnd; // avanzamos al siguiente bloque
        }

        // 5. Retornar el resultado
        return AvailabilityResponse.builder()
                .barberId(barberId)
                .date(date)
                .totalDurationMinutes(totalDuration)
                .availableSlots(availableSlots)
                .build();
    }

    private boolean overlapsBreak(LocalTime blockStart, LocalTime blockEnd, BarberSchedule schedule) {
        if (schedule.getBreakStart() == null || schedule.getBreakEnd() == null) {
            return false;
        }
        return timesOverlap(blockStart, blockEnd, schedule.getBreakStart(), schedule.getBreakEnd());
    }

    private boolean overlapsAnyAppointment(LocalTime blockStart, LocalTime blockEnd, List<Appointment> appointments) {
        return appointments.stream().anyMatch(a ->
                timesOverlap(blockStart, blockEnd, a.getStartTime(), a.getEndTime())
        );
    }

    // Dos intervalos [aStart, aEnd) y [bStart, bEnd) se traslapan si aStart < bEnd Y bStart < aEnd
    private boolean timesOverlap(LocalTime aStart, LocalTime aEnd, LocalTime bStart, LocalTime bEnd) {
        return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
    }
}