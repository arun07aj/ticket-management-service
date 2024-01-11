// TicketForm.js
import React, {useEffect, useState} from 'react';
import axios from 'axios';
import DOMPurify from 'dompurify';
import Cookies from 'js-cookie';
import './TicketForm.css';
import useLogout from '../hooks/useLogout';
import LogoutPopup from './LogoutPopup';

const TicketForm = ({setAuthenticated}) => {
    const [subject, setSubject] = useState('');
    const [description, setDescription] = useState('');
    const [message, setMessage] = useState('');

    const { handleLogout, setLogoutCallback } = useLogout({ setAuthenticated });
    const [showLogoutPopup, setShowLogoutPopup] = useState(false);

    useEffect(() => {
        // Set the callback function in the useLogout hook
        setLogoutCallback(() => {
            // When the callback is triggered, show the logout popup
            setShowLogoutPopup(true);
        });
    }, [setLogoutCallback]);

    const closeLogoutPopup = () => {
        // Close the logout popup
        setShowLogoutPopup(false);

        // Perform the actual logout
        handleLogout();
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Sanitize user input using DOMPurify
        const sanitizedSubject = DOMPurify.sanitize(subject);
        const sanitizedDescription = DOMPurify.sanitize(description);

        // Check if sanitized subject and description are not null or empty
        if (!sanitizedSubject || !sanitizedDescription) {
            setMessage('Please fill out both subject and description.');
            return;
        }

        try {
            // Retrieve the JWT token from the cookie
            const jwtToken = Cookies.get('jwtToken');

            // Get the base URL from the environment variable
            const baseURL = process.env.REACT_APP_API_BASE_URL;

            // Call the createTicket API
            const response = await axios.post(`${baseURL}tickets/create`,
                { subject: sanitizedSubject, description: sanitizedDescription },
                {headers: {Authorization: `Bearer ${jwtToken}`}});

            // Handle the successful response
            setMessage(`Ticket created with ID: ${response.data.id}`);
            // Clear textboxes
            setSubject('');
            setDescription('');
        } catch (error) {
            // Handle API error
            setMessage('Error creating ticket. Please try again.');
        }
    };

    return (
        <div className="ticket-container">
            <h1 className="ticket-heading">Create Ticket</h1>
            <form className="ticket-form" onSubmit={handleSubmit}>
                <div className="form-group">
                    <label className="label" htmlFor="subject">
                        Subject:
                    </label>
                    <input
                        className="input"
                        type="text"
                        id="subject"
                        value={subject}
                        onChange={(e) => setSubject(e.target.value)}
                    />
                </div>
                <div className="form-group">
                    <label className="label" htmlFor="description">
                        Description:
                    </label>
                    <textarea
                        className="description-input"
                        id="description"
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                    />
                </div>
                <button className="button" type="submit">
                    Submit
                </button>
            </form>
            <p className="message">{message}</p>
            {showLogoutPopup && <LogoutPopup onClose={closeLogoutPopup} />}
        </div>
    );
};

export default TicketForm;
