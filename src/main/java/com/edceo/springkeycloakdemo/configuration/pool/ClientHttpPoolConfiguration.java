package com.edceo.springkeycloakdemo.configuration.pool;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.DefaultConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.impl.DefaultConnectionReuseStrategy;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ClientHttpPoolConfiguration {

    private final HttpConnectionPoolProperties httpConnectionPoolProperties;
    private PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;

    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        this.poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(httpConnectionPoolProperties.getMaxPerRoute());
        poolingHttpClientConnectionManager.setMaxTotal(httpConnectionPoolProperties.getMaxTotal());
        return poolingHttpClientConnectionManager;
    }

    @Bean
    public CloseableHttpClient httpClient(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager) {
        return HttpClientBuilder
                .create()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE) // Ensure connections are reused
                .setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE)   // Handle keep-alive logic
                .evictExpiredConnections()
                .evictIdleConnections(TimeValue.ofMilliseconds(httpConnectionPoolProperties.getIdleTimeout()))
                .build();
    }

    @PreDestroy
    public void cleanup() {
        if (poolingHttpClientConnectionManager != null) {
            log.info("poolingHttpClientConnectionManager closed");
            poolingHttpClientConnectionManager.close();
        }
    }

}
