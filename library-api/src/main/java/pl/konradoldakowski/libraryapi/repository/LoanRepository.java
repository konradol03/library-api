package pl.konradoldakowski.libraryapi.repository;

import org.springframework.data.repository.CrudRepository;
import pl.konradoldakowski.libraryapi.entity.Loan;

public interface LoanRepository extends CrudRepository<Loan, Long> {
    boolean existsByBookIdAndReturnedAtIsNull(Long bookId);
}
