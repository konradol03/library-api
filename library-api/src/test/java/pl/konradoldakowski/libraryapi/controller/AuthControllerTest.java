package pl.konradoldakowski.libraryapi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.konradoldakowski.libraryapi.dto.RegisterRequest;
import pl.konradoldakowski.libraryapi.dto.UserResponse;
import pl.konradoldakowski.libraryapi.entity.Role;
import pl.konradoldakowski.libraryapi.exception.EmailAlreadyInUseException;
import pl.konradoldakowski.libraryapi.service.AuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    public void shouldRegisterUser() throws Exception {
        UserResponse userResponse = new UserResponse(1L, "Konrad", "Kowalski", "konrad@gmail.com", "123456789", Role.USER);
        when(authService.registerUser(any(RegisterRequest.class))).thenReturn(userResponse);
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content("""
                {
                "firstName": "Konrad",
                "lastName": "Kowalski",
                "email": "konrad@gmail.com",
                "phoneNumber": "123456789",
                "password": "haslo1234"
                }
                """)).andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("Location", "http://localhost/users/1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Konrad"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Kowalski"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("USER"));
    }
    @Test
    public void shouldReturnIsConflictStatusWhenUserWithGivenEmailAlreadyExists() throws Exception {
        when(authService.registerUser(any(RegisterRequest.class))).thenThrow(new EmailAlreadyInUseException("Email already in use"));
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content("""
                {
                "firstName": "Konrad",
                "lastName": "Kowalski",
                "email": "konrad@gmail.com",
                "phoneNumber": "123456789",
                "password": "haslo1234"
                }
                """)).andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Email already in use"));
    }
    @Test
    public void shouldReturnBadRequestStatusWhenThereIsProblemWithValidation() throws Exception {
        UserResponse userResponse = new UserResponse(1L, "Konrad", "Kowalski", "konrad@gmail.com", "123456789", Role.USER);
        when(authService.registerUser(any(RegisterRequest.class))).thenReturn(userResponse);
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content("""
                {
                "firstName": "Konrad",
                "lastName": "Kowalski",
                "email": "konrad@gmail.com",
                "phoneNumber": "123456789",
                "password": "haslo"
                }
                """)).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
