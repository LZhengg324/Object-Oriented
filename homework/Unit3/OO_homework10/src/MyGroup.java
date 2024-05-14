import com.oocourse.spec2.main.Group;
import com.oocourse.spec2.main.Person;

import java.util.HashMap;

public class MyGroup implements Group {
    private int id;
    private long sum;
    private long sqrtSum;
    private int valueSum;
    private HashMap<Integer, Person> people;

    public MyGroup(int id) {
        this.id = id;
        this.sum = 0;
        this.sqrtSum = 0;
        this.valueSum = 0;
        this.people = new HashMap<>();
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Group) {
            return ((Group) obj).getId() == this.id;
        }
        return false;
    }

    @Override
    public void addPerson(Person person) {
        if (!hasPerson(person)) {
            this.sum = this.sum + person.getAge();
            this.sqrtSum = this.sqrtSum + (long) person.getAge() * person.getAge();
            this.people.put(person.getId(), person);
            for (Person p : people.values()) {
                if (p.isLinked(person)) {
                    addValue(2 * p.queryValue(person));
                }
            }
        }
    }

    @Override
    public boolean hasPerson(Person person) {
        return people.containsKey(person.getId());
    }

    @Override
    public int getValueSum() {
        return this.valueSum;
    }

    @Override
    public int getAgeMean() {
        if (getSize() != 0) {
            return (int) (this.sum / getSize());
        }
        return 0;
    }

    @Override
    public int getAgeVar() {
        if (getSize() != 0) {
            return (int) ((this.sqrtSum - 2 * this.sum * getAgeMean()
                    + getAgeMean() * getAgeMean() * getSize()) / getSize());
        }
        return 0;
    }

    @Override
    public void delPerson(Person person) {
        if (hasPerson(person)) {
            this.sum = this.sum - person.getAge();
            this.sqrtSum = this.sqrtSum - (long) person.getAge() * person.getAge();
            for (Person p : people.values()) {
                if (p.isLinked(person)) {
                    addValue(- 2 * p.queryValue(person));
                }
            }
            people.remove(person.getId());
        }
    }

    @Override
    public int getSize() {
        return people.size();
    }

    public void addValue(int value) {
        this.valueSum = this.valueSum + value;
    }
}
