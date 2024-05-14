public class Book {
    private final String bookId;
    private final char type;
    private boolean smeared;
    private boolean lost;
    private BookStatus status;
    private String borrowedBy;

    public Book(String bookId, char type) {
        this.bookId = bookId;
        this.type = type;
        this.smeared = false;
        this.lost = false;
        this.status = BookStatus.ON_THE_RACK;
        this.borrowedBy = null;
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
}
