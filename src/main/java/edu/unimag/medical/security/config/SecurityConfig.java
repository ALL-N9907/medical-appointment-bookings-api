package edu.unimag.medical.security.config;

import edu.unimag.medical.security.error.Http401EntryPoint;
import edu.unimag.medical.security.error.Http403AccessDenied;
import edu.unimag.medical.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final Http401EntryPoint http401EntryPoint;
    private final Http403AccessDenied http403AccessDenied;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(http401EntryPoint)
                        .accessDeniedHandler(http403AccessDenied)
                )
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.POST,  "/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST,  "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET,   "/actuator/health").permitAll()
                        .requestMatchers(HttpMethod.POST,  "/api/patients").permitAll()
                        .requestMatchers(HttpMethod.GET,   "/api/doctors/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET,   "/api/doctors").permitAll()
                        .requestMatchers(HttpMethod.GET,   "/api/specialties").permitAll()
                        .requestMatchers(HttpMethod.GET,   "/api/appointment-types").permitAll()

                        .requestMatchers(HttpMethod.POST,  "/api/auth/create-staff").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,   "/api/patients").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,  "/api/doctors").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,   "/api/doctors/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,  "/api/specialties").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,  "/api/offices").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,   "/api/offices/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,  "/api/appointment-types").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,   "/api/appointments").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET,   "/api/patients/{id}")
                        .hasAnyRole("ADMIN", "PATIENT", "RECEPTIONIST")

                        .requestMatchers(HttpMethod.PUT,   "/api/patients/{id}")
                        .hasAnyRole("ADMIN", "PATIENT")

                        .requestMatchers(HttpMethod.POST,  "/api/doctors/{id}/schedules")
                        .hasAnyRole("ADMIN", "DOCTOR", "RECEPTIONIST")


                        .requestMatchers(HttpMethod.PUT,   "/api/appointments/{id}/confirm")
                        .hasAnyRole("DOCTOR", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.PUT,   "/api/appointments/{id}/complete")
                        .hasAnyRole("DOCTOR", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.PUT,   "/api/appointments/{id}/no-show")
                        .hasAnyRole("DOCTOR", "RECEPTIONIST")


                        .requestMatchers(HttpMethod.GET,   "/api/offices").authenticated()
                        .requestMatchers(HttpMethod.GET,   "/api/doctors/{id}/schedules").authenticated()
                        .requestMatchers(HttpMethod.GET,   "/api/availability/doctors/{id}").authenticated()
                        .requestMatchers(HttpMethod.GET,   "/api/reports/{id}").authenticated()
                        .requestMatchers(HttpMethod.POST,  "/api/appointments").authenticated()
                        .requestMatchers(HttpMethod.GET,   "/api/appointments/{id}").authenticated()
                        .requestMatchers(HttpMethod.PUT,   "/api/appointments/{id}/cancel").authenticated()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}