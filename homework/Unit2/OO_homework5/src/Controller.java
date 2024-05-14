import java.util.ArrayList;

public class Controller extends Thread {
    private RequestQueue queue;
    private ArrayList<RequestQueue> allQueue;

    public Controller(RequestQueue queue, ArrayList<RequestQueue> allQueue) {
        this.queue = queue;
        this.allQueue = allQueue;
    }

    @Override
    public void run() {
        while (true) {
            for (int i = 0; i < 6; i++) {
                if (queue.isEmpty() && queue.isFinishWork()) {
                    for (RequestQueue q : allQueue) {
                        q.setFinishWork();
                    }
                    return;
                }
                Person person = queue.takeRequest();
                if (person == null) {
                    i--;
                } else {
                    allQueue.get(i).putRequest(person);
                }
            }
        }
    }
}

/*
1-FROM-9-TO-4
2-FROM-2-TO-3
3-FROM-8-TO-1
4-FROM-8-TO-4
5-FROM-2-TO-8
6-FROM-3-TO-10
7-FROM-5-TO-6
 */
