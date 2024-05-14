import com.oocourse.elevator2.Request;

import java.util.ArrayList;

public class Controller extends Thread {
    private final RequestQueue waitingList;
    private ArrayList<RequestQueue> allQueue;
    private ArrayList<RequestQueue> maintainQueue;
    private ArrayList<Elevator> allElevator;

    public Controller(RequestQueue waitingList, ArrayList<RequestQueue> allQueue,
                      ArrayList<Elevator> allElevator, ArrayList<RequestQueue> maintainQueue) {
        this.waitingList = waitingList;
        this.allQueue = allQueue;
        this.allElevator = allElevator;
        this.maintainQueue = maintainQueue;
    }

    @Override
    public void run() {
        while (true) {
            for (int i = 0; i < allElevator.size(); i++) {
                // else /*if (!waitingList.isEmpty())*/ {
                Request request = waitingList.takeRequest();
                if (request == null) {
                    if (waitingList.isFinishWork() && allQueueisEmpty()) {
                        for (RequestQueue q : allQueue) {
                            q.setFinishWork();
                        }
                        //System.out.println("controller end");
                        return;
                    }
                    i--;
                } else {
                    if (request instanceof Person) {
                        while (allElevator.get(i).getQueue().isMaintain()) {
                            i++;
                            if (i >= allElevator.size()) {
                                i = 0;
                            }
                        }
                        allElevator.get(i).getQueue().putRequest(request);
                    }
                }
                //}
            }
        }
    }

    public boolean allQueueisEmpty() {
        for (Elevator e : allElevator) {
            if (e.isWorking()) {
                return false;
            }
            if (!e.getQueue().isEmpty()) {
                return false;
            }
            if (!e.isEmpty()) {
                return false;
            }
        }
        return waitingList.isEmpty();
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
