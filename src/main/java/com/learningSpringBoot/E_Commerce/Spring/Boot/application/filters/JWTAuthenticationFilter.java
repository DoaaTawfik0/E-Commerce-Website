package com.learningSpringBoot.E_Commerce.Spring.Boot.application.filters;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.exception.InvalidTokenException;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.exception.TokenExpiredException;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.services.impl.CustomUserDetailsServiceImpl;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.utilities.JWTUtility;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtility jwtUtil;
    private final CustomUserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String username = jwtUtil.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(token, username, userDetails)) {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        } catch (TokenExpiredException ex) {
            handleException(response, "Token expired"
                    , "Please login again");
            return;
        } catch (InvalidTokenException ex) {
            handleException(response, "Invalid token"
                    , "Authentication failed");
            return;
        } catch (Exception ex) {
            handleException(response, "Authentication error"
                    , "Invalid token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void handleException(HttpServletResponse response, String error, String message)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = String.format(
                "{\"timeStamp\": \"%s\", \"message\": \"%s\", \"details\": \"%s\"}",
                java.time.LocalDateTime.now(),
                message,
                error
        );

        response.getWriter().write(jsonResponse);
    }
}