import java.util.concurrent.Semaphore;

public class Floor {
    private final int floor;
    private final Semaphore elevatorInService;  //服务中
    private final Semaphore elevatorFetchOnly;  //只接人
    //private final ArrayList<Elevator> dockedElevator;

    public Floor(int floor) {
        this.floor = floor;
        //this.dockedElevator = new ArrayList<>();
        this.elevatorInService = new Semaphore(4);
        this.elevatorFetchOnly = new Semaphore(2);
    }

    public void acquireElevatorInService() {
        try {
            elevatorInService.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void acquireElevatorFetchOnly() {
        try {
            elevatorFetchOnly.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void releaseElevatorInService() {
        elevatorInService.release();
    }

    public void releaseElevatorFetchOnly() {
        elevatorFetchOnly.release();
    }

    /*public void addDockedElevator(Elevator e) {
        dockedElevator.add(e);
    }*/

    /*public void removeDockedElevator(Elevator e) {
        dockedElevator.remove(e);
    }*/
}
