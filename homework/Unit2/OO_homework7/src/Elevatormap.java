import java.util.ArrayList;
import java.util.LinkedList;

public class Elevatormap {
    private final int[][] map = new int[100][12];  //11个楼层，12架电梯
    private final ArrayList<Elevator> allElevators;

    public Elevatormap(ArrayList<Elevator> allElevators) {
        this.allElevators = allElevators;
        updateMap();
    }

    public void updateMap() {
        synchronized (map) {
            for (int elevator = 0; elevator < allElevators.size(); elevator++) {
                for (int floor = 1; floor <= 11; floor++) {
                    map[elevator][floor] = allElevators.get(elevator).canAccess(floor);
                }
            }
        }
    }

    public LinkedList<Node> findShortestWay(int elevator, int fromFloor, int toFloor) {
        synchronized (map) {
            int[] dist = new int[12];           //用来记录从起点到这层需要的距离
            int[] path = new int[12];           //用来记录从哪一层过来的
            int[] usedElevator = new int[12];   //用来记录去到这层要搭的电梯
            for (int i = 0; i < 12; i++) {
                dist[i] = -1;
                path[i] = -1;
                usedElevator[i] = -1;
            }

            LinkedList<Integer> elevatorCanReached = new LinkedList<>();    //该楼层可以开门的电梯
            LinkedList<Integer> floorCanReached = new LinkedList<>();       //该电梯可以开门的楼层
            dist[fromFloor] = 0;
            usedElevator[fromFloor] = elevator;

            for (int i = 1; i <= 11; i++) {
                if (map[elevator][i] == 1 && dist[i] == -1) {
                    enqueue(i, floorCanReached);
                    dist[i] = dist[fromFloor] + 1;
                    path[i] = fromFloor;
                    usedElevator[i] = elevator;
                }
            }

            //do {
            while (!floorCanReached.isEmpty()) {
                int floor = dequeue(floorCanReached);
                for (int i = 0; i <= allElevators.size(); i++) {
                    if (map[i][floor] == 1) {
                        enqueue(i, elevatorCanReached);
                    }
                }
                while (!elevatorCanReached.isEmpty()) {
                    int e = dequeue(elevatorCanReached);
                    for (int i = 1; i <= 11; i++) {
                        if (map[e][i] == 1 && dist[i] == -1) {
                            enqueue(i, floorCanReached);
                            dist[i] = dist[floor] + 1;
                            path[i] = floor;
                            usedElevator[i] = e;
                        }
                    }
                }
            }
            LinkedList<Node> schedule = new LinkedList<>();
            int from = path[toFloor];
            int to = toFloor;

            while (from != -1) {
                Node nd = new Node(from, to, usedElevator[to]);
                schedule.offerFirst(nd);
                to = path[to];
                from = path[to];
            }
            return schedule;
        }
    }

    public void enqueue(int vertex, LinkedList<Integer> queue) {
        queue.add(vertex);
    }

    public int dequeue(LinkedList<Integer> queue) {
        if (!queue.isEmpty()) {
            return queue.poll();
        }
        return -1;
    }
}
