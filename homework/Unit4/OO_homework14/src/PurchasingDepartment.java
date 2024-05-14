import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class PurchasingDepartment {
    private final String school;
    //private final ArrayList<AddOnRequest> addOnList;
    //private final ArrayList<SchoolBorrowRequest> borrowList;
    private final ArrayList<Library> allLibrary;
    private final ArrayList<Book> booksBorrowedFromOtherSchool = new ArrayList<>(); //接收书
    private final ArrayList<Book> booksBorrowedToOtherSchool = new ArrayList<>();   //借其他学校书
    private final ArrayList<Book> booksReturnedFromOtherSchool = new ArrayList<>(); //接收其他学校还的书
    private final ArrayList<Book> booksReturnedFromOtherSchooltemp = new ArrayList<>(); //接收其他学校还的书
    private final ArrayList<Book> booksReturnedToOtherSchool = new ArrayList<>();   //还给其他学校的书
    private final ArrayList<HashMap<String, ArrayList<Book>>> rack;

    public PurchasingDepartment(String school, ArrayList<Library> allLibrary,
                                ArrayList<HashMap<String, ArrayList<Book>>> rack) {
        this.school = school;
        //this.addOnList = new ArrayList<>();
        //this.borrowList = new ArrayList<>();
        this.allLibrary = allLibrary;
        this.rack = rack;
    }

    public Book findBookInOtherSchool(String bookId) {
        for (Library library : allLibrary) {
            if (!library.getSchool().equals(school) &&
                    library.haveBook(bookId) && library.availableBorrow(bookId)) {
                return library.getBook(bookId);
            }
        }
        return null;
    }

    public void addOnNewBooks(LocalDate date, ArrayList<AddOnRequest> addOnList) {
        ArrayList<Book> ret = new ArrayList<>();
        for (AddOnRequest request : addOnList) {
            ArrayList<Book> list;
            if (!rack.get(request.getOrderedBookId().charAt(0) - 'A').
                    containsKey(request.getOrderedBookId())) {
                list = new ArrayList<>();
                int n = Math.max(3, request.getCapacity());
                for (int i = 0; i < n; i++) {
                    Book book = new Book(school, request.getOrderedBookId(),
                            request.getOrderedBookId().charAt(0), true);
                    book.setStatus(BookStatus.ON_THE_RACK);
                    list.add(book);
                    ret.add(book);
                }
                //System.out.println( "n = " + n);
                System.out.println("[" + date + "] " + request.getSchool() + "-"
                        + request.getOrderedBookId() + " got purchased by " +
                        "purchasing department in " + request.getSchool());
                rack.get(request.getOrderedBookId().charAt(0) - 'A').
                        put(request.getOrderedBookId(), list);
            }
        }
        booksReturnedFromOtherSchool.addAll(ret);
    }

    public void addBookBorrowedReceived(Book book) {
        booksBorrowedFromOtherSchool.add(book);
        for (Library library : allLibrary) {
            if (library.getSchool().equals(book.getSchool())) {
                library.getPurchasingDepartment().addBookBorrowedTransported(book);
            }
        }
    }

    public void addBookBorrowedTransported(Book book) {
        booksBorrowedToOtherSchool.add(book);
    }

    public void addBookReturnedTransported(Book book) {
        booksReturnedToOtherSchool.add(book);
        for (Library library : allLibrary) {
            if (library.getSchool().equals(book.getSchool())) {
                library.getPurchasingDepartment().addBookReturnedReceived(book);
            }
        }
        booksBorrowedFromOtherSchool.remove(book);
    }

    public void addBookReturnedReceived(Book book) {
        booksReturnedFromOtherSchooltemp.add(book);
    }

    public void receivedBookBorrowed(LocalDate date) {
        if (booksBorrowedFromOtherSchool.isEmpty()) {
            return;
        }
        for (Book book : booksBorrowedFromOtherSchool) {
            System.out.println("[" + date + "] " + book.getSchool() + "-" +
                    book.getBookId() + " got received by purchasing department in " + school);
        }
    }

    public void transportBookBorrowed(LocalDate date) {
        if (booksBorrowedToOtherSchool.isEmpty()) {
            return;
        }
        for (Book book : booksBorrowedToOtherSchool) {
            BookStatus old = book.getStatus();
            book.setStatus(BookStatus.PURCHASING_DEPARTMENT);
            System.out.println("[" + date + "] " + book.getSchool() + "-" +
                    book.getBookId() + " got transported by purchasing department in " + school);
            System.out.println("(State) [" + date + "] " + book.getBookId()
                    + " transfers from " + old + " to " + book.getStatus());
        }
        booksBorrowedToOtherSchool.clear();
    }

    public void receivedBookReturned(LocalDate date) {
        if (booksReturnedFromOtherSchooltemp.isEmpty()) {
            return;
        }
        for (Book book : booksReturnedFromOtherSchooltemp) {
            BookStatus old = book.getStatus();
            book.setStatus(BookStatus.PURCHASING_DEPARTMENT);
            System.out.println("[" + date + "] " + book.getSchool() + "-" +
                    book.getBookId() + " got received by purchasing department in " + school);
            System.out.println("(State) [" + date + "] " + book.getBookId()
                    + " transfers from " + old + " to " + book.getStatus());
        }
        booksReturnedFromOtherSchool.addAll(booksReturnedFromOtherSchooltemp);
        booksReturnedFromOtherSchooltemp.clear();
    }

    public void transportBookReturned(LocalDate date) {
        if (booksReturnedToOtherSchool.isEmpty()) {
            return;
        }
        for (Book book : booksReturnedToOtherSchool) {
            System.out.println("[" + date + "] " + book.getSchool() + "-" +
                    book.getBookId() + " got transported by purchasing department in " + school);
        }
        booksReturnedToOtherSchool.clear();
    }

    public void getBooksFromOtherSchool(LocalDate date, OrderRequest request, Student student) {
        for (Book book : booksBorrowedFromOtherSchool) {
            if (book.getBookId().equals(request.getOrderedBookId())) {
                book.setBorrowedBy(request.getStudentId());
                book.setStatus(BookStatus.BORROWED_BY_STUDENT);
                student.addBorrowedBook(book);
                booksBorrowedFromOtherSchool.remove(book);
                System.out.println("[" + date + "] purchasing department lent "
                        + book.getSchool() + "-" + book.getBookId() + " to "
                        + school + "-" + request.getStudentId());
                System.out.println("[" + date + "] " + request.getSchool()
                        + "-" + request.getStudentId() + " borrowed " + book.getSchool()
                        + "-" + book.getBookId() + " from purchasing department");
                return;
            }
        }
    }

    public ArrayList<Book> arrangeReceivedBooks(LocalDate date) {
        ArrayList<Book> ret = new ArrayList<>();
        if (!booksReturnedFromOtherSchool.isEmpty()) {
            for (Book book : booksReturnedFromOtherSchool) {
                book.setStatus(BookStatus.ON_THE_RACK);
                System.out.println("(State) [" + date + "] " + book.getBookId()
                        + " transfers from " + BookStatus.PURCHASING_DEPARTMENT + " to "
                        + BookStatus.ON_THE_RACK);
                ret.add(book);
            }
            booksReturnedFromOtherSchool.clear();
        }
        return ret;
    }

    public void lostBook(Student student, Book book) {
        for (Library library : allLibrary) {
            if (book.getSchool().equals(library.getSchool())) {
                library.removeBook(book);
                break;
            }
        }
    }
}
