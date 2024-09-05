import {AxiosInstance} from 'axios';
import {CategoryDto, CategoryFilter} from '../dto/categoryDto';
import {Response} from '../dto/common';
import {handleError, handleResponse} from './utils';
import {logger} from "../../config/appConfig.ts";
import {TokenExtractor} from "../types/types.ts";

const BASE_URL: string = '/v1/categories';
const log = logger.getLogger("CategoryApiClient");

class CategoryApiClient {
    constructor(private apiClient: AxiosInstance, private extractor: TokenExtractor) {
        log.debug("Initialized CategoryApiClient");
    }

    async createCategory(category: CategoryDto): Promise<CategoryDto> {
        log.info("createCategory called");
        log.debug(`Request: ${JSON.stringify(category)}`);
        try {
            const jwt = this.extractor();
            const response = await this.apiClient.post<Response<CategoryDto>>(`${BASE_URL}`, category,
                {
                    headers: {
                        'Authorization': `Bearer ${jwt}`
                    }
                });
            log.info("createCategory successful");
            return handleResponse(response);
        } catch (error) {
            log.warn(`createCategory failed with error: ${error}`);
            return handleError(error);
        }
    }

    async getAllCategories(categoryFilter?: CategoryFilter): Promise<CategoryDto[]> {
        log.info("getAllCategories called");
        log.debug(`Request Filter: ${JSON.stringify(categoryFilter)}`);
        try {
            const jwt = this.extractor();
            const response = await this.apiClient.get<Response<CategoryDto[]>>(`${BASE_URL}`,
                {
                    params: categoryFilter,
                    headers: {
                        'Authorization': `Bearer ${jwt}`
                    }
                });
            log.info("getAllCategories successful");
            return handleResponse(response);
        } catch (error) {
            log.warn(`getAllCategories failed with error: ${error}`);
            return handleError(error);
        }
    }

    async getCategory(categoryId: number): Promise<CategoryDto> {
        log.info("getCategory called");
        log.debug(`Category ID: ${categoryId}`);
        try {
            const jwt = this.extractor();
            const response = await this.apiClient.get<Response<CategoryDto>>(`${BASE_URL}/${categoryId}`,
                {
                    headers: {
                        'Authorization': `Bearer ${jwt}`
                    }
                });
            log.info("getCategory successful");
            return handleResponse(response);
        } catch (error) {
            log.warn(`getCategory failed with error: ${error}`);
            return handleError(error);
        }
    }

    async updateCategory(categoryId: number, category: CategoryDto): Promise<CategoryDto> {
        log.info("updateCategory called");
        log.debug(`Category ID: ${categoryId}, Request: ${JSON.stringify(category)}`);
        try {
            const jwt = this.extractor();
            const response = await this.apiClient.put<Response<CategoryDto>>(`${BASE_URL}/${categoryId}`, category,
                {
                    headers: {
                        'Authorization': `Bearer ${jwt}`
                    }
                });
            log.info("updateCategory successful");
            return handleResponse(response);
        } catch (error) {
            log.warn(`updateCategory failed with error: ${error}`);
            return handleError(error);
        }
    }

    async deleteCategory(categoryId: number): Promise<string> {
        log.info("deleteCategory called");
        log.debug(`Category ID: ${categoryId}`);
        try {
            const jwt = this.extractor();
            const response = await this.apiClient.delete<Response<string>>(`${BASE_URL}/${categoryId}`,
                {
                    headers: {
                        'Authorization': `Bearer ${jwt}`
                    }
                });
            log.info("deleteCategory successful");
            return handleResponse(response);
        } catch (error) {
            log.warn(`deleteCategory failed with error: ${error}`);
            return handleError(error);
        }
    }
}

export default CategoryApiClient;
