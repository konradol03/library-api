package pl.konradoldakowski.libraryapi.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.konradoldakowski.libraryapi.dto.ChangePasswordRequest;
import pl.konradoldakowski.libraryapi.dto.UpdateUserRequest;
import pl.konradoldakowski.libraryapi.dto.UserResponse;
import pl.konradoldakowski.libraryapi.entity.Role;
import pl.konradoldakowski.libraryapi.entity.User;
import pl.konradoldakowski.libraryapi.exception.EmailAlreadyInUseException;
import pl.konradoldakowski.libraryapi.exception.InvalidCurrentPasswordException;
import pl.konradoldakowski.libraryapi.exception.UserNotFoundException;
import pl.konradoldakowski.libraryapi.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class UserServiceTest {

    @Test
    public void shouldAddUser(){
        User user = createUser();
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        when(userRepository.save(user)).thenReturn(user);
        UserService userService = new UserService(userRepository,passwordEncoder);
        User result = userService.addUser(user);
        assertEquals(user, result);
        verify(userRepository, times(1)).save(user);

    }
    @Test
    public void shouldThrowErrorWhenUserWithSameEmailAlreadyExists(){
        User user = createUser();
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        when(userRepository.existsByEmail(user.getEmail())).thenThrow(new EmailAlreadyInUseException("User with given email already exists"));
        UserService userService = new UserService(userRepository, passwordEncoder);
        assertThrows(EmailAlreadyInUseException.class, () -> userService.addUser(user));
        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    public void shouldReturnUserIfExists() {
        User user = createUser();
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        UserService userService = new UserService(userRepository,  passwordEncoder);
        UserResponse result = userService.getUserById(user.getId());
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getLastName(), result.getLastName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(user.getRole(), result.getRole());
    }
    @Test
    public void shouldThrowErrorWhenUserWithIdDoesNotExist(){
        User user = createUser();
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        UserService userService = new UserService(userRepository, passwordEncoder);
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(user.getId()));
    }
    @Test
    public void shouldReturnListOfUsers() {
        List<User> users = List.of(createUser(), createUser(), createUser(), createUser());
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        when(userRepository.findAll()).thenReturn(users);
        UserService userService = new UserService(userRepository, passwordEncoder);

        List<UserResponse> result = userService.getAllUsers();

        assertEquals(4, result.size());
        assertEquals(users.get(0).getId(), result.get(0).getId());
        assertEquals(users.get(0).getFirstName(), result.get(0).getFirstName());
        assertEquals(users.get(0).getLastName(), result.get(0).getLastName());
        assertEquals(users.get(0).getEmail(), result.get(0).getEmail());
        assertEquals(users.get(0).getPhoneNumber(), result.get(0).getPhoneNumber());
        assertEquals(users.get(0).getRole(), result.get(0).getRole());

        verify(userRepository, times(1)).findAll();
    }
    @Test
    public void shouldReturnEmptyListWhenNoUsersExist() {
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        when(userRepository.findAll()).thenReturn(List.of());
        UserService userService = new UserService(userRepository, passwordEncoder);
        assertTrue(userService.getAllUsers().isEmpty());
        verify(userRepository, times(1)).findAll();
    }
    @Test
    public void shouldDeleteUserIfExists() {
        User user = createUser();
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        when(userRepository.existsById(user.getId())).thenReturn(true);
        UserService userService = new UserService(userRepository, passwordEncoder);
        userService.deleteUserById(user.getId());
        verify(userRepository, times(1)).deleteById(user.getId());
    }
    @Test
    public void shouldThrowExceptionWhenUserToDeleteDoesNotExist() {
        User user = createUser();
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        when(userRepository.existsById(user.getId())).thenReturn(false);
        UserService userService = new UserService(userRepository, passwordEncoder);
        assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(user.getId()));
        verify(userRepository, times(0)).deleteById(user.getId());
    }
    @Test
    public void shouldThrowExceptionWhenUserToUpdateDoesNotExist() {
        User user = createUser();
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        UserService userService = new UserService(userRepository, passwordEncoder);
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(5L, new UpdateUserRequest()));
    }
    @Test
    public void shouldThrowExceptionWhenUserWithGivenEmailAlreadyExists() {
        User user = createUser();
        User user1 = new User();
        user1.setId(5L);
        user1.setFirstName("Kamil");
        user1.setLastName("Kowal");
        user1.setPhoneNumber("987654321");
        user1.setEmail("jacek@gmail.com");

        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("Adam");
        request.setLastName("Kowalski");
        request.setEmail("jacek@gmail.com");
        request.setPhoneNumber("123456789");
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        UserService userService = new UserService(userRepository, passwordEncoder);

        assertThrows(EmailAlreadyInUseException.class, () -> userService.updateUser(user.getId(), request));
    }
    @Test
    public void shouldUpdateUserIfEverythingisFine() {
        User existingUser = createUser();
        UpdateUserRequest newUserData = new UpdateUserRequest();
        newUserData.setFirstName("Konrad");
        newUserData.setLastName("Lewy");
        newUserData.setPhoneNumber("987654321");
        newUserData.setEmail("konrad@gmail.com");

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("konrad@gmail.com")).thenReturn(Optional.empty());
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        UserService userService = new UserService(userRepository,passwordEncoder);
        UserResponse result = userService.updateUser(existingUser.getId(), newUserData);

        assertEquals("Konrad", result.getFirstName());
        assertEquals("Lewy", result.getLastName());
        assertEquals("987654321", result.getPhoneNumber());
        assertEquals("konrad@gmail.com", result.getEmail());
    }
    @Test
    public void shouldReturUserByEmailWhenExists() {
        User user = createUser();
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        UserService userService = new UserService(userRepository, passwordEncoder);
        UserResponse userByEmail = userService.getUserByEmail(user.getEmail());
        assertEquals(user.getEmail(), userByEmail.getEmail());
        assertEquals(user.getFirstName(), userByEmail.getFirstName());
        assertEquals(user.getLastName(), userByEmail.getLastName());
        assertEquals(user.getPhoneNumber(), userByEmail.getPhoneNumber());
        assertEquals(user.getRole(), userByEmail.getRole());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }
    @Test
    public void shouldThrowExceptionWhenUserWithGivenEmailDoesNotExist() {
        String email = "missing@email.com";
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        UserService userService = new UserService(userRepository, passwordEncoder);
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail(email));
    }
    @Test
    public void shouldChangePasswordWhenEverythingIsCorrect(){
        User user = createUser();
        user.setPassword("hashedOldPassword");
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        UserService userService = new UserService(userRepository, passwordEncoder);
        when(passwordEncoder.matches("oldPassword","hashedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("hashedNewPassword");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword("oldPassword");
        changePasswordRequest.setNewPassword("newPassword");
        userService.changePassword(1L, changePasswordRequest);
        Assertions.assertEquals("hashedNewPassword", user.getPassword());
        verify(userRepository).save(user);
    }
    @Test
    public void shouldThrowExceptionWhenOldPasswordIsInvalid() {
        User user = createUser();
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        UserService userService = new UserService(userRepository, passwordEncoder);
        when(passwordEncoder.matches("oldPassword","hashedOldPassword")).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Assertions.assertThrows(InvalidCurrentPasswordException.class, () -> userService.changePassword(1L, new ChangePasswordRequest()));
        verify(userRepository, never()).save(user);
    }
    @Test
    public void shouldThrowExceptionWhenUserIsNotFound() {
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        UserService userService = new UserService(userRepository, passwordEncoder);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.changePassword(1L, new ChangePasswordRequest()));
        verify(userRepository, never()).save(any());
    }
    public User createUser(){
        User user = new User();
        user.setId(1L);
        user.setFirstName("Adam");
        user.setLastName("Kowalski");
        user.setEmail("adam.kowalski@gmail.com");
        user.setPhoneNumber("123456789");
        user.setRole(Role.ADMIN);
        return user;
    }
}
