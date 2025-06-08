package com.educagestor.config;

import com.educagestor.security.JwtAuthenticationFilter;
import com.educagestor.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security Configuration
 * 
 * Configures authentication, authorization, CORS, and JWT security
 * for the EducaGestor360 application.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configure HTTP security
     * 
     * @param http the HttpSecurity to configure
     * @return configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for stateless API
            .csrf(AbstractHttpConfigurer::disable)
            
            // Configure CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Configure session management
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Configure authorization rules
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                
                // Swagger/OpenAPI endpoints
                .requestMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-ui.html").permitAll()
                
                // Actuator health endpoint
                .requestMatchers("/actuator/health").permitAll()
                
                // Static resources
                .requestMatchers("/", "/favicon.ico", "/static/**").permitAll()
                
                // Admin only endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                
                // Institution management - Admin only
                .requestMatchers("/api/institutions/**").hasRole("ADMIN")
                
                // User management - Admin only
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                
                // Student management - Admin and Teachers
                .requestMatchers(HttpMethod.GET, "/api/students/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers(HttpMethod.POST, "/api/students/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/students/**").hasAnyRole("ADMIN", "TEACHER")
                
                // Teacher management - Admin only
                .requestMatchers("/api/teachers/**").hasRole("ADMIN")
                
                // Course management - Admin and Teachers
                .requestMatchers(HttpMethod.GET, "/api/courses/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                .requestMatchers(HttpMethod.POST, "/api/courses/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers(HttpMethod.PUT, "/api/courses/**").hasAnyRole("ADMIN", "TEACHER")
                
                // Enrollment management - Admin and Teachers
                .requestMatchers(HttpMethod.GET, "/api/enrollments/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                .requestMatchers(HttpMethod.POST, "/api/enrollments/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers(HttpMethod.PUT, "/api/enrollments/**").hasAnyRole("ADMIN", "TEACHER")
                
                // Grade management - Admin and Teachers
                .requestMatchers(HttpMethod.GET, "/api/grades/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                .requestMatchers(HttpMethod.POST, "/api/grades/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers(HttpMethod.PUT, "/api/grades/**").hasAnyRole("ADMIN", "TEACHER")
                
                // Attendance management - Admin and Teachers
                .requestMatchers(HttpMethod.GET, "/api/attendance/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                .requestMatchers(HttpMethod.POST, "/api/attendance/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers(HttpMethod.PUT, "/api/attendance/**").hasAnyRole("ADMIN", "TEACHER")
                
                // Educational resources - All authenticated users can view, Teachers can manage
                .requestMatchers(HttpMethod.GET, "/api/resources/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                .requestMatchers(HttpMethod.POST, "/api/resources/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers(HttpMethod.PUT, "/api/resources/**").hasAnyRole("ADMIN", "TEACHER")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            
            // Configure authentication provider
            .authenticationProvider(authenticationProvider())
            
            // Add JWT filter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configure CORS
     * 
     * @return CORS configuration source
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow specific origins (configure based on environment)
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:5173",
            "http://localhost:8080"
        ));
        
        // Allow specific methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // Allow specific headers
        configuration.setAllowedHeaders(List.of("*"));
        
        // Allow credentials
        configuration.setAllowCredentials(true);
        
        // Cache preflight response
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }

    /**
     * Configure password encoder
     * 
     * @return BCrypt password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Configure authentication manager
     * 
     * @param config the authentication configuration
     * @return authentication manager
     * @throws Exception if configuration fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configure DAO authentication provider
     * 
     * @return configured authentication provider
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
