import axios, { AxiosResponse }                                             from "axios";
import { jwtDecode, JwtPayload }                                            from "jwt-decode";
import { decodeTokenInfo }                                                  from "../api/client/utils";
import { UserDto }                                                          from "../api/dto/authenticationDto";
import { Response }                                                         from "../api/dto/common";
import { logger }                                                           from "../config/appConfig";
import axiosClient                                                          from "../config/axiosConfig";
import { setUserEmail, setUserIsLoggedIn, setUserJwtToken, setUserTimeExp } from "./features/global/globalSlice";
import store, { AppStore }                                                  from "./store";


const log = logger.getLogger("AxiosStoreConfig");
const rawAxiosInstance = axios.create();

export const configureAxiosWithReduxStore = (reduxStore: AppStore) => {
    // Helper function to check if the token is expired
    const isTokenExpired = (token: string): boolean => {
        log.debug("Checking if token is expired");
        try {
            const decoded: JwtPayload = jwtDecode(token);
            const expirationTime = (decoded.exp ?? 0) * 1000; // Convert to milliseconds
            const isExpired = Date.now() >= expirationTime;
            log.debug(`Token expiration time: ${ expirationTime }, Current time: ${ Date.now() }, Is token expired: ${ isExpired }`);
            return isExpired;
        } catch (error) {
            log.error("Error decoding token", error);
            return true; // If decoding fails, consider the token expired
        }
    };

    // Refresh token logic
    const refreshToken = async (): Promise<string | void> => {
        try {
            log.info("Attempting to refresh token");
            const state = reduxStore.getState();
            const email = state.globals.userEmail;
            const jwtToken = state.globals.userJwtToken;

            if (!jwtToken || !email) {
                log.warn("No refresh token or email available for refreshing");
                throw new Error("No refresh token available");
            }

            log.debug("Fetching new token from AuthApiClient");
            const userDtoResponse: AxiosResponse<Response<UserDto>> = await rawAxiosInstance.post<Response<UserDto>>(
                `http://localhost:8081/api/v1/auth/refresh-token`,
                {
                    userEmail: email,
                    accessToken: jwtToken
                },
                {
                    withCredentials: true,
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${ jwtToken }`
                    }
                }
            );

            if (userDtoResponse.data?.data?.jwtToken) {
                log.info("Token refreshed successfully, updating Redux store");
                const res = decodeTokenInfo(userDtoResponse.data?.data?.jwtToken);
                reduxStore.dispatch(setUserJwtToken(res.userJwtToken));
                reduxStore.dispatch(setUserEmail(res.userEmail));
                reduxStore.dispatch(setUserIsLoggedIn(res.userIsLoggedIn));
                reduxStore.dispatch(setUserTimeExp(res.userTimeExp));
            } else {
                log.error("Token refresh failed, no token returned");
                throw new Error("Failed to refresh token");
            }

            return userDtoResponse.data?.data.jwtToken;
        } catch (error) {
            log.error("Error refreshing token", error);
            log.info("Clearing user session due to refresh failure");

            // Clear Redux store on refresh error
            reduxStore.dispatch(setUserJwtToken(""));
            reduxStore.dispatch(setUserEmail(""));
            reduxStore.dispatch(setUserIsLoggedIn(false));
            reduxStore.dispatch(setUserTimeExp(0));
            throw error;
        }
    };

    let refreshingPromise: Promise<string | void> | null = null;

    // Request interceptor
    axiosClient.interceptors.request.use(
        async request => {
            log.debug("Request interceptor triggered", JSON.stringify(request, null, 2));
            let state = reduxStore.getState();
            let accessToken = state.globals.userJwtToken;

            if (accessToken && isTokenExpired(accessToken)) {
                log.info("Token is expired, attempting to refresh");
                if (!refreshingPromise) {
                    refreshingPromise = refreshToken();
                }
                await refreshingPromise;

                state = reduxStore.getState(); // Re-fetch state after refreshing
                accessToken = state.globals.userJwtToken;
                refreshingPromise = null;
            }

            if (accessToken && request.headers) {
                log.debug("Attaching access token to request headers");
                request.headers.Authorization = `Bearer ${ accessToken }`;
            } else {
                log.warn("No valid access token found for request");
            }

            log.info("Request prepared and sent");
            return request;
        },
        async error => {
            log.error("Error during request:", JSON.stringify(error, null, 2));
            return Promise.reject(error);
        }
    );

    // Response interceptor
    axiosClient.interceptors.response.use(
        response => {
            log.debug("Response received", JSON.stringify(response, null, 2));
            return response;
        },
        async error => {
            log.error("Response error:", JSON.stringify(error, null, 2));
            const originalRequest = error.config;

            if (error.response?.status === 401 && !originalRequest._retry) {
                log.info("Received 401 Unauthorized, attempting token refresh");
                originalRequest._retry = true;

                try {
                    if (!refreshingPromise) {
                        refreshingPromise = refreshToken();
                    }
                    await refreshingPromise;

                    const state = store.getState();
                    const accessToken = state.globals.userJwtToken;

                    if (accessToken) {
                        log.info("Retrying original request with refreshed token");
                        originalRequest.headers.Authorization = `Bearer ${ accessToken }`;
                        return axiosClient(originalRequest); // Retry the original request
                    }
                } catch (refreshError) {
                    log.error("Failed to refresh token after 401 error:", refreshError);
                    return Promise.reject(refreshError);
                } finally {
                    refreshingPromise = null;
                }
            } else {
                log.warn("Request failed with status:", error.response?.status);
            }

            return Promise.reject(error);
        }
    );
};