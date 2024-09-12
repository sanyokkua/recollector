import {createAsyncThunk, createSlice, PayloadAction} from "@reduxjs/toolkit";
import axiosClient, {logger} from "../../../config/appConfig";
import {
    currentCategoryIdExtractor,
    currentCategoryNameExtractor,
    userEmailExtractor,
    userJwtRefreshTokenExtractor,
    userJwtTokenExtractor
} from "../../browserStore.ts";
import {jwtDecode, JwtPayload} from "jwt-decode";
import {getDateFromSeconds} from "../../../api/client/utils.ts";
import {
    ChangePasswordRequestDto,
    LoginRequestDto,
    RegisterRequestDto,
    UserDto
} from "../../../api/dto/authenticationDto.ts";
import AuthApiClient from "../../../api/client/authApiClient.ts";
import {Response} from "../../../api/dto/common.ts";

const log = logger.getLogger("globalSlice");

export interface GlobalState {
    currentCategoryId?: number;
    currentCategoryName?: string;
    userJwtToken: string;
    userJwtRefreshToken: string;
    userIsLoggedIn: boolean;
    userEmail: string;
    userTimeExp: number;
    error: string;
}

const initializeState = (): GlobalState => {
    const jwt = userJwtTokenExtractor() ?? "";
    const refresh = userJwtRefreshTokenExtractor() ?? "";
    const curCatId = currentCategoryIdExtractor() ?? -1;
    const curCatName = currentCategoryNameExtractor() ?? "";
    let email = userEmailExtractor() ?? "";
    let userIsLoggedIn = false;
    let userTimeExp = 0;
    let error = "";

    if (jwt) {
        try {
            const decoded = jwtDecode<JwtPayload>(jwt);
            if (decoded?.sub && decoded?.exp) {
                email = email || decoded.sub;
                userTimeExp = decoded.exp;
                userIsLoggedIn = new Date().getTime() < getDateFromSeconds(userTimeExp).getTime();
            } else {
                error = "Failed to decode token";
            }
        } catch (e) {
            log.warn("Failed to parse JWT token.", e);
            error = "Failed to load token from the history";
        }
    }

    return {
        currentCategoryId: curCatId,
        currentCategoryName: curCatName,
        userJwtToken: jwt,
        userJwtRefreshToken: refresh,
        userIsLoggedIn,
        userEmail: email,
        userTimeExp,
        error: error
    };
};

const initialState: GlobalState = initializeState();

export const loginUser = createAsyncThunk(
    "globals/login",
    async (loginRequest: LoginRequestDto, {rejectWithValue}) => {
        log.info("Attempting to login");
        log.debug(`Login request parameters: ${JSON.stringify(loginRequest)}`);
        try {
            const client = new AuthApiClient(axiosClient);
            return await client.loginUser(loginRequest);
        } catch (error: any) {
            const errorMessage = error?.message || "Failed to login";
            log.error("Login API call failed", error);
            return rejectWithValue(errorMessage);
        }
    }
);

export const registerUser = createAsyncThunk(
    "globals/register",
    async (registerRequest: RegisterRequestDto, {rejectWithValue}) => {
        log.info("Attempting to login");
        log.debug(`Register request parameters: ${JSON.stringify(registerRequest)}`);
        try {
            const client = new AuthApiClient(axiosClient);
            return await client.registerUser(registerRequest);
        } catch (error: any) {
            const errorMessage = error?.message || "Failed to Register";
            log.error("Register API call failed", error);
            return rejectWithValue(errorMessage);
        }
    }
);

type ChangePasswordRequest = {
    changePasswordDto: ChangePasswordRequestDto,
    jwtToken: string;
}

export const changePassword = createAsyncThunk("globals/change_password",
    async (changePassReq: ChangePasswordRequest, {rejectWithValue}) => {
        log.info("Attempting to login");
        log.debug(`Register request parameters: ${JSON.stringify(changePassReq)}`);
        try {
            const client = new AuthApiClient(axiosClient, changePassReq.jwtToken);
            return await client.changePassword(changePassReq.changePasswordDto);
        } catch (error: any) {
            const errorMessage = error?.message || "Failed to Change Password";
            log.error("Change Password API call failed", error);
            return rejectWithValue(errorMessage);
        }
    }
);

const resetUserState = (state: GlobalState, errorMessage: string = "") => {
    state.userJwtToken = "";
    state.userJwtRefreshToken = "";
    state.userIsLoggedIn = false;
    state.userEmail = "";
    state.userTimeExp = 0;
    state.error = errorMessage;
};

export const globalSlice = createSlice({
    name: "globals",
    initialState,
    reducers: {
        setCurrentCategoryId: (state, action: PayloadAction<number>) => {
            log.debug(`Setting current category ID: ${action.payload}`);
            state.currentCategoryId = action.payload;
        },
        setCurrentCategoryName: (state, action: PayloadAction<string>) => {
            log.debug(`Setting current category name: ${action.payload}`);
            state.currentCategoryName = action.payload;
        },
        setUserJwtToken: (state, action: PayloadAction<string>) => {
            log.debug(`Setting user JWT token: ${action.payload}`);
            state.userJwtToken = action.payload;
        },
        setUserJwtRefreshToken: (state, action: PayloadAction<string>) => {
            log.debug(`Setting user JWT refresh token: ${action.payload}`);
            state.userJwtRefreshToken = action.payload;
        },
        setUserIsLoggedIn: (state, action: PayloadAction<boolean>) => {
            log.debug(`Setting user login status: ${action.payload}`);
            state.userIsLoggedIn = action.payload;
        },
        setUserEmail: (state, action: PayloadAction<string>) => {
            log.debug(`Setting user email: ${action.payload}`);
            state.userEmail = action.payload;
        },
        setUserTimeExp: (state, action: PayloadAction<number>) => {
            log.debug(`Setting user token expiration time: ${action.payload}`);
            state.userTimeExp = action.payload;
        }
    },
    extraReducers: (builder) => {
        builder
            .addCase(loginUser.pending, (state: GlobalState) => {
                log.debug("Login request pending");
                resetUserState(state);
            })
            .addCase(loginUser.fulfilled, (state: GlobalState, action: PayloadAction<Response<UserDto>>) => {
                log.info("Login request fulfilled successfully");
                const payload = action.payload?.data;
                if (!payload?.jwtToken) {
                    resetUserState(state, "Response doesn't contain required fields");
                    return;
                }

                try {
                    const decoded = jwtDecode<JwtPayload>(payload.jwtToken);
                    if (!decoded?.sub || !decoded.exp) {
                        resetUserState(state, "Token decoding failed");
                        return;
                    }

                    state.userJwtToken = payload.jwtToken;
                    state.userJwtRefreshToken = ""; // Update with actual refresh token if available
                    state.userEmail = payload.email;
                    state.userTimeExp = decoded.exp;
                    state.userIsLoggedIn = new Date().getTime() < getDateFromSeconds(decoded.exp).getTime();
                    state.error = state.userIsLoggedIn ? "" : "Token is expired";

                } catch (e) {
                    log.error("Failed to decode token", e);
                    resetUserState(state, "Token decoding failed");
                }
            })
            .addCase(loginUser.rejected, (state: GlobalState, action) => {
                log.error("Login request was rejected", action.payload);
                resetUserState(state, action.payload as string || "Login request was rejected");
            })

            .addCase(registerUser.pending, (state: GlobalState) => {
                log.debug("Register request pending");
                state.error = "";
            })
            .addCase(registerUser.fulfilled, (state: GlobalState, action: PayloadAction<Response<UserDto>>) => {
                log.info("Register request fulfilled successfully");
                log.debug(`Response: ${JSON.stringify(action.payload)}`);
                state.error = "";
            })
            .addCase(registerUser.rejected, (state: GlobalState, action) => {
                log.error("Register request was rejected", action.payload);
                state.error = "Registration was rejected";
            })

            .addCase(changePassword.pending, (state: GlobalState) => {
                log.debug("Change Password request pending");
                state.error = "";
            })
            .addCase(changePassword.fulfilled, (state: GlobalState, action: PayloadAction<Response<UserDto>>) => {
                log.info("Change Password request fulfilled successfully");
                log.debug(`Response: ${JSON.stringify(action.payload)}`);
                state.error = "";
            })
            .addCase(changePassword.rejected, (state: GlobalState, action) => {
                log.error("Change Password request was rejected", action.payload);
                state.error = "Change Password was rejected";
            })
        ;
    }
});

// Export the actions generated by createSlice
export const {
    setCurrentCategoryId,
    setCurrentCategoryName,
    setUserJwtToken,
    setUserJwtRefreshToken,
    setUserIsLoggedIn,
    setUserEmail,
    setUserTimeExp
} = globalSlice.actions;

// Export the reducer to be used in the store
export default globalSlice.reducer;