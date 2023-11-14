// src/components/Home.js
import React from 'react';
import { Link } from 'react-router-dom';
import './Home.css';

function Home() {
    return (
        <div>
            <h1>Welcome to the Ticket App</h1>
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
