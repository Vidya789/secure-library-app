package com.library.libraryapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/login.html",
                                "/register.html",
                                "/books.html",
                                "/style.css",
                                "/app.js"
                        ).permitAll()

                        .requestMatchers("/auth/register").permitAll()

                        .requestMatchers(HttpMethod.GET, "/books").authenticated()
                        .requestMatchers(HttpMethod.GET, "/books/search").authenticated()

                        .requestMatchers(HttpMethod.POST, "/books").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/books/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/books/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/borrow/my").authenticated()
                        .requestMatchers(HttpMethod.POST, "/borrow/*").authenticated()

                        .requestMatchers(HttpMethod.GET, "/borrow/all").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/borrow/*/approve").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/borrow/*/reject").hasRole("ADMIN")

                        .requestMatchers("/admin.html").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults());

        return http.build();
    }
}