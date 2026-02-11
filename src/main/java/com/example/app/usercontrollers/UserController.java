package com.example.app.usercontrollers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.app.entities.User;
import com.example.app.entities.userdao;
import com.example.app.userservice.UserServiceContract;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@Controller
@RequestMapping("/api/users")
public class UserController {

    private final UserServiceContract userService;

    public UserController(UserServiceContract userService) {
        this.userService = userService;
    }

    // ✅ THIS IS FOR OPENING PAGE (GET)
    @GetMapping("/register-page")
    public String showRegisterPage() {
        return "register";
    }

    // ✅ THIS IS FOR FORM SUBMIT (POST)
    @PostMapping("/register-form")
    public String registerForm(User user) {
        userService.registerUser(user);
        return "redirect:/api/users/login-page";
    }

    // ✅ LOGIN PAGE
    @GetMapping("/login-page")
    public String showLoginPage() {
        return "login";
    }
    
    

    // ✅ REST API (POST JSON)
    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<?> registerUserApi(@RequestBody User user) {
    	try {
            User registeredUser = userService.registerUser(user);

            return ResponseEntity.ok(
                Map.of(
                    "message", "User registered successfully",
                    "user", new userdao(
                        registeredUser.getUserId(),
                        registeredUser.getUsername(),
                        registeredUser.getEmail(),
                        registeredUser.getRole().toString()
                    )
                )
            );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
