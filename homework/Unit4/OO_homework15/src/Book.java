import java.time.LocalDate;

public class Book {
    private final String school;
    private final String bookId;
    private final char type;
    private boolean smeared;
    private boolean lost;
    private boolean availableToBorrow;
    private BookStatus status;
    private String borrowedBy;
    private LocalDate borrowedDay;

    public Book(String schoolId, String bookId, char type, boolean availableToBorrow) {
        this.school = schoolId;
        this.bookId = bookId;
        this.type = type;
        this.smeared = false;
        this.lost = false;
        this.availableToBorrow = availableToBorrow;
        this.status = BookStatus.ON_THE_RACK;
        this.borrowedBy = null;
    }

    public String getSchool() {
        return school;
    }

    public String getBookId() {
        return this.bookId;
    }

    public char getType() {
        return this.type;
    }

    public boolean isSmeared() {
        return smeared;
    }

    public void setSmeared(boolean smeared) {
        this.smeared = smeared;
    }

    public boolean isLost() {
        return lost;
    }

    public void setLost(boolean lost) {
        this.lost = lost;
    }

    public BookStatus getStatus() {
        return this.status;
    }

    public void setStatus(BookStatus bookStatus) {
        this.status = bookStatus;
    }

    public String getBorrowedBy() {
        return borrowedBy;
    }

    public void setBorrowedBy(String borrowedBy) {
        this.borrowedBy = borrowedBy;
    }

    public boolean isAvailableToBorrow() {
        return availableToBorrow;
    }

    public void setAvailableToBorrow(boolean availableToBorrow) {
        this.availableToBorrow = availableToBorrow;
    }

    public LocalDate getBorrwedDay() {
        return borrowedDay;
    }

    public void setBorrwedDay(LocalDate borrwedDay) {
        this.borrowedDay = borrwedDay;
    }
}
