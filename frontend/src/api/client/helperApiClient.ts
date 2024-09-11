import {AxiosInstance} from "axios";
import {StatisticDto} from "../dto/helperDto.ts";
import {Response} from "../dto/common";
import {handleError, handleResponse} from "./utils";
import {logger} from "../../config/appConfig.ts";

const BASE_URL: string = "/v1/helper";
const log = logger.getLogger("HelperApiClient");

class HelperApiClient {
    constructor(private apiClient: AxiosInstance, private jwtToken: string) {
        log.debug("Initialized HelperApiClient");
    }

    async getItemStatuses(): Promise<Response<string[]>> {
        log.info("getItemStatuses called");
        try {
            const response = await this.apiClient.get<Response<string[]>>(`${BASE_URL}/itemStatuses`,
                {
                    headers: {
                        "Authorization": `Bearer ${this.jwtToken}`
                    }
                });
            log.info("getItemStatuses successful");
            return handleResponse(response);
        } catch (error) {
            log.warn(`getItemStatuses failed with error: ${error}`);
            return handleError(error);
        }
    }

    async getStatistics(): Promise<Response<StatisticDto>> {
        log.info("getStatistics called");
        try {
            const response = await this.apiClient.get<Response<StatisticDto>>(`${BASE_URL}/statistics`,
                {
                    headers: {
                        "Authorization": `Bearer ${this.jwtToken}`
                    }
                });
            log.info("getStatistics successful");
            return handleResponse(response);
        } catch (error) {
            log.warn(`getStatistics failed with error: ${error}`);
            return handleError(error);
        }
    }
}

export default HelperApiClient;
