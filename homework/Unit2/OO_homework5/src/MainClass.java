import com.oocourse.elevator1.TimableOutput;

import java.util.ArrayList;

public class MainClass {
    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();
        ArrayList<RequestQueue> allQueue = new ArrayList<>(); //归纳所有Elevator各自的requestqueue

        for (int id = 1; id <= 6; id++) {   //初始化电梯
            RequestQueue queue = new RequestQueue();
            allQueue.add(queue);
            Elevator elevator = new Elevator(id, queue);
            elevator.start();
        }

        RequestQueue waitingList = new RequestQueue();  //waitingList存所有发出request的Person
        Input input = new Input(waitingList);
        input.start();

        Controller controller = new Controller(waitingList, allQueue);
        controller.start();
    }
}
