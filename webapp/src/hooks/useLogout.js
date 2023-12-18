import {useEffect} from 'react';
import { useNavigate } from 'react-router-dom';
import Cookies from 'js-cookie';
import { jwtDecode as jwt_decode } from 'jwt-decode';

const useLogout = ({ setAuthenticated }) => {
    const navigate = useNavigate();
    let logoutCallback = null;
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
                const decodedToken = jwt_decode(JSON.stringify(token));
                if (decodedToken && decodedToken.exp * 1000 <= Date.now()) {
                    // If the token is about to expire, trigger the logout callback
                    if (logoutCallback) {
                        logoutCallback();
                    }
                }
            } catch (error) {
                console.error('Error decoding JWT token:', error);
            }
        }
    };

    useEffect(() => {
        // Check for token expiration every 5s
        const intervalId = setInterval(checkTokenExpiration, 5000);

        // Cleanup the interval on component unmount
        return () => clearInterval(intervalId);
    }, [setAuthenticated, navigate]);

    const setLogoutCallback = (callback) => {
        logoutCallback = callback;
    };

    return { handleLogout, setLogoutCallback };
};

export default useLogout;
