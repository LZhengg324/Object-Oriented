import java.time.LocalDate;

public class Request {
    private final LocalDate date;
    private final String school;
    private final String studentId;
    private final String bookId;
    private Action action; //true = borrow, false = return;

    public Request(LocalDate date, String school, String studentId, String bookId, Action action) {
        this.date = date;
        this.school = school;
        this.studentId = studentId;
        this.bookId = bookId;
        this.action = action;
    }

    public String getSchool() {
        return school;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getBookId() {
        return bookId;
    }

    public Action getAction() {
        return action;
    }

    public LocalDate getDate() {
        return date;
    }
}
