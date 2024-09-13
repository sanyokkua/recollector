import { AxiosInstance }               from "axios";
import { logger }                      from "../../config/appConfig.ts";
import { Response }                    from "../dto/common";
import { SettingsDto, StatisticDto }   from "../dto/helperDto";
import { handleError, handleResponse } from "./utils";


const BASE_URL = "/v1/helper";
const log = logger.getLogger("HelperApiClient");

/**
 * HelperApiClient handles helper-related API requests.
 */
class HelperApiClient {
    private readonly apiClient: AxiosInstance;
    private readonly jwtToken: string;

    constructor(apiClient: AxiosInstance, jwtToken: string) {
        this.apiClient = apiClient;
        this.jwtToken = jwtToken;
        log.info("HelperApiClient initialized");
    }

    /**
     * Retrieves item statuses.
     */
    async getItemStatuses(): Promise<Response<string[]>> {
        log.info("getItemStatuses called");

        try {
            const response = await this.apiClient.get<Response<string[]>>(
                `${ BASE_URL }/itemStatuses`,
                {
                    headers: {
                        Authorization: `Bearer ${ this.jwtToken }`
                    }
                }
            );
            log.info("getItemStatuses successful");
            return handleResponse(response);
        } catch (error) {
            log.error("Error during getItemStatuses", error);
            return handleError(error);
        }
    }

    /**
     * Retrieves statistical data.
     */
    async getStatistics(): Promise<Response<StatisticDto>> {
        log.info("getStatistics called");

        try {
            const response = await this.apiClient.get<Response<StatisticDto>>(
                `${ BASE_URL }/statistics`,
                {
                    headers: {
                        Authorization: `Bearer ${ this.jwtToken }`
                    }
                }
            );
            log.info("getStatistics successful");
            return handleResponse(response);
        } catch (error) {
            log.error("Error during getStatistics", error);
            return handleError(error);
        }
    }

    /**
     * Retrieves current settings.
     */
    async getSettings(): Promise<Response<SettingsDto>> {
        log.info("getSettings called");

        try {
            const response = await this.apiClient.get<Response<SettingsDto>>(
                `${ BASE_URL }/settings`,
                {
                    headers: {
                        Authorization: `Bearer ${ this.jwtToken }`
                    }
                }
            );
            log.info("getSettings successful");
            return handleResponse(response);
        } catch (error) {
            log.error("Error during getSettings", error);
            return handleError(error);
        }
    }

    /**
     * Updates application settings.
     * @param settingsDto - The updated settings data.
     */
    async updateSettings(settingsDto: SettingsDto): Promise<Response<SettingsDto>> {
        log.info("updateSettings called");
        log.debug("Settings update request data", settingsDto);

        try {
            const response = await this.apiClient.put<Response<SettingsDto>>(
                `${ BASE_URL }/settings`,
                settingsDto,
                {
                    headers: {
                        Authorization: `Bearer ${ this.jwtToken }`
                    }
                }
            );
            log.info("updateSettings successful");
            return handleResponse(response);
        } catch (error) {
            log.error("Error during updateSettings", error);
            return handleError(error);
        }
    }
}

export default HelperApiClient;
