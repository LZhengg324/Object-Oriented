import com.oocourse.spec1.main.Person;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MyPerson implements Person {
    private int id;
    private String name;
    private int age;
    private HashMap<Integer, Person> acquaintance;
    private HashMap<Integer, Integer> value;

    public MyPerson(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
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

    public HashMap<Integer, Integer> getValue() {
        return (HashMap<Integer, Integer>) Collections.unmodifiableMap(value);
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
    public int compareTo(Person o) {
        return this.name.compareTo(o.getName());
    }

    public void linkOtherPerson(Person person, int value) {
        this.acquaintance.put(person.getId(), person);
        this.value.put(person.getId(), value);
    }
}
