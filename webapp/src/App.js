// App.js
import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Home from './components/Home';
import LoginForm from './components/LoginForm';
import Dashboard from './components/Dashboard';
import TicketForm from './components/TicketForm';
import TicketList from './components/TicketList';
import ViewTicket from './components/ViewTicket';
import SignupForm from './components/SignupForm';
import ErrorPage from './components/ErrorPage';
import useAuthentication from "./hooks/useAuthentication";

const App = () => {
    const [authenticated, setAuthenticated] = useAuthentication();
    return (
        <Router basename="/tmsapp">
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/signup" element={<SignupForm />} />
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
                    element={authenticated ? <Dashboard setAuthenticated={setAuthenticated} /> : <Navigate to="/login" />}
                />
                <Route
                    path="/create"
                    element={authenticated ? <TicketForm setAuthenticated={setAuthenticated} /> : <Navigate to="/login" />}
                />
                <Route
                    path="/view"
                    element={authenticated ? <TicketList setAuthenticated={setAuthenticated} /> : <Navigate to="/login" />}
                />
                <Route path="/tickets/:id" element={authenticated ? <ViewTicket setAuthenticated={setAuthenticated} /> : <Navigate to="/login" />} />
            </Routes>
        </Router>
    );
};

export default App;
