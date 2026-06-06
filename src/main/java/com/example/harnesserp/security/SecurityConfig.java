package com.example.harnesserp.security;

import com.example.harnesserp.controller.ApiErrorResponse;
import com.example.harnesserp.policy.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    public SecurityConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    SecurityFilterChain erpSecurityFilterChain(
            HttpSecurity http,
            RoleHeaderAuthenticationFilter roleHeaderAuthenticationFilter
    ) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .logout(logout -> logout.disable())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, exception) -> writeForbidden(request, response))
                        .accessDeniedHandler((request, response, exception) -> writeForbidden(request, response))
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/employees").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/employees/*").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/purchase-requests").hasRole(Role.EMPLOYEE.name())
                        .requestMatchers(HttpMethod.POST, "/purchase-requests/*/approve").hasRole(Role.MANAGER.name())
                        .requestMatchers(HttpMethod.POST, "/purchase-requests/*/reject").hasRole(Role.MANAGER.name())
                        .anyRequest().permitAll()
                )
                .addFilterBefore(roleHeaderAuthenticationFilter, AuthorizationFilter.class);

        return http.build();
    }

    @Bean
    UserDetailsService erpUserDetailsService() {
        return username -> {
            throw new UsernameNotFoundException(username);
        };
    }

    private void writeForbidden(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), new ApiErrorResponse(requiredRoleMessage(request)));
    }

    private String requiredRoleMessage(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getRequestURI();

        if (HttpMethod.POST.matches(method) && "/employees".equals(path)) {
            return "ADMIN role is required to create employees";
        }
        if (HttpMethod.PUT.matches(method) && path.startsWith("/employees/")) {
            return "ADMIN role is required to update employees";
        }
        if (HttpMethod.POST.matches(method) && "/purchase-requests".equals(path)) {
            return "EMPLOYEE role is required to create purchase requests";
        }
        if (HttpMethod.POST.matches(method)
                && (path.endsWith("/approve") || path.endsWith("/reject"))) {
            return "MANAGER role is required to approve or reject purchase requests";
        }
        return "Request is not authorized";
    }
}
