// TicketList.js
import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import Cookies from 'js-cookie';
import useLogout from '../hooks/useLogout';
import LogoutPopup from './LogoutPopup';
import './TicketList.css';
import useFetchUserRole from "../hooks/useFetchUserRole";

const TicketList = ({setAuthenticated}) => {
    const [tickets, setTickets] = useState([]);
    // Retrieve the JWT token from the cookie
    const jwtToken = Cookies.get('jwtToken');

    // Get the base URL from the environment variable
    const baseURL = process.env.REACT_APP_API_BASE_URL;

    const [userRole, fetchError] = useFetchUserRole(baseURL, jwtToken);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Fetch tickets from the /list API endpoint
        if(userRole === 'USER') {
            axios.get(`${baseURL}tickets/list/my`, {headers: {Authorization: `Bearer ${jwtToken}`}})
                .then(response => {
                    setTickets(response.data);
                    setLoading(false);
                })
                .catch(() => {
                    setError('Error fetching tickets');
                    setLoading(false);
                });
        }
        else if(userRole === 'ADMIN') {
            axios.get(`${baseURL}tickets/list`, {headers: {Authorization: `Bearer ${jwtToken}`}})
                .then(response => {
                    setTickets(response.data);
                    setLoading(false);
                })
                .catch(() => {
                    setError('Error fetching tickets');
                    setLoading(false);
                });
        }
    }, [baseURL, jwtToken, userRole]);

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
            <h2 className="table-title">Tickets</h2>
            {fetchError && !error && <div className="error-message">{fetchError}</div>}
            {error && !fetchError && <div className="error-message">{error}</div>}
            {loading && !(fetchError || error) && <div className="loading-message">Loading...</div>}
            {!(fetchError || error) && !loading && (
                <React.Fragment>
                    {tickets.length === 0 ? (
                        <div className="message">No tickets found.</div>
                    ) : (
                        <table className="ticket-table">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Creator</th>
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
                                    <td>{ticket.creatorEmail}</td>
                                    <td>{ticket.subject}</td>
                                    <td>{ticket.createdDate}</td>
                                    <td>{ticket.status}</td>
                                    <td>
                                        <Link to={`/tickets/${ticket.id}`}>{`View Ticket #${ticket.id}`}</Link>
                                    </td>
                                </tr>
                            ))
                            }
                            </tbody>
                        </table>
                        )
                    }
                </React.Fragment>
            )}
            {showLogoutPopup && <LogoutPopup onClose={closeLogoutPopup} />}
        </div>
    );
};

export default TicketList;
