package com.educagestor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Password Encoder Configuration
 * 
 * Separate configuration for password encoder to avoid circular dependencies.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * Configure password encoder
     * 
     * @return BCrypt password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
