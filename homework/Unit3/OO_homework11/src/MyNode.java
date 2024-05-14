public class MyNode {
    private final int id;
    private int dist1;
    private int dist2;
    private int origin1;
    private int origin2;

    public MyNode(int id) {
        this.id = id;
        this.dist1 = 0x3f3f3f3f;
        this.dist2 = 0x3f3f3f3f;
        this.origin1 = -1;
        this.origin2 = -1;
    }

    public int getDist1() {
        return dist1;
    }

    public void setDist1(int dist1) {
        this.dist1 = dist1;
    }

    public int getDist2() {
        return dist2;
    }

    public void setDist2(int dist2) {
        this.dist2 = dist2;
    }

    public int getOrigin1() {
        return origin1;
    }

    public void setOrigin1(int origin1) {
        this.origin1 = origin1;
    }

    public int getOrigin2() {
        return origin2;
    }

    public void setOrigin2(int origin2) {
        this.origin2 = origin2;
    }
}
