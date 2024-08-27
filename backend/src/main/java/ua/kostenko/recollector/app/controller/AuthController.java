package ua.kostenko.recollector.app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.kostenko.recollector.app.dto.UserDto;
import ua.kostenko.recollector.app.dto.auth.*;
import ua.kostenko.recollector.app.dto.response.Response;
import ua.kostenko.recollector.app.security.AuthService;
import ua.kostenko.recollector.app.util.ResponseHelper;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Response<UserDto>> registerUser(@RequestBody RegisterRequestDto requestDto) {
        var registeredUser = authService.registerUser(requestDto);
        return ResponseHelper.buildDtoResponse(registeredUser, HttpStatus.CREATED);

    }

    @PostMapping("/login")
    public ResponseEntity<Response<UserDto>> loginUser(@RequestBody LoginRequestDto requestDto) {
        var authentication = new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword());
        var auth = authenticationManager.authenticate(authentication);
        var email = auth.getPrincipal() + "";
        var jwt = auth.getCredentials() + "";
        var userDto = UserDto.builder().email(email).jwtToken(jwt).build();

        return ResponseHelper.buildDtoResponse(userDto, HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Response<String>> forgotPassword(@RequestBody ForgotPasswordRequestDto userDto) {
        authService.forgotPassword(userDto);
        return ResponseHelper.buildDtoResponse("Password reset link sent", HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Response<UserDto>> resetPassword(@RequestBody ResetPasswordRequestDto requestDto) {
        var user = authService.resetPassword(requestDto);
        return ResponseHelper.buildDtoResponse(user, HttpStatus.OK);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Response<UserDto>> changePassword(@RequestBody ChangePasswordRequestDto requestDto) {
        var user = authService.changePassword(requestDto);
        return ResponseHelper.buildDtoResponse(user, HttpStatus.OK);
    }

    @PostMapping("/delete-account")
    public ResponseEntity<Response<String>> deleteAccount(@RequestBody AccountDeleteRequestDto requestDto) {
        var result = authService.deleteAccount(requestDto);
        return ResponseHelper.buildDtoResponse(result, HttpStatus.OK);
    }

}
