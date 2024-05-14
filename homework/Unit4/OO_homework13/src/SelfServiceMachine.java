import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class SelfServiceMachine {   //处理C类书籍
    private final HashMap<String, ArrayList<Book>> bufferList;  //待arranging回收,<BookId, <Book>>
    private final HashMap<String, ArrayList<Book>> borrowList;  //<StudentId, <Book>>
    private final ArrayList<HashMap<String, ArrayList<Book>>> rack;

    public SelfServiceMachine(ArrayList<HashMap<String, ArrayList<Book>>> rack) {
        this.bufferList = new HashMap<>();
        this.borrowList = new HashMap<>();
        this.rack = rack;
    }

    public boolean queryBook(LocalDate date, String studentId, String bookId) {
        System.out.println("[" + date + "] " + studentId +
                " queried " + bookId + " from self-service machine");
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

    public boolean borrowBook(LocalDate date, String studentId, Book book) {
        ArrayList<Book> list;
        if (borrowList.containsKey(studentId)) {
            list = borrowList.get(studentId);
            for (Book b : list) {
                if (b.getBookId().equals(book.getBookId())) {
                    putToBufferList(book);
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
        System.out.println("[" + date + "] " + studentId +
                " borrowed " + book.getBookId() + " from self-service machine");
        return true;
    }

    public void returnBook(LocalDate date, String studentId, Book book) {   //还有smeared和lost情况未处理
        borrowList.get(studentId).remove(book);
        if (borrowList.get(studentId).isEmpty()) {
            borrowList.remove(studentId);
        }
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
                " returned " + book.getBookId() + " to self-service machine");
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
