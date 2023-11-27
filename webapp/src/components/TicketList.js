// TicketList.js
import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import Cookies from 'js-cookie';
import useLogout from '../hooks/useLogout';
import LogoutPopup from './LogoutPopup';
import './TicketList.css';

const TicketList = ({setAuthenticated}) => {
    const [tickets, setTickets] = useState([]);
    // Retrieve the JWT token from the cookie
    const jwtToken = Cookies.get('jwtToken');

    useEffect(() => {
        // Fetch tickets from the /list API endpoint
        axios.get('/tickets/list/my', {headers: {Authorization: `Bearer ${jwtToken}`}})
            .then(response => setTickets(response.data))
            .catch(error => console.error('Error fetching tickets:', error));
    }, []);

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

    return (
        <div className="ticket-list-container">
            <h2 className="table-title">My Tickets</h2>
            <table className="ticket-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Subject</th>
                    <th>Created Date</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                {tickets.map((ticket) => (
                    <tr key={ticket.id}>
                        <td>{ticket.id}</td>
                        <td>{ticket.subject}</td>
                        <td>{ticket.createdDate}</td>
                        <td>{ticket.status}</td>
                        <td>
                            <Link to={`/tickets/${ticket.id}`}>{`View Ticket #${ticket.id}`}</Link>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
            {showLogoutPopup && <LogoutPopup onClose={closeLogoutPopup} />}
        </div>
    );
};

export default TicketList;
