// TicketForm.js
import React, { useState } from 'react';
import axios from 'axios';

const TicketForm = () => {
    const [subject, setSubject] = useState('');
    const [description, setDescription] = useState('');
    const [message, setMessage] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();

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
        <div>
            <h1>Create Ticket</h1>
            <form onSubmit={handleSubmit}>
                <label>
                    Subject:
                    <input type="text" value={subject} onChange={(e) => setSubject(e.target.value)} />
                </label>
                <br />
                <label>
                    Description:
                    <input type="text" value={description} onChange={(e) => setDescription(e.target.value)} />
                </label>
                <br />
                <button type="submit">Submit</button>
            </form>
            <p>{message}</p>
        </div>
    );
};

export default TicketForm;
