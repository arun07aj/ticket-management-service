package com.arunaj.tms.service;

import com.arunaj.tms.util.HttpClientUtil;
import com.arunaj.tms.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CaptchaVerificationService {
    private static final Logger logger = LoggerUtil.getLogger(CaptchaVerificationService.class);

    private final HttpClientUtil httpClientUtil;

    public CaptchaVerificationService(HttpClientUtil httpClientUtil) {
        this.httpClientUtil = httpClientUtil;
    }

    public boolean verifyCaptcha(String captchaResponse) {

        try {
            String url = "https://arunaj.co/verify-captcha";
            Map<String, String> requestBody = Map.of("captchaResponse", captchaResponse);

            Map<String, Object> response = httpClientUtil.post(url, requestBody, Map.class);

            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                logger.info("Captcha verification successful");
                return true;
            } else {
                logger.warn("Captcha verification failed: {}", response != null ? response.get("error-codes") : null);
                return false;
            }
        } catch (Exception ex) {
            logger.error("Error during captcha verification", ex);
            return false;
        }
    }
}