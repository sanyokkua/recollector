import {AxiosInstance} from "axios";
import {
    AccountDeleteRequestDto,
    ChangePasswordRequestDto,
    ForgotPasswordRequestDto,
    LoginRequestDto,
    RegisterRequestDto,
    ResetPasswordRequestDto,
    UserDto
} from "../dto/authenticationDto.ts";
import {Response} from "../dto/common.ts";
import {handleError, handleResponse} from "./utils.ts";
import {logger} from "../../config/appConfig.ts";
import {TokenExtractor, TokenSaver} from "../types/types.ts";

const BASE_URL: string = "/v1/auth";
const log = logger.getLogger("AuthApiClient");

class AuthApiClient {
    constructor(private apiClient: AxiosInstance, private extractor: TokenExtractor, private saver: TokenSaver) {
        log.debug("Initialized AuthApiClient");
    }

    async registerUser(request: RegisterRequestDto): Promise<Response<UserDto>> {
        log.info("registerUser called");
        log.debug(`Request: ${JSON.stringify(request)}`);
        try {
            const response = await this.apiClient.post<Response<UserDto>>(`${BASE_URL}/register`, request);
            log.info("registerUser successful");
            return handleResponse(response);
        } catch (error) {
            log.warn(`registerUser failed with error: ${error}`);
            return handleError(error);
        }
    }

    async loginUser(request: LoginRequestDto): Promise<Response<UserDto>> {
        log.info("loginUser called");
        log.debug(`Request: ${JSON.stringify(request)}`);
        try {
            const response = await this.apiClient.post<Response<UserDto>>(`${BASE_URL}/login`, request);
            log.info("loginUser successful");
            const data = handleResponse(response);
            this.saver(data.data?.jwtToken ?? "");
            return data;
        } catch (error) {
            log.warn(`loginUser failed with error: ${error}`);
            return handleError(error);
        }
    }

    async forgotPassword(request: ForgotPasswordRequestDto): Promise<Response<string>> {
        log.info("forgotPassword called");
        log.debug(`Request: ${JSON.stringify(request)}`);
        try {
            const response = await this.apiClient.post<Response<string>>(`${BASE_URL}/forgot-password`, request);
            log.info("forgotPassword successful");
            return handleResponse(response);
        } catch (error) {
            log.warn(`forgotPassword failed with error: ${error}`);
            return handleError(error);
        }
    }

    async resetPassword(request: ResetPasswordRequestDto): Promise<Response<UserDto>> {
        log.info("resetPassword called");
        log.debug(`Request: ${JSON.stringify(request)}`);
        try {
            const jwt = this.extractor();
            const response = await this.apiClient.post<Response<UserDto>>(`${BASE_URL}/reset-password`, request,
                {
                    headers: {
                        "Authorization": `Bearer ${jwt}`
                    }
                });
            log.info("resetPassword successful");
            return handleResponse(response);
        } catch (error) {
            log.warn(`resetPassword failed with error: ${error}`);
            return handleError(error);
        }
    }

    async changePassword(request: ChangePasswordRequestDto): Promise<Response<UserDto>> {
        log.info("changePassword called");
        log.debug(`Request: ${JSON.stringify(request)}`);
        try {
            const jwt = this.extractor();
            const response = await this.apiClient.post<Response<UserDto>>(`${BASE_URL}/change-password`, request,
                {
                    headers: {
                        "Authorization": `Bearer ${jwt}`
                    }
                });
            log.info("changePassword successful");
            return handleResponse(response);
        } catch (error) {
            log.warn(`changePassword failed with error: ${error}`);
            return handleError(error);
        }
    }

    async deleteAccount(request: AccountDeleteRequestDto): Promise<Response<string>> {
        log.info("deleteAccount called");
        log.debug(`Request: ${JSON.stringify(request)}`);
        try {
            const jwt = this.extractor();
            const response = await this.apiClient.post<Response<string>>(`${BASE_URL}/delete-account`, request,
                {
                    headers: {
                        "Authorization": `Bearer ${jwt}`
                    }
                });
            log.info("deleteAccount successful");
            return handleResponse(response);
        } catch (error) {
            log.warn(`deleteAccount failed with error: ${error}`);
            return handleError(error);
        }
    }
}

export default AuthApiClient;
