import java.util.ArrayList;

public class RequestQueue {
    private ArrayList<Person> queue = new ArrayList<>();
    private boolean finishWork;

    public RequestQueue() {
        this.finishWork = false;
    }

    public synchronized void putRequest(Person person) {
        queue.add(person);
        notifyAll();
    }

    public synchronized Person takeRequest() {
        if (!isFinishWork() && isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (isEmpty()) {
            return null;
        }
        notifyAll();
        return queue.remove(0);
    }

    public synchronized void isFreeAndWaiting() {   //进入等待状态
        while (queue.isEmpty() && !isFinishWork()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        notifyAll();
    }

    public Person personGetInLift(int currentFloor, boolean currentDirection) {
        for (Person p : queue) {
            if ((p.getFromFloor() == currentFloor)) {
                if ((p.goUp() && currentDirection) || (p.goDown() && !currentDirection)) {
                    queue.remove(p);
                    return p;
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

    public synchronized void setFinishWork() {
        this.finishWork = true;
        notifyAll();
    }

    public boolean upHaveRequest(int currentFloor) {
        for (Person p : queue) {
            if (p.getFromFloor() > currentFloor) {
                return true;
            }
        }
        return false;
    }

    public boolean downHaveRequest(int currentFloor) {
        for (Person p : queue) {
            if (p.getFromFloor() < currentFloor) {
                return true;
            }
        }
        return false;
    }

    public boolean currentRequestGoUp(int currentFloor) {
        for (Person p : queue) {
            if (p.getFromFloor() == currentFloor) {
                if (p.getToFloor() > currentFloor) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean currentRequestGoDown(int currentFloor) {
        for (Person p : queue) {
            if (p.getFromFloor() == currentFloor) {
                if (p.getToFloor() < currentFloor) {
                    return true;
                }
            }
        }
        return false;
    }
}
