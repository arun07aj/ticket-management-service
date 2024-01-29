import { useEffect } from 'react';
import Cookies from 'js-cookie';

const useAuthentication = (setAuthenticated) => {
    useEffect(() => {
        // Check for existing authentication token on component mount
        const token = Cookies.get('jwtToken');
        if (token) {
            // Token exists, set the authentication state to true
            setAuthenticated(true);
        }
    }, [setAuthenticated]);
};

export default useAuthentication;