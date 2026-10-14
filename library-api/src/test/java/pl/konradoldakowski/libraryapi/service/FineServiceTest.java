package pl.konradoldakowski.libraryapi.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.konradoldakowski.libraryapi.config.FineProperties;
import pl.konradoldakowski.libraryapi.entity.Fine;
import pl.konradoldakowski.libraryapi.entity.Loan;
import pl.konradoldakowski.libraryapi.entity.User;
import pl.konradoldakowski.libraryapi.exception.FineAlreadyExists;
import pl.konradoldakowski.libraryapi.repository.FineRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FineServiceTest {

    @Mock
    private FineRepository fineRepository;
    @Mock
    private FineProperties fineProperties;
    @InjectMocks
    private FineService fineService;

    @Test
    public void shouldCreateFineForLateReturn() {
        User user = new User();
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setUser(user);
        loan.setDueDate(LocalDate.of(2026, 10, 10));
        loan.setReturnedAt(LocalDate.of(2026, 10, 14));

        when(fineProperties.getDailyFee()).thenReturn(new BigDecimal("1.5"));
        when(fineProperties.getDailyInterestRate()).thenReturn(new BigDecimal("0.01"));

        fineService.createFine(loan);
        ArgumentCaptor<Fine> captor = ArgumentCaptor.forClass(Fine.class);
        verify(fineRepository).save(captor.capture());
        Fine fine = captor.getValue();
        Assertions.assertNotNull(fine);
        Assertions.assertEquals(6, fine.getBaseAmount().intValue());

    }

    @Test
    public void shouldThrowExceptionWhenFineAlreadyExists() {
        Loan loan = new Loan();
        loan.setId(1L);

        when(fineRepository.existsByLoanId(1L)).thenReturn(true);
        Assertions.assertThrows(FineAlreadyExists.class, () -> fineService.createFine(loan));
        verify(fineRepository, never()).save(any(Fine.class));
    }

}
