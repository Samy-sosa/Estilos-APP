package com.example.estilosapp.dto.plan;

import com.example.estilosapp.entity.enums.BillingFrequency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlanRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String description;

    @NotNull(message = "El precio es obligatorio")
    private BigDecimal price;

    @NotNull(message = "La frecuencia de cobro es obligatoria")
    private BillingFrequency billingFrequency;

    private Integer trialDays;
}