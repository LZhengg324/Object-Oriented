import com.oocourse.spec2.exceptions.RelationNotFoundException;

import java.util.HashMap;

public class MyRelationNotFoundException extends RelationNotFoundException {
    private final int id1;
    private final int id2;
    private static HashMap<Integer, Integer> idCount = new HashMap<>();
    private static int totalCount = 0;

    public MyRelationNotFoundException(int id1, int id2) {
        if (id1 < id2) {
            this.id1 = id1;
            this.id2 = id2;
        } else {
            this.id1 = id2;
            this.id2 = id1;
        }
        totalCount++;
        int count1 = 1;
        int count2 = 1;
        if (idCount.containsKey(id1)) {
            count1 = idCount.get(id1) + 1;
        }
        if (idCount.containsKey(id2)) {
            count2 = idCount.get(id2) + 1;
        }
        idCount.put(id1, count1);
        idCount.put(id2, count2);
    }

    @Override
    public void print() {
        System.out.println("rnf-" + totalCount + ", " + this.id1 + "-" + idCount.get(this.id1)
                + ", " + this.id2 + "-" + idCount.get(this.id2));
    }
}
