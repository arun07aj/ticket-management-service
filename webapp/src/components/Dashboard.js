import React from 'react';
import {Link, useNavigate} from 'react-router-dom';
import Cookies from 'js-cookie';
import './Dashboard.css'; // Import the CSS file

function Dashboard({ setAuthenticated }) {
    const navigate = useNavigate();

    const handleLogout = () => {
        // Clear the authentication token
        Cookies.remove('jwtToken');

        // Set the authentication state to false
        setAuthenticated(false);

        // Redirect to the login page upon logout
        navigate('/login');
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
        </div>
    );
}

export default Dashboard;