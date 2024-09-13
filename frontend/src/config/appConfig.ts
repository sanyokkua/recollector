import axios  from "axios";
import log    from "loglevel";
import prefix from "loglevel-plugin-prefix";

// Configure logging
prefix.reg(log);
log.enableAll();
log.setLevel("debug");

prefix.apply(log, {
    template: "[%t] %l (%n):",
    levelFormatter(level) {
        return level.toUpperCase();
    },
    nameFormatter(name) {
        return name || "[anonymous]";
    },
    timestampFormatter(date) {
        return date.toISOString();
    }
});

log.info("Initialized logger");

// Create and configure Axios client
const axiosClient = axios.create({
                                     baseURL: "http://localhost:8081/api",
                                     headers: {
                                         "Content-Type": "application/json"
                                     }
                                 });

// Request interceptor
axiosClient.interceptors.request.use(request => {
    log.debug("Starting Request:", JSON.stringify(request, null, 2));
    return request;
}, error => {
    log.error("Request Error:", JSON.stringify(error, null, 2));
    return Promise.reject(error);
});

// Response interceptor
axiosClient.interceptors.response.use(response => {
    log.debug("Response:", JSON.stringify(response, null, 2));
    return response;
}, error => {
    log.error("Response Error:", JSON.stringify(error, null, 2));
    return Promise.reject(error);
});

export const logger = log;
export default axiosClient;
