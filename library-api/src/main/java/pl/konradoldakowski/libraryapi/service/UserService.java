package pl.konradoldakowski.libraryapi.service;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.konradoldakowski.libraryapi.dto.ChangePasswordRequest;
import pl.konradoldakowski.libraryapi.dto.ChangeRoleRequest;
import pl.konradoldakowski.libraryapi.dto.UpdateUserRequest;
import pl.konradoldakowski.libraryapi.dto.UserResponse;
import pl.konradoldakowski.libraryapi.entity.Role;
import pl.konradoldakowski.libraryapi.entity.User;
import pl.konradoldakowski.libraryapi.exception.EmailAlreadyInUseException;
import pl.konradoldakowski.libraryapi.exception.InvalidCurrentPasswordException;
import pl.konradoldakowski.libraryapi.exception.LastAdminException;
import pl.konradoldakowski.libraryapi.exception.UserNotFoundException;
import pl.konradoldakowski.libraryapi.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User addUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyInUseException("User with given email already exists");
        }
        return userRepository.save(user);
    }
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhoneNumber(), user.getRole());
    }
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User with given email not found"));
        return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhoneNumber(), user.getRole());
    }
    public List<UserResponse> getAllUsers() {
        Iterable<User> all = userRepository.findAll();
        return StreamSupport.stream(all.spliterator(), false).map(user -> {
           return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhoneNumber(), user.getRole());
        }).toList();
    }
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with given id not found"));
        if(user.getRole() ==  Role.ADMIN && userRepository.countByRole(Role.ADMIN) <= 1) {
            throw new LastAdminException("Cannot delete last administrator");
        }
        userRepository.deleteById(id);
    }
    public UserResponse updateUser(Long id, UpdateUserRequest user) {
        User userToUpdate = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        Optional<User> userByEmail = userRepository.findByEmail(user.getEmail());
        if(userByEmail.isPresent() && !userByEmail.get().getId().equals(id)) {
            throw new EmailAlreadyInUseException("User with given email already exists");
        }
        userToUpdate.setFirstName(user.getFirstName());
        userToUpdate.setLastName(user.getLastName());
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setPhoneNumber(user.getPhoneNumber());
        userRepository.save(userToUpdate);
        return new UserResponse(userToUpdate.getId(), userToUpdate.getFirstName(), userToUpdate.getLastName(), userToUpdate.getEmail(), userToUpdate.getPhoneNumber(), userToUpdate.getRole());
    }
    public void changePassword(Long Id, ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findById(Id).orElseThrow(() -> new UserNotFoundException("User with given ID not found"));
        if(!passwordEncoder.matches(changePasswordRequest.getOldPassword(),user.getPassword())){
            throw new InvalidCurrentPasswordException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
    }
    public UserResponse changeUserRole(Long id, ChangeRoleRequest request){
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with given id not found"));
        if(user.getRole() == Role.ADMIN && request.getRole() == Role.USER && userRepository.countByRole(Role.ADMIN) <= 1){
            throw new LastAdminException("Cannot remove the last administrator");
        }
        user.setRole(request.getRole());
        userRepository.save(user);
        return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhoneNumber(), user.getRole());
    }
}
