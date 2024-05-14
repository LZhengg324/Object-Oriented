import java.time.LocalDate;

public class test {
    public static void main(String[] args) {
        LocalDate date1 = LocalDate.of(2023,1,2);
        LocalDate date2 = LocalDate.of(2023,1,1);
        System.out.println(date1.compareTo(date2));
    }
}
