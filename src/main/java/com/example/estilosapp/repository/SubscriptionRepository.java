package com.example.estilosapp.repository;

import com.example.estilosapp.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByBarbershopId(Long barbershopId);
    Optional<Subscription> findByMpPreapprovalId(String mpPreapprovalId);

    // La suscripción vigente más reciente de una barbería (para saber su estado actual)
    Optional<Subscription> findFirstByBarbershopIdOrderByCreatedAtDesc(Long barbershopId);
}