import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class OrderingLibrarian {
    private final ArrayList<OrderRequest> orderList;
    private final HashMap<String, Integer> orderTimes;  //<StudentId, Times>
    private final ArrayList<HashMap<String, ArrayList<Book>>> rack;

    public OrderingLibrarian(ArrayList<HashMap<String, ArrayList<Book>>> rack) {
        this.orderList = new ArrayList<>();
        this.orderTimes = new HashMap<>();
        this.rack = rack;
    }

    public void addOrder(LocalDate date, OrderRequest orderRequest) {
        String studentId = orderRequest.getStudentId();
        for (OrderRequest request : orderList) {
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
        orderList.add(orderRequest);
        System.out.println("[" + date + "] " + studentId + " ordered "
                + orderRequest.getOrderedBookId() + " from ordering librarian");
    }

    public boolean isEmpty() {
        return orderList.isEmpty();
    }

    public ArrayList<ArrayList<Book>> checkOrderList(LocalDate date) {
        ArrayList<ArrayList<Book>> list = new ArrayList<>();
        ArrayList<Book> listB = new ArrayList<>();
        ArrayList<Book> listC = new ArrayList<>();
        list.add(listB);
        list.add(listC);

        for (OrderRequest request : orderList) {
            if (request.isValid()) {
                String bookId = request.getOrderedBookId();
                String studentId = request.getStudentId();
                for (Book book : rack.get(bookId.charAt(0) - 'A').get(bookId)) {
                    if (book.getStatus().equals(BookStatus.ON_THE_RACK)) {
                        System.out.println("[" + date + "] " + studentId
                                + " borrowed " + bookId + " from ordering librarian");
                        book.setStatus(BookStatus.BORROWED_BY_STUDENT);
                        book.setBorrowedBy(studentId);
                        list.get(book.getType() - 'B').add(book);
                        request.setValid(false);
                        setInvalidSamePersonOrder(studentId, bookId);
                        break;
                    }
                }
            }
        }
        for (int i = 0; i < orderList.size(); i++) {
            if (!orderList.get(i).isValid()) {
                orderList.remove(i--);
            }
        }
        return list;
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
}
