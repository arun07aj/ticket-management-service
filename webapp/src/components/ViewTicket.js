// ViewTicket.js
import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import DOMPurify from 'dompurify';
import Cookies from 'js-cookie';
import useLogout from '../hooks/useLogout';
import LogoutPopup from './LogoutPopup';
import './ViewTicket.css';
import useFetchUserRole from "../hooks/useFetchUserRole";

const ViewTicket = ({ setAuthenticated }) => {
    const { id } = useParams();
    // Retrieve the JWT token from the cookie
    const jwtToken = Cookies.get('jwtToken');

    // Get the base URL from the environment variable
    const baseURL = process.env.REACT_APP_API_BASE_URL;

    const [loading, setLoading] = useState(true);
    const [ticketDetails, setTicketDetails] = useState(null);
    const [comments, setComments] = useState('');
    const [error, setError] = useState(null);
    const [message, setMessage] = useState('');
    const [userRole, fetchError] = useFetchUserRole(baseURL, jwtToken);

    const isAdmin = userRole === 'ADMIN';
    const [selectedStatusByAdmin, setSelectedStatusByAdmin] = useState('');
    const isSubmitDisabled = isAdmin && !selectedStatusByAdmin;
    const [resolutionAccepted, setResolutionAccepted] = useState(false);
    const [commentFormDisabled, setCommentFormDisabled] = useState(false);

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

    useEffect(() => {
        axios.get(`${baseURL}tickets/list/${id}`, {headers: {Authorization: `Bearer ${jwtToken}`}})
            .then(response => {
                setTicketDetails(response.data);
                setLoading(false);
                if (response.data) {
                    setCommentFormDisabled(!isAdmin && response.data.status === "RESOLVED");
                }
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
    }, [id, baseURL, jwtToken]);

    // Function to handle drop-down selection change
    const handleStatusChangeByAdmin = (e) => {
        setSelectedStatusByAdmin(e.target.value);
    };

    const updateTicketDetails = async (comment, updatedStatus) => {
        try {
            // Call the backend API to update the ticket
            await axios.patch(`${baseURL}tickets/edit/${id}`, { comment, updatedStatus }, { headers: { Authorization: `Bearer ${jwtToken}` } });

            // Refresh ticket details after update
            const response = await axios.get(`${baseURL}tickets/list/${id}`, { headers: { Authorization: `Bearer ${jwtToken}` } });
            setTicketDetails(response.data);

            // Clear the comment textbox and selectedStatusByAdmin
            setComments('');
            setSelectedStatusByAdmin('');
            setMessage('Comment added successfully.');
        } catch (error) {
            setError('Error updating ticket details, please try again later.');
        }
    };

    const handleCommentSubmit = async (e) => {
        e.preventDefault();

        // Sanitize user input using DOMPurify
        const sanitizedComment = DOMPurify.sanitize(comments);

        // Check if sanitized description is  not null or empty
        if (!sanitizedComment) {
            setMessage('Comment cannot be empty.');
            return;
        }

        // Ensure the selectedStatusByAdmin is not empty if user is admin
        if (isAdmin && !selectedStatusByAdmin) {
            setMessage('Please select a status.');
            return;
        }

        setMessage('Adding comment in progress, please wait...');
        await updateTicketDetails(sanitizedComment, selectedStatusByAdmin);
    };

    const handleAcceptResolution = async () => {
        // Sanitize user input using DOMPurify
        const sanitizedComment = DOMPurify.sanitize(comments);

        // Check if sanitized description is  not null or empty
        if (!sanitizedComment) {
            setMessage('Comment cannot be empty.');
            return;
        }

        setMessage('Updating resolution and comment, please wait...');
        await updateTicketDetails(sanitizedComment, 'RESOLVED');
        setResolutionAccepted(true);
        setCommentFormDisabled(true);
    };

    const handleRejectResolution = async () => {
        // Sanitize user input using DOMPurify
        const sanitizedComment = DOMPurify.sanitize(comments);

        // Check if sanitized description is  not null or empty
        if (!sanitizedComment) {
            setMessage('Comment cannot be empty.');
            return;
        }

        setMessage('Updating resolution and comment, please wait...');
        await updateTicketDetails(sanitizedComment, 'OPEN');
        setResolutionAccepted(false);
        setCommentFormDisabled(false);
    };

    return (
        <div className="ticket-container">
            {(fetchError || error || loading) && (
                <h1 style={{ textAlign: 'center' }}>View Ticket</h1>
            )}
            <div className="error-loading-message">
                {(fetchError || error) && <div className="error-message">{error}</div>}
                {loading && !(fetchError || error) && <div className="loading-message">Loading...</div>}
            </div>
            {!(fetchError || error) && !loading && ticketDetails && (
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

                    <form className="comment-form">
                        <div className="form-group">
                            <label htmlFor="comments">Add Comment</label>
                            <textarea
                                id="comments"
                                value={comments}
                                onChange={(e) => setComments(e.target.value)}
                                disabled={commentFormDisabled}
                            />
                        </div>
                        <div className="form-group">
                            {isAdmin && (
                                <div className="dropdown-container">
                                    <label htmlFor="status">Select Status</label>
                                    <select id="status" value={selectedStatusByAdmin} onChange={handleStatusChangeByAdmin}>
                                        <option value="">Select</option>
                                        <option value="WIP">WIP</option>
                                        <option value="MARK AS RESOLVED">MARK AS RESOLVED</option>
                                    </select>
                                </div>
                            )}

                        </div>
                        <div className="form-group">
                            {!isAdmin && ticketDetails.status === "MARK AS RESOLVED" && !(resolutionAccepted || commentFormDisabled) && (
                                <div className="resolution-container">
                                    <label>Accept or Reject Resolution:</label>
                                    <button type="button" onClick={handleAcceptResolution}>Accept</button>
                                    <button type="button" onClick={handleRejectResolution} className="reject-button">Reject</button>
                                </div>
                            )}
                            {(isAdmin || (!isAdmin && (ticketDetails.status !== "MARK AS RESOLVED" && ticketDetails.status !== "RESOLVED"))) && (
                                <button type="button" onClick={handleCommentSubmit} disabled={isSubmitDisabled}>Submit</button>
                            )}
                            {!isAdmin && ticketDetails.status === "RESOLVED" && (
                                <label>You can no longer add comments to this ticket.</label>
                            )}
                        </div>
                    </form>
                    <p className="comment-message">{message}</p>
                </React.Fragment>
            )}
            {showLogoutPopup && <LogoutPopup onClose={closeLogoutPopup} />}
        </div>
    );
};

export default ViewTicket;