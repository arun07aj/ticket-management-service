// ViewTicket.js
import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import DOMPurify from 'dompurify';
import Cookies from 'js-cookie';
import useLogout from '../hooks/useLogout';
import LogoutPopup from './LogoutPopup';
import './ViewTicket.css';

const ViewTicket = ({ setAuthenticated }) => {
    const { id } = useParams();
    // Retrieve the JWT token from the cookie
    const jwtToken = Cookies.get('jwtToken');

    const [loading, setLoading] = useState(true);
    const [ticketDetails, setTicketDetails] = useState(null);
    const [comments, setComments] = useState('');
    const [error, setError] = useState(null);
    const [message, setMessage] = useState('');

    const { handleLogout, setLogoutCallback } = useLogout({ setAuthenticated });
    const [showLogoutPopup, setShowLogoutPopup] = useState(false);

    // Get the base URL from the environment variable
    const baseURL = process.env.REACT_APP_API_BASE_URL;

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

    useEffect(() => {
        axios.get(`${baseURL}tickets/list/${id}`, {headers: {Authorization: `Bearer ${jwtToken}`}})
            .then(response => {
                setTicketDetails(response.data);
                setLoading(false);
            })
            .catch(error => {
                setLoading(false);
                if (error.response && error.response.status === 403) {
                    setError("Sorry, you are not authorized to access the ticket.");
                } else if (error.response && error.response.status === 404) {
                    setError("Sorry, the ticket does not exist.");
                } else {
                    setError("Error loading ticket details. Please try again later.");
                }
            });
    }, [id]);

    const handleCommentSubmit = async (e) => {
        e.preventDefault();

        // Sanitize user input using DOMPurify
        const sanitizedComment = DOMPurify.sanitize(comments);

        // Check if sanitized description is  not null or empty
        if (!sanitizedComment) {
            setMessage('Comment cannot be empty.');
            return;
        }

        try {
            // Call the backend API to update the ticket with new comments
            await axios.patch(`${baseURL}tickets/edit/${id}`,{ comment: sanitizedComment }, {headers: {Authorization: `Bearer ${jwtToken}`}});

            // Refresh ticket details after update
            const response = await axios.get(`${baseURL}tickets/list/${id}`, {headers: {Authorization: `Bearer ${jwtToken}`}});
            setTicketDetails(response.data);

            // Clear the comments textbox
            setComments('');
            setMessage('Comment added successfully.')
        } catch (error) {
            setError('Error updating ticket comments, please try again later.');
        }
    };

    return (
        <div className="ticket-container">
            {(error || loading) && (
                <h1 style={{ textAlign: 'center' }}>View Ticket</h1>
            )}
            <div className="error-loading-message">
                {error && <div className="error-message">{error}</div>}
                {loading && <div className="loading-message">Loading...</div>}
            </div>
            {!error && !loading && ticketDetails && (
                <React.Fragment>
                    <h1 style={{ textAlign: 'center' }}>{`View Ticket #${ticketDetails.id}`}</h1>
                    <div className="ticket-details">
                        <p><strong>Subject:</strong> {ticketDetails.subject}</p>
                        <p><strong>Ticket Raised By:</strong> {ticketDetails.creatorEmail}</p>
                        <p><strong>Created Time:</strong> {ticketDetails.createdDate} | <strong>Status:</strong> {ticketDetails.status}</p>
                        <div><strong>Description:</strong> <div dangerouslySetInnerHTML={{ __html: ticketDetails.description }} /></div>
                    </div>

                    <div className="comments-section">
                        <p><strong>Comments</strong></p>
                        {ticketDetails.comments.map((comment) => (
                            <div key={comment.id} className="comment">
                                <p>{comment.username}: {comment.content} [{comment.commentTime}]</p>
                            </div>
                        ))}
                    </div>

                    <form className="comment-form" onSubmit={handleCommentSubmit}>
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
                    <p className="comment-message">{message}</p>
                </React.Fragment>
            )}
            {showLogoutPopup && <LogoutPopup onClose={closeLogoutPopup} />}
        </div>
    );
};

export default ViewTicket;