import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class SelfServiceMachine {   //处理C类书籍
    private final String school;
    private final HashMap<String, ArrayList<Book>> bufferList;  //待arranging回收,<BookId, <Book>>
    private final HashMap<String, ArrayList<Book>> borrowList;  //<StudentId, <Book>>
    private final ArrayList<HashMap<String, ArrayList<Book>>> rack;

    public SelfServiceMachine(String school, ArrayList<HashMap<String, ArrayList<Book>>> rack) {
        this.school = school;
        this.bufferList = new HashMap<>();
        this.borrowList = new HashMap<>();
        this.rack = rack;
    }

    public boolean queryBook(LocalDate date,  String bookId) {
        System.out.println("[" + date + "] self-service machine provided information of " + bookId);
        char type = bookId.charAt(0);
        if (type != 'A') {
            if (rack.get(type - 'A').containsKey(bookId)) {
                for (Book book : rack.get(type - 'A').get(bookId)) {
                    if (book.getStatus().equals(BookStatus.ON_THE_RACK)) {
                        return true;
                    }
                }
            }
        }
        /*if (type == 'B') {
            if (rackB.containsKey(bookId)) {
                for (Book book : rackB.get(bookId)) {
                    if (book.getStatus().equals(BookStatus.ON_THE_RACK)) {
                        return true;
                    }
                }
            }
        } else if (type == 'C') {
            if (rackC.containsKey(bookId)) {
                for (Book book : rackC.get(bookId)) {
                    if (book.getStatus().equals(BookStatus.ON_THE_RACK)) {
                        return true;
                    }
                }
            }
        }*/
        return false;
    }

    public boolean borrowBook(LocalDate date, Student student, Book book) {
        ArrayList<Book> list;
        String studentId = student.getStudentId();
        if (borrowList.containsKey(studentId)) {
            list = borrowList.get(studentId);
            for (Book b : list) {
                if (b.getBookId().equals(book.getBookId())) {
                    BookStatus old = book.getStatus();
                    putToBufferList(book);
                    System.out.println("[" + date + "] self-service " +
                            "machine refused lending " + book.getSchool() + "-" +
                            book.getBookId() + " to " + school + "-" + studentId);
                    System.out.println("(State) [" + date + "] " + book.getBookId()
                            + " transfers from " + old + " to " + book.getStatus());
                    return false;
                }
            }
            list.add(book);
        } else {
            list = new ArrayList<>();
            list.add(book);
            borrowList.put(studentId, list);
        }
        book.setStatus(BookStatus.BORROWED_BY_STUDENT);
        book.setBorrowedBy(studentId);
        student.addBorrowedBook(book);
        System.out.println("[" + date + "] self-service machine lent "
                + book.getSchool() + "-" + book.getBookId() + " to "
                + school + "-" + studentId);
        return true;
    }

    public void returnBook(LocalDate date, Student student, Book book) {

        String studentId = student.getStudentId();
        final BookStatus old = book.getStatus();
        if (book.getSchool().equals(school)) {
            borrowList.get(studentId).remove(book);
            student.removeBook(book);

            if (borrowList.get(studentId).isEmpty()) {
                borrowList.remove(studentId);
            }

            book.setBorrowedBy(null);
            if (book.isLost()) {
                return;
            } else if (!book.isSmeared()) {
                putToBufferList(book);
            }
        } else {
            book.setStatus(BookStatus.SELF_SERVICE_MACHINE);
            book.setBorrowedBy(null);
            if (book.isLost()) {
                return;
            }
        }
        System.out.println("[" + date + "] " + school + "-"
                + studentId + " returned " + book.getSchool() + "-"
                + book.getBookId() + " to self-service machine");
        System.out.println("[" + date + "] self-service machine collected "
                + book.getSchool() + "-" + book.getBookId() + " from " + school + "-" + studentId);
        System.out.println("(State) [" + date + "] " + book.getBookId()
                + " transfers from " + old + " to " + book.getStatus());
    }

    public void putToBufferList(Book book) {
        if (bufferList.containsKey(book.getBookId())) {
            bufferList.get(book.getBookId()).add(book);
        } else {
            ArrayList<Book> newList = new ArrayList<>();
            newList.add(book);
            bufferList.put(book.getBookId(), newList);
        }
        book.setStatus(BookStatus.SELF_SERVICE_MACHINE);
    }

    public ArrayList<Book> arrangeBufferList() {
        ArrayList<Book> ret = new ArrayList<>();
        if (!bufferList.isEmpty()) {
            for (ArrayList<Book> list : bufferList.values()) {
                for (Book book : list) {
                    ret.add(book);
                    book.setStatus(BookStatus.ON_THE_RACK);
                }
            }
            bufferList.clear();
        }
        return ret;
    }

    public boolean checkStudent(String studentId, String bookId) {
        if (borrowList.containsKey(studentId)) {
            for (Book book : borrowList.get(studentId)) {
                if (book.getBookId().equals(bookId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addBorrowList(ArrayList<Book> books) {
        for (Book book : books) {
            if (borrowList.containsKey(book.getBorrowedBy())) {
                borrowList.get(book.getBorrowedBy()).add(book);
            } else {
                ArrayList<Book> newList = new ArrayList<>();
                newList.add(book);
                borrowList.put(book.getBorrowedBy(), newList);
            }
        }
    }
}
