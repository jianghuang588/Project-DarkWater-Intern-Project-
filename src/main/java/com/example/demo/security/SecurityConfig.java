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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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

                // FIXED: Use STATEFUL sessions for web pages, STATELESS for API
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

                // ADD THIS: Configure SecurityContext to persist in session
                .securityContext(securityContext ->
                        securityContext.requireExplicitSave(false))

                // Who can access what
                .authorizeHttpRequests(authz -> authz
                        // Public web pages (no authentication required)
                        .requestMatchers(
                                "/",
                                "/home",
                                "/about",              // About Project Darkwater page
                                "/faq",                // FAQ page
                                "/roadmap",            // Interactive Roadmap page
                                "/news",               // News (Blog-post style) page
                                "/release-notes",      // Release Notes page
                                "/apply",              // Apply to join the crew page
                                "/staff",              // Staff panel (protected by controller logic)
                                "/help",               // Help page
                                "/play",               // Play/Download page
                                "/login",              // Login page
                                "/register",           // Registration page
                                "/verify",             // Email verification page
                                "/forgot-password",    // Forgot password page
                                "/reset-password",     // Reset password page
                                "/splash",             // Splash screen page
                                "/email-verified",     // Email verification success page
                                "/error",              // Error page
                                "/check-session"       // Debug endpoint
                        ).permitAll()

                        // Static resources
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**").permitAll()

                        // FIXED: Allow these web pages but let controller handle auth
                        .requestMatchers("/profile", "/profile/**", "/settings", "/content").permitAll()

                        // Admin web pages - let controller handle auth
                        .requestMatchers("/admin/**").permitAll()

                        // API endpoints - These use JWT
                        .requestMatchers("/api/auth/**").permitAll()                    // Anyone can login/register

                        // News API endpoints - Public access for reading
                        .requestMatchers("/api/news/carousel").permitAll()              // Public news carousel
                        .requestMatchers("/api/news/latest-patch").permitAll()          // Public patch notes
                        .requestMatchers("/api/news/published/**").permitAll()          // Public published posts
                        .requestMatchers("/api/news/{id:[0-9]+}").permitAll()           // Public single post view

                        // FIXED: Allow session-based authentication for admin APIs
                        // News API endpoints - Admin only for management
                        .requestMatchers("/api/news/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN")  // Admin-only news management
                        .requestMatchers("/api/news").hasAnyRole("ADMIN", "SUPER_ADMIN")           // Create new posts
                        .requestMatchers("/api/news/{id:[0-9]+}/schedule").hasAnyRole("ADMIN", "SUPER_ADMIN")  // Schedule posts
                        .requestMatchers("/api/news/{id:[0-9]+}/publish").hasAnyRole("ADMIN", "SUPER_ADMIN")   // Publish posts
                        .requestMatchers("/api/news/**").hasAnyRole("ADMIN", "SUPER_ADMIN")        // All other news endpoints require admin

                        // Other admin endpoints
                        .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN")       // Admins only

                        // Support endpoints
                        .requestMatchers("/api/support/**").hasAnyRole("SUPPORT_STAFF", "ADMIN", "SUPER_ADMIN")  // Support staff + admins

                        // User endpoints
                        .requestMatchers("/api/users/**").authenticated()               // Must be logged in

                        .anyRequest().permitAll()                                        // Everything else is open
                )

                // Only apply JWT filter to API endpoints
                .addFilterBefore(new JwtFilterWrapper(jwtAuthenticationFilter), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Custom filter wrapper to only apply JWT to API endpoints
    private static class JwtFilterWrapper extends JwtAuthenticationFilter {
        private final JwtAuthenticationFilter jwtFilter;

        public JwtFilterWrapper(JwtAuthenticationFilter jwtFilter) {
            this.jwtFilter = jwtFilter;
        }

        @Override
        protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request,
                                        jakarta.servlet.http.HttpServletResponse response,
                                        jakarta.servlet.FilterChain filterChain) throws jakarta.servlet.ServletException, java.io.IOException {
            String path = request.getRequestURI();

            // Skip JWT for admin news APIs (use session auth instead)
            if (path.startsWith("/api/news") && !path.startsWith("/api/news/carousel") &&
                    !path.startsWith("/api/news/latest-patch") && !path.startsWith("/api/news/published")) {
                // Admin news APIs - use session authentication only
                filterChain.doFilter(request, response);
            } else if (path.startsWith("/api/")) {
                // Check if user is already authenticated via session
                Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
                if (existingAuth != null && existingAuth.isAuthenticated() &&
                        !existingAuth.getName().equals("anonymousUser")) {
                    // User is already authenticated via session, skip JWT processing
                    filterChain.doFilter(request, response);
                } else {
                    // No session auth, try JWT
                    super.doFilterInternal(request, response, filterChain);
                }
            } else {
                // Web pages - no JWT
                filterChain.doFilter(request, response);
            }
        }
    }
}