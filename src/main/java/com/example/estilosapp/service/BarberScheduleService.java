package com.example.estilosapp.service;

import com.example.estilosapp.dto.schedule.ScheduleRequest;
import com.example.estilosapp.dto.schedule.ScheduleResponse;
import com.example.estilosapp.entity.Barber;
import com.example.estilosapp.entity.BarberSchedule;
import com.example.estilosapp.exception.ResourceNotFoundException;
import com.example.estilosapp.repository.BarberRepository;
import com.example.estilosapp.repository.BarberScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BarberScheduleService {

    private final BarberScheduleRepository scheduleRepository;
    private final BarberRepository barberRepository;

    public List<ScheduleResponse> findAll() {
        return scheduleRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ScheduleResponse findById(Long id) {
        return toResponse(getEntityOrThrow(id));
    }

    public List<ScheduleResponse> findByBarber(Long barberId) {
        return scheduleRepository.findByBarberId(barberId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ScheduleResponse create(ScheduleRequest request) {
        Barber barber = barberRepository.findById(request.getBarberId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Barbero no encontrado con id: " + request.getBarberId()));

        validateTimes(request);

        BarberSchedule schedule = BarberSchedule.builder()
                .barber(barber)
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .breakStart(request.getBreakStart())
                .breakEnd(request.getBreakEnd())
                .build();

        return toResponse(scheduleRepository.save(schedule));
    }

    public ScheduleResponse update(Long id, ScheduleRequest request) {
        BarberSchedule schedule = getEntityOrThrow(id);

        validateTimes(request);

        schedule.setDayOfWeek(request.getDayOfWeek());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setBreakStart(request.getBreakStart());
        schedule.setBreakEnd(request.getBreakEnd());

        return toResponse(scheduleRepository.save(schedule));
    }

    public void delete(Long id) {
        scheduleRepository.delete(getEntityOrThrow(id));
    }

    private void validateTimes(ScheduleRequest request) {
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin");
        }
        boolean hasBreakStart = request.getBreakStart() != null;
        boolean hasBreakEnd = request.getBreakEnd() != null;
        if (hasBreakStart != hasBreakEnd) {
            throw new IllegalArgumentException("Debe especificar ambas horas de descanso o ninguna");
        }
        if (hasBreakStart && !request.getBreakStart().isBefore(request.getBreakEnd())) {
            throw new IllegalArgumentException("La hora de inicio del descanso debe ser anterior a la de fin");
        }
    }

    private BarberSchedule getEntityOrThrow(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado con id: " + id));
    }

    private ScheduleResponse toResponse(BarberSchedule s) {
        return ScheduleResponse.builder()
                .id(s.getId())
                .barberId(s.getBarber().getId())
                .barberName(s.getBarber().getName())
                .dayOfWeek(s.getDayOfWeek())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .breakStart(s.getBreakStart())
                .breakEnd(s.getBreakEnd())
                .build();
    }
}