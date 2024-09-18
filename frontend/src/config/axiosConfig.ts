// Create and configure Axios client
import axios from "axios";


const axiosClient = axios.create(
    {
        baseURL: "http://localhost:8081/api",
        headers: {
            "Content-Type": "application/json"
        },
        withCredentials: true
    }
);

export default axiosClient;