import { useEffect, useState } from 'react';
import axios from "axios";

const useFetchUserRole = (baseURL, jwtToken) => {
    const [userRole, setUserRole] = useState(null);
    const [fetchError, setFetchError] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await axios.get(`${baseURL}users/role`, {
                    headers: { Authorization: `Bearer ${jwtToken}` }
                });
                setUserRole(response.data);
            } catch (error) {
                setFetchError('Error fetching user role. Please re-login or try again later.');
            }
        };

        fetchData(); // Invoking the async function directly

    }, [baseURL, jwtToken]);

    return [userRole, fetchError];
};

export default useFetchUserRole;