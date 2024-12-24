package com.arunaj.tms.config;

import com.arunaj.tms.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class HttpClientConfig {
    private static final Logger logger = LoggerUtil.getLogger(HttpClientConfig.class);

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(10))
                .interceptors((request, body, execution) -> {
                    logger.info("Request URI: {}, Request Method: {}", request.getURI(), request.getMethod());
                    return execution.execute(request, body);
                })
                .build();
    }
}
