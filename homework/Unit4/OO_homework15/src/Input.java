import java.time.LocalDate;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Input {

    private final LibrarySystem librarySystem;

    public Input(LibrarySystem librarySystem) {
        this.librarySystem = librarySystem;
    }

    public void getInput() {
        Scanner scanner = new Scanner(System.in);
        int num = Integer.parseInt(scanner.nextLine());

        for (int i = 0; i < num; i++) {
            String format = "(^\\w+)\\s+(\\d+)";
            Pattern pattern1 = Pattern.compile(format);

            String str = scanner.nextLine();
            Matcher matcher1 = pattern1.matcher(str);

            if (matcher1.matches()) {
                String school = matcher1.group(1);
                Library library = new Library(school, librarySystem.getAllLibrary());
                format = "((^[ABC])-\\d+)\\s+(\\d+)\\s+([YN])";
                Pattern pattern2 = Pattern.compile(format);
                for (int j = 0; j < Integer.parseInt(matcher1.group(2)); j++) {
                    String book = scanner.nextLine();
                    Matcher matcher2 = pattern2.matcher(book);
                    if (matcher2.matches()) {
                        library.addBook(school, matcher2.group(1),
                                matcher2.group(2).charAt(0),
                                Integer.parseInt(matcher2.group(3)),
                                matcher2.group(4).charAt(0) == 'Y');
                    }
                }
                librarySystem.addLibrary(library);
                //System.out.println(librarySystem.getSize());
            }
        }

        String format = "\\[(\\d+)-(\\d+)-(\\d+)]\\s+(\\w+)-(\\d+)\\s+(\\w+)\\s+([ABC]-\\d+)";
        Pattern pattern = Pattern.compile(format);
        num = Integer.parseInt(scanner.nextLine());

        for (int i = 0; i < num; i++) {
            String str = scanner.nextLine();
            Matcher matcher = pattern.matcher(str);

            if (matcher.matches()) {
                LocalDate date = LocalDate.of(Integer.parseInt(matcher.group(1)),
                        Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)));
                Action action;
                if (matcher.group(6).compareTo("borrowed") == 0) {
                    action = Action.BORROWED;
                } else if (matcher.group(6).compareTo("returned") == 0) {
                    action = Action.RETURNED;
                } else if (matcher.group(6).compareTo("smeared") == 0) {
                    action = Action.SMEARED;
                } else {
                    action = Action.LOST;
                }
                Request request = new Request(date, matcher.group(4),
                        matcher.group(5), matcher.group(7), action);
                librarySystem.addRequest(request);
            }

        }
    }
}
