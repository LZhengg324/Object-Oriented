import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;

public class Library {
    private LocalDate date;
    private final ArrayList<HashMap<String, ArrayList<Book>>> rack;
    private final ArrayList<Request> allRequest;
    private final SelfServiceMachine selfServiceMachine;
    private final BorrowAndReturnLibrarian borrowAndReturnLibrarian;
    private final LogisticDivision logisticDivision;
    private final OrderingLibrarian orderingLibrarian;

    public Library(LocalDate date) {
        this.date = date;
        this.rack = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            rack.add(new HashMap<>());
        }
        this.allRequest = new ArrayList<>();
        this.selfServiceMachine = new SelfServiceMachine(rack);
        this.borrowAndReturnLibrarian = new BorrowAndReturnLibrarian();
        this.logisticDivision = new LogisticDivision();
        this.orderingLibrarian = new OrderingLibrarian(rack);
    }

    public void addBook(String bookId, char type, int capacity) {
        ArrayList<Book> books = new ArrayList<>();
        for (int i = 0; i < capacity; i++) {
            Book book = new Book(bookId, type);
            books.add(book);
        }
        rack.get(type - 'A').put(bookId, books);
        /*if (type == 'A') {
            rackA.put(bookId, books);
        } else if (type == 'B') {
            rackB.put(bookId, books);
        } else {
            rackC.put(bookId, books);
        }*/
    }

    public void addRequest(Request request) {
        allRequest.add(request);
    }

    public void start() {
        while (true) {
            if (allRequest.isEmpty()
                    || this.date.isAfter(LocalDate.of(2023, 12, 31))) {
                break;
            }
            arrangeDay();
            ArrayList<ArrayList<Book>> list = orderingLibrarian.checkOrderList(date);
            borrowAndReturnLibrarian.addBorrowList(list.get(0));
            selfServiceMachine.addBorrowList(list.get(1));
            processRequest();
            deleteProcessedRequest();
            orderingLibrarian.clearOrderTimes();
            this.date = this.date.plusDays(1);
        }
    }

    private void processRequest() {
        for (Request request : allRequest) {
            if (request.getDate().isAfter(this.date)) {
                break;
            }
            String studentId = request.getStudentId();
            String bookId = request.getBookId();
            switch (request.getAction()) {
                case BORROWED:
                    bookBorrowed(studentId, bookId);
                    break;
                case RETURNED:
                    for (Book book : rack.get(bookId.charAt(0) - 'A').get(bookId)) {
                        if (book.getStatus().equals(BookStatus.BORROWED_BY_STUDENT)
                                && book.getBorrowedBy().equals(studentId)) {
                            if (bookId.charAt(0) == 'B') {
                                borrowAndReturnLibrarian.returnBook(date, studentId, book);
                            } else {
                                selfServiceMachine.returnBook(date, studentId, book);
                            }
                            if (book.isSmeared()) {
                                logisticDivision.repairBook(date, book);
                            }
                            break;
                        }
                    }
                    break;
                case SMEARED:
                    bookSetSmeared(studentId, bookId);
                    break;
                case LOST:
                    bookSetLost(studentId, bookId);
                    break;
                default:
                    System.out.println("Unknown Command");
                    break;
            }
        }
    }

    private void deleteProcessedRequest() {
        while (!allRequest.isEmpty()
                && (allRequest.get(0).getDate().isBefore(this.date) //删除request处理过的
                || allRequest.get(0).getDate().isEqual(this.date))) {
            allRequest.remove(0);
        }
    }

    private void arrangeDay() {
        long days = ChronoUnit.DAYS.between(this.date, LocalDate.of(2023, 1, 1));
        if (days % 3 == 0) {
            selfServiceMachine.arrangeBufferList();
            borrowAndReturnLibrarian.arrangeBufferList();
            logisticDivision.arrangeBufferList();
        }
    }

    private void bookBorrowed(String studentId, String bookId) {
        if (selfServiceMachine.queryBook(this.date, studentId, bookId)) {   //尚有在书架上的书
            for (Book book : rack.get(bookId.charAt(0) - 'A').get(bookId)) {
                if (book.getStatus().equals(BookStatus.ON_THE_RACK)) {
                    if (bookId.charAt(0) == 'B') {
                        if (borrowAndReturnLibrarian.borrowBook(date, studentId, book)) {
                            orderingLibrarian.deleteOrderListB(studentId, book);
                        }
                    } else if (bookId.charAt(0) == 'C') {
                        if (selfServiceMachine.borrowBook(date, studentId, book)) {
                            orderingLibrarian.deleteOrderListC(studentId, book);
                        }
                    }
                    break;
                }
            }
        } else if (bookId.charAt(0) != 'A') {
            if ((bookId.charAt(0) == 'B'
                    && !borrowAndReturnLibrarian.checkStudent(studentId))
                    || (bookId.charAt(0) == 'C'
                    && !selfServiceMachine.checkStudent(studentId, bookId))) {
                orderingLibrarian.addOrder(date, new OrderRequest(studentId,
                        bookId, bookId.charAt(0)));
            }
        }
    }

    private void bookSetSmeared(String studentId, String bookId) {
        for (Book book : rack.get(bookId.charAt(0) - 'A').get(bookId)) {
            if (book.getBorrowedBy() != null && book.getBorrowedBy().equals(studentId)
                    && book.getStatus().equals(BookStatus.BORROWED_BY_STUDENT)) {
                book.setSmeared(true);
                return;
            }
        }
    }

    private void bookSetLost(String studentId, String bookId) {
        for (Book book : rack.get(bookId.charAt(0) - 'A').get(bookId)) {
            if (book.getBorrowedBy() != null && book.getBorrowedBy().equals(studentId)
                    && book.getStatus().equals(BookStatus.BORROWED_BY_STUDENT)) {
                System.out.println("[" + date + "] " + studentId +
                        " got punished by borrowing and returning librarian");
                book.setLost(true);
                if (bookId.charAt(0) == 'B') {
                    borrowAndReturnLibrarian.returnBook(date, studentId, book);
                } else if (bookId.charAt(0) == 'C') {
                    selfServiceMachine.returnBook(date, studentId, book);
                }
                rack.get(bookId.charAt(0) - 'A').get(bookId).remove(book);
                return;
            }
        }
    }
}
