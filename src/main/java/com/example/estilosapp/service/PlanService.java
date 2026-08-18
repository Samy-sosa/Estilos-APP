package com.example.estilosapp.service;

import com.example.estilosapp.dto.plan.PlanRequest;
import com.example.estilosapp.dto.plan.PlanResponse;
import com.example.estilosapp.entity.Plan;
import com.example.estilosapp.exception.ResourceNotFoundException;
import com.example.estilosapp.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;

    public List<PlanResponse> findAllActive() {
        return planRepository.findByIsActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PlanResponse findById(Long id) {
        return toResponse(getEntityOrThrow(id));
    }

    public PlanResponse create(PlanRequest request) {
        Plan plan = Plan.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .billingFrequency(request.getBillingFrequency())
                .trialDays(request.getTrialDays())
                .isActive(true)
                .build();
        return toResponse(planRepository.save(plan));
    }

    public PlanResponse update(Long id, PlanRequest request) {
        Plan plan = getEntityOrThrow(id);
        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setPrice(request.getPrice());
        plan.setBillingFrequency(request.getBillingFrequency());
        plan.setTrialDays(request.getTrialDays());
        return toResponse(planRepository.save(plan));
    }

    public void deactivate(Long id) {
        Plan plan = getEntityOrThrow(id);
        plan.setIsActive(false);
        planRepository.save(plan);
    }

    private Plan getEntityOrThrow(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado con id: " + id));
    }

    private PlanResponse toResponse(Plan p) {
        return PlanResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .billingFrequency(p.getBillingFrequency())
                .trialDays(p.getTrialDays())
                .isActive(p.getIsActive())
                .build();
    }
}