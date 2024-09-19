export interface LoginRequestDto {
    email?: string;
    password?: string;
}

export interface RegisterRequestDto {
    email?: string;
    password?: string;
    passwordConfirm?: string;
}

export interface ResetPasswordRequestDto {
    email?: string;
    password?: string;
    passwordConfirm?: string;
    passwordResetToken?: string;
}

export interface AccountDeleteRequestDto {
    email: string;
    password: string;
    passwordConfirm: string;
}

export interface ChangePasswordRequestDto {
    email?: string;
    passwordCurrent?: string;
    password?: string;
    passwordConfirm?: string;
}

export interface ForgotPasswordRequestDto {
    email?: string;
}

export interface UserDto {
    email: string;
    jwtToken: string;
}

export interface TokenRefreshRequest {
    userEmail: string;
}

export interface LogoutDto {
    userEmail: string;
}