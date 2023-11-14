import React from 'react';
import { Link } from 'react-router-dom';
import './Home.css'; // Import the CSS file

function Home() {
    return (
        <div className="home-container">
            <h1 className="home-heading">Welcome to the Ticket Management Service</h1>
            <Link to="/create-ticket">
                <button>Create a New Ticket</button>
            </Link>
            <Link to="/view-tickets">
                <button>View Tickets</button>
            </Link>
        </div>
    );
}

export default Home;
