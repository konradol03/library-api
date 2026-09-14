package pl.konradoldakowski.libraryapi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.konradoldakowski.libraryapi.dto.UserResponse;
import pl.konradoldakowski.libraryapi.entity.Role;
import pl.konradoldakowski.libraryapi.entity.User;
import pl.konradoldakowski.libraryapi.exception.EmailAlreadyInUseException;
import pl.konradoldakowski.libraryapi.exception.UserNotFoundException;
import pl.konradoldakowski.libraryapi.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    public void shouldReturnUserByIdWhenExists() throws Exception {
        User user = createUser();
        UserResponse userResponse = new UserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhoneNumber(), user.getRole());
        when(userService.getUserById(user.getId())).thenReturn(userResponse);
        mockMvc.perform(get("/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.phoneNumber").value(user.getPhoneNumber()))
                .andExpect(jsonPath("$.role").value(user.getRole().name()));
    }
    @Test
    public void shouldReturnListOfUsers() throws Exception {
        List<User> users = List.of(createUser(), createUser(), createUser(), createUser());
        List<UserResponse> userResponses = users.stream().map(user -> {
            return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhoneNumber(), user.getRole());
        }).toList();
        when(userService.getAllUsers()).thenReturn(userResponses);
        mockMvc.perform(get("/users")).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[1].id").value(users.get(1).getId()))
                .andExpect(jsonPath("$[1].firstName").value(users.get(1).getFirstName()));
    }
    @Test
    public void shouldReturnEmptyListWhenNoUsersExist() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of());
        mockMvc.perform(get("/users")).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
    @Test
    public void shouldThrowExceptionWhenUserDoesNotExist() throws Exception {
        User user = createUser();
        when(userService.getUserById(user.getId())).thenThrow(new UserNotFoundException("User with given ID not found"));
        mockMvc.perform(get("/users/{id}", user.getId())).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User with given ID not found"));
    }
    @Test
    public void shouldDeleteUserIfExists() throws Exception {
        mockMvc.perform(delete("/users/{id}",1L)).andExpect(status().isNoContent());
        verify(userService).deleteUserById(1L);
    }
    @Test
    public void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() throws Exception {
        doThrow(new UserNotFoundException("User with given ID not found")).when(userService).deleteUserById(1L);
        mockMvc.perform(delete("/users/{id}",1L)).andExpect(status().isNotFound());
    }
    @Test
    public void shouldUpdateUser() throws Exception {
        User user = createUser();
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(user);
        mockMvc.perform(put("/users/{id}",1L).contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "firstName": "Adam",
                        "lastName": "Kowalski",
                        "email": "adam.kowalski@gmail.com",
                        "phoneNumber": "123456789"
                        }
                        """)).andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.phoneNumber").value(user.getPhoneNumber()));
    }
    @Test
    public void shouldThrowExceptionUserIsNotFoundWhenUserDoesNotExist() throws Exception {
        when(userService.updateUser(eq(1L), any(User.class))).thenThrow(new UserNotFoundException("User with given ID not found"));
        mockMvc.perform(put("/users/{id}",1L).contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "firstName": "Adam",
                        "lastName": "Kowalski",
                        "email": "adam.kowalski@gmail.com",
                        "phoneNumber": "123456789"
                        }
                        """)).andExpect(status().isNotFound());
    }
    @Test
    public void shouldThrowExceptionIfUserWithGivenEmailAlreadyExists() throws Exception {
        when(userService.updateUser(eq(1L), any(User.class))).thenThrow(new EmailAlreadyInUseException("User with given email already exists"));
        mockMvc.perform(put("/users/{id}",1L).contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "firstName": "Adam",
                        "lastName": "Kowalski",
                        "email": "adam.kowalski@gmail.com",
                        "phoneNumber": "123456789"
                        }
                        """)).andExpect(status().isConflict());
    }
    @Test
    public void shouldReturnBadRequestInPutMethod() throws Exception {
        mockMvc.perform(put("/users/{id}",1L).contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "firstName": "",
                        "lastName": "Kowalski",
                        "email": "adam.kowalski@gmail.com",
                        "phoneNumber": "123456789"
                        }
                        """)).andExpect(status().isBadRequest());
    }
    public User createUser(){
        User user = new User();
        user.setId(1L);
        user.setFirstName("Adam");
        user.setLastName("Kowalski");
        user.setEmail("adam.kowalski@gmail.com");
        user.setPhoneNumber("123456789");
        user.setRole(Role.USER);
        return user;
    }
}
