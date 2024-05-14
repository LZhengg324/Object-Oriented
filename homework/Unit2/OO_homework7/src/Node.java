public class Node {
    private final int fromFloor;
    private final int toFloor;
    private final int useElevator;

    public Node(int fromFloor, int toFloor, int useElevator) {
        this.fromFloor = fromFloor;
        this.toFloor = toFloor;
        this.useElevator = useElevator;
    }

    public int getFromFloor() {
        return fromFloor;
    }

    public int getToFloor() {
        return toFloor;
    }

    public int getUseElevator() {
        return useElevator;
    }
}
