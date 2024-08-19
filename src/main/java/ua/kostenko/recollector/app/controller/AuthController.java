package ua.kostenko.recollector.app.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.kostenko.recollector.app.entity.User;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        // Handle user registration
        return "User registered";
    }
// TODO: implement after the main logic is implemented
//    @PostMapping("/login")
//    public String loginUser(@RequestBody UserLoginRequest loginRequest) {
//        // Handle user login
//        return "User logged in";
//    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody String email) {
        // Handle password reset request
        return "Password reset link sent";
    }
// TODO: implement after the main logic is implemented
//    @PostMapping("/reset-password")
//    public String resetPassword(@RequestBody PasswordResetRequest resetRequest) {
//        // Handle password reset
//        return "Password reset successful";
//    }
}
