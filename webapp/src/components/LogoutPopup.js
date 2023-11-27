import React from 'react';
import './LogoutPopup.css';

const LogoutPopup = ({ onClose }) => {
    return (
        <div className="popup-overlay">
            <div className="popup">
                <p>You've been logged out due to session expiry.</p>
                <p>Click here to Login again</p>
                <button onClick={onClose}>Login</button>
            </div>
        </div>
    );
};

export default LogoutPopup;
