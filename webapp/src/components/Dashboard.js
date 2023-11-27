import React, {useEffect, useState} from 'react';
import {Link} from 'react-router-dom';
import './Dashboard.css';
import useLogout from '../hooks/useLogout';
import LogoutPopup from './LogoutPopup';

function Dashboard({ setAuthenticated }) {
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
        <div className="dashboard-container">
            <h1 className="dashboard-heading">Welcome to the Ticket Management Service</h1>
            <Link to="/create">
                <button>Create a New Ticket</button>
            </Link>
            <Link to="/view">
                <button>View My Tickets</button>
            </Link>
            <button onClick={handleLogout}>Logout</button>
            {showLogoutPopup && <LogoutPopup onClose={closeLogoutPopup} />}
        </div>
    );
}

export default Dashboard;