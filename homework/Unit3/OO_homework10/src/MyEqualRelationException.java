import com.oocourse.spec2.exceptions.EqualRelationException;

import java.util.HashMap;

public class MyEqualRelationException extends EqualRelationException {
    private final int id1;
    private final int id2;
    private static HashMap<Integer, Integer> idCount = new HashMap<>();
    private static int totalCount = 0;

    public MyEqualRelationException(int id1, int id2) {
        if (id1 < id2) {
            this.id1 = id1;
            this.id2 = id2;
        } else {
            this.id1 = id2;
            this.id2 = id1;
        }
        totalCount++;
        int count1 = 1;
        if (idCount.containsKey(id1)) {
            count1 = idCount.get(id1) + 1;
        }
        idCount.put(id1, count1);
        if (id1 != id2) {
            int count2 = 1;
            if (idCount.containsKey(id2)) {
                count2 = idCount.get(id2) + 1;
            }
            idCount.put(id2, count2);
        }
    }

    @Override
    public void print() {
        System.out.println("er-" + totalCount + ", " + id1 + "-" + idCount.get(id1) +
                ", " + id2 + "-" + idCount.get(id2));
    }
}
