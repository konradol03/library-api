package pl.konradoldakowski.libraryapi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.konradoldakowski.libraryapi.dto.CreateLoanRequest;
import pl.konradoldakowski.libraryapi.entity.Book;
import pl.konradoldakowski.libraryapi.entity.Loan;
import pl.konradoldakowski.libraryapi.entity.User;
import pl.konradoldakowski.libraryapi.exception.*;
import pl.konradoldakowski.libraryapi.service.LoanService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanController.class)
public class LoanControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoanService loanService;

    @Test
    public void shouldCreateLoan() throws Exception {
        Loan loan = createLoan();
        when(loanService.createLoan(any(CreateLoanRequest.class))).thenReturn(loan);
        mockMvc.perform(post("/loans").contentType(MediaType.APPLICATION_JSON).content("""
                {
                "userId": 1,
                "bookId": 1,
                "days" : 4
                }
                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/loans/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.book.id").value(1))
                .andExpect(jsonPath("$.borrowedAt").value(LocalDate.now().toString()));

    }
    @Test
    public void shouldThrowExceptionWhenBookIsAlreadyLent() throws Exception {
        when(loanService.createLoan(any(CreateLoanRequest.class))).thenThrow(new BookAlreadyLentException("Book with given Id is already Lent"));
        mockMvc.perform(post("/loans").contentType(MediaType.APPLICATION_JSON).content("""
                {
                "userId": 1,
                "bookId": 1,
                "days" : 4
                }
                """)).andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Book with given Id is already Lent"));
    }
    @Test
    public void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        when(loanService.createLoan(any(CreateLoanRequest.class))).thenThrow(new UserNotFoundException("User with given Id does not exist"));
        mockMvc.perform(post("/loans").contentType(MediaType.APPLICATION_JSON).content("""
                    {
                    "userId": 1,
                    "bookId": 1,
                    "days": 4
                    }
                    """))
                .andExpect(status().isNotFound());
    }
    @Test
    public void shouldReturnNotFoundWhenBookDoesNotExist() throws Exception {
        when(loanService.createLoan(any(CreateLoanRequest.class))).thenThrow(new BookNotFoundException("Book with given Id does not exist"));
        mockMvc.perform(post("/loans").contentType(MediaType.APPLICATION_JSON).content("""
                    {
                    "userId": 1,
                    "bookId": 1,
                    "days": 4
                    }
                    """))
                .andExpect(status().isNotFound());
    }
    @Test
    public void shouldReturnBadRequestWhenBookIsLentForMoreThan30Days() throws Exception {
        mockMvc.perform(post("/loans").contentType(MediaType.APPLICATION_JSON).content("""
                {
                "userId": 1,
                "bookId": 1,
                "days" : 31
                }
                """))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void shouldReturnBadRequestWhenBookIsLentForLessThan1Day() throws Exception {
        mockMvc.perform(post("/loans").contentType(MediaType.APPLICATION_JSON).content("""
                {
                "userId": 1,
                "bookId": 1,
                "days" : 0
                }
                """))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void shouldReturnBadRequestWhenUserIdIsMissing() throws Exception {
        mockMvc.perform(post("/loans").contentType(MediaType.APPLICATION_JSON).content("""
                {
                "bookId": 1,
                "days" : 10
                }
                """))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void shouldReturnBadRequestWhenBookIdIsMissing() throws Exception {
        mockMvc.perform(post("/loans").contentType(MediaType.APPLICATION_JSON).content("""
                {
                "userId": 1,
                "days" : 10
                }
                """))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void shouldReturnLoanWhenExists() throws Exception {
        Loan loan = createLoan();
        when(loanService.getLoanById(1L)).thenReturn(loan);
        mockMvc.perform(get("/loans/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.book.id").value(1))
                .andExpect(jsonPath("$.borrowedAt").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.dueDate").value(LocalDate.now().plusDays(4).toString()))
                .andExpect(jsonPath("$.returnedAt").isEmpty());
    }
    @Test
    public void shouldReturnNotFoundWhenLoanDoesNotExist() throws Exception {
        when(loanService.getLoanById(1L)).thenThrow(new LoanNotFoundException("Loan with id " + 1L + " does not exist"));
        mockMvc.perform(get("/loans/1")).andExpect(status().isNotFound());
    }
    @Test
    public void shouldReturnListOfLoans() throws Exception {
        List<Loan> loans = List.of(createLoan(), createLoan(), createLoan());
        when(loanService.getAllLoans()).thenReturn(loans);
        mockMvc.perform(get("/loans")).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.length()").value(loans.size()))
                .andExpect(jsonPath("$[0].id").value(1));
    }
    @Test
    public void shouldReturnEmptyListWhenNoLoanExists() throws Exception {
        when(loanService.getAllLoans()).thenReturn(List.of());
        mockMvc.perform(get("/loans")).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
    @Test
    public void shouldReturnUserLoans() throws Exception {
        List<Loan> loans = List.of(createLoan(), createLoan(), createLoan());
        when(loanService.getLoansByUserId(1L)).thenReturn(loans);
        mockMvc.perform(get("/loans/user/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.length()").value(loans.size()))
                .andExpect(jsonPath("$[0].id").value(1));
    }
    @Test
    public void shouldReturnBook() throws Exception {
        Loan loan = createLoan();
        loan.setReturnedAt(LocalDate.now());

        when(loanService.returnBook(1L)).thenReturn(loan);
        mockMvc.perform(patch("/loans/1/return")).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.returnedAt").value(LocalDate.now().toString()));
    }
    @Test
    public void shouldReturnNotFoundWhenReturningNonExistingLoan() throws Exception {
        when(loanService.returnBook(1L)).thenThrow(new LoanNotFoundException("Loan with id: 1 not found"));
        mockMvc.perform(patch("/loans/1/return")).andExpect(status().isNotFound());
    }
    @Test
    public void shouldReturnConflictWhenBookIsAlreadyReturned() throws Exception {
        when(loanService.returnBook(1L)).thenThrow(new BookAlreadyReturnedException("Book already returned"));
        mockMvc.perform(patch("/loans/1/return")).andExpect(status().isConflict());
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
