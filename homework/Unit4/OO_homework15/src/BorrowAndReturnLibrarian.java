import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class BorrowAndReturnLibrarian {
    private final String school;
    private final HashMap<String, Book> borrowList;
    private final HashMap<String, ArrayList<Book>> bufferList;  //待arranging回收,<BookId, <Book>>

    public BorrowAndReturnLibrarian(String school) {
        this.school = school;
        this.borrowList = new HashMap<>();
        this.bufferList = new HashMap<>();
    }

    public boolean borrowBook(LocalDate date, Student student, Book book) {
        String studentId = student.getStudentId();
        if (/*checkStudent(studentId)*/!student.isAvailableB()) {
            BookStatus old = book.getStatus();
            putToBufferList(book);
            System.out.println("[" + date + "] borrowing and returning " +
                    "librarian refused lending " + book.getSchool() + "-" +
                    book.getBookId() + " to " + school + "-" + studentId);
            System.out.println("(State) [" + date + "] " + book.getBookId() + " transfers from "
                    + old + " to " + book.getStatus());
            System.out.println("(Sequence) [" + date + "] " +
                    "BorrowAndReturnLibrarian sends a message to Library");
            return false;
        } else {
            borrowList.put(studentId, book);
            book.setStatus(BookStatus.BORROWED_BY_STUDENT);
            book.setBorrowedBy(studentId);
            book.setBorrwedDay(date);
            student.addBorrowedBook(book);
            student.setAvailableB(false);

            System.out.println("[" + date + "] borrowing and returning librarian lent "
                    + book.getSchool() + "-" + book.getBookId() + " to "
                    + school + "-" + studentId);

            return true;
        }
    }

    public void returnBook(LocalDate date, Student student, Book book) {
        String studentId = student.getStudentId();
        final BookStatus old = book.getStatus();
        if (book.getSchool().equals(school)) {
            borrowList.remove(studentId);
            student.setAvailableB(true);
            student.setAvailableBforOtherSchool(true);
            student.removeBook(book);
            book.setBorrowedBy(null);

            if (book.isLost()) {
                return;
            } else if (!book.isSmeared()) {
                putToBufferList(book);
            }
        } else {
            book.setStatus(BookStatus.BORROWING_AND_RETURN);
            book.setBorrowedBy(null);
            if (book.isLost()) {
                return;
            }
        }
        System.out.println("[" + date + "] " + student.getSchoolId() + "-" + studentId +
                " returned " + book.getSchool() + "-" + book.getBookId()
                + " to borrowing and returning librarian");
        System.out.println("[" + date + "] borrowing and returning librarian collected "
                + book.getSchool() + "-" + book.getBookId() + " from " + school + "-" + studentId);
        System.out.println("(State) [" + date + "] " + book.getBookId() + " transfers from "
                + old + " to " + book.getStatus());
    }

    public void putToBufferList(Book book) {
        if (bufferList.containsKey(book.getBookId())) {
            bufferList.get(book.getBookId()).add(book);
        } else {
            ArrayList<Book> newList = new ArrayList<>();
            newList.add(book);
            bufferList.put(book.getBookId(), newList);
        }
        book.setStatus(BookStatus.BORROWING_AND_RETURN);
    }

    public ArrayList<Book> arrangeBufferList() {
        ArrayList<Book> ret = new ArrayList<>();
        if (!bufferList.isEmpty()) {
            for (ArrayList<Book> list : bufferList.values()) {
                for (Book book : list) {
                    book.setStatus(BookStatus.ON_THE_RACK);
                    ret.add(book);
                }
            }
            bufferList.clear();
        }
        return ret;
    }

    public boolean checkStudent(String studentId) {
        return borrowList.containsKey(studentId);
    }

    public void addBorrowList(ArrayList<Book> list) {
        for (Book book : list) {
            borrowList.put(book.getBorrowedBy(), book);
        }
    }

    public void punishment(LocalDate date, String studentId) {
        System.out.println("[" + date + "] " + school + "-" + studentId +
                " got punished by borrowing and returning librarian");
        System.out.println("[" + date + "] borrowing and returning librarian received "
                + school + "-" + studentId + "'s fine");
    }
}
