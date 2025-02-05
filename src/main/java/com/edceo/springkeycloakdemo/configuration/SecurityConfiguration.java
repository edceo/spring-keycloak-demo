package com.edceo.springkeycloakdemo.configuration;

import com.edceo.springkeycloakdemo.exception.PolicyEnforcerConfigException;
import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.authorization.integration.jakarta.ServletPolicyEnforcerFilter;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.keycloak.util.JsonSerialization;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentMap;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Value("${owner.policy-enforcer.content}")
    private String policyEnforcerContent;

    private static final String JWK_CACHE = "jwk-set-cache";
    private static final int CACHE_TIME = 2;

    @Bean
    public JwtDecoder jwtDecoder(RestTemplate restTemplate, OAuth2ResourceServerProperties oAuth2ResourceServerProperties) {
        ConcurrentMap<Object, Object> cacheMap = CacheBuilder.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(CACHE_TIME))
                .build().asMap();

        ConcurrentMapCache jwkSetCache = new ConcurrentMapCache(JWK_CACHE, cacheMap, false);

        return NimbusJwtDecoder.withJwkSetUri(oAuth2ResourceServerProperties.getJwt().getJwkSetUri())
                .restOperations(restTemplate)
                .cache(jwkSetCache)
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.addFilterAfter(createPolicyEnforcerFilter(), BearerTokenAuthenticationFilter.class);
        http.oauth2ResourceServer(t -> t.jwt(withDefaults()));
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private PolicyEnforcerFilter createPolicyEnforcerFilter() {
        return new PolicyEnforcerFilter(getServletPolicyEnforcerFilter());
    }

    private ServletPolicyEnforcerFilter getServletPolicyEnforcerFilter() {
        PolicyEnforcerConfig policyEnforcerConfig;
        try {
            policyEnforcerConfig = JsonSerialization.readValue(policyEnforcerContent, PolicyEnforcerConfig.class);
        } catch (IOException e) {
            throw new PolicyEnforcerConfigException("Error loading configuration file", e);
        }

        return new ServletPolicyEnforcerFilter(request -> policyEnforcerConfig);
    }

}
