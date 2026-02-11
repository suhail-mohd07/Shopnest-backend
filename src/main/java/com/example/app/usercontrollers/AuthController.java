package com.example.app.usercontrollers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.entities.LoginRequest;
import com.example.app.entities.User;
import com.example.app.userservice.AuthServiceContract;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/auth")
public class AuthController {
	private final AuthServiceContract authService;
	
	 public AuthController(AuthServiceContract authService) {
	        this.authService = authService;
	    }
	 
	 @PostMapping("/login")
	    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response)
	    {
	        try {
	            User user = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
	            String token = authService.generateToken(user);

	            Cookie cookie = new Cookie("authToken", token);
	            cookie.setHttpOnly(true);
	            cookie.setSecure(false); // localhost http
	            cookie.setPath("/");
	            cookie.setMaxAge(3600); // 1 hour
	            response.addCookie(cookie);


	            Map<String, Object> responseBody = new HashMap<>();
	            responseBody.put("message", "Login successful");
	            responseBody.put("role", user.getRole().name());
	            responseBody.put("username", user.getUsername());

	            return ResponseEntity.ok(responseBody);

	        }
	        catch (RuntimeException e)
	        {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
	        }
	    }
}
