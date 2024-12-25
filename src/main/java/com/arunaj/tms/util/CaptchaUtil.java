package com.arunaj.tms.util;

import com.arunaj.tms.service.CaptchaVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CaptchaUtil {
    @Autowired
    private CaptchaVerificationService captchaVerificationService;

    public boolean captchaHelper(String captchaResponse) {
        return !captchaVerificationService.verifyCaptcha(captchaResponse);
    }
}
