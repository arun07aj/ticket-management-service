import React, {useState} from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import DOMPurify from 'dompurify';
import Cookies from 'js-cookie';
import ReCAPTCHA from "react-google-recaptcha"
import './LoginForm.css'
import useAuthentication from "../hooks/useAuthentication";
import useCaptchaReset from "../hooks/useCaptchaReset";

const LoginForm = ({ setAuthenticated }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');
    const navigate = useNavigate();

    // Check for existing authentication token on component mount
    useAuthentication(setAuthenticated);

    const isCaptchaEnabled = process.env.REACT_APP_ENABLE_CAPTCHA === 'true';
    const { captchaResponse, setCaptchaResponse, recaptchaRef, resetCaptcha } = useCaptchaReset(isCaptchaEnabled);

    const handleLogin = async () => {
        try {
            // Sanitize user input using DOMPurify
            const sanitizedUsername = DOMPurify.sanitize(username);
            const sanitizedPassword = DOMPurify.sanitize(password);

            // Get the base URL from the environment variable
            const baseURL = process.env.REACT_APP_API_BASE_URL;

            // Make API call to login endpoint
            const response = await axios.post(`${baseURL}api/public/login`, {
                username: sanitizedUsername,
                password: sanitizedPassword,
                captchaResponse: isCaptchaEnabled ? captchaResponse : null,
            });

            // API returns a token upon successful login
            const token = response.data;

            // Store the token in a secure cookie
            Cookies.set('jwtToken', token, { secure: true, sameSite: 'None' });

            // Set the authentication state
            setAuthenticated(true);

            // Redirect to Dashboard upon successful login
            navigate('/dashboard');
        } catch (error) {
            // Handle login error
            if (error.message === 'Network Error') {
                setMessage('Unable to connect to the server. Please check your internet connection.');
                resetCaptcha();
                throw new Error('Network Error');
            }
            else if (error.response && error.response.status === 401) {
                setMessage('Invalid username or password. Please try again.');
                resetCaptcha();
                throw new Error('Authentication Error');
            } else if (error.response && error.response.status === 403) {
                setMessage('Invalid CAPTCHA response. Please try again.');
                resetCaptcha();
                throw new Error('CAPTCHA Error');
            } else {
                setMessage('An error occurred during login. Please try again later.');
                resetCaptcha();
                throw error;
            }
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!username || !password) {
            setMessage('Please enter both username and password.');
            return;
        }

        if (isCaptchaEnabled && !captchaResponse) {
            setMessage('Please complete the CAPTCHA verification.');
            return;
        }

        try {
            // Call the login handler and wait for it to complete
            await handleLogin();
        } catch (error) {
            // Handle any errors from the handleLogin function
            console.error('Error during login:', error.message);
        }
    };

    return (
        <div className="login-container">
            <h1 className="login-heading">Login</h1>
            <form className="login-form" onSubmit={handleSubmit}>
                <div className="form-group">
                    <label className="label" htmlFor="username">
                        Username:
                    </label>
                    <input
                        className="input"
                        type="text"
                        id="username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />
                </div>
                <div className="form-group">
                    <label className="label" htmlFor="password">
                        Password:
                    </label>
                    <input
                        className="input"
                        type="password"
                        id="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                </div>
                {isCaptchaEnabled && (
                    <div className="form-group recaptcha-container">
                        <ReCAPTCHA
                            ref={recaptchaRef}
                            sitekey={process.env.REACT_APP_RECAPTCHA_SITE_KEY}
                            onChange={setCaptchaResponse}
                            onExpired={() => setCaptchaResponse('')}
                            onErrored={resetCaptcha}
                        />
                    </div>
                )}
                <button className="button" type="submit">
                    Login
                </button>
            </form>
            <p className="message">{message}</p>
        </div>
    );
};

export default LoginForm;
