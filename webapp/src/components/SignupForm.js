import React, { useState } from 'react';
import axios from 'axios';
import DOMPurify from 'dompurify';
import './SignupForm.css'

const SignupForm = () => {
    const [email, setEmail] = useState('');
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');

    const sanitizeInput = (input) => {
        return DOMPurify.sanitize(input.trim());
    };

    const handleSignup = async () => {
        try {
            const sanitizedEmail = sanitizeInput(email);
            const sanitizedUsername = sanitizeInput(username);
            const sanitizedPassword = sanitizeInput(password);

            // Get the base URL from the environment variable
            const baseURL = process.env.REACT_APP_API_BASE_URL;

            const response = await axios.post(`${baseURL}api/public/signup`, {
                email: sanitizedEmail,
                username: sanitizedUsername,
                password: sanitizedPassword
            });

            setMessage(`${response.data}`);

            // Clear textboxes
            setUsername('');
            setEmail('');
            setPassword('');

        } catch (error) {
            if(error.response.status === 400 || error.response.status === 409) {
                setMessage(error.response.data);
            }
            else {
                console.error('Error during signup:', error);
                setMessage('An error occurred during signup. Please try again later.');
            }
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!email || !username || !password) {
            setMessage('Please enter all required fields.');
            return;
        }

        try {
            await handleSignup();
        } catch (error) {
            console.error('Error during signup:', error.message);
        }
    };

    return (
        <div className="signup-container">
            <h1 className="signup-heading">Sign Up</h1>
            <form className="signup-form" onSubmit={handleSubmit}>
                <div className="form-group">
                    <label className="label" htmlFor="email">
                        Email:
                    </label>
                    <input
                        className="input"
                        type="text"
                        id="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />
                </div>
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
                <button className="button" type="submit">
                    Sign Up
                </button>
            </form>
            <p className="message">{message}</p>
        </div>
    );

};

export default SignupForm;
