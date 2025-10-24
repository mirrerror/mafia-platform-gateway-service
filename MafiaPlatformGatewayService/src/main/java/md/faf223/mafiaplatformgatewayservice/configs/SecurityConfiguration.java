package md.faf223.mafiaplatformgatewayservice.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/ws/**").permitAll()
                        .requestMatchers("/ws/**").permitAll() // Allow WebSocket proxy endpoints
                        .requestMatchers("/api/chat/**").permitAll()
                        .requestMatchers("/api/rumours/**").permitAll()
                        // Internal Game Service endpoints (called by other services, no JWT required)
                        .requestMatchers(HttpMethod.GET, "/api/game/*/state").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/game/*/players/status").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/game/*/players/*/status").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/game/*/events").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/game/*/players-roles").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/game/*/voting/elimination").permitAll()
                        // Internal User Management endpoint (called by other services, no JWT required)
                        .requestMatchers(HttpMethod.PUT, "/api/users/currency/*").permitAll()
                        // Legacy currency endpoint for backward compatibility with rumours service
                        .requestMatchers(HttpMethod.PUT, "/currency/*").permitAll()
                        // User management endpoints require authentication (handled by JWT filter)
                        .requestMatchers("/api/users/**").authenticated()
                        .requestMatchers("/api/tasks/**").permitAll()
                        .requestMatchers("/api/voting/**").permitAll()
                        .requestMatchers("/api/town/**").permitAll()
                        .requestMatchers("/api/character/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With",
                "Sec-WebSocket-Extensions", "Sec-WebSocket-Key", "Sec-WebSocket-Version", "Origin", "Accept"));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}