import java.time.LocalDate;

public class MainClass {
    public static void main(String[] args) {
        Library library = new Library(LocalDate.of(2023, 1, 1));
        Input input = new Input(library);
        input.getBooksAndRequest();
        library.start();
    }
}
