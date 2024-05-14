import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class BorrowAndReturnLibrarian {
    private final HashMap<String, Book> borrowList;
    private final HashMap<String, ArrayList<Book>> bufferList;  //待arranging回收,<BookId, <Book>>

    public BorrowAndReturnLibrarian() {
        this.borrowList = new HashMap<>();
        this.bufferList = new HashMap<>();
    }

    public boolean borrowBook(LocalDate date, String studentId, Book book) {
        if (checkStudent(studentId)) {
            putToBufferList(book);
            return false;
        } else {
            borrowList.put(studentId, book);
            book.setStatus(BookStatus.BORROWED_BY_STUDENT);
            book.setBorrowedBy(studentId);
            System.out.println("[" + date + "] " + studentId +
                    " borrowed " + book.getBookId() + " from borrowing and returning librarian");
            return true;
        }
    }

    public void returnBook(LocalDate date, String studentId, Book book) {
        borrowList.remove(studentId);
        book.setBorrowedBy(null);
        if (book.isLost()) {
            return;
        } else if (book.isSmeared()) {
            System.out.println("[" + date + "] " + studentId +
                    " got punished by borrowing and returning librarian");
        } else {
            putToBufferList(book);
        }
        System.out.println("[" + date + "] " + studentId +
                " returned " + book.getBookId() + " to borrowing and returning librarian");
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

    public void arrangeBufferList() {
        if (!bufferList.isEmpty()) {
            for (ArrayList<Book> list : bufferList.values()) {
                for (Book book : list) {
                    book.setStatus(BookStatus.ON_THE_RACK);
                }
            }
            bufferList.clear();
        }
    }

    public boolean checkStudent(String studentId) {
        return borrowList.containsKey(studentId);
    }

    public void addBorrowList(ArrayList<Book> list) {
        for (Book book : list) {
            borrowList.put(book.getBorrowedBy(), book);
        }
    }
}
