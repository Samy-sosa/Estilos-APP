package com.example.estilosapp.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final com.example.estilosapp.service.UserDetailsServiceImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        // Lectura pública: cualquiera puede ver barberías, servicios, horarios y disponibilidad
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/barbershops/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/availability/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/barbers/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/services/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/schedules/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/mercadopago/status/**", "/api/mercadopago/callback").permitAll()
                        // Escritura: solo usuarios autenticados con rol TENANT_ADMIN o SUPER_ADMIN
                        .requestMatchers("/api/barbershops/**", "/api/barbers/**", "/api/services/**", "/api/schedules/**")
                        .hasAnyRole("TENANT_ADMIN", "SUPER_ADMIN")
                        // Citas: cualquier usuario autenticado (cliente, barbero o admin)
                        .requestMatchers("/api/appointments/**").authenticated()
                        // Regla general al final: todo lo demás requiere estar autenticado
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}