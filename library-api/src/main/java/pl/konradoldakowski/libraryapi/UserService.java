package pl.konradoldakowski.libraryapi;


import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class UserService {
    private final  UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User addUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyInUseException("User with given email already exists");
        }
        return userRepository.save(user);
    }
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }
    public List<User> getAllUsers() {
        Iterable<User> all = userRepository.findAll();
        return StreamSupport.stream(all.spliterator(), false).toList();
    }

}
