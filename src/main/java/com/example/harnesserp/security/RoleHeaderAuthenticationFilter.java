package com.example.harnesserp.security;

import com.example.harnesserp.policy.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RoleHeaderAuthenticationFilter extends OncePerRequestFilter {

    public static final String ROLE_HEADER = "X-ERP-Role";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Role role = roleFrom(request.getHeader(ROLE_HEADER));
        if (role != null) {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    role.name(),
                    ROLE_HEADER,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role.name()))
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private Role roleFrom(String headerValue) {
        if (headerValue == null || headerValue.isBlank()) {
            return null;
        }
        try {
            return Role.valueOf(headerValue.strip());
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
