package com.fridgemate.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    // OncePerRequestFilter guarantees this filter runs exactly once per HTTP request,
    // even if the request is forwarded internally within the application.

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Read the Authorization header — expected format: "Bearer eyJ..."
        final String authHeader = request.getHeader("Authorization");

        // 2. If there's no token (e.g. a public endpoint like /api/auth/login),
        //    skip this filter entirely and pass the request along the chain.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Strip the "Bearer " prefix to get the raw token string
        final String token = authHeader.substring(7);

        // 4. Validate the token before doing anything else
        if (!jwtUtil.isTokenValid(token)) {
            filterChain.doFilter(request, response); // invalid token — proceed unauthenticated
            return;
        }

        // 5. Extract the email from the token payload
        final String email = jwtUtil.extractEmail(token);

        // 6. Only set authentication if not already set (avoid redundant work on forwarded requests)
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 7. Load the full user from the database to get their roles/authorities
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // 8. Create an authentication token — this is Spring Security's way of representing
            //    "this request belongs to this authenticated user"
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,               // credentials (password) — not needed post-authentication
                            userDetails.getAuthorities()
                    );

            // 9. Attach request details (IP address, session) to the authentication object
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 10. Store in the SecurityContext — from this point, any code in the request
            //     thread can call SecurityContextHolder.getContext().getAuthentication()
            //     to find out who made the request.
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // 11. Pass the request to the next filter / controller
        filterChain.doFilter(request, response);
    }
}
