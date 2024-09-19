import { AxiosInstance }               from "axios";
import { logger }                      from "../../config/appConfig.ts";
import { Response }                    from "../dto/common";
import { ItemDto, ItemFilter }         from "../dto/itemDto";
import { handleError, handleResponse } from "./utils";


const BASE_URL = "/v1/categories";
const log = logger.getLogger("ItemApiClient");

/**
 * ItemApiClient handles item-related API requests.
 */
class ItemApiClient {
    private readonly apiClient: AxiosInstance;

    constructor(apiClient: AxiosInstance) {
        this.apiClient = apiClient;
        log.info("ItemApiClient initialized");
    }

    /**
     * Creates a new item within a specified category.
     * @param categoryId - The ID of the category.
     * @param itemDto - The item data to create.
     */
    async createItem(categoryId: number, itemDto: ItemDto): Promise<Response<ItemDto>> {
        log.info("createItem called");
        log.debug("Category ID:", categoryId, "Request Data:", itemDto);

        try {
            const response = await this.apiClient.post<Response<ItemDto>>(
                `${ BASE_URL }/${ categoryId }/items`,
                itemDto
            );
            log.info("createItem successful");
            return handleResponse(response);
        } catch (error) {
            log.error("createItem failed", error);
            return handleError(error);
        }
    }

    /**
     * Retrieves all items within a specified category, optionally filtered.
     * @param categoryId - The ID of the category.
     * @param itemFilter - Optional filters for item retrieval.
     */
    async getAllItems(categoryId: number, itemFilter?: ItemFilter): Promise<Response<ItemDto[]>> {
        log.info("getAllItems called");
        log.debug("Category ID:", categoryId, "Filter:", itemFilter);

        try {
            const response = await this.apiClient.get<Response<ItemDto[]>>(
                `${ BASE_URL }/${ categoryId }/items`,
                {
                    params: itemFilter
                }
            );
            log.info("getAllItems successful");
            return handleResponse(response);
        } catch (error) {
            log.error("getAllItems failed", error);
            return handleError(error);
        }
    }

    /**
     * Retrieves a specific item by its ID within a category.
     * @param categoryId - The ID of the category.
     * @param itemId - The ID of the item.
     */
    async getItem(categoryId: number, itemId: number): Promise<Response<ItemDto>> {
        log.info("getItem called");
        log.debug("Category ID:", categoryId, "Item ID:", itemId);

        try {
            const response = await this.apiClient.get<Response<ItemDto>>(
                `${ BASE_URL }/${ categoryId }/items/${ itemId }`
            );
            log.info("getItem successful");
            return handleResponse(response);
        } catch (error) {
            log.error("getItem failed", error);
            return handleError(error);
        }
    }

    /**
     * Updates an existing item within a category.
     * @param categoryId - The ID of the category.
     * @param itemId - The ID of the item.
     * @param itemDto - The updated item data.
     */
    async updateItem(categoryId: number, itemId: number, itemDto: ItemDto): Promise<Response<ItemDto>> {
        log.info("updateItem called");
        log.debug("Category ID:", categoryId, "Item ID:", itemId, "Request Data:", itemDto);

        try {
            const response = await this.apiClient.put<Response<ItemDto>>(
                `${ BASE_URL }/${ categoryId }/items/${ itemId }`,
                itemDto
            );
            log.info("updateItem successful");
            return handleResponse(response);
        } catch (error) {
            log.error("updateItem failed", error);
            return handleError(error);
        }
    }

    /**
     * Deletes an item by its ID within a category.
     * @param categoryId - The ID of the category.
     * @param itemId - The ID of the item.
     */
    async deleteItem(categoryId: number, itemId: number): Promise<Response<string>> {
        log.info("deleteItem called");
        log.debug("Category ID:", categoryId, "Item ID:", itemId);

        try {
            const response = await this.apiClient.delete<Response<string>>(
                `${ BASE_URL }/${ categoryId }/items/${ itemId }`
            );
            log.info("deleteItem successful");
            return handleResponse(response);
        } catch (error) {
            log.error("deleteItem failed", error);
            return handleError(error);
        }
    }
}

export default ItemApiClient;
