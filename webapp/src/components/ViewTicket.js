// ViewTicket.js
import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import DOMPurify from 'dompurify';

const ViewTicket = () => {
    const { id } = useParams();

    const [ticketDetails, setTicketDetails] = useState(null);
    const [comments, setComments] = useState('');
    const [error, setError] = useState(null);
    const [message, setMessage] = useState('');

    useEffect(() => {
        axios.get(`/tickets/list/${id}`)
            .then(response => setTicketDetails(response.data))
            .catch(error => {
                console.error('Error fetching ticket details:', error);
                setError(error);
            });
    }, [id]);

    const handleCommentSubmit = async (e) => {
        e.preventDefault();

        // Sanitize user input using DOMPurify
        const sanitizedDesc = DOMPurify.sanitize(comments);

        // Check if sanitized description is  not null or empty
        if (!sanitizedDesc) {
            setMessage('Comment cannot be empty.');
            return;
        }

        try {
            // Call the backend API to update the ticket with new comments
            await axios.patch(`/tickets/edit/${id}`, { updatedDescription: sanitizedDesc });

            // Refresh ticket details after update
            const response = await axios.get(`/tickets/list/${id}`);
            setTicketDetails(response.data);

            // Clear the comments textbox
            setComments('');
            setMessage('Comment added successfully.')
        } catch (error) {
            console.error('Error updating ticket comments:', error);
            setError(error);
        }
    };

    if (error) {
        return <div>Error loading ticket details. Please try again later.</div>;
    }

    if (!ticketDetails) {
        return <p>Loading...</p>;
    }

    return (
        <div>
            <h1>{`View Ticket #${ticketDetails.id}`}</h1>
            <p><strong>Subject:</strong> {ticketDetails.subject}</p>
            <p><strong>Description:</strong> <div dangerouslySetInnerHTML={{ __html: ticketDetails.description }} /></p>
            <p><strong>Created Time:</strong> {ticketDetails.createdDate}</p>
            <p><strong>Last Updated Time:</strong> {ticketDetails.lastUpdatedDate}</p>
            <p><strong>Status:</strong> {ticketDetails.status}</p>


            <form onSubmit={handleCommentSubmit}>
                <div>
                    <label htmlFor="comments">Add Comment</label>
                    <textarea
                        id="comments"
                        value={comments}
                        onChange={(e) => setComments(e.target.value)}
                    />
                </div>
                <button type="submit">Submit</button>
            </form>
            <p className="message">{message}</p>
        </div>
    );
};

export default ViewTicket;
