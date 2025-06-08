package com.educagestor.security;

import com.educagestor.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter
 * 
 * Intercepts HTTP requests to validate JWT tokens and set up
 * Spring Security authentication context.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    /**
     * Filter incoming requests to validate JWT tokens
     * 
     * @param request the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateAccessToken(jwt)) {
                String username = jwtTokenProvider.getUsernameFromToken(jwt);

                UserDetails userDetails = userService.loadUserByUsername(username);
                
                if (userDetails != null) {
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, 
                                    null, 
                                    userDetails.getAuthorities()
                            );
                    
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    log.debug("Set authentication for user: {}", username);
                }
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from request header
     * 
     * @param request the HTTP request
     * @return JWT token or null if not found
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        return jwtTokenProvider.extractTokenFromHeader(bearerToken);
    }

    /**
     * Determine if this filter should be applied to the request
     * 
     * @param request the HTTP request
     * @return true if filter should be applied
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Skip JWT validation for public endpoints
        return path.startsWith("/api/auth/") ||
               path.startsWith("/api/public/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/api-docs/") ||
               path.startsWith("/actuator/health") ||
               path.equals("/") ||
               path.startsWith("/static/") ||
               path.startsWith("/favicon.ico");
    }
}
