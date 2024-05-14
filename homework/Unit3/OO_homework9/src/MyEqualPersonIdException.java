import com.oocourse.spec1.exceptions.EqualPersonIdException;

import java.util.HashMap;

public class MyEqualPersonIdException extends EqualPersonIdException {
    private final int id;
    private static HashMap<Integer, Integer> idCount = new HashMap<>();
    private static int totalCount = 0;

    public MyEqualPersonIdException(int id) {
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
        System.out.println("epi-" + totalCount + ", " + this.id + "-" + idCount.get(id));
    }
}
