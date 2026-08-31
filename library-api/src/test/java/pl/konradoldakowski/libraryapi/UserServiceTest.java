package pl.konradoldakowski.libraryapi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

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
