import java.time.LocalDate;
import java.util.ArrayList;

public class LogisticDivision {
    private final String school;
    private final ArrayList<Book> bufferList;

    public LogisticDivision(String school) {
        this.school = school;
        bufferList = new ArrayList<>();
    }

    public void repairBook(LocalDate date, Book book) {
        BookStatus old = book.getStatus();
        book.setStatus(BookStatus.LOGISTICS_DIVISION);
        System.out.println("[" + date + "] " + book.getSchool() + "-" + book.getBookId()
                + " got repaired by logistics division in " + school);
        if (book.getType() == 'B') {
            System.out.println("(State) [" + date + "] " + book.getBookId()
                    + " transfers from " + old + " to " + book.getStatus());
        } else if (book.getType() == 'C') {
            System.out.println("(State) [" + date + "] " + book.getBookId()
                    + " transfers from " + old + " to " + book.getStatus());
        }
        book.setSmeared(false);
        if (book.getSchool().equals(school)) {
            bufferList.add(book);
        }
    }

    public ArrayList<Book> arrangeBufferList() {
        ArrayList<Book> ret = new ArrayList<>();
        if (!bufferList.isEmpty()) {
            for (Book book : bufferList) {
                ret.add(book);
                book.setStatus(BookStatus.ON_THE_RACK);
            }
            bufferList.clear();
        }
        return ret;
    }
}
