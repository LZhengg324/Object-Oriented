import java.util.ArrayList;

public class Student {
    private final String studentId;
    private final String schoolId;
    private boolean availableB;
    private boolean availableBforOtherSchool;
    private ArrayList<Book> borrowedList;

    public Student(String studentId, String schoolId) {
        this.studentId = studentId;
        this.schoolId = schoolId;
        this.availableB = true;
        this.availableBforOtherSchool = true;
        this.borrowedList = new ArrayList<>();
    }

    public String getStudentId() {
        return studentId;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public boolean isAvailableB() {
        return availableB;
    }

    public void setAvailableB(boolean availableB) {
        this.availableB = availableB;
    }

    public boolean isAvailableBforOtherSchool() {
        return availableBforOtherSchool;
    }

    public void setAvailableBforOtherSchool(boolean availableBforOtherSchool) {
        this.availableBforOtherSchool = availableBforOtherSchool;
    }

    public boolean containsBook(Book book) {
        for (Book b : borrowedList) {
            if (b.getBookId().equals(book.getBookId())) {
                return true;
            }
        }
        return false;
    }

    public void addBorrowedBook(Book book) {
        if (!containsBook(book)) {
            borrowedList.add(book);
        }
    }

    public Book getBorrowedBook(String bookId) {
        for (Book book : borrowedList) {
            if (book.getBookId().equals(bookId)) {
                return book;
            }
        }
        return null;
    }

    public void removeBook(Book book) {
        if (containsBook(book)) {
            borrowedList.remove(book);
        }
    }

    public boolean haveBookC(String bookId) {
        for (Book b : borrowedList) {
            if (b.getType() == 'C' && b.getBookId().equals(bookId)) {
                return true;
            }
        }
        return false;
    }
}
