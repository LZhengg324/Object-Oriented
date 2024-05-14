import com.oocourse.spec2.exceptions.PersonIdNotFoundException;

import java.util.HashMap;

public class MyPersonIdNotFoundException extends PersonIdNotFoundException {
    private final int id;
    private static HashMap<Integer, Integer> idCount = new HashMap<>();
    private static int totalCount = 0;

    public MyPersonIdNotFoundException(int id) {
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
        System.out.println("pinf-" + totalCount + ", " + this.id + "-" + idCount.get(id));
    }
}
