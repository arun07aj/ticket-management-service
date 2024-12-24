package com.arunaj.tms.util;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HttpClientUtil {
    private final RestTemplate restTemplate;

    public HttpClientUtil(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> T post(String url, Object requestBody, Class<T> responseType) {
        return restTemplate.postForObject(url, requestBody, responseType);
    }
}
