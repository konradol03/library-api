package pl.konradoldakowski.libraryapi;


import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BookServiceTest {

    @Test
    public void shouldNotCreateBookWhenIsbnAlreadyExists() {
        Book book = createBook();

        BookRepository bookRepository = mock(BookRepository.class);
        when(bookRepository.existsByIsbn(book.getIsbn())).thenReturn(true);
        BookService bookService = new BookService(bookRepository);

        Assertions.assertThrows(BookAlreadyExistsException.class, () -> bookService.createBook(book));
        verify(bookRepository, never()).save(book);
    }

    @Test
    public void shouldCreateBookWhenIsbnDoesNotExist() {
        Book book = createBook();
        BookRepository bookRepository = mock(BookRepository.class);
        when(bookRepository.existsByIsbn(book.getIsbn())).thenReturn(false);

        BookService bookService = new BookService(bookRepository);
        Assertions.assertDoesNotThrow(() -> bookService.createBook(book));
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    public void shouldReturnBookWhenBookExists() {
        Book book = createBook();

        BookRepository bookRepository = mock(BookRepository.class);
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        BookService bookService = new BookService(bookRepository);
        assertEquals(book, bookService.getBookById(book.getId()));
    }
    @Test
    public void shouldNotReturnBookWhenBookDoesNotExist() {
        Long bookId = 1L;
        BookRepository bookRepository = mock(BookRepository.class);
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        BookService bookService = new BookService(bookRepository);
        Assertions.assertThrows(BookNotFoundException.class, () -> bookService.getBookById(bookId));
    }

    @Test
    public void shouldReturnListOfAllBooks() {
        List<Book> books = List.of(createBook(), createBook(), createBook(), createBook(), createBook());
        BookRepository bookRepository = mock(BookRepository.class);
        when(bookRepository.findAll()).thenReturn(books);
        BookService bookService = new BookService(bookRepository);
        assertEquals(books, bookService.getAllBooks());
    }
    @Test
    public void shouldDeleteBookWhenBookExists() {
        Book book = createBook();
        BookRepository bookRepository = mock(BookRepository.class);
        when(bookRepository.existsById(book.getId())).thenReturn(true);
        BookService bookService = new BookService(bookRepository);
        bookService.deleteBook(book.getId());
        verify(bookRepository, times(1)).deleteById(book.getId());
    }
    @Test
    public void shouldThrowExceptionWhenBookToDeleteDoesNotExist() {
        Book book = createBook();
        BookRepository bookRepository = mock(BookRepository.class);
        when(bookRepository.existsById(book.getId())).thenReturn(false);
        BookService bookService = new BookService(bookRepository);
        Assertions.assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(book.getId()));
    }

    private static @NonNull Book createBook() {
        Book book = new Book();
        book.setId(1);
        book.setTitle("Harry Potter");
        book.setAuthor("J.K. Rowling");
        book.setPublicationYear(2001);
        book.setIsbn("978-1234567890");
        return book;
    }
}
