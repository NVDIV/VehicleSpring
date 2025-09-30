package com.example.Vehicles.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration //adnotacja konfiguracji – definicja beanów
@EnableWebSecurity //włączamy mechanizm WebSecurity!
@EnableMethodSecurity //Włączamy zabezpieczania metod – np. @PreAuthorize
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http, // obiekt do łańcucha ustawień obsługi bezpieczeństwa
            JwtAuthFilter jwtAuthFilter, // Niestandardowy filtr do implementacji - weryfikacja tokenu!
            AuthenticationProvider authProvider //Dostawca uwierzytelnienia
    ) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) //w rest api – brak sesji i ciasteczek, wyłączamy ochronę //Cross-Site Request Forgery
                .authorizeHttpRequests(auth -> auth //konfigurator autoryzacji i ustalanie reguł:
                        .requestMatchers("/api/auth/**").permitAll() //endpointy do auth(login,register)
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") //Endpointy Dla ROLE_ADMIN!(zmieni w bazie dla Usera na ROLE_USER i ROLE_ADMIN !
                        .anyRequest().authenticated() //każdy inny request wymaga zalogowania
                )
                //Brak sesji, rest api z tokenami jwt!
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //Dostawca uwierzytelnienia
                .authenticationProvider(authProvider)
                //Weryfikacja tokenu przed standardowym sposobem autoryzacji!
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider( //Bean z dostawcą autentykacj
                                                          UserDetailsService userDetailsService, //Serwis do ładowania danych usera
                                                          PasswordEncoder passwordEncoder //Encoder
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(); //Obiekt do Autentykacja zbazy
        provider.setUserDetailsService(userDetailsService); //serwis używany do autentykacji
        provider.setPasswordEncoder(passwordEncoder); //encoder
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() { //Używamy bcrypt do hashowania hasla
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config //konfiguracja uwierzytelniania
    ) throws Exception {
        return config.getAuthenticationManager(); //dostęp do AuthenticationManagera.
    }
}