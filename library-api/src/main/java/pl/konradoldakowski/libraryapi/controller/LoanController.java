package pl.konradoldakowski.libraryapi.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.konradoldakowski.libraryapi.dto.CreateLoanRequest;
import pl.konradoldakowski.libraryapi.dto.LoanResponse;
import pl.konradoldakowski.libraryapi.entity.Loan;
import pl.konradoldakowski.libraryapi.security.CustomUserDetails;
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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<LoanResponse> createLoan(@Valid @RequestBody CreateLoanRequest request){
        LoanResponse loan = loanService.createLoan(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(loan.getId()).toUri();
        return ResponseEntity.created(location).body(loan);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<LoanResponse>> getAllLoans(){
        return ResponseEntity.ok(loanService.getAllLoans());
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LoanResponse>> getAllLoansByUserId(@PathVariable Long userId){
        return ResponseEntity.ok(loanService.getLoansByUserId(userId));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<LoanResponse> getLoanById(@PathVariable Long id){
        return ResponseEntity.ok(loanService.getLoanById(id));
    }
    @GetMapping("/my")
    public ResponseEntity<List<LoanResponse>> getMyLoans(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(loanService.getLoansByUserId(userDetails.getId()));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/return")
    public ResponseEntity<LoanResponse> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.returnBook(id));
    }
}
