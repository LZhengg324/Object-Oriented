import com.oocourse.spec3.exceptions.MessageIdNotFoundException;

import java.util.HashMap;

public class MyMessageIdNotFoundException extends MessageIdNotFoundException {
    private final int id;
    private static int totalCount = 0;
    private static HashMap<Integer, Integer> idCount = new HashMap<>();

    public MyMessageIdNotFoundException(int id) {
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
        System.out.println("minf-" + totalCount + ", " + this.id + "-" + idCount.get(id));
    }
}
