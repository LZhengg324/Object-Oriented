import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class LibrarySystem {
    private LocalDate date;
    private final ArrayList<Library> allLibrary;
    private final ArrayList<Request> allRequest;

    public LibrarySystem() {
        this.date = LocalDate.of(2023, 1, 1);
        this.allLibrary = new ArrayList<>();
        this.allRequest = new ArrayList<>();
    }

    public void addLibrary(Library library) {
        allLibrary.add(library);
    }

    public LocalDate getDate() {
        return this.date;
    }

    public ArrayList<Library> getAllLibrary() {
        return allLibrary;
    }

    public void addRequest(Request request) {
        allRequest.add(request);
    }

    public void start() {
        //System.out.println(allRequest.size());
        while (!allRequest.isEmpty()
                && this.date.isBefore(LocalDate.of(2023, 12, 31))) {
            //System.out.println("hello");
            received();
            studentsGetBooksFromOtherSchool();
            arrangeDay();
            /*开馆*/
            processRequest();
            deleteProcessedRequest();
            /*闭馆*/
            clearOrderTimes(date);
            transport();
            tommorow();
        }
    }

    public void studentsGetBooksFromOtherSchool() {
        for (Library library : allLibrary) {
            library.getOrderingLibrarian().getBooksFromOtherSchool(date);
        }
    }

    private void tommorow() {
        this.date = this.date.plusDays(1);
    }

    private void clearOrderTimes(LocalDate date) {
        for (Library library : allLibrary) {
            library.getOrderingLibrarian().validateOrderRequest(date);
        }
    }

    private void arrangeDay() {
        long days = ChronoUnit.DAYS.between(this.date, LocalDate.of(2023, 1, 1));
        if (days % 3 == 0) {
            for (Library library : allLibrary) {
                library.getOrderingLibrarian().buyNewBooks(date);
            }
            System.out.println("[" + this.date + "] " + "arranging " +
                    "librarian arranged all the books");
            for (Library library : allLibrary) {
                //library.getOrderingLibrarian().buyNewBooks(date);
                ArrayList<Book> temp = library.arrangeDay(date);
                ArrayList<ArrayList<Book>> list = library.getOrderingLibrarian().
                        checkOrderList(date, temp);
                library.getBorrowAndReturnLibrarian().addBorrowList(list.get(0));
                library.getSelfServiceMachine().addBorrowList(list.get(1));
            }
            //checkOrderList();
        }
    }

    private void deleteProcessedRequest() {
        while (!allRequest.isEmpty()
                && (allRequest.get(0).getDate().isBefore(this.date) //删除request处理过的
                || allRequest.get(0).getDate().isEqual(this.date))) {
            allRequest.remove(0);
        }
    }

    private Library getSchoolLibrary(String school) {
        for (Library library : allLibrary) {
            if (library.getSchool().equals(school)) {
                return library;
            }
        }
        return null;
    }

    private void processRequest() {
        for (Request request : allRequest) {
            if (request.getDate().isAfter(this.date)) {
                break;
            }
            Library library = getSchoolLibrary(request.getSchool());
            if (library != null) {
                library.processRequest(date, request);
            }
        }
    }

    public void received() {
        for (Library library : allLibrary) {
            library.getPurchasingDepartment().receivedBookBorrowed(date);
            library.getPurchasingDepartment().receivedBookReturned(date);
        }
    }

    public void transport() {
        for (Library library : allLibrary) {
            library.getPurchasingDepartment().transportBookBorrowed(date);
            library.getPurchasingDepartment().transportBookReturned(date);
        }
    }

    public int getSize() {
        return allLibrary.size();
    }
}
