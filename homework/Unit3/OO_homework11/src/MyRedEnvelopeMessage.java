import com.oocourse.spec3.main.Group;
import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.RedEnvelopeMessage;

public class MyRedEnvelopeMessage extends MyMessage implements RedEnvelopeMessage {
    private final int money;

    public MyRedEnvelopeMessage(int messageId, int luckyMoney,
                                Person messagePerson1, Person messagePerson2) {
        super(messageId, luckyMoney * 5, messagePerson1, messagePerson2);
        this.money = luckyMoney;
    }

    public MyRedEnvelopeMessage(int messageId, int luckyMoney,
                                Person messagePerson1, Group messageGroup) {
        super(messageId, luckyMoney * 5, messagePerson1, messageGroup);
        this.money = luckyMoney;
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

    @Override
    public int getMoney() {
        return this.money;
    }
}
