package com.example.projetTechnique.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

import com.example.projetTechnique.repository.UserRepository;
import com.example.projetTechnique.model.User;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/users/delete/**").hasRole("ADMIN")
                        .requestMatchers("/users/register/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/users/login/**").permitAll()
                        .requestMatchers("/users/forgot-password/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/users/reset-password/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/users/getAll/**").hasAnyRole("ADMIN")
                        .requestMatchers("/users/one/{id}").hasAnyRole( "ADMIN")
                        .requestMatchers("/comments/create/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/comments/getAll/**").hasAnyRole("ADMIN")
                        .requestMatchers("/comments/one/{id}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/comments/delete/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/comments/update/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
