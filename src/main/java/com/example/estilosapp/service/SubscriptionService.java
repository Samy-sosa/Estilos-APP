package com.example.estilosapp.service;

import com.example.estilosapp.config.MercadoPagoProperties;
import com.example.estilosapp.dto.mercadopago.MpPreapprovalResponse;
import com.example.estilosapp.dto.subscription.SubscriptionRequest;
import com.example.estilosapp.dto.subscription.SubscriptionResponse;
import com.example.estilosapp.entity.Barbershop;
import com.example.estilosapp.entity.Plan;
import com.example.estilosapp.entity.Subscription;
import com.example.estilosapp.entity.enums.BillingFrequency;
import com.example.estilosapp.entity.enums.SubscriptionStatus;
import com.example.estilosapp.exception.ResourceNotFoundException;
import com.example.estilosapp.repository.BarbershopRepository;
import com.example.estilosapp.repository.PlanRepository;
import com.example.estilosapp.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private static final String PREAPPROVAL_URL = "https://api.mercadopago.com/preapproval";

    private final SubscriptionRepository subscriptionRepository;
    private final BarbershopRepository barbershopRepository;
    private final PlanRepository planRepository;
    private final MercadoPagoProperties mpProperties;
    private final RestClient restClient = RestClient.create();

    public SubscriptionResponse subscribe(SubscriptionRequest request) {
        Barbershop barbershop = barbershopRepository.findById(request.getBarbershopId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Barbería no encontrada con id: " + request.getBarbershopId()));

        Plan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Plan no encontrado con id: " + request.getPlanId()));

        if (plan.getBillingFrequency() == BillingFrequency.TRIAL) {
            return subscribeToTrial(barbershop, plan);
        }
        return subscribeToPaidPlan(barbershop, plan, request.getPayerEmail());
    }

    private SubscriptionResponse subscribeToTrial(Barbershop barbershop, Plan plan) {
        int days = plan.getTrialDays() != null ? plan.getTrialDays() : 30;

        Subscription subscription = Subscription.builder()
                .barbershop(barbershop)
                .plan(plan)
                .status(SubscriptionStatus.TRIALING)
                .startDate(LocalDate.now())
                .currentPeriodEnd(LocalDate.now().plusDays(days))
                .build();

        Subscription saved = subscriptionRepository.save(subscription);
        return toResponse(saved, null);
    }

    private SubscriptionResponse subscribeToPaidPlan(Barbershop barbershop, Plan plan, String payerEmail) {
        int frequency = plan.getBillingFrequency() == BillingFrequency.YEARLY ? 12 : 1;

        Map<String, Object> autoRecurring = new HashMap<>();
        autoRecurring.put("frequency", frequency);
        autoRecurring.put("frequency_type", "months");
        autoRecurring.put("transaction_amount", plan.getPrice());
        autoRecurring.put("currency_id", "MXN");

        Map<String, Object> body = new HashMap<>();
        body.put("reason", "BarberApp Yucatán - Plan " + plan.getName());
        body.put("external_reference", "barbershop_" + barbershop.getId() + "_plan_" + plan.getId());
        body.put("payer_email", payerEmail);
        body.put("auto_recurring", autoRecurring);
        body.put("back_url", "https://tu-dominio.com/suscripcion/confirmacion"); // ajustar cuando tengas dominio

        MpPreapprovalResponse response = restClient.post()
                .uri(PREAPPROVAL_URL)
                .header("Authorization", "Bearer " + mpProperties.getPlatformAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(MpPreapprovalResponse.class);

        if (response == null || response.getId() == null) {
            throw new IllegalStateException("No se pudo crear la suscripción en Mercado Pago");
        }

        Subscription subscription = Subscription.builder()
                .barbershop(barbershop)
                .plan(plan)
                .status(SubscriptionStatus.PENDING) // se activa cuando MP confirma el pago (webhook)
                .mpPreapprovalId(response.getId())
                .startDate(LocalDate.now())
                .build();

        Subscription saved = subscriptionRepository.save(subscription);
        return toResponse(saved, response.getInit_point());
    }

    public List<SubscriptionResponse> findByBarbershop(Long barbershopId) {
        return subscriptionRepository.findByBarbershopId(barbershopId).stream()
                .map(s -> toResponse(s, null))
                .collect(Collectors.toList());
    }

    /**
     * Llamado desde el webhook de Mercado Pago cuando cambia el estado de una suscripción
     * (ej. autorizada, pausada, cancelada).
     */
    public void updateStatusFromWebhook(String mpPreapprovalId, String mpStatus) {
        Subscription subscription = subscriptionRepository.findByMpPreapprovalId(mpPreapprovalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Suscripción no encontrada para mp_preapproval_id: " + mpPreapprovalId));

        SubscriptionStatus newStatus = switch (mpStatus) {
            case "authorized" -> SubscriptionStatus.ACTIVE;
            case "paused" -> SubscriptionStatus.PAST_DUE;
            case "cancelled" -> SubscriptionStatus.CANCELLED;
            default -> subscription.getStatus();
        };

        subscription.setStatus(newStatus);
        subscriptionRepository.save(subscription);
    }

    private SubscriptionResponse toResponse(Subscription s, String checkoutUrl) {
        return SubscriptionResponse.builder()
                .id(s.getId())
                .barbershopId(s.getBarbershop().getId())
                .barbershopName(s.getBarbershop().getName())
                .planName(s.getPlan().getName())
                .status(s.getStatus())
                .startDate(s.getStartDate())
                .currentPeriodEnd(s.getCurrentPeriodEnd())
                .checkoutUrl(checkoutUrl)
                .build();
    }
}