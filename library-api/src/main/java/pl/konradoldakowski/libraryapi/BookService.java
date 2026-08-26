package pl.konradoldakowski.libraryapi;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void createBook(Book book) {
        if(bookRepository.existsByIsbn(book.getIsbn())) {
            throw new BookAlreadyExistsException("Book with ISBN already exists!");
        }
        bookRepository.save(book);
    }
    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException("Book with Id: " + id + " not found!"));
    }
    public List<Book> getAllBooks() {
        Iterable<Book> allBooks = bookRepository.findAll();
        return StreamSupport.stream(allBooks.spliterator(), false).toList();
    }

}
