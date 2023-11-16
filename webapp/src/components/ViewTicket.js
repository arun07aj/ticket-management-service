// ViewTicket.js
import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';

const ViewTicket = () => {
    const { id } = useParams();

    const [ticketDetails, setTicketDetails] = useState(null);
    const [error, setError] = useState(null);

    useEffect(() => {
        axios.get(`/tickets/list/${id}`)
            .then(response => setTicketDetails(response.data))
            .catch(error => {
                console.error('Error fetching ticket details:', error);
                setError(error);
            });
    }, [id]);

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
            <p><strong>Description:</strong> {ticketDetails.description}</p>
            <p><strong>Created Time:</strong> {ticketDetails.createdDate}</p>
            <p><strong>Last Updated Time:</strong> {ticketDetails.lastUpdatedDate}</p>
            <p><strong>Status:</strong> {ticketDetails.status}</p>
        </div>
    );
};

export default ViewTicket;
