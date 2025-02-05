package com.edceo.springkeycloakdemo.configuration.pool;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "http.pool")
public class HttpConnectionPoolProperties {

    private int maxPerRoute = 10;

    private int maxTotal = 200;

    private int responseTimeout = 30000;

    private int connectionRequestTimeout = 30000;

    private int connectTimeout = 30000;

    private int socketTimeout = 30000;

    private int idleTimeout = 30000;
}
