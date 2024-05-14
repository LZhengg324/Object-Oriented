import com.oocourse.spec1.main.Runner;

public class MainClass {
    public static void main(String[] args) throws Exception {
        new Runner(MyPerson.class, MyNetwork.class).run();
    }
}
