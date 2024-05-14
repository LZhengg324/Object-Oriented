import com.oocourse.elevator1.ElevatorInput;
import com.oocourse.elevator1.PersonRequest;
import java.io.IOException;

public class Input extends Thread {
    private RequestQueue waitingList;

    public Input(RequestQueue waitingList) {
        this.waitingList = waitingList;
    }

    @Override
    public void run() {
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        while (true) {
            PersonRequest request = elevatorInput.nextPersonRequest();
            // when request == null
            // it means there are no more lines in stdin
            if (request == null) {
                waitingList.setFinishWork();
                break;
            } else {
                // a new valid request
                Person person = new Person(request.getPersonId(),
                        request.getFromFloor(), request.getToFloor());
                waitingList.putRequest(person);
            }
        }
        try {
            elevatorInput.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
