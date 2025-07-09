package com.example.demo.security;

import com.example.demo.token.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // How we hash passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Turn off CSRF (we use JWT instead)
                .csrf(csrf -> csrf.disable())

                // Enable CORS
                .cors(cors -> cors.and())

                // Don't create sessions (stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Who can access what
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/**").permitAll()                    // Anyone can login/register
                        .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN")  // Admins only
                        .requestMatchers("/api/support/**").hasAnyRole("SUPPORT_STAFF", "ADMIN", "SUPER_ADMIN")  // Support staff + admins
                        .requestMatchers("/api/users/**").authenticated()               // Must be logged in
                        .anyRequest().permitAll()                                        // Everything else is open
                )

                // Check JWT token before processing request
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}