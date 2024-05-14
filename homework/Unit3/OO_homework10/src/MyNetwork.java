import com.oocourse.spec2.exceptions.EqualMessageIdException;
import com.oocourse.spec2.exceptions.MessageIdNotFoundException;
import com.oocourse.spec2.exceptions.EqualGroupIdException;
import com.oocourse.spec2.exceptions.GroupIdNotFoundException;
import com.oocourse.spec2.exceptions.EqualPersonIdException;
import com.oocourse.spec2.exceptions.AcquaintanceNotFoundException;
import com.oocourse.spec2.exceptions.PersonIdNotFoundException;
import com.oocourse.spec2.exceptions.RelationNotFoundException;
import com.oocourse.spec2.exceptions.EqualRelationException;
import com.oocourse.spec2.main.Group;
import com.oocourse.spec2.main.Message;
import com.oocourse.spec2.main.Network;
import com.oocourse.spec2.main.Person;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class MyNetwork implements Network {
    private final HashMap<Integer, Person> people;    //容器内装每一个Person
    private final HashMap<Integer, Integer> tree;     //容器内key为id, value为parentId
    private final HashMap<Integer, Group> groups;
    private final HashMap<Integer, Message> messages;
    private final HashSet<Pair<Integer, Integer>> backup;
    private int block = 0;
    private int triangle = 0;

    public MyNetwork() {
        this.people = new HashMap<>();
        this.tree = new HashMap<>();
        this.groups = new HashMap<>();
        this.messages = new HashMap<>();
        this.backup = new HashSet<>();
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
            tree.put(person.getId(), person.getId());
            block++;
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

            int rootId1 = findRoot(id1);
            int rootId2 = findRoot(id2);

            if (rootId1 != rootId2) {
                block--;
                tree.put(rootId2, rootId1);
            }

            backup.add(new Pair<>(id1, id2));
        } else if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!contains(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        } else if (getPerson(id1).isLinked(getPerson(id2))) {
            throw new MyEqualRelationException(id1,id2);
        }
    }

    public int findRoot(int id) {
        int father = tree.get(id);
        if (father == id) {
            return id;
        } else {
            int root = findRoot(father);
            tree.put(id, father);
            return root;
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
                backup.removeIf(p -> (p.getKey() == id1 && p.getValue() == id2)
                        || (p.getKey() == id2 && p.getValue() == id1));
                tree.clear();
                block = 0;
                for (Person p : people.values()) {
                    tree.put(p.getId(), p.getId());
                    block++;
                }
                Iterator<Pair<Integer, Integer>> it = backup.iterator();
                while (it.hasNext()) {  //重新建立relation树
                    Pair<Integer, Integer> pair = it.next();
                    int rootId1 = findRoot(pair.getKey());
                    int rootId2 = findRoot(pair.getValue());

                    if (rootId1 != rootId2) {
                        block--;
                        tree.put(rootId2, rootId1);
                    }
                }
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
            return findRoot(id1) == findRoot(id2);
        } else if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else {
            throw new MyPersonIdNotFoundException(id2);
        }
    }

    @Override
    public int queryBlockSum() {
        return this.block;
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
    public void addMessage(Message message) throws EqualMessageIdException, EqualPersonIdException {
        if (containsMessage(message.getId())) {
            throw new MyEqualMessageIdExcpetion(message.getId());
        } else if (message.getType() == 0 && message.getPerson1() == message.getPerson2()) {
            throw new MyEqualPersonIdException(message.getId());
        } else {
            messages.put(message.getId(), message);
        }
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
        } else {
            MyMessage message = (MyMessage) getMessage(id);
            MyPerson person1 = (MyPerson) getMessage(id).getPerson1();
            if (message.getType() == 0 &&
                    person1.isLinked(getMessage(id).getPerson2())
                    && !person1.equals(getMessage(id).getPerson2())) {
                MyPerson person2 = (MyPerson) getMessage(id).getPerson2();
                person1.addSocialValue(message.getSocialValue());
                person2.addSocialValue(message.getSocialValue());
                person2.addMessageInHead(message);
            } else if (message.getType() == 1 && message.getGroup().hasPerson(person1)) {
                for (Person p : this.people.values()) {
                    if (message.getGroup().hasPerson(p)) {
                        p.addSocialValue(message.getSocialValue());
                    }
                }
            }
            this.messages.remove(message.getId());
        }
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
    public int modifyRelationOKTest(int id1, int id2, int value,
                                    HashMap<Integer, HashMap<Integer, Integer>> beforeData,
                                    HashMap<Integer, HashMap<Integer, Integer>> afterData) {
        if (!beforeData.containsKey(id1) || !beforeData.containsKey(id2)
                || !(beforeData.get(id1).containsKey(id2) && beforeData.get(id2).containsKey(id1))
                || id1 == id2) {
            if (!beforeData.equals(afterData)) {
                return -1;
            }
        } else {
            if (beforeData.size() != afterData.size()) {
                return 1;
            }
            for (Integer i : beforeData.keySet()) {
                if (!afterData.containsKey(i)) {
                    return 2;
                }
            }
            for (Integer i : beforeData.keySet()) {
                if (i != id1 && i != id2 && !beforeData.get(i).equals(afterData.get(i))) {
                    return 3;
                }
            }
            int ret;
            if (beforeData.get(id1).get(id2) + value > 0) {
                ret = mrokTestBiggerZero(id1, id2, value, beforeData, afterData);
            } else {
                ret = mrokTestSmallerEqualZero(id1, id2, value, beforeData, afterData);
            }
            return ret;
        }
        return 0;
    }

    public int mrokTestBiggerZero(int id1, int id2, int value,
                                  HashMap<Integer, HashMap<Integer, Integer>> beforeData,
                                  HashMap<Integer, HashMap<Integer, Integer>> afterData) {
        if (!afterData.get(id1).containsKey(id2) || !afterData.get(id2).containsKey(id1)) {
            return 4;
        }

        if (afterData.get(id1).get(id2) != beforeData.get(id1).get(id2) + value) {
            return 5;
        }

        if (afterData.get(id2).get(id1) != beforeData.get(id2).get(id1) + value) {
            return 6;
        }

        if (afterData.get(id1).size() != beforeData.get(id1).size()) {
            return 7;
        }

        if (afterData.get(id2).size() != beforeData.get(id2).size()) {
            return 8;
        }

        for (Integer i : afterData.get(id1).keySet()) {
            if (!beforeData.get(id1).containsKey(i)) {
                return 9;
            }
        }

        for (Integer i : afterData.get(id2).keySet()) {
            if (!beforeData.get(id2).containsKey(i)) {
                return 10;
            }
        }

        for (Integer i : afterData.get(id1).keySet()) {
            if (i != id2 && !afterData.get(id1).get(i).equals(beforeData.get(id1).get(i))) {
                return 11;
            }
        }

        for (Integer i : afterData.get(id2).keySet()) {
            if (i != id1 && !afterData.get(id2).get(i).equals(beforeData.get(id2).get(i))) {
                return 12;
            }
        }
        return 0;
    }

    public int mrokTestSmallerEqualZero(int id1, int id2, int value,
                                        HashMap<Integer, HashMap<Integer, Integer>> beforeData,
                                        HashMap<Integer, HashMap<Integer, Integer>> afterData) {
        if (afterData.get(id1).containsKey(id2) || afterData.get(id2).containsKey(id1)) {
            return 15;
        }

        if (beforeData.get(id1).size() != afterData.get(id1).size() + 1) {
            return 16;
        }

        if (beforeData.get(id2).size() != afterData.get(id2).size() + 1) {
            return 17;
        }

        for (Integer i : afterData.get(id1).keySet()) {
            if (!beforeData.get(id1).containsKey(i)
                    || !beforeData.get(id1).get(i).equals(afterData.get(id1).get(i))) {
                return 20;
            }
        }

        for (Integer i : afterData.get(id2).keySet()) {
            if (!beforeData.get(id2).containsKey(i)
                    || !beforeData.get(id2).get(i).equals(afterData.get(id2).get(i))) {
                return 21;
            }
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

