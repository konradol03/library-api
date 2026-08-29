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

    public Book createBook(Book book) {
        if(bookRepository.existsByIsbn(book.getIsbn())) {
            throw new BookAlreadyExistsException("Book with ISBN already exists!");
        }
        return bookRepository.save(book);
    }
    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException("Book with Id: " + id + " not found!"));
    }
    public List<Book> getAllBooks() {
        Iterable<Book> allBooks = bookRepository.findAll();
        return StreamSupport.stream(allBooks.spliterator(), false).toList();
    }
    public void deleteBook(Long id) {
        if(!bookRepository.existsById(id)) {
            throw new BookNotFoundException("Book with Id: "+id+" not found!");
        }
        bookRepository.deleteById(id);
    }
    public Book updateBook(Long id, Book book) {
        Book updatedBook = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException("Book with Id: " + id + " not found!"));
        Optional<Book> bookWithSameIsbn = bookRepository.findByIsbn(book.getIsbn());

        if(bookWithSameIsbn.isPresent() && !bookWithSameIsbn.get().getId().equals(id)) {
            throw new BookAlreadyExistsException("Book with ISBN already exists!");
        }

        updatedBook.setTitle(book.getTitle());
        updatedBook.setAuthor(book.getAuthor());
        updatedBook.setPublicationYear(book.getPublicationYear());
        updatedBook.setIsbn(book.getIsbn());
        return bookRepository.save(updatedBook);
    }

}
