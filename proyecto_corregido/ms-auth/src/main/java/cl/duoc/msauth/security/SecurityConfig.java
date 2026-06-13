package cl.duoc.msauth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(c -> c.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/api/v1/auth/v3/api-docs",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/doc/swagger-ui.html",
                                "/webjars/**",
                                "/api/v1/auth/login",
                                "/api/v1/auth/register")
                        .permitAll()
                        .anyRequest().permitAll())
                .build();
    }
}
