import com.oocourse.elevator2.TimableOutput;

import java.util.ArrayList;

public class MainClass {
    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();
        RequestQueue waitingList = new RequestQueue();  //waitingList存所有发出request的Person
        ArrayList<RequestQueue> allQueue = new ArrayList<>(); //归纳所有Elevator各自的requestqueue
        ArrayList<RequestQueue> maintainQueue = new ArrayList<>();
        ArrayList<Elevator> allElevator = new ArrayList<>();

        for (int id = 1; id <= 6; id++) {   //初始化电梯
            RequestQueue queue = new RequestQueue();
            allQueue.add(queue);
            Elevator elevator = new Elevator(id, queue, waitingList);
            allElevator.add(elevator);
            elevator.start();
        }

        Input input = new Input(waitingList, allQueue, allElevator, maintainQueue);
        input.start();

        Controller controller = new Controller(waitingList, allQueue, allElevator,maintainQueue);
        controller.start();
    }
}
