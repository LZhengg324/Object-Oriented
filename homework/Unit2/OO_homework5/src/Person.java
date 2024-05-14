public class Person {
    private int id;
    private int fromFloor;
    private int toFloor;

    public Person(int id, int fromFloor, int toFloor) {
        this.id = id;
        this.fromFloor = fromFloor;
        this.toFloor = toFloor;
    }

    public int getId() {
        return id;
    }

    public int getFromFloor() {
        return this.fromFloor;
    }

    public int getToFloor() {
        return this.toFloor;
    }

    public boolean goUp() {
        return fromFloor < toFloor;
    }

    public boolean goDown() {
        return fromFloor > toFloor;
    }
}
