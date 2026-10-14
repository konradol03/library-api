package pl.konradoldakowski.libraryapi.service;

import org.springframework.stereotype.Service;
import pl.konradoldakowski.libraryapi.config.FineProperties;
import pl.konradoldakowski.libraryapi.entity.Fine;
import pl.konradoldakowski.libraryapi.entity.Loan;
import pl.konradoldakowski.libraryapi.exception.FineAlreadyExists;
import pl.konradoldakowski.libraryapi.repository.FineRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class FineService {
    private final FineRepository fineRepository;
    private final FineProperties fineProperties;
    public FineService(FineRepository fineRepository, FineProperties fineProperties) {
        this.fineRepository = fineRepository;
        this.fineProperties = fineProperties;
    }

    public void createFine(Loan loan) {
        if(fineRepository.existsByLoanId(loan.getId())){
            throw new FineAlreadyExists("Fine with given Loan ID already exists");
        }
        long daysLate = ChronoUnit.DAYS.between(loan.getDueDate(), loan.getReturnedAt());
        BigDecimal baseAmount = fineProperties.getDailyFee().multiply(BigDecimal.valueOf(daysLate));
        Fine fine = new Fine(loan, loan.getUser(), baseAmount, fineProperties.getDailyInterestRate(), LocalDate.now());
        fineRepository.save(fine);
    }
}
