package pl.konradoldakowski.libraryapi.repository;

import org.springframework.data.repository.CrudRepository;
import pl.konradoldakowski.libraryapi.entity.Book;

import java.util.Optional;

public interface BookRepository extends CrudRepository<Book, Long> {
    boolean existsByIsbn(String isbn);
    Optional<Book> findByIsbn(String isbn);
}
