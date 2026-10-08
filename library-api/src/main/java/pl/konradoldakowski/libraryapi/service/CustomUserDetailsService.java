package pl.konradoldakowski.libraryapi.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.konradoldakowski.libraryapi.entity.User;
import pl.konradoldakowski.libraryapi.repository.UserRepository;
import pl.konradoldakowski.libraryapi.security.CustomUserDetails;


@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User with email "+ username + " not found"));
        return new CustomUserDetails(user);
    }
}
