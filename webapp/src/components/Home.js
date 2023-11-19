import React from 'react';
import { Link } from 'react-router-dom';
import './Home.css'; // Import the CSS file

function Home() {
    return (
        <div className="home-container">
            <h1 className="home-heading">Welcome to the Ticket Management Service</h1>
            <Link to="/login">
                <button>Login</button>
            </Link>
            <Link to="/signup">
                <button>Sign Up</button>
            </Link>
        </div>
    );
}

export default Home;
