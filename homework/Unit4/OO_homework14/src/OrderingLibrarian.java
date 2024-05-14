import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class OrderingLibrarian {
    private final String school;
    private final ArrayList<OrderRequest> orderList;
    private final ArrayList<OrderRequest> todayOrderList;
    private final ArrayList<OrderRequest> borrowFromOtherSchool;
    private final ArrayList<AddOnRequest> addOnList;
    private final HashMap<String, Student> allStudent;
    private final HashMap<String, Integer> orderTimes;  //<StudentId, Times>
    private final ArrayList<HashMap<String, ArrayList<Book>>> rack;
    private final PurchasingDepartment purchasingDepartment;

    public OrderingLibrarian(String school, HashMap<String, Student> allStudent,
                             ArrayList<HashMap<String, ArrayList<Book>>> rack,
                             PurchasingDepartment purchasingDepartment) {
        this.school = school;
        this.orderList = new ArrayList<>();
        this.todayOrderList = new ArrayList<>();
        this.borrowFromOtherSchool = new ArrayList<>();
        this.addOnList = new ArrayList<>();
        this.allStudent = allStudent;
        this.orderTimes = new HashMap<>();
        this.rack = rack;
        this.purchasingDepartment = purchasingDepartment;
    }

    public void addOrder(OrderRequest orderRequest) {
        String studentId = orderRequest.getStudentId();
        for (OrderRequest request : orderList) {
            if (request.getOrderedBookId().equals(orderRequest.getOrderedBookId())
                    && request.getStudentId().equals(studentId)) {
                return;
            }
        }
        for (OrderRequest request : todayOrderList) {
            if (request.getOrderedBookId().equals(orderRequest.getOrderedBookId())
                    && request.getStudentId().equals(studentId)) {
                return;
            }
        }
        if (orderTimes.containsKey(studentId)) {
            if (orderTimes.get(studentId) >= 3) {
                return;
            } else {
                int count = orderTimes.get(studentId);
                orderTimes.put(studentId, ++count);
            }
        } else {
            orderTimes.put(studentId, 1);
        }
        todayOrderList.add(orderRequest);
    }

    public boolean isEmpty() {
        return orderList.isEmpty();
    }

    public ArrayList<ArrayList<Book>> checkOrderList(LocalDate date, ArrayList<Book> arrangeList) {
        ArrayList<ArrayList<Book>> list = new ArrayList<>();
        ArrayList<Book> listB = new ArrayList<>();
        ArrayList<Book> listC = new ArrayList<>();
        list.add(listB);
        list.add(listC);

        for (OrderRequest request : orderList) {
            for (Book book : arrangeList) {
                if (request.isValid()) {
                    Student student = request.getStudent();
                    if (book.getBookId().equals(request.getOrderedBookId())
                            && book.getStatus().equals(BookStatus.ON_THE_RACK)) {
                        if ((request.getType() == 'B' && student.isAvailableB())
                                || (request.getType() == 'C'
                                && !student.haveBookC(book.getBookId()))) {
                            final BookStatus old = book.getStatus();
                            System.out.println("[" + date + "] ordering librarian lent "
                                    + book.getSchool() + "-" + book.getBookId() + " to "
                                    + school + "-" + request.getStudentId());
                            book.setStatus(BookStatus.BORROWED_BY_STUDENT);
                            book.setBorrowedBy(request.getStudentId());
                            student.addBorrowedBook(book);
                            if (book.getType() == 'B') {
                                student.setAvailableB(false);
                            }
                            list.get(book.getType() - 'B').add(book);
                            System.out.println("[" + date + "] " + school
                                    + "-" + request.getStudentId() + " borrowed "
                                    + book.getSchool() + "-" + book.getBookId()
                                    + " from ordering librarian");
                            System.out.println("(State) [" + date + "] " + book.getBookId()
                                    + " transfers from " + old + " to " + book.getStatus());
                        }
                        request.setValid(false);
                    }
                }
            }
        }
        removeInvalidRequests();
        return list;
    }

    public void checkForOtherSchoolBook() {
        for (int i = 0; i < todayOrderList.size(); i++) {
            OrderRequest request = todayOrderList.get(i);
            Book book = purchasingDepartment.findBookInOtherSchool(request.getOrderedBookId());
            if (book != null) {
                if ((book.getType() == 'B' && request.getStudent().isAvailableB()
                        && request.getStudent().isAvailableBforOtherSchool())
                        || (book.getType() == 'C'
                        && !request.getStudent().haveBookC(book.getBookId()))) {
                    request.setValid(true);
                    //request.getStudent().setAvailableB(false);
                    if (book.getType() == 'B') {
                        request.getStudent().setAvailableBforOtherSchool(false);
                        request.setValid(false);
                    }
                    borrowFromOtherSchool.add(request);
                    purchasingDepartment.addBookBorrowedReceived(book);
                    book.setStatus(BookStatus.PURCHASING_DEPARTMENT);
                    todayOrderList.remove(i--);
                }
            }
        }
    }

    public boolean haveAddOnRequest(String bookId) {
        for (AddOnRequest request : addOnList) {
            if (request.getOrderedBookId().equals(bookId)) {
                return true;
            }
        }
        return false;
    }

    public void validateOrderRequest(LocalDate date) {  //晚上用以确认预定的合法性（校内预定）
        checkForOtherSchoolBook();
        for (OrderRequest request : todayOrderList) {
            if (!request.isValid()) {
                if ((request.getType() == 'B' && request.getStudent().isAvailableB())
                        || (request.getType() == 'C' && !request.getStudent().
                        haveBookC(request.getOrderedBookId()))) {
                    if (!request.getStudent().isAvailableBforOtherSchool()) {
                        continue;
                    }
                    request.setValid(true);
                    System.out.println("[" + date + "] " + school + "-"
                            + request.getStudentId() + " ordered " + school + "-"
                            + request.getOrderedBookId() + " from ordering librarian");
                    System.out.println("[" + date + "] ordering librarian recorded "
                            + request.getSchool() + "-" + request.getStudentId() + "'s order of "
                            + request.getSchool() + "-" + request.getOrderedBookId());
                    if (!rack.get(request.getType() - 'A').
                            containsKey(request.getOrderedBookId())) {
                        if (!haveAddOnRequest(request.getOrderedBookId())) {
                            addOnList.add(new AddOnRequest(school, request.getOrderedBookId()));
                        } else {
                            for (AddOnRequest r : addOnList) {
                                if (r.getOrderedBookId().equals(request.getOrderedBookId())) {
                                    r.setCapacity(r.getCapacity() + 1);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < todayOrderList.size(); i++) {
            if (!todayOrderList.get(i).isValid()) {
                todayOrderList.remove(i--);
            }
        }
        addToOrderList();
        clearOrderTimes();
    }

    private void addToOrderList() {
        orderList.addAll(todayOrderList);
        todayOrderList.clear();
    }

    public void removeInvalidRequests() {
        for (int i = 0; i < orderList.size(); i++) {
            if (!orderList.get(i).isValid()) {
                orderList.remove(i--);
            }
        }
    }

    public void clearOrderTimes() {
        orderTimes.clear();
    }

    public void setInvalidSamePersonOrder(String studentId, String bookId) {
        if (bookId.charAt(0) == 'B') {
            for (OrderRequest request : orderList) {
                if (request.getType() == bookId.charAt(0)
                        && request.getStudentId().equals(studentId)) {
                    request.setValid(false);
                }
            }
        } else if (bookId.charAt(0) == 'C') {
            for (OrderRequest request : orderList) {
                if (request.getOrderedBookId().equals(bookId)
                        && request.getStudentId().equals(studentId)) {
                    request.setValid(false);
                }
            }
        }
    }

    public void deleteOrderListB(String studentId, Book book) {
        for (int i = 0; i < orderList.size(); i++) {
            if (orderList.get(i).getStudentId().equals(studentId)
                    && orderList.get(i).getType() == book.getType()) {
                orderList.remove(i--);
            }
        }
    }

    public void deleteOrderListC(String studentId, Book book) {
        for (int i = 0; i < orderList.size(); i++) {
            if (orderList.get(i).getStudentId().equals(studentId)
                    && orderList.get(i).getOrderedBookId().equals(book.getBookId())) {
                orderList.remove(i--);
            }
        }
    }

    public void getBooksFromOtherSchool(LocalDate date) {
        for (OrderRequest request : borrowFromOtherSchool) {
            purchasingDepartment.getBooksFromOtherSchool(date,
                    request, allStudent.get(request.getStudentId()));
        }
        borrowFromOtherSchool.clear();
    }

    public void returnOtherSchoolBook(LocalDate date, Book borrowedBook) {
        purchasingDepartment.addBookReturnedTransported(borrowedBook);
    }

    public void buyNewBooks(LocalDate date) {
        purchasingDepartment.addOnNewBooks(date, addOnList);
        addOnList.clear();
    }
}
