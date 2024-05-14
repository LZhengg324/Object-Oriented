import com.oocourse.elevator1.TimableOutput;

import java.util.ArrayList;

public class Elevator extends Thread {
    private int elevatorId;
    private int currentFloor = 1; //初始位置为1层
    private boolean currentDirection;   //true为向上，false为向下
    private final int maximumPerson = 6;
    private boolean isWorking;
    private boolean door;   //记录门的状态
    private RequestQueue queue; //该电梯待处理请求
    private final ArrayList<Person> personInElevator;

    public Elevator(int elevatorId, RequestQueue queue) {
        this.elevatorId = elevatorId;
        this.currentFloor = 1;
        this.queue = queue;
        this.currentDirection = true;
        this.isWorking = false;
        this.door = false;
        personInElevator = new ArrayList<>();
    }

    @Override
    public void run() {
        while (true) {
            if (queue.isEmpty() && queue.isFinishWork() && personInElevator.isEmpty()) {
                return;
            }
            if (personInElevator.size() > 0) {  //电梯内有人
                dealRequest();
            } else {
                queue.isFreeAndWaiting();   //当前电梯无请求，进入等待状态
                if (!queue.isEmpty()) {
                    dealRequest();
                }
            }
        }
    }

    private void dealRequest() {
        if (!isWorking && (queue.currentRequestGoUp(currentFloor)
                || queue.currentRequestGoDown(currentFloor))) {
            isWorking = true;
            personInOrOut();
        } else {
            if (currentDirection) {
                goingUp();
            } else {
                goingDown();
            }
            changeDirection();
            personInOrOut();
        }
    }

    private void changeDirection() {
        if (currentDirection) {
            currentDirection = currentFloor < 11 && ((queue.upHaveRequest(currentFloor) &&
                    personInElevator.size() < maximumPerson)  ||
                    upHaveToFloor(currentFloor) ||
                    queue.currentRequestGoUp(currentFloor));
        } else {
            currentDirection = !(currentFloor > 1 && ((queue.downHaveRequest(currentFloor) &&
                    personInElevator.size() < maximumPerson) ||
                    downHaveToFloor(currentFloor) ||
                    queue.currentRequestGoDown(currentFloor)));
        }
    }

    private boolean upHaveToFloor(int currentFloor) {
        for (Person p : personInElevator) {
            if (p.getToFloor() > currentFloor) {
                return true;
            }
        }
        return false;
    }

    private boolean downHaveToFloor(int currentFloor) {
        for (Person p : personInElevator) {
            if (p.getToFloor() < currentFloor) {
                return true;
            }
        }
        return false;
    }

    public void personInOrOut() {
        synchronized (personInElevator) {
            while (personInElevator.size() > 0) {  //先判断有没有人要出去
                Person p = personGetOutLift(currentFloor);
                if (p == null) {
                    break;
                } else {
                    if (door) {
                        personOut(p);
                    } else {
                        doorOpen();
                        personOut(p);
                    }
                }
            }

            while (personInElevator.size() < maximumPerson) {   //再判断有没有人要进来
                Person p = queue.personGetInLift(currentFloor, currentDirection);
                if (p == null) {
                    break;
                } else {
                    if (door) {
                        personIn(p);
                    } else {
                        doorOpen();
                        personIn(p);
                    }
                }
            }
            if (door) {
                doorClose();
            }
        }
    }

    public void goingUp() {
        try {
            sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TimableOutput.println("ARRIVE-" + ++currentFloor + "-" + elevatorId);
    }

    public void goingDown() {
        try {
            sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TimableOutput.println("ARRIVE-" + --currentFloor + "-" + elevatorId);
    }

    public void doorOpen() {
        this.door = true;
        TimableOutput.println("OPEN-" + currentFloor + "-" + elevatorId);
        try {
            sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void doorClose() {
        try {
            sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.door = false;
        TimableOutput.println("CLOSE-" + currentFloor + "-" + elevatorId);
    }

    public void personIn(Person p) {
        personInElevator.add(p);
        TimableOutput.println("IN-" + p.getId() + "-" + currentFloor + "-" + elevatorId);

    }

    public void personOut(Person p) {
        personInElevator.remove(p);
        TimableOutput.println("OUT-" + p.getId() + "-" + currentFloor + "-" + elevatorId);

    }

    public Person personGetOutLift(int currentFloor) {
        for (Person p : personInElevator) {
            if (p.getToFloor() == currentFloor) {
                personInElevator.remove(p);
                return p;
            }
        }
        return null;
    }
}
