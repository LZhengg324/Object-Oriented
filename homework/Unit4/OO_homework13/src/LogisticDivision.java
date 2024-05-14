import java.time.LocalDate;
import java.util.ArrayList;

public class LogisticDivision {
    private final ArrayList<Book> bufferList;

    public LogisticDivision() {
        bufferList = new ArrayList<>();
    }

    public void repairBook(LocalDate date, Book book) {
        book.setStatus(BookStatus.LOGISTICS_DIVISION);
        System.out.println("[" + date + "] " + book.getBookId()
                + " got repaired by logistics division");
        book.setSmeared(false);
        bufferList.add(book);
    }

    public void arrangeBufferList() {
        if (!bufferList.isEmpty()) {
            for (Book book : bufferList) {
                book.setStatus(BookStatus.ON_THE_RACK);
            }
            bufferList.clear();
        }
    }
}
