package pl.konradoldakowski.libraryapi.repository;

import org.springframework.data.repository.CrudRepository;
import pl.konradoldakowski.libraryapi.entity.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
