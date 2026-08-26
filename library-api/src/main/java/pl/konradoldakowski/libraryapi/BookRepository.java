package pl.konradoldakowski.libraryapi;

import org.springframework.data.repository.CrudRepository;

public interface BookRepository extends CrudRepository<Book, Long> {
    public boolean existsByIsbn(String isbn);
}
