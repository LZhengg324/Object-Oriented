import com.oocourse.elevator3.ElevatorInput;
import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.ElevatorRequest;
import com.oocourse.elevator3.MaintainRequest;
import com.oocourse.elevator3.Request;
import java.io.IOException;
import java.util.ArrayList;

public class Input extends Thread {
    private RequestQueue waitingList;
    private Elevatormap map;
    private ArrayList<RequestQueue> allQueue;
    private ArrayList<Elevator> allElevator;
    private ArrayList<Floor> allFloors;

    public Input(RequestQueue waitingList, ArrayList<RequestQueue> allQueue,
                 ArrayList<Elevator> allElevator, ArrayList<Floor> allFloors, Elevatormap map) {
        this.waitingList = waitingList;
        this.map = map;
        this.allQueue = allQueue;
        this.allElevator = allElevator;
        this.allFloors = allFloors;
    }

    @Override
    public void run() {
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        while (true) {
            Request request = elevatorInput.nextRequest();
            // when request == null
            // it means there are no more lines in stdin
            if (request == null) {
                waitingList.setFinishWork();
                break;
            } else {
                // a new valid request
                if (request instanceof PersonRequest) {
                    PersonRequest p = (PersonRequest)request;
                    Person person = new Person(p.getPersonId(),
                            p.getFromFloor(), p.getToFloor());
                    waitingList.putRequest(person);
                } else if (request instanceof ElevatorRequest) {
                    ElevatorRequest e = (ElevatorRequest) request;
                    RequestQueue queue = new RequestQueue();
                    allQueue.add(queue);
                    //System.out.println("have" + allQueue.size() + "queues");
                    Elevator elevator = new Elevator(e.getElevatorId(), e.getFloor(),
                            e.getCapacity(), (int)(e.getSpeed() * 1000),
                            e.getAccess(), queue, waitingList, allFloors, allElevator, map);
                    allElevator.add(elevator);
                    //System.out.println("addElevator" + e.getElevatorId());
                    map.updateMap();
                    elevator.start();
                } else if (request instanceof MaintainRequest) {
                    MaintainRequest m = (MaintainRequest) request;
                    for (Elevator e : allElevator) {
                        if (m.getElevatorId() == e.getElevatorId()) {
                            e.setElevatorMaintain();
                            map.updateMap();
                            while (e.getQueue().getsize() > 0) {
                                Person p = e.getQueue().remove();
                                p.setFromFloorFinal(p.getFromFloor());
                                waitingList.putRequest(p);
                            }
                            break;
                        }
                    }
                }
            }
        }
        try {
            elevatorInput.close();
            //System.out.println("Input End");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
