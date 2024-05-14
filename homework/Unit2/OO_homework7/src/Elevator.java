import com.oocourse.elevator3.TimableOutput;

import java.util.ArrayList;

public class Elevator extends Thread {
    private final int elevatorId;
    private int currentFloor = 1; //初始位置为1层
    private boolean currentDirection;   //true为向上，false为向下
    private final int maximumPerson;
    private final int access;
    private final int movingTime;
    private boolean isWorking;
    private boolean isMaintain;
    private boolean door;   //记录门的状态(T开F关)
    private boolean fetchOnly;  //只接人
    private final RequestQueue queue; //该电梯待处理请求
    private final RequestQueue waitingList;
    private final Elevatormap map;
    private final ArrayList<Person> personInElevator;
    private final ArrayList<Floor> allFloors;
    private final ArrayList<Elevator> allElevators;

    public Elevator(int elevatorId, RequestQueue queue, RequestQueue waitingList,
                    ArrayList<Floor> allFloors, ArrayList<Elevator> allElevators, Elevatormap map) {
        this.elevatorId = elevatorId;
        this.currentFloor = 1;
        this.maximumPerson = 6;
        this.access = 2047;
        this.queue = queue;
        this.waitingList = waitingList;
        this.map = map;
        this.currentDirection = true;
        this.isWorking = false;
        this.isMaintain = false;
        this.door = false;
        this.fetchOnly = true;
        this.movingTime = 400;
        this.personInElevator = new ArrayList<>();
        this.allFloors = allFloors;
        this.allElevators = allElevators;
        //allFloors.get(0).addDockedElevator(this);
    }

    public Elevator(int elevatorId, int currentFloor, int maximumPerson,
                    int movingTime, int access, RequestQueue queue,
                    RequestQueue waitingList, ArrayList<Floor> allFloors,
                    ArrayList<Elevator> allElevators, Elevatormap map) {
        this.elevatorId = elevatorId;
        this.currentFloor = currentFloor;
        this.maximumPerson = maximumPerson;
        this.movingTime = movingTime;
        this.access = access;
        this.queue = queue;
        this.waitingList = waitingList;
        this.map = map;
        this.currentDirection = true;
        this.door = false;
        this.fetchOnly = true;
        this.isWorking = false;
        this.isMaintain = false;
        this.personInElevator = new ArrayList<>();
        this.allFloors = allFloors;
        this.allElevators = allElevators;
        //allFloors.get(currentFloor - 1).addDockedElevator(this);
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
            if (canAccess(currentFloor) == 1) {
                personInOrOut();
            }
            setCurrentDirection();
        } else {
            if (currentDirection) {
                goingUp();
            } else {
                goingDown();
            }
            changeDirection();
            if (canAccess(currentFloor) == 1) {
                personInOrOut();
            }
        }
        isWorking = !(queue.isEmpty() && personInElevator.isEmpty());
    }

    public void allPeopleOut() {
        synchronized (personInElevator) {
            if (!personInElevator.isEmpty()) {
                doorOpen();
                for (Person p : personInElevator) {
                    if (p.getToFloor() != currentFloor || (p.getToFloor() == currentFloor
                            && p.getToFloorFinal() != currentFloor)) {
                        p.setFromFloorFinal(currentFloor);
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
            //System.out.println("Elevator" + elevatorId + "personIn left "
            // + personInElevator.size());
            //System.out.println("Elevator" + elevatorId + "queue left " + queue.getsize());
            maintainAble();
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

    public int canAccess(int floor) {
        if (((this.access & (1 << floor - 1)) != 0) && !this.isMaintain) {
            return 1;
        } else {
            return 0;
        }
    }

    public void setElevatorMaintain() {
        this.isMaintain = true;
        queue.setMaintain();
    }

    public int getElevatorId() {
        return this.elevatorId;
    }

    public RequestQueue getQueue() {
        return this.queue;
    }

    public boolean isMaintain() {
        return isMaintain;
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

                    if (p.scheduleSize() > 0) {
                        p.updateSchedule();
                        if (allElevators.get(p.getUseElevator()).isMaintain) {
                            p.setFromFloorFinal(currentFloor);
                            p.setFromFloor(currentFloor);
                            waitingList.putRequest(p);
                        } else {
                            allElevators.get(p.getUseElevator()).getQueue().putRequest(p);
                        }
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
        //allFloors.get(currentFloor - 1).removeDockedElevator(this);
        TimableOutput.println("ARRIVE-" + ++currentFloor + "-" + elevatorId);
        //allFloors.get(currentFloor - 1).addDockedElevator(this);
    }

    public void goingDown() {
        try {
            sleep(movingTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //allFloors.get(currentFloor - 1).removeDockedElevator(this);
        TimableOutput.println("ARRIVE-" + --currentFloor + "-" + elevatorId);
        //allFloors.get(currentFloor - 1).addDockedElevator(this);
    }

    public void doorOpen() {
        if (isFetchOnly()) {
            fetchOnly = true;
            allFloors.get(currentFloor - 1).acquireElevatorFetchOnly();
            allFloors.get(currentFloor - 1).acquireElevatorInService();
        } else {
            fetchOnly = false;
            allFloors.get(currentFloor - 1).acquireElevatorInService();
        }
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
        if (this.fetchOnly) {
            allFloors.get(currentFloor - 1).releaseElevatorInService();
            allFloors.get(currentFloor - 1).releaseElevatorFetchOnly();
        } else {
            allFloors.get(currentFloor - 1).releaseElevatorInService();
        }
        fetchOnly = false;
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

    private boolean isFetchOnly() {
        synchronized (personInElevator) {
            if (!personInElevator.isEmpty()) {
                for (Person p : personInElevator) {
                    if (p.getToFloor() == currentFloor) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    public int getCurrentFloor() {
        return this.currentFloor;
    }
}
