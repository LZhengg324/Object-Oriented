import com.oocourse.spec3.exceptions.MessageIdNotFoundException;
import com.oocourse.spec3.exceptions.EqualPersonIdException;
import com.oocourse.spec3.exceptions.PathNotFoundException;
import com.oocourse.spec3.exceptions.EmojiIdNotFoundException;
import com.oocourse.spec3.exceptions.EqualEmojiIdException;
import com.oocourse.spec3.exceptions.EqualRelationException;
import com.oocourse.spec3.exceptions.EqualGroupIdException;
import com.oocourse.spec3.exceptions.AcquaintanceNotFoundException;
import com.oocourse.spec3.exceptions.PersonIdNotFoundException;
import com.oocourse.spec3.exceptions.GroupIdNotFoundException;
import com.oocourse.spec3.exceptions.RelationNotFoundException;
import com.oocourse.spec3.exceptions.EqualMessageIdException;
import com.oocourse.spec3.main.NoticeMessage;
import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.EmojiMessage;
import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.Group;
import com.oocourse.spec3.main.RedEnvelopeMessage;
import com.oocourse.spec3.main.Network;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;

public class MyNetwork implements Network {
    private final HashMap<Integer, Person> people;    //容器内装每一个Person
    private final HashMap<Integer, Group> groups;
    private final HashMap<Integer, Message> messages;
    private final HashSet<Integer> emojiIdList;
    private final HashMap<Integer, Integer> emojiHeatList;
    private final MyDisjoint disjoint;
    private final MyPath myPath;
    private int triangle = 0;

    public MyNetwork() {
        this.people = new HashMap<>();
        this.groups = new HashMap<>();
        this.messages = new HashMap<>();
        this.emojiIdList = new HashSet<>();
        this.emojiHeatList = new HashMap<>();
        this.disjoint = new MyDisjoint();
        this.myPath = new MyPath(disjoint, people);
    }

    @Override
    public boolean contains(int id) {
        return people.containsKey(id);
    }

    @Override
    public Person getPerson(int id) {
        if (contains(id)) {
            return people.get(id);
        }
        return null;
    }

    @Override
    public void addPerson(Person person) throws EqualPersonIdException {
        if (!this.contains(person.getId())) {
            people.put(person.getId(), person);
            disjoint.addNodes(person.getId());
        } else {
            throw new MyEqualPersonIdException(person.getId());
        }
    }

    @Override
    public void addRelation(int id1, int id2, int value)
            throws PersonIdNotFoundException, EqualRelationException {
        if (contains(id1) && contains(id2)
                && !getPerson(id1).isLinked(getPerson(id2))) {
            int v1 = Math.min(id1, id2);
            int v2 = Math.max(id1, id2);
            MyPerson person = (MyPerson) people.get(v1);
            for (Person p : person.getAcquaintance().values()) {
                if (p.isLinked(people.get(v2))) {
                    triangle++;
                }
            }
            ((MyPerson)getPerson(id1)).linkOtherPerson(getPerson(id2), value);
            ((MyPerson)getPerson(id2)).linkOtherPerson(getPerson(id1), value);
            for (Group g : groups.values()) {
                if (g.hasPerson(getPerson(id1)) && g.hasPerson(getPerson(id2))) {
                    ((MyGroup)g).addValue(2 * getPerson(id1).queryValue(getPerson(id2)));
                }
            }
            disjoint.linkNodes(id1, id2);
        } else if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!contains(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        } else if (getPerson(id1).isLinked(getPerson(id2))) {
            throw new MyEqualRelationException(id1,id2);
        }
    }

    @Override
    public void modifyRelation(int id1, int id2, int value) throws PersonIdNotFoundException,
            EqualPersonIdException, RelationNotFoundException {
        if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!contains(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        } else if (id1 == id2) {
            throw new MyEqualPersonIdException(id1);
        } else if (!getPerson(id1).isLinked(getPerson(id2))) {
            throw new MyRelationNotFoundException(id1, id2);
        } else {
            MyPerson person1 = (MyPerson)getPerson(id1);
            MyPerson person2 = (MyPerson)getPerson(id2);
            if (person1.queryValue(person2) + value > 0) {
                person1.addValue(id2, value);
                person2.addValue(id1, value);
                for (Group g : groups.values()) {
                    if (g.hasPerson(person1) && g.hasPerson(person2)) {
                        ((MyGroup)g).addValue(2 * value);
                    }
                }
            } else {
                for (Group g : groups.values()) {
                    if (g.hasPerson(person1) && g.hasPerson(person2)) {
                        ((MyGroup)g).addValue(-2 * person1.queryValue(person2));
                    }
                }
                person1.deleteAcquaintance(person2.getId());
                person2.deleteAcquaintance(person1.getId());
                modifyTripleSum(person1, person2);
                disjoint.modifyTree(id1, id2);
            }
        }
    }

    @Override
    public int queryValue(int id1, int id2)
            throws PersonIdNotFoundException, RelationNotFoundException {
        if (contains(id1) && contains(id2) && getPerson(id1).isLinked(getPerson(id2))) {
            return getPerson(id1).queryValue(getPerson(id2));
        } else if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (contains(id1) && !contains(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        } else {
            throw new MyRelationNotFoundException(id1, id2);
        }
    }

    @Override
    public boolean isCircle(int id1, int id2) throws PersonIdNotFoundException {
        if (contains(id1) && contains(id2)) {
            return disjoint.isCircle(id1, id2);
        } else if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else {
            throw new MyPersonIdNotFoundException(id2);
        }
    }

    @Override
    public int queryBlockSum() {
        return disjoint.getBlock();
    }

    @Override
    public int queryTripleSum() {
        return this.triangle;
    }

    @Override
    public void addGroup(Group group) throws EqualGroupIdException {
        if (groups.containsKey(group.getId())) {
            throw new MyEqualGroupIdException(group.getId());
        }
        groups.put(group.getId(), group);
    }

    @Override
    public Group getGroup(int id) {
        if (!groups.containsKey(id)) {
            return null;
        }
        return groups.get(id);
    }

    @Override
    public void addToGroup(int id1, int id2) throws GroupIdNotFoundException,
            PersonIdNotFoundException, EqualPersonIdException {
        if (!groups.containsKey(id2)) {
            throw new MyGroupIdNotFoundException(id2);
        } else if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (getGroup(id2).hasPerson(getPerson(id1))) {
            throw new MyEqualPersonIdException(id1);
        } else {
            if (getGroup(id2).getSize() > 1111) {
                return;
            }
            getGroup(id2).addPerson(getPerson(id1));
        }
    }

    @Override
    public int queryGroupValueSum(int id) throws GroupIdNotFoundException {
        if (!groups.containsKey(id)) {
            throw new MyGroupIdNotFoundException(id);
        }
        return getGroup(id).getValueSum();
    }

    @Override
    public int queryGroupAgeVar(int id) throws GroupIdNotFoundException {
        if (!groups.containsKey(id)) {
            throw new MyGroupIdNotFoundException(id);
        }
        return getGroup(id).getAgeVar();
    }

    @Override
    public void delFromGroup(int id1, int id2) throws GroupIdNotFoundException,
            PersonIdNotFoundException, EqualPersonIdException {
        if (!groups.containsKey(id2)) {
            throw new MyGroupIdNotFoundException(id2);
        } else if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!getGroup(id2).hasPerson(getPerson(id1))) {
            throw new MyEqualPersonIdException(id1);
        } else {
            getGroup(id2).delPerson(getPerson(id1));
        }
    }

    @Override
    public boolean containsMessage(int id) {
        return messages.containsKey(id);
    }

    @Override
    public void addMessage(Message message) throws EqualMessageIdException,
            EmojiIdNotFoundException, EqualPersonIdException {
        if (containsMessage(message.getId())) {
            throw new MyEqualMessageIdExcpetion(message.getId());
        } else if (message instanceof EmojiMessage
                && !containsEmojiId(((EmojiMessage) message).getEmojiId())) {
            throw new MyEmojiIdNotFoundException(((EmojiMessage) message).getEmojiId());
        } else if ((!(message instanceof EmojiMessage)
                || containsEmojiId(((EmojiMessage) message).getEmojiId()))
                && message.getType() == 0 && message.getPerson1().equals(message.getPerson2())) {
            throw new MyEqualPersonIdException(message.getPerson1().getId());
        }
        messages.put(message.getId(), message);
    }

    @Override
    public Message getMessage(int id) {
        if (!containsMessage(id)) {
            return null;
        }
        return messages.get(id);
    }

    @Override
    public void sendMessage(int id) throws RelationNotFoundException,
            MessageIdNotFoundException, PersonIdNotFoundException {
        if (!containsMessage(id)) {
            throw new MyMessageIdNotFoundException(id);
        } else if (getMessage(id).getType() == 0 &&
                !getMessage(id).getPerson1().isLinked(getMessage(id).getPerson2())) {
            throw new MyRelationNotFoundException(getMessage(id).getPerson1().getId(),
                    getMessage(id).getPerson2().getId());
        } else if (getMessage(id).getType() == 1 &&
                !(getMessage(id).getGroup().hasPerson(getMessage(id).getPerson1()))) {
            throw new MyPersonIdNotFoundException(getMessage(id).getPerson1().getId());
        }
        MyMessage message = (MyMessage) getMessage(id);
        MyPerson person1 = (MyPerson) getMessage(id).getPerson1();
        if (message.getType() == 0 &&
                person1.isLinked(getMessage(id).getPerson2())
                && !person1.equals(getMessage(id).getPerson2())) {
            MyPerson person2 = (MyPerson) getMessage(id).getPerson2();
            person1.addSocialValue(message.getSocialValue());
            person2.addSocialValue(message.getSocialValue());
            if (message instanceof RedEnvelopeMessage) {
                person1.addMoney(-1 * ((RedEnvelopeMessage)message).getMoney());
                person2.addMoney(((RedEnvelopeMessage)message).getMoney());
            } else if (message instanceof EmojiMessage) {
                int emojiId = ((EmojiMessage) message).getEmojiId();
                int emojiIdHeatValue = emojiHeatList.get(emojiId) + 1;
                emojiHeatList.put(emojiId, emojiIdHeatValue);
            }
            person2.addMessageInHead(message);
        } else if (message.getType() == 1 && message.getGroup().hasPerson(person1)) {
            for (Person p : this.people.values()) {
                if (message.getGroup().hasPerson(p)) {
                    p.addSocialValue(message.getSocialValue());
                }
            }
            if (message instanceof RedEnvelopeMessage) {
                int money = ((RedEnvelopeMessage) message).getMoney()
                        / message.getGroup().getSize();
                person1.addMoney(-1 * money * message.getGroup().getSize());
                for (Person p : people.values()) {
                    if (message.getGroup().hasPerson(p)) {
                        p.addMoney(money);
                    }
                }
            } else if (message instanceof EmojiMessage) {
                int emojiId = ((EmojiMessage) message).getEmojiId();
                int emojiIdHeatValue = emojiHeatList.get(emojiId) + 1;
                emojiHeatList.put(emojiId, emojiIdHeatValue);
            }
        }
        this.messages.remove(message.getId());
    }

    @Override
    public int querySocialValue(int id) throws PersonIdNotFoundException {
        if (!contains(id)) {
            throw new MyPersonIdNotFoundException(id);
        }
        return getPerson(id).getSocialValue();
    }

    @Override
    public List<Message> queryReceivedMessages(int id) throws PersonIdNotFoundException {
        if (!contains(id)) {
            throw new MyPersonIdNotFoundException(id);
        }
        return getPerson(id).getReceivedMessages();
    }

    @Override
    public boolean containsEmojiId(int id) {
        return emojiIdList.contains(id);
    }

    @Override
    public void storeEmojiId(int id) throws EqualEmojiIdException {
        if (containsEmojiId(id)) {
            throw new MyEqualEmojiIdException(id);
        }
        emojiIdList.add(id);
        emojiHeatList.put(id, 0);
    }

    @Override
    public int queryMoney(int id) throws PersonIdNotFoundException {
        if (!contains(id)) {
            throw new MyPersonIdNotFoundException(id);
        }
        return getPerson(id).getMoney();
    }

    @Override
    public int queryPopularity(int id) throws EmojiIdNotFoundException {
        if (!containsEmojiId(id)) {
            throw new MyEmojiIdNotFoundException(id);
        }
        return emojiHeatList.get(id);
    }

    @Override
    public int deleteColdEmoji(int limit) {
        ArrayList<Integer> deleteEmojiList = new ArrayList<>();
        ArrayList<Integer> deleteMessageList = new ArrayList<>();
        for (Integer id : emojiHeatList.keySet()) {
            if (emojiHeatList.get(id) < limit) {
                deleteEmojiList.add(id);
            }
        }
        deleteEmojiList.forEach(emojiHeatList.keySet()::remove);
        deleteEmojiList.forEach(emojiIdList::remove);

        for (Message message : messages.values()) {
            if (message instanceof EmojiMessage) {
                if (!containsEmojiId(((EmojiMessage) message).getEmojiId())) {
                    deleteMessageList.add(message.getId());
                }
            }
        }
        deleteMessageList.forEach(messages.keySet()::remove);
        return emojiIdList.size();
    }

    @Override
    public void clearNotices(int personId) throws PersonIdNotFoundException {
        if (!contains(personId)) {
            throw new MyPersonIdNotFoundException(personId);
        }
        getPerson(personId).getMessages().removeIf(message -> message instanceof NoticeMessage);
    }

    @Override
    public int queryBestAcquaintance(int id) throws PersonIdNotFoundException,
            AcquaintanceNotFoundException {
        if (!contains(id)) {
            throw new MyPersonIdNotFoundException(id);
        } else if (((MyPerson)getPerson(id)).getAcquaintance().size() == 0) {
            throw new MyAcquaintanceNotFoundException(id);
        }
        return ((MyPerson)people.get(id)).getBestAcquaintance();
    }

    @Override
    public int queryCoupleSum() {
        int sum = 0;
        for (Person p1 : people.values()) {
            if (((MyPerson) p1).getBestAcquaintance() != -1) {
                Person p2 = people.get(((MyPerson) p1).getBestAcquaintance());
                if (((MyPerson) p1).getBestAcquaintance() == p2.getId()
                        && ((MyPerson) p2).getBestAcquaintance() == p1.getId() &&
                        p1.getId() < p2.getId()) {
                    sum++;
                }
            }
        }
        return sum;
    }

    @Override
    public int queryLeastMoments(int id) throws PersonIdNotFoundException, PathNotFoundException {
        if (!contains(id)) {
            throw new MyPersonIdNotFoundException(id);
        }
        int ret = myPath.dijkstra(id);
        if (ret == -1) {
            throw new MyPathNotFoundException(id);
        }
        return ret;
    }

    @Override
    public int deleteColdEmojiOKTest(int limit,
                                     ArrayList<HashMap<Integer, Integer>> beforeData,
                                     ArrayList<HashMap<Integer, Integer>> afterData, int result) {
        HashMap<Integer, Integer> oldEmojiHeatList = beforeData.get(0);
        HashMap<Integer, Integer> emojiHeatList = afterData.get(0);
        int length = 0;
        for (Integer emojiId : oldEmojiHeatList.keySet()) {
            if (oldEmojiHeatList.get(emojiId) >= limit) {
                if (!emojiHeatList.containsKey(emojiId)) {
                    return 1;
                }
                length++;
            }
        }
        for (Integer emojiId : emojiHeatList.keySet()) {
            if (!oldEmojiHeatList.containsKey(emojiId)
                    || !oldEmojiHeatList.get(emojiId).equals(emojiHeatList.get(emojiId))) {
                return 2;
            }
        }
        if (length != emojiHeatList.size()) {
            return 3;
        }
        HashMap<Integer, Integer> oldMessages = beforeData.get(1);
        HashMap<Integer, Integer> messages = afterData.get(1);
        for (Integer messageId : oldMessages.keySet()) {
            if (oldMessages.get(messageId) != null
                    && emojiHeatList.containsKey(oldEmojiHeatList.get(messageId))) {
                if (!messages.containsKey(messageId)
                        || (messages.get(messageId) != oldEmojiHeatList.get(messageId))) {
                    return 5;
                }
            }
        }
        for (Integer messageId : oldMessages.keySet()) {
            if (oldMessages.get(messageId) == null) {
                if (!messages.containsKey(messageId)
                        || messages.get(messageId) != oldEmojiHeatList.get(messageId)) {
                    return 6;
                }
            }
        }
        length = 0;
        for (Integer messageId : oldMessages.keySet()) {
            if (oldMessages.get(messageId) == null
                    || emojiHeatList.containsKey(oldMessages.get(messageId))) {
                length++;
            }
        }
        if (messages.size() != length) {
            return 7;
        }
        if (emojiHeatList.size() != result) {
            return 8;
        }
        return 0;
    }

    public void modifyTripleSum(Person person1, Person person2) {
        if (((MyPerson)person1).getAcquaintanceSize() != 0
                && ((MyPerson)person2).getAcquaintanceSize() != 0) {
            for (Person p : ((MyPerson) person1).getAcquaintance().values()) {
                if (p.isLinked(person2)) {
                    triangle--;
                }
            }
        }
    }
}