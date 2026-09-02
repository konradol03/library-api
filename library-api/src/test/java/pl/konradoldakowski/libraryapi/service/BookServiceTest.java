package pl.konradoldakowski.libraryapi.service;


import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.konradoldakowski.libraryapi.entity.Book;
import pl.konradoldakowski.libraryapi.exception.BookAlreadyExistsException;
import pl.konradoldakowski.libraryapi.exception.BookNotFoundException;
import pl.konradoldakowski.libraryapi.repository.BookRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class BookServiceTest {

    @Test
    public void shouldNotCreateBookWhenIsbnAlreadyExists() {
        Book book = createBook();

        BookRepository bookRepository = mock(BookRepository.class);
        when(bookRepository.existsByIsbn(book.getIsbn())).thenReturn(true);
        BookService bookService = new BookService(bookRepository);

        assertThrows(BookAlreadyExistsException.class, () -> bookService.createBook(book));
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
        assertThrows(BookNotFoundException.class, () -> bookService.getBookById(bookId));
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
        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(book.getId()));
    }
    @Test
    public void shouldUpdateBookWhenBookExists() {
        Book existingBook = createBook();
        Book newBookData = new Book();

        newBookData.setTitle("Hobbit");
        newBookData.setAuthor("Tolkien");
        newBookData.setPublicationYear(2000);
        newBookData.setIsbn("123456789");

        BookRepository bookRepository = mock(BookRepository.class);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookRepository.findByIsbn("123456789")).thenReturn(Optional.empty());
        when(bookRepository.save(existingBook)).thenReturn(existingBook);

        BookService bookService = new BookService(bookRepository);
        Book result = bookService.updateBook(existingBook.getId(), newBookData);

        assertEquals("Hobbit", result.getTitle());
        assertEquals("Tolkien", result.getAuthor());
        assertEquals(2000, result.getPublicationYear());
        assertEquals("123456789", result.getIsbn());

        verify(bookRepository).save(existingBook);
    }
    @Test
    public void shouldThrowExceptionWhenBookToUpdateDoesNotExist() {
        Book bookToUpdate = createBook();
        Long id = 3L;
        BookRepository bookRepository = mock(BookRepository.class);
        when(bookRepository.findById(id)).thenReturn(Optional.empty());
        BookService bookService = new BookService(bookRepository);
        assertThrows(BookNotFoundException.class, () -> bookService.updateBook(id,bookToUpdate));
    }
    @Test
    public void shouldThrowExceptionWhenIsbnAlreadyExists() {
        Long id = 5L;

        Book existingBook = new Book();
        existingBook.setId(id);
        existingBook.setTitle("Hobbit");
        existingBook.setAuthor("Tolkien");
        existingBook.setPublicationYear(2000);
        existingBook.setIsbn("123456789");

        Book otherBook = new Book();
        otherBook.setId(10L);
        otherBook.setTitle("Harry Potter");
        otherBook.setAuthor("J.K. Rowling");
        otherBook.setPublicationYear(2001);
        otherBook.setIsbn("987654321");

        Book newBookData = new Book();
        newBookData.setTitle("Hobbit 2");
        newBookData.setAuthor("Tolkien");
        newBookData.setPublicationYear(2001);
        newBookData.setIsbn("987654321");

        BookRepository bookRepository = mock(BookRepository.class);
        when(bookRepository.findById(id)).thenReturn(Optional.of(existingBook));

        when(bookRepository.findByIsbn("987654321")).thenReturn(Optional.of(otherBook));

        BookService bookService = new BookService(bookRepository);
        assertThrows(BookAlreadyExistsException.class, ()->bookService.updateBook(id, newBookData)
        );

        verify(bookRepository, never()).save(any(Book.class));
    }
    @Test
    public void shouldUpdateBookWhenIsbnRemainsTheSame() {
        Long id = 5L;
        Book existingBook = new Book();
        existingBook.setId(id);
        existingBook.setTitle("Hobbit");
        existingBook.setAuthor("Tolkien");
        existingBook.setPublicationYear(2000);
        existingBook.setIsbn("123456789");

        Book newBookData = new Book();
        newBookData.setTitle("Hobbit - wydanie nowe");
        newBookData.setAuthor("Tolkien");
        newBookData.setPublicationYear(2020);
        newBookData.setIsbn("123456789");

        BookRepository bookRepository = mock(BookRepository.class);
        when(bookRepository.findById(id)).thenReturn(Optional.of(existingBook));
        when(bookRepository.findByIsbn("123456789")).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(existingBook)).thenReturn(existingBook);

        BookService bookService = new BookService(bookRepository);
        Book result = bookService.updateBook(id, newBookData);
        assertEquals("Hobbit - wydanie nowe", result.getTitle());
        assertEquals("Tolkien", result.getAuthor());
        assertEquals(2020, result.getPublicationYear());
        assertEquals("123456789", result.getIsbn());

        verify(bookRepository).save(existingBook);
    }
    private static @NonNull Book createBook() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Harry Potter");
        book.setAuthor("J.K. Rowling");
        book.setPublicationYear(2001);
        book.setIsbn("978-1234567890");
        return book;
    }
}
