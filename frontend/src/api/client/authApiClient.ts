import { AxiosInstance }               from "axios";
import { logger }                      from "../../config/appConfig.ts";
import {
    AccountDeleteRequestDto,
    ChangePasswordRequestDto,
    ForgotPasswordRequestDto,
    LoginRequestDto,
    RegisterRequestDto,
    ResetPasswordRequestDto,
    UserDto
}                                      from "../dto/authenticationDto.ts";
import { Response }                    from "../dto/common.ts";
import { handleError, handleResponse } from "./utils.ts";


const BASE_URL = "/v1/auth";
const log = logger.getLogger("AuthApiClient");

/**
 * AuthApiClient handles authentication-related API requests.
 */
class AuthApiClient {
    private readonly apiClient: AxiosInstance;
    private readonly jwtToken: string;

    constructor(apiClient: AxiosInstance, jwtToken: string = "") {
        this.apiClient = apiClient;
        this.jwtToken = jwtToken;
        log.info("AuthApiClient initialized");
    }

    /**
     * Registers a new user.
     * @param request - The registration data.
     */
    async registerUser(request: RegisterRequestDto): Promise<Response<UserDto>> {
        log.info("registerUser called");
        log.debug("Register request data", request);

        try {
            const response = await this.apiClient.post<Response<UserDto>>(
                `${ BASE_URL }/register`,
                request
            );
            log.info("User registration successful");
            return handleResponse(response);
        } catch (error) {
            log.error("Error during user registration", error);
            return handleError(error);
        }
    }

    /**
     * Logs in a user.
     * @param request - The login data.
     */
    async loginUser(request: LoginRequestDto): Promise<Response<UserDto>> {
        log.info("loginUser called");
        log.debug("Login request data", request);

        try {
            const response = await this.apiClient.post<Response<UserDto>>(
                `${ BASE_URL }/login`,
                request
            );
            log.info("User login successful");
            return handleResponse(response);
        } catch (error) {
            log.error("Error during user login", error);
            return handleError(error);
        }
    }

    /**
     * Sends a forgot password request.
     * @param request - The forgot password data.
     */
    async forgotPassword(
        request: ForgotPasswordRequestDto
    ): Promise<Response<string>> {
        log.info("forgotPassword called");
        log.debug("Forgot password request data", request);

        try {
            const response = await this.apiClient.post<Response<string>>(
                `${ BASE_URL }/forgot-password`,
                request
            );
            log.info("Forgot password request successful");
            return handleResponse(response);
        } catch (error) {
            log.error("Error during forgot password request", error);
            return handleError(error);
        }
    }

    /**
     * Resets the user's password.
     * @param request - The reset password data.
     */
    async resetPassword(
        request: ResetPasswordRequestDto
    ): Promise<Response<UserDto>> {
        log.info("resetPassword called");
        log.debug("Reset password request data", request);

        try {
            const response = await this.apiClient.post<Response<UserDto>>(
                `${ BASE_URL }/reset-password`,
                request,
                {
                    headers: {
                        Authorization: `Bearer ${ this.jwtToken }`
                    }
                }
            );
            log.info("Password reset successful");
            return handleResponse(response);
        } catch (error) {
            log.error("Error during password reset", error);
            return handleError(error);
        }
    }

    /**
     * Changes the user's password.
     * @param request - The change password data.
     */
    async changePassword(
        request: ChangePasswordRequestDto
    ): Promise<Response<UserDto>> {
        log.info("changePassword called");
        log.debug("Change password request data", request);

        try {
            const response = await this.apiClient.post<Response<UserDto>>(
                `${ BASE_URL }/change-password`,
                request,
                {
                    headers: {
                        Authorization: `Bearer ${ this.jwtToken }`
                    }
                }
            );
            log.info("Password change successful");
            return handleResponse(response);
        } catch (error) {
            log.error("Error during password change", error);
            return handleError(error);
        }
    }

    /**
     * Deletes the user's account.
     * @param request - The delete account data.
     */
    async deleteAccount(
        request: AccountDeleteRequestDto
    ): Promise<Response<string>> {
        log.info("deleteAccount called");
        log.debug("Delete account request data", request);

        try {
            const response = await this.apiClient.post<Response<string>>(
                `${ BASE_URL }/delete-account`,
                request,
                {
                    headers: {
                        Authorization: `Bearer ${ this.jwtToken }`
                    }
                }
            );
            log.info("Account deletion successful");
            return handleResponse(response);
        } catch (error) {
            log.error("Error during account deletion", error);
            return handleError(error);
        }
    }
}

export default AuthApiClient;
