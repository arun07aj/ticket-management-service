import {useEffect, useRef} from 'react';
import { useNavigate } from 'react-router-dom';
import Cookies from 'js-cookie';
import { jwtDecode as jwt_decode } from 'jwt-decode';

const useLogout = ({ setAuthenticated }) => {
    const navigate = useNavigate();
    const logoutCallbackRef = useRef(null);
    const handleLogout = () => {
        // Clear the authentication token
        Cookies.remove('jwtToken', { sameSite: 'None', secure: true });

        // Set the authentication state to false
        setAuthenticated(false);

        // Redirect to the login page upon logout
        navigate('/login');
    };

    const checkTokenExpiration = () => {
        const token = Cookies.get('jwtToken');
        if (token) {
            try {
                const decodedToken = jwt_decode(token);
                if (decodedToken && decodedToken.exp * 1000 <= Date.now()) {
                    // Clear the cookie immediately once the token is found as expired
                    Cookies.remove('jwtToken', { sameSite: 'None', secure: true });
                    // Trigger the logout callback
                    if (logoutCallbackRef.current) {
                        logoutCallbackRef.current();
                    }
                    else {
                        handleLogout()
                    }
                }
            } catch (error) {
                console.error('Error decoding JWT token:', error);
            }
        }
    };

    useEffect(() => {
        // Check token status first
        checkTokenExpiration();

        // Check for token expiration every 15s
        const intervalId = setInterval(checkTokenExpiration, 15000);

        // Cleanup the interval on component unmount
        return () => clearInterval(intervalId);
    }, [setAuthenticated, navigate]);

    const setLogoutCallback = (callback) => {
        logoutCallbackRef.current = callback;
    };

    return { handleLogout, setLogoutCallback };
};

export default useLogout;
