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
import ua.kostenko.recollector.app.dto.response.Response;
import ua.kostenko.recollector.app.exceptions.UserCredentialsValidationException;
import ua.kostenko.recollector.app.exceptions.UserRegistrationException;
import ua.kostenko.recollector.app.service.AuthService;
import ua.kostenko.recollector.app.util.ResponseHelper;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Response<UserDto>> registerUser(@RequestBody UserDto userDto) {
        try {
            var registeredUser = authService.registerUser(userDto);
            return ResponseHelper.buildDtoResponse(registeredUser, HttpStatus.CREATED);
        } catch (UserCredentialsValidationException | UserRegistrationException ex) {
            return ResponseHelper.buildDtoErrorResponse(userDto, HttpStatus.BAD_REQUEST, ex);
        } catch (Exception ex) {
            return ResponseHelper.buildDtoErrorResponse(userDto, HttpStatus.INTERNAL_SERVER_ERROR, ex);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Response<UserDto>> loginUser(@RequestBody UserDto loginRequest) {
        var authentication = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                                                                     loginRequest.getPassword());
        var auth = authenticationManager.authenticate(authentication);
        var email = auth.getPrincipal() + "";
        var jwt = auth.getCredentials() + "";
        var userDto = UserDto.builder().email(email).jwtToken(jwt).build();

        return ResponseHelper.buildDtoResponse(userDto, HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Response<String>> forgotPassword(@RequestBody UserDto userDto) {
        authService.forgotPassword(userDto);
        return ResponseHelper.buildDtoResponse("Password reset link sent", HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Response<UserDto>> resetPassword(@RequestBody UserDto resetRequest) {
        var user = authService.resetPassword(resetRequest);
        return ResponseHelper.buildDtoResponse(user, HttpStatus.OK);
    }
}
