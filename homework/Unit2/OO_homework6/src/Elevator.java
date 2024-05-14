import com.oocourse.elevator2.TimableOutput;

import java.util.ArrayList;

public class Elevator extends Thread {
    private int elevatorId;
    private int currentFloor = 1; //初始位置为1层
    private boolean currentDirection;   //true为向上，false为向下
    private int maximumPerson;
    private final int movingTime;
    private boolean isWorking;
    private boolean door;   //记录门的状态(T开F关)
    private RequestQueue queue; //该电梯待处理请求
    private RequestQueue waitingList;
    private final ArrayList<Person> personInElevator;

    public Elevator(int elevatorId, RequestQueue queue, RequestQueue waitingList) {
        this.elevatorId = elevatorId;
        this.currentFloor = 1;
        this.maximumPerson = 6;
        this.queue = queue;
        this.waitingList = waitingList;
        this.currentDirection = true;
        this.isWorking = false;
        this.door = false;
        this.movingTime = 400;
        this.personInElevator = new ArrayList<>();
    }

    public Elevator(int elevatorId, int currentFloor, int maximumPerson,
                    int movingTime, RequestQueue queue, RequestQueue waitingList) {
        this.elevatorId = elevatorId;
        this.currentFloor = currentFloor;
        this.maximumPerson = maximumPerson;
        this.movingTime = movingTime;
        this.queue = queue;
        this.waitingList = waitingList;
        this.currentDirection = true;
        this.door = false;
        this.isWorking = false;
        this.personInElevator = new ArrayList<>();
    }

    @Override
    public void run() {
        /*System.out.println("elevator id-" + elevatorId + "start()");
        System.out.println("currentFloor: " + currentFloor);
        System.out.println("maximumPerson: " + maximumPerson);
        System.out.println("movingTime: " + movingTime);
        System.out.println("=============");*/
        while (true) {
            if (!queue.isMaintain()) {
                if (queue.isEmpty() && queue.isFinishWork() && personInElevator.isEmpty()) {
                    //System.out.println("Elevator-" + elevatorId + " end");
                    return;
                }
                if (personInElevator.size() > 0 || isWorking) {  //电梯内有人
                    dealRequest();
                } else {
                    queue.isFreeAndWaiting();   //当前电梯无请求，进入等待状态
                    if (!queue.isEmpty()) {
                        dealRequest();
                    }
                }

                if (personInElevator.isEmpty() && queue.isEmpty()
                        && waitingList.isFinishWork()) {
                    waitingList.putRequest(null);
                }
            } else {
                //System.out.println("Elevator-" + elevatorId + " end");
                isWorking = false;
                if (!personInElevator.isEmpty()) {
                    if (currentDirection) {
                        goingUp();
                    } else {
                        goingDown();
                    }
                }
                allPeopleOut();
                break;
            }
        }
    }

    private void dealRequest() {
        if (!isWorking && (queue.currentRequestGoUp(currentFloor)
                || queue.currentRequestGoDown(currentFloor) || queue.upHaveRequest(currentFloor)
                || queue.downHaveRequest(currentFloor))) {
            personInOrOut();
            setCurrentDirection();
        } else {
            if (currentDirection) {
                goingUp();
            } else {
                goingDown();
            }
            changeDirection();
            personInOrOut();
        }
        isWorking = !(queue.isEmpty() && personInElevator.isEmpty());
    }

    public void allPeopleOut() {
        synchronized (personInElevator) {
            if (!personInElevator.isEmpty()) {
                doorOpen();
                for (Person p : personInElevator) {
                    if (p.getToFloor() != currentFloor) {
                        p.setFromFloor(currentFloor);
                        waitingList.putRequest(p);
                    }
                    personOut(p);
                }
                while (!personInElevator.isEmpty()) {
                    personInElevator.remove(0);
                }
                doorClose();
            }
        }
        maintainAble();
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

    public void setCurrentDirection() {
        if ((queue.currentRequestGoUp(currentFloor) || queue.upHaveRequest(currentFloor))
                && currentFloor < 11) {
            currentDirection = true;
            //isWorking = true;
        } else if ((queue.currentRequestGoDown(currentFloor) ||
                queue.downHaveRequest(currentFloor)) && currentFloor > 1) {
            currentDirection = false;
            //isWorking = true;
        }
    }

    public void setElevatorMaintain() {
        queue.setMaintain();
    }

    public int getElevatorId() {
        return this.elevatorId;
    }

    public RequestQueue getQueue() {
        return this.queue;
    }

    public boolean isEmpty() {
        return personInElevator.isEmpty();
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
                        personInElevator.remove(p);
                    } else {
                        doorOpen();
                        personOut(p);
                        personInElevator.remove(p);
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
                        personInElevator.add(p);
                    } else {
                        doorOpen();
                        personIn(p);
                        personInElevator.add(p);
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
            sleep(movingTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TimableOutput.println("ARRIVE-" + ++currentFloor + "-" + elevatorId);
    }

    public void goingDown() {
        try {
            sleep(movingTime);
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

    public void maintainAble() {
        TimableOutput.println("MAINTAIN_ABLE-" + elevatorId);
    }

    public void personIn(Person p) {
        TimableOutput.println("IN-" + p.getId() + "-" + currentFloor + "-" + elevatorId);

    }

    public void personOut(Person p) {
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

    public boolean isWorking() {
        return this.isWorking;
    }
}
