package com.example.estilosapp.entity;

import com.example.estilosapp.entity.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "barbershop_id", nullable = false)
    private Barbershop barbershop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;

    // Nulo para el plan TRIAL, ya que no pasa por Mercado Pago
    @Column(name = "mp_preapproval_id")
    private String mpPreapprovalId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    // Para TRIAL: cuándo vence la prueba. Para planes pagados, MP administra la renovación,
    // pero igual guardamos la fecha estimada del siguiente cobro para referencia rápida.
    @Column(name = "current_period_end")
    private LocalDate currentPeriodEnd;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}