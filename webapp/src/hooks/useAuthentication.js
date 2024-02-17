import {useState} from 'react';
import Cookies from 'js-cookie';

const useAuthentication = () => {
    const [authenticated, setAuthenticated] = useState(() => {
        const token = Cookies.get('jwtToken');
        return !!token;
    });

    return [authenticated, setAuthenticated];
};

export default useAuthentication;