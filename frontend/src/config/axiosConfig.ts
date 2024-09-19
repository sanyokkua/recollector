// Create and configure Axios client
import axios from "axios";


const baseUrl = import.meta.env.VITE_APP_BASE_URL;

const axiosClient = axios.create(
    {
        baseURL: `${ baseUrl }/api`,
        headers: {
            "Content-Type": "application/json"
        },
        withCredentials: true
    }
);

export default axiosClient;