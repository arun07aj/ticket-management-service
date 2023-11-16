// TicketList.js
import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import './TicketList.css';

const TicketList = () => {
    const [tickets, setTickets] = useState([]);

    useEffect(() => {
        // Fetch tickets from the /list API endpoint
        axios.get('/tickets/list')
            .then(response => setTickets(response.data))
            .catch(error => console.error('Error fetching tickets:', error));
    }, []);

    return (
        <div className="ticket-list-container">
            <h2 className="table-title">All Tickets Status</h2>
            <table className="ticket-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Subject</th>
                    <th>Description</th>
                    <th>Date</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                {tickets.map((ticket) => (
                    <tr key={ticket.id}>
                        <td>{ticket.id}</td>
                        <td>{ticket.subject}</td>
                        <td>{ticket.description}</td>
                        <td>{ticket.createdDate}</td>
                        <td>{ticket.status}</td>
                        <td>
                            <Link to={`/tickets/${ticket.id}`}>{`View Ticket #${ticket.id}`}</Link>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default TicketList;
