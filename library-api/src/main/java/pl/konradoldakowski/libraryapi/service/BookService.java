package pl.konradoldakowski.libraryapi.service;


import org.springframework.stereotype.Service;
import pl.konradoldakowski.libraryapi.dto.BookResponse;
import pl.konradoldakowski.libraryapi.entity.Book;
import pl.konradoldakowski.libraryapi.exception.BookAlreadyExistsException;
import pl.konradoldakowski.libraryapi.exception.BookNotFoundException;
import pl.konradoldakowski.libraryapi.repository.BookRepository;
import pl.konradoldakowski.libraryapi.repository.LoanRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    public BookService(BookRepository bookRepository, LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
    }

    public Book createBook(Book book) {
        if(bookRepository.existsByIsbn(book.getIsbn())) {
            throw new BookAlreadyExistsException("Book with ISBN already exists!");
        }
        return bookRepository.save(book);
    }
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException("Book with Id: " + id + " not found!"));
        return mapToBookResponse(book);
    }
    public List<BookResponse> getAllBooks() {
        Iterable<Book> allBooks = bookRepository.findAll();
        return StreamSupport.stream(allBooks.spliterator(), false).map(this::mapToBookResponse).toList();
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
    private BookResponse mapToBookResponse(Book book) {
        boolean available = !loanRepository.existsByBookIdAndReturnedAtIsNull(book.getId());
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getPublicationYear(), book.getIsbn(), available);
    }
}
