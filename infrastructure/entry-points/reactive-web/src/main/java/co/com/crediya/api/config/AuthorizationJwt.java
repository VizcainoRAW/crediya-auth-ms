package co.com.crediya.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.spec.SecretKeySpec;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class AuthorizationJwt implements WebFluxConfigurer {

    private final String issuerUri;
    private final String clientId;
    private final String jsonExpRoles;
    private final boolean localAuthEnabled;
    private final String jwtSecret;
    private final ObjectMapper mapper;
    
    private static final String ROLE = "ROLE_";
    private static final String AZP = "azp";

    public AuthorizationJwt(@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri,
                         @Value("${spring.security.oauth2.resourceserver.jwt.client-id}") String clientId,
                         @Value("${jwt.json-exp-roles}") String jsonExpRoles,
                         @Value("${jwt.local-auth.enabled:false}") boolean localAuthEnabled,
                         @Value("${spring.security.oauth2.resourceserver.jwt.secret}") String jwtSecret,
                         ObjectMapper mapper) {
        this.issuerUri = issuerUri;
        this.clientId = clientId;
        this.jsonExpRoles = jsonExpRoles;
        this.localAuthEnabled = localAuthEnabled;
        this.jwtSecret = jwtSecret;
        this.mapper = mapper;
    }

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {

        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(auth -> auth
                .pathMatchers(HttpMethod.POST, "/api/users").hasAnyRole("ADMIN")
                .pathMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/users/exists").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/users/{id}/exists").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/users/exists/by-id").permitAll()
                .pathMatchers("/h2/**").permitAll()
                .pathMatchers("/actuator/**").permitAll()

                .pathMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")

                .pathMatchers(HttpMethod.GET, "/api/users/**").hasAnyRole("USER", "ADMIN")

                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtSpec ->
                    jwtSpec
                        .jwtDecoder(jwtDecoder())
                        .jwtAuthenticationConverter(grantedAuthoritiesExtractor())
                )
            );
        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return NimbusReactiveJwtDecoder.withSecretKey(
            new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256")
        ).build();
    }

    public Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        var jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwt ->
                getRoles(jwt.getClaims(), jsonExpRoles)
                .stream()
                .map(ROLE::concat)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()));
        return new ReactiveJwtAuthenticationConverterAdapter(jwtConverter);
    }

    private List<String> getRoles(Map<String, Object> claims, String jsonExpClaim) {
        Object role = claims.get("role");
        if (role == null) {
            return List.of();
        }
        return List.of(role.toString());
    }
}