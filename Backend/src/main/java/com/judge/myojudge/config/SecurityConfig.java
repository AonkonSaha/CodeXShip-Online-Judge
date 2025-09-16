package com.judge.myojudge.config;

import com.judge.myojudge.jwt.JwtAuthFilter;

import com.judge.myojudge.routes.AuthApiRoute;
import com.judge.myojudge.routes.ProblemApiRoute;
import com.judge.myojudge.service.imp.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig  {
    String[] adminApi={

            ProblemApiRoute.PROBLEM_FETCH_BY_ID_V1,
            ProblemApiRoute.PROBLEM_SAVE,
            ProblemApiRoute.PROBLEM_DELETE_ALL,
            ProblemApiRoute.PROBLEM_DELETE_BY_HANDLE,
            ProblemApiRoute.PROBLEM_UPDATE_BY_ID,
    };
    String[] normalUserApi={
            AuthApiRoute.USER_LOGOUT,
            AuthApiRoute.USER_UPDATE,
            AuthApiRoute.USER_PROFILE,
            AuthApiRoute.USER_UPDATE_PASSWORD,
    };
    String[] contestUserApi={

    };
    String[] publicUserApi={
            AuthApiRoute.USER_REGISTER,
            AuthApiRoute.USER_LOGIN,
            ProblemApiRoute.PROBLEM_FETCH_ALL,
            ProblemApiRoute.PROBLEM_FETCH_BY_ID_V2,
            ProblemApiRoute.PROBLEM_FETCH_BY_CATEGORY,
            "/api/role/**"
    };

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(@Autowired CustomUserDetailsService customUserDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("I am in SecurityFilterChain");
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for APIs
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS globally
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(adminApi).hasRole("ADMIN")
                        .requestMatchers(normalUserApi).hasRole("NORMAL_USER")
                        .requestMatchers("/code/**").hasAnyRole("NORMAL_USER","ADMIN")
                        .requestMatchers(publicUserApi).permitAll()
                        .anyRequest().authenticated() // All other endpoints require authentication
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(
                "*"
                )); // Allow frontend origin

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

