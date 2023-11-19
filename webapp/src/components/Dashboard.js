import React from 'react';
import { Link } from 'react-router-dom';
import './Dashboard.css'; // Import the CSS file

function Dashboard() {
    return (
        <div className="dashboard-container">
            <h1 className="dashboard-heading">Welcome to the Ticket Management Service</h1>
            <Link to="/create">
                <button>Create a New Ticket</button>
            </Link>
            <Link to="/view">
                <button>View My Tickets</button>
            </Link>
        </div>
    );
}

export default Dashboard;