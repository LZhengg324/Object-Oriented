import com.oocourse.spec1.exceptions.EqualPersonIdException;
import com.oocourse.spec1.exceptions.EqualRelationException;
import com.oocourse.spec1.exceptions.PersonIdNotFoundException;
import com.oocourse.spec1.exceptions.RelationNotFoundException;
import com.oocourse.spec1.main.Network;
import com.oocourse.spec1.main.Person;

import java.util.ArrayList;
import java.util.HashMap;

public class MyNetwork implements Network {
    private HashMap<Integer, Person> people;    //容器内装每一个Person
    private HashMap<Integer, Integer> tree;     //容器内key为id, value为parentId
    private int block = 0;
    private int triangle = 0;

    public MyNetwork() {
        this.people = new HashMap<>();
        this.tree = new HashMap<>();
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
            if (!tree.get(id1).equals(tree.get(id2))) {
                block--;
            }
            for (Integer i : tree.keySet()) {
                if (tree.get(i).equals(tree.get(id2)) && i != id2) {
                    tree.put(i, tree.get(id1));
                }
            }
            tree.put(id2, tree.get(id1));
        } else if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!contains(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        } else if (getPerson(id1).isLinked(getPerson(id2))) {
            throw new MyEqualRelationException(id1,id2);
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
            return tree.get(id1).equals(tree.get(id2));
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
    public boolean queryTripleSumOKTest(HashMap<Integer, HashMap<Integer, Integer>> beforeData,
                                        HashMap<Integer, HashMap<Integer, Integer>> afterData,
                                        int result) {
        if (!beforeData.equals(afterData)) {
            return false;
        }

        int count = 0;
        ArrayList<Integer> personId = new ArrayList<>(afterData.keySet());

        for (int i = 0; i < personId.size(); i++) {
            for (int j = i + 1; j < personId.size(); j++) {
                for (int k = j + 1; k < personId.size(); k++) {
                    HashMap<Integer, Integer> map1 = afterData.get(personId.get(i));
                    HashMap<Integer, Integer> map2 = afterData.get(personId.get(j));
                    HashMap<Integer, Integer> map3 = afterData.get(personId.get(k));
                    if (map1.containsKey(personId.get(j)) && map2.containsKey(personId.get(k))
                            && map3.containsKey(personId.get(i))) {
                        count++;
                    }
                }
            }
        }
        return count == result;
    }
}
