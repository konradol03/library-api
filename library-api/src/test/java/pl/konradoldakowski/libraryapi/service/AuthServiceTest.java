package pl.konradoldakowski.libraryapi.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.konradoldakowski.libraryapi.dto.RegisterRequest;
import pl.konradoldakowski.libraryapi.dto.UserResponse;
import pl.konradoldakowski.libraryapi.entity.Role;
import pl.konradoldakowski.libraryapi.entity.User;
import pl.konradoldakowski.libraryapi.exception.EmailAlreadyInUseException;
import pl.konradoldakowski.libraryapi.repository.UserRepository;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        authService = new AuthService(userRepository, passwordEncoder);
    }

    @Test
    public void shouldRegisterUser() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Konrad");
        registerRequest.setLastName("Kowalski");
        registerRequest.setEmail("konrad@gmail.com");
        registerRequest.setPhoneNumber("123456789");
        registerRequest.setPassword("haslo123");

        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("zahashowaneHaslo");
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        UserResponse userResponse = authService.registerUser(registerRequest);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(registerRequest.getPassword());
        assertEquals("Konrad", savedUser.getFirstName());
        assertEquals("Kowalski", savedUser.getLastName());
        assertEquals("konrad@gmail.com", savedUser.getEmail());
        assertEquals("123456789", savedUser.getPhoneNumber());
        assertEquals("zahashowaneHaslo", savedUser.getPassword());
        assertEquals(Role.USER, savedUser.getRole());
    }
    @Test
    public void shouldThrownExceptionWhenUserWithGivenEmailExists() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Konrad");
        registerRequest.setLastName("Kowalski");
        registerRequest.setEmail("konrad@gmail.com");
        registerRequest.setPhoneNumber("123456789");
        registerRequest.setPassword("haslo123");

        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);
        Assertions.assertThrows(EmailAlreadyInUseException.class, () -> authService.registerUser(registerRequest));

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }
}
