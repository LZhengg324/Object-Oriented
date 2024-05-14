import com.oocourse.elevator3.Request;

import java.util.LinkedList;

public class Person extends Request {
    private final int id;
    private int fromFloor;
    private int toFloor;
    private int fromFloorFinal;
    private final int toFloorFinal;
    private int useElevator;
    private LinkedList<Node> schedule;

    public Person(int id, int fromFloor, int toFloor) {
        this.id = id;
        this.fromFloor = fromFloor;
        this.toFloor = toFloor;
        this.fromFloorFinal = fromFloor;
        this.toFloorFinal = toFloor;
    }

    public int getId() {
        return id;
    }

    public int getFromFloor() {
        return this.fromFloor;
    }

    public void setFromFloor(int fromFloor) {
        this.fromFloor = fromFloor;
    }

    public void setSchedule(LinkedList<Node> schedule) {
        this.schedule = schedule;
    }

    public void updateSchedule() {
        Node nd = schedule.poll();
        if (nd != null) {
            this.fromFloor = nd.getFromFloor();
            this.toFloor = nd.getToFloor();
            this.useElevator = nd.getUseElevator();
        }
    }

    public int scheduleSize() {
        if (schedule == null) {
            return 0;
        }
        return schedule.size();
    }

    public int getToFloor() {
        return this.toFloor;
    }

    public int getUseElevator() {
        return this.useElevator;
    }

    public int getFromFloorFinal() {
        return fromFloorFinal;
    }

    public int getToFloorFinal() {
        return toFloorFinal;
    }

    public boolean goUp() {
        return fromFloor < toFloor;
    }

    public boolean goDown() {
        return fromFloor > toFloor;
    }

    public void setFromFloorFinal(int currentFloor) {
        this.fromFloorFinal = currentFloor;
    }
}
