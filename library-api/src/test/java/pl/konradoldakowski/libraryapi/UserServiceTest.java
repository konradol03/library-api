package pl.konradoldakowski.libraryapi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

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
    @Test
    public void shouldReturnEmptyListWhenNoUsersExist() {
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findAll()).thenReturn(List.of());
        UserService userService = new UserService(userRepository);
        assertTrue(userService.getAllUsers().isEmpty());
        verify(userRepository, times(1)).findAll();
    }
    @Test
    public void shouldDeleteUserIfExists() {
        User user = createUser();
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.existsById(user.getId())).thenReturn(true);
        UserService userService = new UserService(userRepository);
        userService.deleteUserById(user.getId());
        verify(userRepository, times(1)).deleteById(user.getId());
    }
    @Test
    public void shouldThrowExceptionWhenUserToDeleteDoesNotExist() {
        User user = createUser();
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.existsById(user.getId())).thenReturn(false);
        UserService userService = new UserService(userRepository);
        assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(user.getId()));
        verify(userRepository, times(0)).deleteById(user.getId());
    }
    @Test
    public void shouldThrowExceptionWhenUserToUpdateDoesNotExist() {
        User user = createUser();
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        UserService userService = new UserService(userRepository);
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(5L, user));
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
        User user2 = new User();
        user2.setId(10L);
        user2.setFirstName("Wojciech");
        user2.setLastName("Nowak");
        user2.setPhoneNumber("987654321");
        user2.setEmail("jacek@gmail.com");
        UserRepository userRepository = mock(UserRepository.class);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        UserService userService = new UserService(userRepository);

        assertThrows(EmailAlreadyInUseException.class, () -> userService.updateUser(user.getId(), user2));
    }
    @Test
    public void shouldUpdateUserIfEverythingisFine() {
        User existingUser = createUser();
        User newUserData = new User();
        newUserData.setFirstName("Konrad");
        newUserData.setLastName("Lewy");
        newUserData.setPhoneNumber("987654321");
        newUserData.setEmail("konrad@gmail.com");

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("konrad@gmail.com")).thenReturn(Optional.empty());
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        UserService userService = new UserService(userRepository);
        User result = userService.updateUser(existingUser.getId(), newUserData);

        assertEquals("Konrad", result.getFirstName());
        assertEquals("Lewy", result.getLastName());
        assertEquals("987654321", result.getPhoneNumber());
        assertEquals("konrad@gmail.com", result.getEmail());
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
