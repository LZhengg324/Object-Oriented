import com.oocourse.spec3.main.EmojiMessage;
import com.oocourse.spec3.main.Group;
import com.oocourse.spec3.main.Person;

public class MyEmojiMessage extends MyMessage implements EmojiMessage {
    private final int emojiId;

    public MyEmojiMessage(int messageId, int emojiNumber,
                          Person messagePerson1, Person messagePerson2) {
        super(messageId, emojiNumber, messagePerson1, messagePerson2);
        this.emojiId = emojiNumber;
    }

    public MyEmojiMessage(int messageId, int emojiNumber,
                          Person messagePerson1, Group messageGroup) {
        super(messageId, emojiNumber, messagePerson1, messageGroup);
        this.emojiId = emojiNumber;
    }

    @Override
    public int getEmojiId() {
        return this.emojiId;
    }

    @Override
    public int getType() {
        return super.getType();
    }

    @Override
    public int getId() {
        return super.getId();
    }

    @Override
    public int getSocialValue() {
        return super.getSocialValue();
    }

    @Override
    public Person getPerson1() {
        return super.getPerson1();
    }

    @Override
    public Person getPerson2() {
        return super.getPerson2();
    }

    @Override
    public Group getGroup() {
        return super.getGroup();
    }
}
