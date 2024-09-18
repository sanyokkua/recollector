package ua.kostenko.recollector.app.config;

import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ua.kostenko.recollector.app.security.JwtRequestFilter;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Security configuration class for setting up Spring Security in the application.
 * <p>
 * This class configures security settings such as authentication, authorization,
 * CORS (Cross-Origin Resource Sharing), and JWT (JSON Web Token) handling.
 * It uses {@code @Configuration} and {@code @EnableWebSecurity} annotations to
 * indicate that it contains Spring Security configuration.
 * </p>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${recollector.app.jwt.secret}")
    private String jwtSecretKey;

    @Value("${recollector.app.jwt.refresh}")
    private String jwtSecretRefreshKey;

    /**
     * Provides a {@code PasswordEncoder} bean for encoding passwords.
     * <p>
     * This bean uses BCrypt hashing for password encoding, which is a widely
     * accepted and secure method for hashing passwords.
     * </p>
     *
     * @return a {@code PasswordEncoder} instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Provides a {@code CorsConfigurationSource} bean to configure CORS settings.
     * <p>
     * This method sets allowed origins, methods, headers, and credentials for CORS
     * requests. It allows requests from specific origins and methods, and configures
     * the headers that are permitted.
     * </p>
     *
     * @return a {@code CorsConfigurationSource} instance
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:8081"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("Authorization", "*", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Configures the {@code SecurityFilterChain} for HTTP security.
     * <p>
     * This method configures various aspects of security, including disabling CSRF
     * protection, setting up CORS, configuring authorization rules, and adding a
     * JWT filter. It also sets the session management policy to stateless.
     * </p>
     *
     * @param http                           the {@code HttpSecurity} object to configure
     * @param jwtRequestFilter               the {@code JwtRequestFilter} for filtering JWT requests
     * @param customAuthenticationEntryPoint the custom {@code AuthenticationEntryPoint}
     *                                       for handling authentication errors
     * @param customAccessDeniedHandler      the custom {@code AccessDeniedHandler} for handling
     *                                       access denial errors
     *
     * @return a configured {@code SecurityFilterChain} instance
     *
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter,
                                           AuthenticationEntryPoint customAuthenticationEntryPoint,
                                           AccessDeniedHandler customAccessDeniedHandler) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(Customizer.withDefaults());
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(auth -> auth.requestMatchers("/", "/public/**", "/static/**", "/js/**",
                                                                // TODO: investigate why "js" is not under static
                                                                "api/v1/auth/login",
                                                                "api/v1/auth/register", "api/v1/auth/refresh-token",
                                                                "api/v1/auth/forgot-password",
                                                                "api/v1/auth/reset-password",
                                                                "/v3/api-docs/**",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html")
                                               .permitAll()
                                               .anyRequest()
                                               .authenticated());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling(exception -> {
            exception.authenticationEntryPoint(customAuthenticationEntryPoint);
            exception.accessDeniedHandler(customAccessDeniedHandler);
        });
        return http.build();
    }

    /**
     * Provides a {@code SecretKey} bean for JWT signing and verification.
     * <p>
     * This method creates a {@code SecretKey} instance using the configured JWT secret key,
     * which is used for signing and verifying JWT tokens.
     * </p>
     *
     * @return a {@code SecretKey} instance for JWT signing and verification
     */
    @Bean
    public SecretKey jwtSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Provides a {@code SecretKey} bean for JWT refresh token signing and verification.
     * <p>
     * This method creates a {@code SecretKey} instance using the configured JWT refresh
     * secret key, which is used specifically for signing and verifying JWT refresh tokens.
     * </p>
     *
     * @return a {@code SecretKey} instance for JWT refresh token signing and verification
     */
    @Bean
    public SecretKey jwtRefreshSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretRefreshKey.getBytes(StandardCharsets.UTF_8));
    }
}
