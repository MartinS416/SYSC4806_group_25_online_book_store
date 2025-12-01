package com.bookstore.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration for the online bookstore application.
 * Defines authentication provider, authorization rules, and password encoding strategy.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final CustomDetailsService customDetailsService;

    public SecurityConfiguration(CustomDetailsService customDetailsService) {
        this.customDetailsService = customDetailsService;
    }

    /**
     * Configures the security filter chain with authorization rules and login flow.
     * <p>
     * Authorization rules:
     * - Public endpoints: /login, /register, /shop, /h2-console, static resources (/css, /js)
     * - Admin-only endpoints: /admin/** (requires ROLE_ADMIN)
     * - All other endpoints: require authentication
     * <p>
     * Login configuration:
     * - Custom login page at /login
     * - Login processing at /login (POST)
     * - Redirect to / on successful authentication
     * - Redirect to /login?error=true on failed authentication
     *
     * @param http the HttpSecurity object to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // Disabled for form-based authentication
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login**", "/register**", "/shop**", "/h2/**", "/css/**", "/js/**")
                        .permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .authenticationProvider(authProvider());

        return http.build();
    }

    /**
     * Creates and configures the DAO authentication provider.
     * Uses the modern constructor-based approach (Spring Security 6.5+) that accepts
     * UserDetailsService and PasswordEncoder directly.
     *
     * @return configured DaoAuthenticationProvider
     */
    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Password encoding function using BCrypt with default strength.
     *
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}