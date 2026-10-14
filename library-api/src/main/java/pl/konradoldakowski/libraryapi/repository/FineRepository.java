package pl.konradoldakowski.libraryapi.repository;

import org.springframework.data.repository.CrudRepository;
import pl.konradoldakowski.libraryapi.entity.Fine;

public interface FineRepository extends CrudRepository<Fine, Long> {
    boolean existsByLoanId(Long loanId);
}
