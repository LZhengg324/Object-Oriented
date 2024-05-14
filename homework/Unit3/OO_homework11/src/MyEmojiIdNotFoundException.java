import com.oocourse.spec3.exceptions.EmojiIdNotFoundException;

import java.util.HashMap;

public class MyEmojiIdNotFoundException extends EmojiIdNotFoundException {
    private final int id;
    private static int totalCount = 0;
    private static HashMap<Integer, Integer> idCount = new HashMap<>();

    public MyEmojiIdNotFoundException(int id) {
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
        System.out.println("einf-" + totalCount + ", " + this.id + "-" + idCount.get(id));
    }
}
