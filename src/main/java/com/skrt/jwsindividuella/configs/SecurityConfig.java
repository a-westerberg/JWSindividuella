package com.skrt.jwsindividuella.configs;

import com.skrt.jwsindividuella.converters.JwtAuthConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity

public class SecurityConfig {

    private final JwtAuthConverter jwtAuthConverter;

    @Autowired
    public SecurityConfig(JwtAuthConverter jwtAuthConverter) {
        this.jwtAuthConverter = jwtAuthConverter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error", "error/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v2/posts").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v2/post/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v2/newpost").hasRole("user")
                        .requestMatchers(HttpMethod.PUT, "/api/v2/updatepost").hasRole("user")
                        .requestMatchers(HttpMethod.DELETE, "/api/v2/deletepost/**").hasAnyRole("user", "admin")
                        .requestMatchers(HttpMethod.GET, "/api/v2/count").hasRole("admin")
                        .anyRequest().denyAll()
                )
                .headers(h -> h.frameOptions(f -> f.disable()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth->oauth.jwt(j -> j.jwtAuthenticationConverter(jwtAuthConverter))
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, e) -> {
                            response.setStatus(403);
                            response.setContentType("application/json");
                            response.getWriter().write("""
                                    {
                                    "status": 403,
                                    "error": "Forbidden",
                                    "message": "You need role 'admin' to access this endpoint",
                                    "path": "%s"
                                    }
                                    """.formatted(request.getRequestURI()));
                        })
                        .authenticationEntryPoint((request, response, e) -> {
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.getWriter().write("""
                                    {"status": 401,
                                    "error": "Unauthorized",
                                    "message": "Missing Bearer Token",
                                    "path": "%s"
                                    }
                                    """.formatted(request.getRequestURI()));
                        })
                );
        return http.build();
    }

}
