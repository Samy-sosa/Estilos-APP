package com.example.estilosapp.dto.appointment;

import com.example.estilosapp.entity.enums.AppointmentStatus;
import com.example.estilosapp.entity.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponse {
    private Long id;
    private Long barbershopId;
    private String barbershopName;
    private Long clientId;
    private String clientName;
    private Long barberId;
    private String barberName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal totalPrice;
    private AppointmentStatus status;
    private PaymentMethod paymentMethod;
    private List<ServiceLine> services;
    private LocalDateTime createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ServiceLine {
        private Long serviceId;
        private String serviceName;
        private BigDecimal price;
    }
}