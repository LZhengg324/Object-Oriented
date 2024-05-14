import com.oocourse.spec2.exceptions.GroupIdNotFoundException;

import java.util.HashMap;

public class MyGroupIdNotFoundException extends GroupIdNotFoundException {
    private final int id;
    private static int totalCount = 0;
    private static HashMap<Integer, Integer> idCount = new HashMap<>();

    public MyGroupIdNotFoundException(int id) {
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
        System.out.println("ginf-" + totalCount + ", " + this.id + "-" + idCount.get(id));
    }
}
