import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class Library {
    private final String school;
    private final ArrayList<HashMap<String, ArrayList<Book>>> rack;
    private final HashMap<String, Student> allStudent;
    //private final ArrayList<Library> allLibrary;
    private final SelfServiceMachine selfServiceMachine;
    private final BorrowAndReturnLibrarian borrowAndReturnLibrarian;
    private final LogisticDivision logisticDivision;
    private final OrderingLibrarian orderingLibrarian;
    private final PurchasingDepartment purchasingDepartment;

    public Library(String school, ArrayList<Library> allLibrary) {
        this.school = school;
        //this.date = date;
        this.rack = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            rack.add(new HashMap<>());
        }
        this.allStudent = new HashMap<>();
        //this.allLibrary = allLibrary;
        this.selfServiceMachine = new SelfServiceMachine(school, rack);
        this.borrowAndReturnLibrarian = new BorrowAndReturnLibrarian(school);
        this.logisticDivision = new LogisticDivision(school);
        this.purchasingDepartment = new PurchasingDepartment(school, allLibrary, rack);
        this.orderingLibrarian = new OrderingLibrarian(school, allStudent,
                rack, purchasingDepartment);
    }

    public boolean haveBook(String bookId) {
        return this.rack.get(bookId.charAt(0) - 'A').containsKey(bookId);
    }

    public boolean availableBorrow(String bookId) {
        return this.rack.get(bookId.charAt(0) - 'A').get(bookId).get(0).isAvailableToBorrow();
    }

    public String getSchool() {
        return school;
    }

    public void addStudent(Student student) {
        allStudent.put(student.getStudentId(), student);
    }

    public void addBook(String schoolId, String bookId, char type,
                        int capacity, boolean availableBorrow) {
        ArrayList<Book> books = new ArrayList<>();
        for (int i = 0; i < capacity; i++) {
            Book book = new Book(schoolId, bookId, type, availableBorrow);
            books.add(book);
        }
        rack.get(type - 'A').put(bookId, books);
    }

    public Book getBook(String bookId) {
        if (haveBook(bookId)) {
            for (Book book : rack.get(bookId.charAt(0) - 'A').get(bookId)) {
                if (book.getStatus().equals(BookStatus.ON_THE_RACK)) {
                    return book;
                }
            }
        }
        return null;
    }

    public void processRequest(LocalDate date, Request request) {
        String studentId = request.getStudentId();
        String bookId = request.getBookId();
        Student student;
        if (!allStudent.containsKey(studentId)) {
            student = new Student(studentId, school);
            addStudent(student);
        }  else {
            student = allStudent.get(studentId);
        }
        switch (request.getAction()) {
            case BORROWED:
                bookBorrowed(date, student, bookId);
                break;
            case RETURNED:
                Book book = student.getBorrowedBook(bookId);
                if (book.isSmeared()) {
                    borrowAndReturnLibrarian.punishment(date, studentId);
                }
                if (bookId.charAt(0) == 'B') {
                    if (!book.getSchool().equals(school) && !book.isSmeared()) {
                        purchasingDepartment.addBookReturnedTransported(book);
                    }
                    borrowAndReturnLibrarian.returnBook(date, student, book);
                } else {
                    if (!book.getSchool().equals(school) && !book.isSmeared()) {
                        purchasingDepartment.addBookReturnedTransported(book);
                    }
                    selfServiceMachine.returnBook(date, student, book);
                }
                if (book.isSmeared()) {
                    logisticDivision.repairBook(date, book);
                    if (!book.getSchool().equals(school)) {
                        purchasingDepartment.addBookReturnedTransported(book);
                    }
                }
                break;
            case SMEARED:
                bookSetSmeared(student, bookId);
                break;
            case LOST:
                bookSetLost(date, student, bookId);
                break;
            default:
                System.out.println("Unknown Command");
                break;
        }
    }

    public ArrayList<Book> arrangeDay(LocalDate date) {
        ArrayList<Book> ret = new ArrayList<>();
        ret.addAll(selfServiceMachine.arrangeBufferList());
        ret.addAll(borrowAndReturnLibrarian.arrangeBufferList());
        ret.addAll(logisticDivision.arrangeBufferList());
        ret.addAll(purchasingDepartment.arrangeReceivedBooks(date));
        return ret;
    }

    public void bookBorrowed(LocalDate date, Student student, String bookId) {

        System.out.println("[" + date + "] " + school + "-" + student.getStudentId() +
                " queried " + bookId + " from self-service machine");

        String studentId = student.getStudentId();
        if (selfServiceMachine.queryBook(date, bookId)) {   //尚有在书架上的书
            for (Book book : rack.get(bookId.charAt(0) - 'A').get(bookId)) {
                if (book.getStatus().equals(BookStatus.ON_THE_RACK)) {
                    BookStatus old = book.getStatus();
                    if (bookId.charAt(0) == 'B') {
                        if (borrowAndReturnLibrarian.borrowBook(date, student, book)) {

                            System.out.println("[" + date + "] " + student.getSchoolId()
                                    + "-" + studentId + " borrowed " + book.getSchool() + "-"
                                    + bookId + " from borrowing and returning librarian");
                            System.out.println("(State) [" + date + "] "
                                    + book.getBookId() + " transfers from "
                                    + old + " to " + book.getStatus());
                        }
                    } else if (bookId.charAt(0) == 'C') {
                        if (selfServiceMachine.borrowBook(date, student, book)) {
                            System.out.println("[" + date + "] " + school + "-" +
                                    studentId + " borrowed " + book.getSchool() + "-" +
                                    bookId + " from self-service machine");
                            System.out.println("(State) [" + date + "] "
                                    + book.getBookId() + " transfers from "
                                    + old + " to " + book.getStatus());
                        }
                    }
                    break;
                }
            }
        } else if (bookId.charAt(0) != 'A') {
            if ((bookId.charAt(0) == 'B' && student.isAvailableB())
                || (bookId.charAt(0) == 'C' && !student.haveBookC(bookId))) {
                orderingLibrarian.addOrder(new OrderRequest(student, bookId, bookId.charAt(0)));
            }
        }
    }

    public void bookSetSmeared(Student student, String bookId) {
        Book book = student.getBorrowedBook(bookId);
        book.setSmeared(true);
    }

    public void bookSetLost(LocalDate date, Student student, String bookId) {
        String studentId = student.getStudentId();
        Book book = student.getBorrowedBook(bookId);

        borrowAndReturnLibrarian.punishment(date, studentId);
        book.setLost(true);
        student.removeBook(book);
        if (bookId.charAt(0) == 'B') {
            borrowAndReturnLibrarian.returnBook(date, student, book);
        } else if (bookId.charAt(0) == 'C') {
            selfServiceMachine.returnBook(date, student, book);
        }
        if (rack.get(bookId.charAt(0) - 'A').containsKey(bookId)) {
            rack.get(bookId.charAt(0) - 'A').get(bookId).remove(book);
        } else {
            purchasingDepartment.lostBook(student, book);
        }
    }

    public SelfServiceMachine getSelfServiceMachine() {
        return selfServiceMachine;
    }

    public BorrowAndReturnLibrarian getBorrowAndReturnLibrarian() {
        return borrowAndReturnLibrarian;
    }

    public LogisticDivision getLogisticDivision() {
        return logisticDivision;
    }

    public OrderingLibrarian getOrderingLibrarian() {
        return orderingLibrarian;
    }

    public PurchasingDepartment getPurchasingDepartment() {
        return purchasingDepartment;
    }

    public void removeBook(Book book) {
        rack.get(book.getType() - 'A').get(book.getBookId()).remove(book);
    }
}
