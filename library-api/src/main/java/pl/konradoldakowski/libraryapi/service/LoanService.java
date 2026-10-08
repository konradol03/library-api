package pl.konradoldakowski.libraryapi.service;

import org.springframework.stereotype.Service;
import pl.konradoldakowski.libraryapi.dto.CreateLoanRequest;
import pl.konradoldakowski.libraryapi.dto.LoanResponse;
import pl.konradoldakowski.libraryapi.entity.Book;
import pl.konradoldakowski.libraryapi.entity.Loan;
import pl.konradoldakowski.libraryapi.entity.User;
import pl.konradoldakowski.libraryapi.exception.*;
import pl.konradoldakowski.libraryapi.repository.BookRepository;
import pl.konradoldakowski.libraryapi.repository.LoanRepository;
import pl.konradoldakowski.libraryapi.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class LoanService {
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public LoanService(LoanRepository loanRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public LoanResponse createLoan(CreateLoanRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found"));

        Book book = bookRepository.findById(request.getBookId()).orElseThrow(() -> new BookNotFoundException("Book not found"));

        if(loanRepository.existsByBookIdAndReturnedAtIsNull(book.getId())) {
            throw new BookAlreadyLentException("Book already lent");
        }

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBook(book);

        LocalDate borrowedAt = LocalDate.now();
        loan.setBorrowedAt(borrowedAt);
        loan.setDueDate(borrowedAt.plusDays(request.getDays()));

        Loan savedLoan = loanRepository.save(loan);
        return mapToLoanResponse(savedLoan);
    }
    public LoanResponse getLoanById(Long id) {
        Loan loan = loanRepository.findById(id).orElseThrow(() -> new LoanNotFoundException("Loan with id: " + id + " not found"));
        return mapToLoanResponse(loan);
    }
    public List<LoanResponse> getAllLoans() {
        Iterable<Loan> allLoans = loanRepository.findAll();
        List<Loan> loans = StreamSupport.stream(allLoans.spliterator(), false).toList();
        return loans.stream().map(this::mapToLoanResponse).toList();
    }
    public LoanResponse returnBook(Long loanId){
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new LoanNotFoundException("Loan with id: " + loanId + " not found"));
        if (loan.getReturnedAt() != null) {
            throw new BookAlreadyReturnedException("Book already returned");
        }
        loan.setReturnedAt(LocalDate.now());
        Loan savedLoan = loanRepository.save(loan);
        return mapToLoanResponse(savedLoan);
    }
    public List<LoanResponse> getLoansByUserId(Long userId) {
        if(!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with id: "+userId+" not found");
        }
        List<Loan> loansByUserId = loanRepository.findByUserId(userId);
        return loansByUserId.stream().map(this::mapToLoanResponse).toList();
    }

    private LoanResponse mapToLoanResponse(Loan loan) {
        return new LoanResponse(loan.getId(), loan.getUser().getId(), loan.getBook().getId(), loan.getBorrowedAt(), loan.getDueDate(), loan.getReturnedAt());
    }
}
