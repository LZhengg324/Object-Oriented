import com.oocourse.spec3.main.Runner;

public class MainClass {
    public static void main(String[] args) throws Exception {
        new Runner(MyPerson.class, MyNetwork.class,
                MyGroup.class, MyMessage.class, MyEmojiMessage.class,
                MyNoticeMessage.class, MyRedEnvelopeMessage.class).run();
    }
}
