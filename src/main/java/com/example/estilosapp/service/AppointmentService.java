package com.example.estilosapp.service;

import com.example.estilosapp.dto.appointment.AppointmentRequest;
import com.example.estilosapp.dto.appointment.AppointmentResponse;
import com.example.estilosapp.entity.*;
import com.example.estilosapp.entity.enums.AppointmentStatus;
import com.example.estilosapp.exception.ResourceNotFoundException;
import com.example.estilosapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final BarbershopRepository barbershopRepository;
    private final UserRepository userRepository;
    private final BarberRepository barberRepository;
    private final com.example.estilosapp.repository.ServiceRepository serviceRepository;
    private final AppointmentServiceRepository appointmentServiceRepository;
    private final AvailabilityService availabilityService;

    @Transactional
    public AppointmentResponse create(AppointmentRequest request) {

        Barbershop barbershop = barbershopRepository.findById(request.getBarbershopId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Barbería no encontrada con id: " + request.getBarbershopId()));

        User client = userRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado con id: " + request.getClientId()));

        Barber barber = barberRepository.findById(request.getBarberId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Barbero no encontrado con id: " + request.getBarberId()));

        List<com.example.estilosapp.entity.Service> services =
                serviceRepository.findAllById(request.getServiceIds());
        if (services.size() != request.getServiceIds().size()) {
            throw new ResourceNotFoundException("Uno o más servicios no existen");
        }

        int totalDuration = services.stream()
                .mapToInt(com.example.estilosapp.entity.Service::getDurationMinutes)
                .sum();
        BigDecimal totalPrice = services.stream()
                .map(com.example.estilosapp.entity.Service::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalTime endTime = request.getStartTime().plusMinutes(totalDuration);

        // Revalidamos disponibilidad justo antes de guardar (evita choques de última hora)
        boolean slotStillFree = availabilityService
                .calculateAvailability(request.getBarberId(), request.getDate(), request.getServiceIds())
                .getAvailableSlots()
                .stream()
                .anyMatch(slot -> slot.getStartTime().equals(request.getStartTime()));

        if (!slotStillFree) {
            throw new IllegalArgumentException(
                    "El horario seleccionado ya no está disponible. Por favor elige otro.");
        }

        AppointmentStatus initialStatus = switch (request.getPaymentMethod()) {
            case CASH -> AppointmentStatus.CONFIRMED;
            case MERCADO_PAGO -> AppointmentStatus.PENDING_PAYMENT;
        };

        Appointment appointment = Appointment.builder()
                .barbershop(barbershop)
                .client(client)
                .barber(barber)
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(endTime)
                .totalPrice(totalPrice)
                .status(initialStatus)
                .paymentMethod(request.getPaymentMethod())
                .build();

        Appointment saved = appointmentRepository.save(appointment);

        List<com.example.estilosapp.entity.AppointmentService> lines = services.stream()
                .map(s -> com.example.estilosapp.entity.AppointmentService.builder()
                        .appointment(saved)
                        .service(s)
                        .priceAtBooking(s.getPrice())
                        .build())
                .collect(Collectors.toList());
        appointmentServiceRepository.saveAll(lines);
        saved.setServices(lines);

        return toResponse(saved);
    }

    public AppointmentResponse findById(Long id) {
        return toResponse(getEntityOrThrow(id));
    }

    public List<AppointmentResponse> findByClient(Long clientId) {
        return appointmentRepository.findByClientId(clientId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<AppointmentResponse> findByBarbershop(Long barbershopId) {
        return appointmentRepository.findByBarbershopId(barbershopId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AppointmentResponse cancel(Long id) {
        Appointment appointment = getEntityOrThrow(id);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        return toResponse(appointmentRepository.save(appointment));
    }

    private Appointment getEntityOrThrow(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id: " + id));
    }

    private AppointmentResponse toResponse(Appointment a) {
        List<AppointmentResponse.ServiceLine> lines = a.getServices().stream()
                .map(line -> AppointmentResponse.ServiceLine.builder()
                        .serviceId(line.getService().getId())
                        .serviceName(line.getService().getName())
                        .price(line.getPriceAtBooking())
                        .build())
                .collect(Collectors.toList());

        return AppointmentResponse.builder()
                .id(a.getId())
                .barbershopId(a.getBarbershop().getId())
                .barbershopName(a.getBarbershop().getName())
                .clientId(a.getClient().getId())
                .clientName(a.getClient().getName())
                .barberId(a.getBarber().getId())
                .barberName(a.getBarber().getName())
                .date(a.getDate())
                .startTime(a.getStartTime())
                .endTime(a.getEndTime())
                .totalPrice(a.getTotalPrice())
                .status(a.getStatus())
                .paymentMethod(a.getPaymentMethod())
                .services(lines)
                .createdAt(a.getCreatedAt())
                .build();
    }
}