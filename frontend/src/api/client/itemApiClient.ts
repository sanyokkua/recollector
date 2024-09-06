import {AxiosInstance} from "axios";
import {ItemDto, ItemFilter} from "../dto/itemDto";
import {Response} from "../dto/common";
import {handleError, handleResponse} from "./utils";
import {logger} from "../../config/appConfig.ts";
import {TokenExtractor} from "../types/types.ts";

const BASE_URL = "/v1/categories";
const log = logger.getLogger("ItemApiClient");

class ItemApiClient {
    constructor(private apiClient: AxiosInstance, private extractor: TokenExtractor) {
        log.debug("Initialized ItemApiClient");
    }

    async createItem(categoryId: number, itemDto: ItemDto): Promise<Response<ItemDto>> {
        log.info("createItem called");
        log.debug(`Category ID: ${categoryId}, Request: ${JSON.stringify(itemDto)}`);
        try {
            const jwt = this.extractor();
            const response = await this.apiClient.post<Response<ItemDto>>(`${BASE_URL}/${categoryId}/items`, itemDto,
                {
                    headers: {
                        "Authorization": `Bearer ${jwt}`
                    }
                });
            log.info("createItem successful");
            return handleResponse(response);
        } catch (error) {
            log.warn(`createItem failed with error: ${error}`);
            return handleError(error);
        }
    }

    async getAllItems(categoryId: number, itemFilter: ItemFilter): Promise<Response<ItemDto[]>> {
        log.info("getAllItems called");
        log.debug(`Category ID: ${categoryId}, Filter: ${JSON.stringify(itemFilter)}`);
        try {
            const jwt = this.extractor();
            const response = await this.apiClient.get<Response<ItemDto[]>>(`${BASE_URL}/${categoryId}/items`, {
                params: itemFilter,
                headers: {
                    "Authorization": `Bearer ${jwt}`
                }
            });
            log.info("getAllItems successful");
            return handleResponse(response);
        } catch (error) {
            log.warn(`getAllItems failed with error: ${error}`);
            return handleError(error);
        }
    }

    async getItem(categoryId: number, itemId: number): Promise<Response<ItemDto>> {
        log.info("getItem called");
        log.debug(`Category ID: ${categoryId}, Item ID: ${itemId}`);
        try {
            const jwt = this.extractor();
            const response = await this.apiClient.get<Response<ItemDto>>(`${BASE_URL}/${categoryId}/items/${itemId}`,
                {
                    headers: {
                        "Authorization": `Bearer ${jwt}`
                    }
                });
            log.info("getItem successful");
            return handleResponse(response);
        } catch (error) {
            log.warn(`getItem failed with error: ${error}`);
            return handleError(error);
        }
    }

    async updateItem(categoryId: number, itemId: number, itemDto: ItemDto): Promise<Response<ItemDto>> {
        log.info("updateItem called");
        log.debug(`Category ID: ${categoryId}, Item ID: ${itemId}, Request: ${JSON.stringify(itemDto)}`);
        try {
            const jwt = this.extractor();
            const response = await this.apiClient.put<Response<ItemDto>>(`${BASE_URL}/${categoryId}/items/${itemId}`, itemDto,
                {
                    headers: {
                        "Authorization": `Bearer ${jwt}`
                    }
                });
            log.info("updateItem successful");
            return handleResponse(response);
        } catch (error) {
            log.warn(`updateItem failed with error: ${error}`);
            return handleError(error);
        }
    }

    async deleteItem(categoryId: number, itemId: number): Promise<Response<string>> {
        log.info("deleteItem called");
        log.debug(`Category ID: ${categoryId}, Item ID: ${itemId}`);
        try {
            const jwt = this.extractor();
            const response = await this.apiClient.delete<Response<string>>(`${BASE_URL}/${categoryId}/items/${itemId}`,
                {
                    headers: {
                        "Authorization": `Bearer ${jwt}`
                    }
                });
            log.info("deleteItem successful");
            return handleResponse(response);
        } catch (error) {
            log.warn(`deleteItem failed with error: ${error}`);
            return handleError(error);
        }
    }
}

export default ItemApiClient;
