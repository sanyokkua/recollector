import { AxiosInstance }               from "axios";
import { logger }                      from "../../config/appConfig.ts";
import { CategoryDto, CategoryFilter } from "../dto/categoryDto";
import { Response }                    from "../dto/common";
import { handleError, handleResponse } from "./utils";


const BASE_URL = "/v1/categories";
const log = logger.getLogger("CategoryApiClient");

/**
 * CategoryApiClient handles category-related API requests.
 */
class CategoryApiClient {
    private readonly apiClient: AxiosInstance;
    private readonly jwtToken: string;

    constructor(apiClient: AxiosInstance, jwtToken: string) {
        this.apiClient = apiClient;
        this.jwtToken = jwtToken;
        log.info("CategoryApiClient initialized");
    }

    /**
     * Creates a new category.
     * @param category - The category data.
     */
    async createCategory(category: CategoryDto): Promise<Response<CategoryDto>> {
        log.info("createCategory called");
        log.debug("Create category request data", category);

        try {
            const response = await this.apiClient.post<Response<CategoryDto>>(
                `${ BASE_URL }`,
                category,
                {
                    headers: {
                        Authorization: `Bearer ${ this.jwtToken }`
                    }
                }
            );
            log.info("Category creation successful");
            return handleResponse(response);
        } catch (error) {
            log.error("Error during category creation", error);
            return handleError(error);
        }
    }

    /**
     * Retrieves all categories with optional filters.
     * @param categoryFilter - Optional filters for the categories.
     */
    async getAllCategories(
        categoryFilter?: CategoryFilter
    ): Promise<Response<CategoryDto[]>> {
        log.info("getAllCategories called");
        log.debug("Category filter data", categoryFilter);

        try {
            const response = await this.apiClient.get<Response<CategoryDto[]>>(
                `${ BASE_URL }`,
                {
                    params: categoryFilter,
                    headers: {
                        Authorization: `Bearer ${ this.jwtToken }`
                    }
                }
            );
            log.info("Retrieved all categories successfully");
            return handleResponse(response);
        } catch (error) {
            log.error("Error retrieving all categories", error);
            return handleError(error);
        }
    }

    /**
     * Retrieves a category by its ID.
     * @param categoryId - The ID of the category.
     */
    async getCategory(categoryId: number): Promise<Response<CategoryDto>> {
        log.info("getCategory called");
        log.debug(`Fetching category with ID: ${ categoryId }`);

        try {
            const response = await this.apiClient.get<Response<CategoryDto>>(
                `${ BASE_URL }/${ categoryId }`,
                {
                    headers: {
                        Authorization: `Bearer ${ this.jwtToken }`
                    }
                }
            );
            log.info("Retrieved category successfully");
            return handleResponse(response);
        } catch (error) {
            log.error(`Error retrieving category with ID: ${ categoryId }`, error);
            return handleError(error);
        }
    }

    /**
     * Updates an existing category.
     * @param categoryId - The ID of the category to update.
     * @param category - The updated category data.
     */
    async updateCategory(
        categoryId: number,
        category: CategoryDto
    ): Promise<Response<CategoryDto>> {
        log.info("updateCategory called");
        log.debug(`Updating category with ID: ${ categoryId }`, category);

        try {
            const response = await this.apiClient.put<Response<CategoryDto>>(
                `${ BASE_URL }/${ categoryId }`,
                category,
                {
                    headers: {
                        Authorization: `Bearer ${ this.jwtToken }`
                    }
                }
            );
            log.info("Category update successful");
            return handleResponse(response);
        } catch (error) {
            log.error(`Error updating category with ID: ${ categoryId }`, error);
            return handleError(error);
        }
    }

    /**
     * Deletes a category by its ID.
     * @param categoryId - The ID of the category to delete.
     */
    async deleteCategory(categoryId: number): Promise<Response<string>> {
        log.info("deleteCategory called");
        log.debug(`Deleting category with ID: ${ categoryId }`);

        try {
            const response = await this.apiClient.delete<Response<string>>(
                `${ BASE_URL }/${ categoryId }`,
                {
                    headers: {
                        Authorization: `Bearer ${ this.jwtToken }`
                    }
                }
            );
            log.info("Category deletion successful");
            return handleResponse(response);
        } catch (error) {
            log.error(`Error deleting category with ID: ${ categoryId }`, error);
            return handleError(error);
        }
    }
}

export default CategoryApiClient;
