import log from "loglevel";
import prefix from "loglevel-plugin-prefix";
import axios from "axios";

prefix.reg(log);
log.enableAll();
log.setLevel('debug');
prefix.apply(log, {
    template: '[%t] %l (%n):',
    levelFormatter(level) {
        return level.toUpperCase();
    },
    nameFormatter(name) {
        return name || '[anonymous]';
    },
    timestampFormatter(date) {
        return date.toISOString();
    },
});
log.info("Initialized logger");


const axiosClient = axios.create({
    baseURL: 'http://localhost:8081/api',
    headers: {
        'Content-Type': 'application/json',
    },
});

axiosClient.interceptors.request.use(request => {
    console.log('Starting Request', JSON.stringify(request, null, 2));
    return request;
});
axiosClient.interceptors.response.use(response => {
    console.log('Response:', JSON.stringify(response, null, 2));
    return response;
});


export const logger = log;
export default axiosClient;