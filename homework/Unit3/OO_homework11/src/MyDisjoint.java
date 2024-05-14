import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class MyDisjoint {
    private final HashMap<Integer, Integer> tree;
    private final ArrayList<Integer> list;
    private final HashSet<Pair<Integer, Integer>> backup;
    private int block;

    public MyDisjoint() {
        this.tree = new HashMap<>();
        this.list = new ArrayList<>();
        this.backup = new HashSet<>();
        block = 0;
    }

    public void addNodes(int id) {
        tree.put(id, id);
        list.add(id);
        block++;
    }

    public void linkNodes(int id1, int id2) {
        int root1 = findRoot(id1);
        int root2 = findRoot(id2);
        if (root1 != root2) {
            tree.put(root2, root1);
            block--;
        }
        backup.add(new Pair<>(id1, id2));
    }

    public int findRoot(int id) {
        int father = tree.get(id);
        if (father == id) {
            return id;
        } else {
            int root = findRoot(father);
            tree.put(id, root);
            return root;
        }
    }

    public boolean isCircle(int id1, int id2) {
        return findRoot(id1) == findRoot(id2);
    }

    public int getBlock() {
        return this.block;
    }

    public void modifyTree(int id1, int id2) {
        backup.removeIf(p -> (p.getKey() == id1 && p.getValue() == id2)
                || p.getKey() == id2 && p.getValue() == id1);
        block = 0;
        for (Integer id : list) {
            tree.put(id, id);
            block++;
        }
        Iterator<Pair<Integer, Integer>> iter = backup.iterator();
        while (iter.hasNext()) {
            Pair<Integer, Integer> pair = iter.next();
            int root1 = findRoot(pair.getKey());
            int root2 = findRoot(pair.getValue());
            if (root1 != root2) {
                tree.put(root2, root1);
                block--;
            }
        }
    }

    public List<Integer> sameBlock(int id) {
        List<Integer> retList = new ArrayList<>();
        int root1 = findRoot(id);
        retList.add(id);
        for (Integer i : tree.keySet()) {
            int root2 = findRoot(i);
            if (root1 == root2) {
                retList.add(i);
            }
        }
        return retList;
    }
}
