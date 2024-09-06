import axios, {AxiosResponse} from "axios";
import {Response} from "../dto/common.ts";
import {logger} from "../../config/appConfig.ts";

const log = logger.getLogger("API_Utils");

export const handleResponse = <T>(response: AxiosResponse<Response<T>>): Response<T> => {
    log.debug(`handleResponse: ${JSON.stringify(response)}`);
    if (response?.data?.data !== undefined) {
        log.debug(`handleResponse: data is valid -> ${response.data.data}`);
        return response.data;
    }
    log.debug(`handleResponse: data is not valid, error will be thrown`);
    throw new Error("Unexpected response format or missing data.");
};

export const handleError = (error: any): never => {
    log.debug(`handleError: ${error}`);
    if (axios.isAxiosError(error) && error.response) {
        log.debug(`handleError: It is an axios Error`);
        throw new Error(error.response.data.message || "An error occurred");
    }
    log.debug(`handleError: It is Not an axios Error`);
    throw new Error("An unexpected error occurred");
};
