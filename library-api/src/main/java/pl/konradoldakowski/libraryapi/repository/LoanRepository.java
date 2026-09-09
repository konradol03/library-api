package pl.konradoldakowski.libraryapi.repository;

import org.springframework.data.repository.CrudRepository;
import pl.konradoldakowski.libraryapi.entity.Loan;

import java.util.List;

public interface LoanRepository extends CrudRepository<Loan, Long> {
    boolean existsByBookIdAndReturnedAtIsNull(Long bookId);
    List<Loan> findByUserId(Long userId);
}
