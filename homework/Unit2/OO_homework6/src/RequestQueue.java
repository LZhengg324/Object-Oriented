import com.oocourse.elevator2.Request;
import java.util.ArrayList;

public class RequestQueue {
    private final ArrayList<Request> queue = new ArrayList<>();
    private boolean finishWork;
    private boolean maintain;

    public RequestQueue() {
        this.finishWork = false;
        this.maintain = false;
    }

    public synchronized void putRequest(Request request) {
        queue.add(request);
        notifyAll();
    }

    public Request takeRequest() {
        synchronized (this) {
            if (/*!isFinishWork() &&*/ isEmpty()) {
                try {
                    //System.out.println("wait....");
                    wait();
                    //System.out.println("wait end....");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (isEmpty()) {
                return null;
            }
            //notifyAll();
            return queue.remove(0);
        }
    }

    public synchronized void isFreeAndWaiting() {   //进入等待状态
        while (queue.isEmpty() && !isFinishWork() && !isMaintain()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //notifyAll();  /*试试看*/
    }

    public Person personGetInLift(int currentFloor, boolean currentDirection) {
        for (Request r : queue) {
            if (r instanceof Person) {
                Person p = (Person) r;
                if ((p.getFromFloor() == currentFloor)) {
                    if ((p.goUp() && currentDirection) || (p.goDown() && !currentDirection)) {
                        queue.remove(p);
                        return p;
                    }
                }
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return queue.size() == 0;
    }

    public boolean isFinishWork() {
        return finishWork;
    }

    public int getsize() {
        return this.queue.size();
    }

    public Person remove() {
        return (Person) queue.remove(0);
    }

    public synchronized void setFinishWork() {
        this.finishWork = true;
        notifyAll();
    }

    public synchronized void setMaintain() {
        notifyAll();
        this.maintain = true;
    }

    public boolean upHaveRequest(int currentFloor) {
        if (queue.size() > 0) {
            for (Request r : queue) {
                if (r instanceof Person) {
                    Person p = (Person) r;
                    if (p.getFromFloor() > currentFloor) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean downHaveRequest(int currentFloor) {
        if (queue.size() > 0) {
            for (Request r : queue) {
                if (r instanceof Person) {
                    Person p = (Person) r;
                    if (p.getFromFloor() < currentFloor) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean currentRequestGoUp(int currentFloor) {
        if (queue.size() > 0) {
            for (Request r : queue) {
                if (r instanceof Person) {
                    Person p = (Person) r;
                    if (p.getFromFloor() == currentFloor) {
                        if (p.getToFloor() > currentFloor) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean currentRequestGoDown(int currentFloor) {
        if (queue.size() > 0) {
            for (Request r : queue) {
                if (r instanceof Person) {
                    Person p = (Person) r;
                    if (p.getFromFloor() == currentFloor) {
                        if (p.getToFloor() < currentFloor) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isMaintain() {
        return this.maintain;
    }
}
