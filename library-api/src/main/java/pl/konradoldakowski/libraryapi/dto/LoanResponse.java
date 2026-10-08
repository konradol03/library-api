package pl.konradoldakowski.libraryapi.dto;

import java.time.LocalDate;

public class LoanResponse {
    private Long id;
    private Long userId;
    private Long bookId;
    private LocalDate borrowedAt;
    private LocalDate dueDate;
    private LocalDate returnedAt;

    public LoanResponse(Long id, Long userId, Long bookId, LocalDate borrowedAt, LocalDate dueDate, LocalDate returnedAt) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.borrowedAt = borrowedAt;
        this.dueDate = dueDate;
        this.returnedAt = returnedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getBookId() {
        return bookId;
    }

    public LocalDate getBorrowedAt() {
        return borrowedAt;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnedAt() {
        return returnedAt;
    }
}
