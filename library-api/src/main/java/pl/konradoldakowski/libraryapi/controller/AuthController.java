package pl.konradoldakowski.libraryapi.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.konradoldakowski.libraryapi.dto.LoginRequest;
import pl.konradoldakowski.libraryapi.dto.LoginResponse;
import pl.konradoldakowski.libraryapi.dto.RegisterRequest;
import pl.konradoldakowski.libraryapi.dto.UserResponse;
import pl.konradoldakowski.libraryapi.service.AuthService;

import java.net.URI;

@RequestMapping("/auth")
@RestController
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        UserResponse userResponse = authService.registerUser(registerRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("users/{id}").buildAndExpand(userResponse.getId()).toUri();
        return ResponseEntity.created(location).body(userResponse);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        String jwt = authService.loginUser(loginRequest);
        LoginResponse loginResponse = new LoginResponse(jwt);
        return ResponseEntity.ok(loginResponse);
    }
}
