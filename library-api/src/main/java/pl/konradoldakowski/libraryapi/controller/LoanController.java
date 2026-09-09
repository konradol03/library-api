package pl.konradoldakowski.libraryapi.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.konradoldakowski.libraryapi.dto.CreateLoanRequest;
import pl.konradoldakowski.libraryapi.entity.Loan;
import pl.konradoldakowski.libraryapi.service.LoanService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/loans")
public class LoanController {
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<Loan> createLoan(@Valid @RequestBody CreateLoanRequest request){
        Loan loan = loanService.createLoan(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(loan.getId()).toUri();
        return ResponseEntity.created(location).body(loan);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Loan> getLoan(@PathVariable Long id){
        return ResponseEntity.ok(loanService.getLoanById(id));
    }
    @GetMapping
    public ResponseEntity<List<Loan>> getAllLoans(){
        return ResponseEntity.ok(loanService.getAllLoans());
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Loan>> getAllLoansByUserId(@PathVariable Long userId){
        return ResponseEntity.ok(loanService.getLoansByUserId(userId));
    }
    @PatchMapping("/{id}/return")
    public ResponseEntity<Loan> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.returnBook(id));
    }
}
