package pl.konradoldakowski.libraryapi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class UserServiceTest {

    @Test
    public void shouldAddUser(){
        User user = createUser();
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.save(user)).thenReturn(user);
        UserService userService = new UserService(userRepository);
        User result = userService.addUser(user);
        assertEquals(user, result);
        verify(userRepository, times(1)).save(user);

    }
    @Test
    public void shouldThrowErrorWhenUserWithSameEmailAlreadyExists(){
        User user = createUser();
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.existsByEmail(user.getEmail())).thenThrow(new EmailAlreadyInUseException("User with given email already exists"));
        UserService userService = new UserService(userRepository);
        assertThrows(EmailAlreadyInUseException.class, () -> userService.addUser(user));
        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    public void shouldReturnUserIfExists() {
        User user = createUser();
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        UserService userService = new UserService(userRepository);
        assertEquals(user, userService.getUserById(user.getId()));
    }
    @Test
    public void shouldThrowErrorWhenUserWithIdDoesNotExist(){
        User user = createUser();
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        UserService userService = new UserService(userRepository);
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(user.getId()));
    }
    @Test
    public void shouldReturnListOfUsers() {
        List<User> users = List.of(new User(), new User(), new User(), new User());
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findAll()).thenReturn(users);
        UserService userService = new UserService(userRepository);
        assertEquals(users, userService.getAllUsers());
        verify(userRepository, times(1)).findAll();
    }

    public User createUser(){
        User user = new User();
        user.setId(1L);
        user.setFirstName("Adam");
        user.setLastName("Kowalski");
        user.setEmail("adam.kowalski@gmail.com");
        user.setPhoneNumber("123456789");
        return user;
    }
}
