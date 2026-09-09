package pl.konradoldakowski.libraryapi.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.konradoldakowski.libraryapi.dto.CreateLoanRequest;
import pl.konradoldakowski.libraryapi.entity.Book;
import pl.konradoldakowski.libraryapi.entity.Loan;
import pl.konradoldakowski.libraryapi.entity.User;
import pl.konradoldakowski.libraryapi.exception.*;
import pl.konradoldakowski.libraryapi.repository.BookRepository;
import pl.konradoldakowski.libraryapi.repository.LoanRepository;
import pl.konradoldakowski.libraryapi.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LoanServiceTest {
    @Test
    public void createLoanTest(){
        CreateLoanRequest request = new CreateLoanRequest();
        request.setBookId(1L);
        request.setUserId(1L);
        request.setDays(4);

        Book book = createBook();
        User user = createUser();

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBook(book);
        loan.setId(1L);
        loan.setBorrowedAt(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(4));

        LoanRepository loanRepository = mock(LoanRepository.class);
        BookRepository bookRepository = mock(BookRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(loanRepository.existsByBookIdAndReturnedAtIsNull(1L)).thenReturn(false);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        LoanService loanService = new LoanService(loanRepository, userRepository, bookRepository);
        Loan loan1 = loanService.createLoan(request);

        assertEquals(request.getBookId(), loan1.getBook().getId());
        assertEquals(request.getUserId(), loan1.getUser().getId());
        assertEquals(LocalDate.now(), loan1.getBorrowedAt());
        assertEquals(LocalDate.now().plusDays(4), loan1.getDueDate());
        assertNull(loan1.getReturnedAt());
        verify(loanRepository,times(1)).save(any(Loan.class));
    }
    @Test
    public void shouldThrowExceptionWhenUserIsNotFound() {
        CreateLoanRequest request = new CreateLoanRequest();
        request.setBookId(1L);
        request.setUserId(1L);
        request.setDays(4);

        LoanRepository loanRepository = mock(LoanRepository.class);
        BookRepository bookRepository = mock(BookRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        LoanService loanService = new LoanService(loanRepository, userRepository, bookRepository);
        Assertions.assertThrows(UserNotFoundException.class, () -> loanService.createLoan(request));
    }
    @Test
    public void shouldThrowExceptionWhenBookIsNotFound() {
        CreateLoanRequest request = new CreateLoanRequest();
        request.setBookId(1L);
        request.setUserId(1L);
        request.setDays(4);

        User user = createUser();

        LoanRepository loanRepository = mock(LoanRepository.class);
        BookRepository bookRepository = mock(BookRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        LoanService loanService = new LoanService(loanRepository, userRepository, bookRepository);
        Assertions.assertThrows(BookNotFoundException.class, () -> loanService.createLoan(request));
    }
    @Test
    public void shouldThrowExceptionWhenBookIsAlreadyLent() {
        CreateLoanRequest request = new CreateLoanRequest();
        request.setBookId(1L);
        request.setUserId(1L);
        request.setDays(4);

        User user = createUser();
        Book book = createBook();

        LoanRepository loanRepository = mock(LoanRepository.class);
        BookRepository bookRepository = mock(BookRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(loanRepository.existsByBookIdAndReturnedAtIsNull(1L)).thenReturn(true);
        LoanService loanService = new LoanService(loanRepository, userRepository, bookRepository);
        Assertions.assertThrows(BookAlreadyLentException.class, () -> loanService.createLoan(request));
    }
    @Test
    public void shouldReturnLoanById() {
        Loan loan = createLoan();

        LoanRepository loanRepository = mock(LoanRepository.class);
        BookRepository bookRepository = mock(BookRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        LoanService loanService = new LoanService(loanRepository, userRepository, bookRepository);

        Loan loanById = loanService.getLoanById(1L);
        assertEquals(loan,loanById);
    }
    @Test
    public void shouldThrowExceptionWhenLoanWithGivenIdIsNotFound() {
        LoanRepository loanRepository = mock(LoanRepository.class);
        BookRepository bookRepository = mock(BookRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        when(loanRepository.findById(1L)).thenReturn(Optional.empty());
        LoanService loanService = new LoanService(loanRepository, userRepository, bookRepository);

        assertThrows(LoanNotFoundException.class, () -> loanService.getLoanById(1L));
    }
    @Test
    public void shouldReturnListOfLoans() {
        List<Loan> loans = List.of(createLoan(), createLoan(), createLoan(), createLoan(), createLoan());
        LoanRepository loanRepository = mock(LoanRepository.class);
        BookRepository bookRepository = mock(BookRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        when(loanRepository.findAll()).thenReturn(loans);
        LoanService loanService = new LoanService(loanRepository, userRepository, bookRepository);
        assertEquals(loans, loanService.getAllLoans());
    }
    @Test
    public void shouldSetReturnedDateWhenBookIsReturned() {
        Loan loan = createLoan();

        LoanRepository loanRepository = mock(LoanRepository.class);
        BookRepository bookRepository = mock(BookRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(loan)).thenReturn(loan);
        LoanService loanService = new LoanService(loanRepository, userRepository, bookRepository);

        Loan loan2 = loanService.returnBook(1L);
        assertEquals(LocalDate.now(),loan2.getReturnedAt());
    }
    @Test
    public void shouldThrowExceptionWhenLoanDoesNotExist() {
        LoanRepository loanRepository = mock(LoanRepository.class);
        BookRepository bookRepository = mock(BookRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        when(loanRepository.findById(1L)).thenReturn(Optional.empty());

        LoanService loanService = new LoanService(loanRepository, userRepository, bookRepository);
        assertThrows(LoanNotFoundException.class, () -> loanService.returnBook(1L));
    }
    @Test
    public void shouldThrowExceptionWhenBookIsAlreadyReturned() {
        Loan loan = createLoan();
        loan.setReturnedAt(LocalDate.now());
        LoanRepository loanRepository = mock(LoanRepository.class);
        BookRepository bookRepository = mock(BookRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        LoanService loanService = new LoanService(loanRepository, userRepository, bookRepository);
        assertThrows(BookAlreadyReturnedException.class, () -> loanService.returnBook(1L));
    }
    @Test
    public void shouldReturnLoansByUserId() {
        List<Loan> loans = List.of(createLoan(), createLoan(), createLoan());
        LoanRepository loanRepository = mock(LoanRepository.class);
        BookRepository bookRepository = mock(BookRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(loanRepository.findByUserId(1L)).thenReturn(loans);
        LoanService loanService = new LoanService(loanRepository, userRepository, bookRepository);
        List<Loan> loansByUserId = loanService.getLoansByUserId(1L);
        assertEquals(loans, loansByUserId);
    }
    @Test
    public void shouldThrowExceptionWhenUserDoesNotExist() {
        LoanRepository loanRepository = mock(LoanRepository.class);
        BookRepository bookRepository = mock(BookRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.existsById(1L)).thenReturn(false);
        LoanService loanService = new LoanService(loanRepository, userRepository, bookRepository);
        assertThrows(UserNotFoundException.class, () -> loanService.getLoansByUserId(1L));
    }
    private static Book createBook() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Harry Potter");
        book.setAuthor("J.K. Rowling");
        book.setPublicationYear(2001);
        book.setIsbn("978-1234567890");
        return book;
    }
    public User createUser(){
        User user = new User();
        user.setId(1L);
        user.setFirstName("Adam");
        user.setLastName("Kowalski");
        user.setEmail("adam.kowalski@gmail.com");
        user.setPhoneNumber("123456789");
        return user;
    }
    public Loan createLoan() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setUser(createUser());
        loan.setBook(createBook());
        loan.setBorrowedAt(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(4));
        return loan;
    }
}
