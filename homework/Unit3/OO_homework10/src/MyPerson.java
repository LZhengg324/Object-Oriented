import com.oocourse.spec2.main.Message;
import com.oocourse.spec2.main.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

public class MyPerson implements Person {
    private final int id;
    private final String name;
    private final int age;
    private int socialValue;
    private int bestAcquaintance = -1;
    private int bestAcquaintanceValue = 0;
    private final List<Message> messages;
    private final HashMap<Integer, Person> acquaintance;
    private final HashMap<Integer, Integer> value;

    public MyPerson(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.socialValue = 0;
        this.messages = new ArrayList<>();
        this.acquaintance = new HashMap<>();
        this.value = new HashMap<>();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getAge() {
        return age;
    }

    public Map<Integer, Person> getAcquaintance() {
        return Collections.unmodifiableMap(acquaintance);
    }

    public int getAcquaintanceSize() {
        return this.acquaintance.size();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Person)) {
            return false;
        }
        return ((Person) obj).getId() == this.id;
    }

    @Override
    public boolean isLinked(Person person) {
        return (this.equals(person) || acquaintance.containsKey(person.getId()));
    }

    @Override
    public int queryValue(Person person) {
        if (acquaintance.containsKey(person.getId())) {
            return value.get(person.getId());
        }
        return 0;
    }

    @Override
    public void addSocialValue(int num) {
        this.socialValue = this.socialValue + num;
    }

    @Override
    public int getSocialValue() {
        return this.socialValue;
    }

    @Override
    public List<Message> getMessages() {
        return this.messages;
    }

    public void addMessageInHead(Message message) {
        this.messages.add(0, message);
    }

    @Override
    public List<Message> getReceivedMessages() {
        List<Message> receivedMessages = new ArrayList<>();
        int length = Math.min(messages.size(), 5);
        for (int i = 0; i < length; i++) {
            receivedMessages.add(messages.get(i));
        }
        return receivedMessages;
    }

    @Override
    public int compareTo(Person o) {
        return this.name.compareTo(o.getName());
    }

    public void linkOtherPerson(Person person, int value) {
        this.acquaintance.put(person.getId(), person);
        this.value.put(person.getId(), value);
        updateBestAcquaintance(person.getId());
    }

    public void updateBestAcquaintance(int id) {    //还没弄完
        if (acquaintance.size() == 1 || value.get(id) > this.bestAcquaintanceValue
                || (value.get(id).equals(this.bestAcquaintanceValue)
                && id < bestAcquaintance)) {
            this.bestAcquaintance = id;
            this.bestAcquaintanceValue = this.value.get(id);
        } else if (value.get(id) < this.bestAcquaintanceValue && id == bestAcquaintance) {
            int max = 0;
            int maxId = 0;
            for (Integer i : this.value.keySet()) {
                if (this.value.get(i) > max || (this.value.get(i) == max && i < maxId)) {
                    maxId = i;
                    max = this.value.get(i);
                }
            }
            this.bestAcquaintance = maxId;
            this.bestAcquaintanceValue = max;
        }
    }

    public int getBestAcquaintance() {
        return this.bestAcquaintance;
    }

    public void addValue(int id, int value) {
        int newValue = this.value.get(id) + value;
        this.value.put(id, newValue);
        updateBestAcquaintance(id);
    }

    public void deleteAcquaintance(int id) {
        acquaintance.remove(id);
        value.remove(id);
        if (this.bestAcquaintance == id) {
            int tempBestAcquaintanceId = -1;
            int max = 0;
            if (!acquaintance.isEmpty()) {
                for (Person p : acquaintance.values()) {
                    if (value.get(p.getId()) > max) {
                        max = value.get(p.getId());
                        tempBestAcquaintanceId = p.getId();
                    } else if (value.get(p.getId()) == max && tempBestAcquaintanceId > p.getId()) {
                        tempBestAcquaintanceId = p.getId();
                    }
                }
            }
            this.bestAcquaintance = tempBestAcquaintanceId;
            if (tempBestAcquaintanceId == -1) {
                this.bestAcquaintanceValue = 0;
            } else {
                this.bestAcquaintanceValue = this.value.get(this.bestAcquaintance);
            }
        }
    }
}
