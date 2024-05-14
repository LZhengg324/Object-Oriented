import java.time.LocalDate;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Input {
    private final Library library;

    public Input(Library library) {
        this.library = library;
    }

    public void getBooksAndRequest() {
        String format = "((^[ABC])-\\d+)\\s+(\\d+)";
        Scanner scanner = new Scanner(System.in);
        Pattern pattern = Pattern.compile(format);

        int num = Integer.parseInt(scanner.nextLine());

        for (int i = 0; i < num; i++) {
            String str = scanner.nextLine();
            Matcher matcher = pattern.matcher(str);

            if (matcher.matches()) {
                library.addBook(matcher.group(1),
                        matcher.group(2).charAt(0),
                        Integer.parseInt(matcher.group(3)));
            }

        }

        format = "\\[(\\d+)-(\\d+)-(\\d+)]\\s+(\\d+)\\s+(\\w+)\\s+([ABC]-\\d+)";
        pattern = Pattern.compile(format);

        num = Integer.parseInt(scanner.nextLine());

        for (int i = 0; i < num; i++) {
            String str = scanner.nextLine();
            Matcher matcher = pattern.matcher(str);

            if (matcher.matches()) {
                LocalDate date = LocalDate.of(Integer.parseInt(matcher.group(1)),
                        Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)));
                Action action;
                if (matcher.group(5).compareTo("borrowed") == 0) {
                    action = Action.BORROWED;
                } else if (matcher.group(5).compareTo("returned") == 0) {
                    action = Action.RETURNED;
                } else if (matcher.group(5).compareTo("smeared") == 0) {
                    action = Action.SMEARED;
                } else {
                    action = Action.LOST;
                }
                Request request = new Request(date, matcher.group(4), matcher.group(6), action);
                library.addRequest(request);
            }

        }
    }
}
