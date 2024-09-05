import {AxiosInstance} from 'axios';
import {StatisticDto} from '../dto/helperDto.ts';
import {Response} from '../dto/common';
import {handleError, handleResponse} from './utils';
import {logger} from "../../config/appConfig.ts";
import {TokenExtractor} from "../types/types.ts";

const BASE_URL: string = '/v1/helper';
const log = logger.getLogger("HelperApiClient");

class HelperApiClient {
    constructor(private apiClient: AxiosInstance, private extractor: TokenExtractor) {
        log.debug("Initialized HelperApiClient");
    }

    async getItemStatuses(): Promise<string[]> {
        log.info("getItemStatuses called");
        try {
            const jwt = this.extractor();
            const response = await this.apiClient.get<Response<string[]>>(`${BASE_URL}/itemStatuses`,
                {
                    headers: {
                        'Authorization': `Bearer ${jwt}`
                    }
                });
            log.info("getItemStatuses successful");
            return handleResponse(response);
        } catch (error) {
            log.warn(`getItemStatuses failed with error: ${error}`);
            return handleError(error);
        }
    }

    async getStatistics(): Promise<StatisticDto> {
        log.info("getStatistics called");
        try {
            const jwt = this.extractor();
            const response = await this.apiClient.get<Response<StatisticDto>>(`${BASE_URL}/statistics`,
                {
                    headers: {
                        'Authorization': `Bearer ${jwt}`
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
