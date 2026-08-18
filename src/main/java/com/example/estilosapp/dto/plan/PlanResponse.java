package com.example.estilosapp.dto.plan;

import com.example.estilosapp.entity.enums.BillingFrequency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BillingFrequency billingFrequency;
    private Integer trialDays;
    private Boolean isActive;
}