// App.js
import React, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Home from './components/Home';
import LoginForm from './components/LoginForm';
import Dashboard from './components/Dashboard';
import TicketForm from './components/TicketForm';
import TicketList from './components/TicketList';
import ViewTicket from './components/ViewTicket';

const App = () => {
    const [authenticated, setAuthenticated] = useState(false);

    useEffect(() => {
        // Check if the user is authenticated
        const token = localStorage.getItem('jwtToken');
        if (token) {
            // Additional validation can be performed here if needed
            setAuthenticated(true);
        }
    }, []);

    return (
        <Router>
            <Routes>
                <Route path="/" element={<Home />} />
                <Route
                    path="/login"
                    element={
                        authenticated ? (
                            <Navigate to="/dashboard" />
                        ) : (
                            <LoginForm setAuthenticated={setAuthenticated} />
                        )
                    }
                />
                <Route
                    path="/dashboard"
                    element={authenticated ? <Dashboard /> : <Navigate to="/login" />}
                />
                <Route
                    path="/create"
                    element={authenticated ? <TicketForm /> : <Navigate to="/login" />}
                />
                <Route
                    path="/view"
                    element={authenticated ? <TicketList /> : <Navigate to="/login" />}
                />
                <Route path="/tickets/:id" element={authenticated ? <ViewTicket /> : <Navigate to="/login" />} />
            </Routes>
        </Router>
    );
};

export default App;
