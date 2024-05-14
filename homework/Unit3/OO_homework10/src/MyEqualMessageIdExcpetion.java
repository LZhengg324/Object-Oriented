import com.oocourse.spec2.exceptions.EqualMessageIdException;

import java.util.HashMap;

public class MyEqualMessageIdExcpetion extends EqualMessageIdException {
    private final int id;
    private static int totalCount = 0;
    private static HashMap<Integer, Integer> idCount = new HashMap<>();

    public MyEqualMessageIdExcpetion(int id) {
        this.id = id;
        totalCount++;
        int count = 1;
        if (idCount.containsKey(id)) {
            count = idCount.get(id) + 1;
        }
        idCount.put(id, count);
    }

    @Override
    public void print() {
        System.out.println("emi-" + totalCount + ", " + this.id + "-" + idCount.get(id));
    }
}
