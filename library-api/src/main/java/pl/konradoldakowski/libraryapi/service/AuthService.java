package pl.konradoldakowski.libraryapi.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.konradoldakowski.libraryapi.dto.LoginRequest;
import pl.konradoldakowski.libraryapi.dto.RegisterRequest;
import pl.konradoldakowski.libraryapi.dto.UserResponse;
import pl.konradoldakowski.libraryapi.entity.Role;
import pl.konradoldakowski.libraryapi.entity.User;
import pl.konradoldakowski.libraryapi.exception.EmailAlreadyInUseException;
import pl.konradoldakowski.libraryapi.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public UserResponse registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyInUseException("User with given email already exists!");
        }
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User createdUser = new User(request.getFirstName(), request.getLastName(), request.getEmail(), request.getPhoneNumber(), encodedPassword, Role.USER);
        userRepository.save(createdUser);
        return new UserResponse(createdUser.getId(), createdUser.getFirstName(), createdUser.getLastName(), createdUser.getEmail(), createdUser.getPhoneNumber(), createdUser.getRole());
    }

    public void loginUser(LoginRequest request){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    }
}
