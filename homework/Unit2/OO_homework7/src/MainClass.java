import com.oocourse.elevator3.TimableOutput;

import java.util.ArrayList;

public class MainClass {
    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();
        RequestQueue waitingList = new RequestQueue();  //waitingList存所有发出request的Person
        ArrayList<RequestQueue> allQueue = new ArrayList<>(); //归纳所有Elevator各自的requestqueue
        ArrayList<Elevator> allElevator = new ArrayList<>();
        ArrayList<Floor> allFloors = new ArrayList<>();
        Elevatormap map = new Elevatormap(allElevator);

        for (int floor = 1; floor <= 11; floor++) {
            allFloors.add(new Floor(floor));
        }

        for (int id = 1; id <= 6; id++) {   //初始化电梯
            RequestQueue queue = new RequestQueue();
            allQueue.add(queue);
            Elevator elevator = new Elevator(id, queue, waitingList, allFloors, allElevator, map);
            allElevator.add(elevator);
            map.updateMap();
            elevator.start();
        }

        Input input = new Input(waitingList, allQueue, allElevator, allFloors, map);
        input.start();

        Controller controller = new Controller(waitingList, allQueue, allElevator, map);
        controller.start();
    }
}
