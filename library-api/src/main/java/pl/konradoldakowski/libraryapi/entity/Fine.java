package pl.konradoldakowski.libraryapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Fine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne
    @JoinColumn(name="loan_id", nullable = false, unique = true)
    private Loan loan;

    @NotNull
    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @NotNull
    @Column(nullable = false)
    @DecimalMin(value = "0.00")
    private BigDecimal baseAmount;

    @NotNull
    @Column(nullable = false)
    @DecimalMin(value = "0.0000")
    private BigDecimal interestRate;

    @NotNull
    @Column(nullable = false)
    private LocalDate createdAt;

    private LocalDate paidAt;

    public Fine() {
    }

    public Fine(Loan loan, User user, BigDecimal baseAmount, BigDecimal interestRate, LocalDate createdAt) {
        this.loan = loan;
        this.user = user;
        this.baseAmount = baseAmount;
        this.interestRate = interestRate;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(BigDecimal baseAmount) {
        this.baseAmount = baseAmount;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDate paidAt) {
        this.paidAt = paidAt;
    }
}
