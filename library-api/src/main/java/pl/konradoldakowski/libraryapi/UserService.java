package pl.konradoldakowski.libraryapi;


import org.springframework.stereotype.Service;

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

}
