package com.library.libraryapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_USER = "USER";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        try {
            http
                    .csrf(csrf -> csrf.disable())
                    .headers(headers -> headers
                            .contentTypeOptions(contentTypeOptions -> {})
                            .frameOptions(frame -> frame.sameOrigin())
                            .contentSecurityPolicy(csp -> csp
                                    .policyDirectives("default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self' data:;")
                            )
                    )
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(
                                    "/login.html",
                                    "/register.html",
                                    "/style.css",
                                    "/app.js",
                                    "/bg.png",
                                    "/auth/register",
                                    "/login",
                                    "/error"
                            ).permitAll()

                            .requestMatchers("/admin.html").hasRole("ADMIN")
                            .requestMatchers("/books.html").hasRole("USER")
                            .requestMatchers("/auth/me").authenticated()

                            .requestMatchers(HttpMethod.GET, "/books").hasAnyRole("USER", "ADMIN")
                            .requestMatchers(HttpMethod.GET, "/books/**").hasAnyRole("USER", "ADMIN")
                            .requestMatchers(HttpMethod.POST, "/books").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT, "/books/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/books/**").hasRole("ADMIN")

                            .requestMatchers("/borrow/all").hasRole("ADMIN")
                            .requestMatchers("/borrow/**").hasAnyRole("USER", "ADMIN")

                            .anyRequest().authenticated()
                    )
                    .formLogin(form -> form
                            .loginPage("/login.html")
                            .loginProcessingUrl("/login")
                            .defaultSuccessUrl("/index.html", true)
                            .failureUrl("/login.html?error")
                            .permitAll()
                    )
                    .logout(logout -> logout
                            .logoutUrl("/logout")
                            .logoutSuccessUrl("/login.html?logout")
                            .permitAll()
                    );

            return http.build();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to configure security filter chain", ex);
        }
    }
}
