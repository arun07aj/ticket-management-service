// TicketForm.js
import React, { useState } from 'react';
import axios from 'axios';
import './TicketForm.css'; // Import the CSS file

const TicketForm = () => {
    const [subject, setSubject] = useState('');
    const [description, setDescription] = useState('');
    const [message, setMessage] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Check if subject and description are not null or empty
        if (!subject || !description) {
            setMessage('Please fill out both subject and description.');
            return;
        }

        try {
            // Call the createTicket API
            const response = await axios.post('/tickets/create', { subject, description });

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
        </div>
    );
};

export default TicketForm;
