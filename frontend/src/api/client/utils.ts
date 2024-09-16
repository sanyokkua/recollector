import axios, { AxiosResponse } from "axios";
import { logger }               from "../../config/appConfig.ts";
import { Response }             from "../dto/common.ts";


const log = logger.getLogger("API_Utils");

/**
 * Handles the API response to ensure it contains valid data.
 * @param response - The Axios response object.
 * @returns The response data if valid.
 * @throws Error if the response format is unexpected or data is missing.
 */
export const handleResponse = <T>(response: AxiosResponse<Response<T>>): Response<T> => {
    log.debug("handleResponse called", { response: response.data });

    if (response?.data?.data !== undefined) {
        log.debug("handleResponse: Valid data found", { data: response.data.data });
        return response.data;
    }

    log.error("handleResponse: Data is not valid or missing");
    throw new Error("Unexpected response format or missing data.");
};

/**
 * Handles errors that occur during API calls.
 * @param error - The error object from Axios or a general error.
 * @throws Error with a message derived from the error.
 */
export const handleError = (error: any): never => {
    log.debug("handleError called", { error });

    if (axios.isAxiosError(error) && error.response) {
        log.debug("handleError: Axios error detected", { response: error.response.data });

        if (error.response.data && error.response.data.error) {
            log.error("handleError: API error", { error: error.response.data.error });
            throw new Error(error.response.data.error);
        } else {
            log.error("handleError: API error with message", { message: error.response.data.message || "An error occurred" });
            throw new Error(error.response.data.message || "An error occurred");
        }
    }

    log.error("handleError: Non-Axios error detected");
    throw new Error("An unexpected error occurred");
};

/**
 * Converts seconds into a Date object.
 * @param seconds - The time in seconds.
 * @returns A Date object representing the given time.
 */
export const getDateFromSeconds = (seconds: number): Date => {
    log.debug("getDateFromSeconds called", { seconds });
    return new Date(seconds * 1000);
};

export const parseErrorMessage = (error: any, defaultMsg: string = "An unknown error occurred"): string => {
    if (!error) {
        return defaultMsg;
    }

    if (typeof error === "string") {
        return error || defaultMsg;
    } else if (axios.isAxiosError(error) && error.response) {
        // Axios error
        return error.response.data?.error || error.response.data?.message || error.message;
    } else if (error instanceof Error) {
        // General JavaScript/Node.js error
        return error.message;
    } else if (error.request) {
        // Axios error with no response
        return "No response received from server";
    } else if (error.message) {
        // Fetch error or other errors with message property
        return error.message;
    }

    return defaultMsg;
};