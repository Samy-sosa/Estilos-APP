package com.example.estilosapp.dto.subscription;

import com.example.estilosapp.entity.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponse {
    private Long id;
    private Long barbershopId;
    private String barbershopName;
    private String planName;
    private SubscriptionStatus status;
    private LocalDate startDate;
    private LocalDate currentPeriodEnd;
    // Solo viene lleno si el plan es pagado: URL a la que se debe mandar
    // al dueño de la barbería para que autorice el cobro recurrente
    private String checkoutUrl;
}