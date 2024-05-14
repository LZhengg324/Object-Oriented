import com.oocourse.spec3.main.Person;

import java.util.HashMap;
import java.util.List;

public class MyPath {
    private final HashMap<Integer, Person> peoples;
    private final HashMap<Integer, MyNode> nodes;
    private final HashMap<Integer, Boolean> collected;
    private final MyDisjoint disjoint;

    public MyPath(MyDisjoint disjoint, HashMap<Integer, Person> people) {
        this.peoples = people;
        this.nodes = new HashMap<>();
        this.collected = new HashMap<>();
        this.disjoint = disjoint;
    }

    private void addNodes(List<Integer> list) {
        nodes.clear();
        collected.clear();
        for (Integer i : list) {
            nodes.put(i, new MyNode(i));
            collected.put(i, false);
        }
    }

    public int dijkstra(int startId) {
        int ret = 0x3f3f3f3f;

        addNodes(disjoint.sameBlock(startId));
        //nodes.remove(startId);
        collected.put(startId, true);

        for (Person p : ((MyPerson)peoples.get(startId)).getAcquaintance().values()) {
            nodes.get(p.getId()).setDist1(p.queryValue(peoples.get(startId)));
            nodes.get(p.getId()).setOrigin1(p.getId());
        }
        while (true) {
            int min = 0x3f3f3f3f;
            int minId = -1;
            for (Integer i : nodes.keySet()) {
                if (nodes.get(i).getDist1() < min && !collected.get(i)) {
                    min = nodes.get(i).getDist1();
                    minId = i;
                }
            }
            if (minId == -1) {
                break;
            }
            collected.put(minId, true);
            for (Person p : ((MyPerson) peoples.get(minId)).getAcquaintance().values()) {
                if (!collected.get(p.getId())) {
                    int fromOrigin1 = nodes.get(minId).getOrigin1();
                    int fromOrigin2 = nodes.get(minId).getOrigin2();
                    int dist1 = nodes.get(minId).getDist1();
                    int dist2 = nodes.get(minId).getDist2();
                    update(minId, p.getId(), fromOrigin1,
                            dist1, p.queryValue(peoples.get(minId)));
                    update(minId, p.getId(), fromOrigin2,
                            dist2, p.queryValue(peoples.get(minId)));
                }
            }
        }

        int flag = 1;
        for (MyNode node : nodes.values()) {
            if (node.getDist1() + node.getDist2() < ret
                    && node.getOrigin1() != node.getOrigin2()
                    && node.getOrigin1() != -1
                    && node.getOrigin2() != -1) {
                ret = node.getDist1() + node.getDist2();
                flag = 0;
            }
        }
        if (flag == 1) {
            return -1;
        }
        return ret;
    }

    void update(int fromId, int toId, int fromOrigin, int dist, int value) {
        MyNode nodeFrom = nodes.get(fromId);
        MyNode nodeTo = nodes.get(toId);
        if (dist + value < nodeTo.getDist1()) {
            if (fromOrigin != nodeTo.getOrigin1()) {
                nodeTo.setOrigin2(nodeTo.getOrigin1());
                nodeTo.setDist2(nodeTo.getDist1());
            }
            nodeTo.setDist1(dist + value);
            nodeTo.setOrigin1(fromOrigin);
        } else if (dist + value < nodeTo.getDist2()) {
            if (fromOrigin != nodeTo.getOrigin1()) {
                nodeTo.setDist2(dist + value);
                nodeTo.setOrigin2(fromOrigin);
            }
        }
    }
}
