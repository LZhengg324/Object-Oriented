import com.oocourse.elevator2.ElevatorInput;
import com.oocourse.elevator2.PersonRequest;
import com.oocourse.elevator2.ElevatorRequest;
import com.oocourse.elevator2.MaintainRequest;
import com.oocourse.elevator2.Request;
import java.io.IOException;
import java.util.ArrayList;

public class Input extends Thread {
    private RequestQueue waitingList;
    private ArrayList<RequestQueue> allQueue;
    private ArrayList<RequestQueue> maintainQueue;
    private ArrayList<Elevator> allElevator;

    public Input(RequestQueue waitingList, ArrayList<RequestQueue> allQueue,
                 ArrayList<Elevator> allElevator, ArrayList<RequestQueue> maintainQueue) {
        this.waitingList = waitingList;
        this.allQueue = allQueue;
        this.allElevator = allElevator;
        this.maintainQueue = maintainQueue;
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
                            e.getCapacity(), (int)(e.getSpeed() * 1000), queue, waitingList);
                    allElevator.add(elevator);
                    /*System.out.println("========================");
                    System.out.println("allQueue.size = " + allQueue.size());
                    System.out.println("allElevators.size = " + allElevator.size());
                    System.out.println("========================");*/
                    elevator.start();
                } else if (request instanceof MaintainRequest) {
                    MaintainRequest m = (MaintainRequest) request;
                    for (Elevator e : allElevator) {
                        if (m.getElevatorId() == e.getElevatorId()) {
                            e.setElevatorMaintain();
                            while (e.getQueue().getsize() != 0) {
                                waitingList.putRequest(e.getQueue().remove());
                            }
                            maintainQueue.add(e.getQueue());
                            allQueue.remove(e.getQueue());
                            /*System.out.println("========================");
                            System.out.println("allQueue.size = " + allQueue.size());
                            System.out.println("allElevators.size = " + allElevator.size());
                            System.out.println("========================");*/
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
