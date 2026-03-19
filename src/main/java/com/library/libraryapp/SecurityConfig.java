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
                                    "/",
                                    "/index.html",
                                    "/login.html",
                                    "/register.html",
                                    "/books.html",
                                    "/admin.html",
                                    "/style.css",
                                    "/app.js",
                                    "/bg.png",
                                    "/favicon.ico"
                            ).permitAll()
                            .requestMatchers("/auth/register").permitAll()
                            .requestMatchers(HttpMethod.GET, "/books").authenticated()
                            .requestMatchers(HttpMethod.GET, "/books/search").authenticated()
                            .requestMatchers(HttpMethod.POST, "/books").hasRole(ROLE_ADMIN)
                            .requestMatchers(HttpMethod.PUT, "/books/**").hasRole(ROLE_ADMIN)
                            .requestMatchers(HttpMethod.DELETE, "/books/**").hasRole(ROLE_ADMIN)
                            .requestMatchers(HttpMethod.GET, "/borrow/my").authenticated()
                            .requestMatchers(HttpMethod.POST, "/borrow/*").authenticated()
                            .requestMatchers(HttpMethod.GET, "/borrow/all").hasRole(ROLE_ADMIN)
                            .requestMatchers(HttpMethod.PUT, "/borrow/*/approve").hasRole(ROLE_ADMIN)
                            .requestMatchers(HttpMethod.PUT, "/borrow/*/reject").hasRole(ROLE_ADMIN)
                            .requestMatchers("/admin.html").hasRole(ROLE_ADMIN)
                            .anyRequest().authenticated()
                    )
                    .formLogin(form -> form
                            .loginPage("/login.html")
                            .loginProcessingUrl("/login")
                            .defaultSuccessUrl("/books.html", true)
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
