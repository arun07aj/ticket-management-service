import { useRef, useState } from 'react';

const useCaptchaReset = (isCaptchaEnabled) => {
    const [captchaResponse, setCaptchaResponse] = useState('');
    const recaptchaRef = useRef(null);

    const resetCaptcha = () => {
        if (!isCaptchaEnabled) {
            return;
        }

        setCaptchaResponse('');

        if (recaptchaRef.current) {
            recaptchaRef.current.reset();
        }
    };

    return {
        captchaResponse,
        setCaptchaResponse,
        recaptchaRef,
        resetCaptcha,
    };
};

export default useCaptchaReset;
