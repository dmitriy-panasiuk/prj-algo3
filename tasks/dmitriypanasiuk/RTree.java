package dmitriypanasiuk;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

public class RTree {
    Node root;
    public static final int MAX_CHILDREN = 4;
    private int size;
    public static List<Combination> combinations = generateCombinations();

    public RTree() {
        size = 0;
    }

    public List<Entry> search(Point point) {
        List<Entry> result = new ArrayList<>();
        search(this.root, point, result);
        return result;
    }

    private void search(Node root, Point point, List<Entry> result) {
        if (root.isLeaf()) {
            Leaf r = (Leaf)root;
            for (Entry e : r.entries()) {
                if (e.mbr().contains(point)) {
                    result.add(e);
                }
            }
        } else {
            NonLeaf r = (NonLeaf)root;
            for (Node n : r.children()) {
                if (n.mbr().contains(point)) {
                    search(n, point, result);
                }
            }
        }
    }

    public void insert(String value, Rectangle r) {
        Entry e = new Entry(value, r);
        size++;
        if (root == null) {
            List<Entry> list = new ArrayList<>();
            list.add(e);
            root = new Leaf(list, null);
            return;
        }
        //find position for a new entry
        Leaf l = chooseLeaf(root, r);
        //insert entry to leaf and split if needed
        if (l.count() + 1 <= MAX_CHILDREN) {
            l.entries().add(e);
        } else {
            List<Entry> newList = new ArrayList<>(l.entries());
            newList.add(e);
            NonLeaf newNode = splitNode(newList, l.parent());
            if (l.parent() == null) {
                this.root = newNode;
            }
        }
    }

    private Leaf chooseLeaf(Node root, Rectangle r) {
        if (root.isLeaf()) {
            return (Leaf)root;
        } else {
            NonLeaf root2 = (NonLeaf) root;
            double minEnlargementArea = Double.POSITIVE_INFINITY;
            Node subTree = null;
            for (Node n : root2.children()) {
                double enlargementArea = n.mbr().add(r).area() - n.mbr().area();
                if (minEnlargementArea > enlargementArea) {
                    minEnlargementArea = enlargementArea;
                    subTree = n;
                }
            }
            return chooseLeaf(subTree,r);
        }
    }

    private NonLeaf splitNode(List<Entry> l, Node parent) {
        double minTotalArea = Double.POSITIVE_INFINITY;
        double totalArea = 0;
        Combination bestCombo = null;
        for (Combination c : combinations) {
            Rectangle first = null;
            Rectangle second = null;
            for (int i : c.first) {
                if (first == null) {
                    first = l.get(i).mbr();
                } else {
                    first = first.add(l.get(i).mbr());
                }
            }
            for (int j : c.second) {
                if (second == null) {
                    second = l.get(j).mbr();
                } else {
                    second = second.add(l.get(j).mbr());
                }
            }
            totalArea = first.area() + second.area();
            if (totalArea < minTotalArea) {
                minTotalArea = totalArea;
                bestCombo = c;
            }
        }
        NonLeaf result = new NonLeaf(new ArrayList<>(), parent);
        Leaf f = new Leaf(new ArrayList<>(), result);
        for (int i : bestCombo.first) {
            f.entries().add(l.get(i));
        }
        Leaf s = new Leaf(new ArrayList<>(), result);
        for (int i : bestCombo.second) {
            s.entries().add(l.get(i));
        }
        result.children().add(f);
        result.children().add(s);
        return result;
    }

    static List<Combination> generateCombinations() {
        List<Combination> result = new ArrayList<>();
        /*result.add(new Combination(new int[]{0},new int[]{1,2,3,4}));
        result.add(new Combination(new int[]{1},new int[]{0,2,3,4}));
        result.add(new Combination(new int[]{2},new int[]{0,1,3,4}));
        result.add(new Combination(new int[]{3},new int[]{0,1,2,4}));
        result.add(new Combination(new int[]{4},new int[]{0,1,2,3}));*/
        result.add(new Combination(new int[]{0,1},new int[]{2,3,4}));
        result.add(new Combination(new int[]{0,2},new int[]{1,3,4}));
        result.add(new Combination(new int[]{0,3},new int[]{1,2,4}));
        result.add(new Combination(new int[]{0,4},new int[]{1,2,3}));
        result.add(new Combination(new int[]{1,2},new int[]{0,3,4}));
        result.add(new Combination(new int[]{1,3},new int[]{0,2,4}));
        result.add(new Combination(new int[]{1,4},new int[]{0,2,3}));
        result.add(new Combination(new int[]{2,3},new int[]{0,1,4}));
        result.add(new Combination(new int[]{2,4},new int[]{0,1,3}));
        result.add(new Combination(new int[]{3,4},new int[]{0,1,2}));

        return result;
    }
}

abstract class Node implements hasMBR {
    abstract List<Node> add(Entry e, int min, int max);
    abstract int count();
    abstract boolean isLeaf();
    abstract Node parent();
    public Rectangle mbr(List<? extends hasMBR> entries) {
        double minX1 = Double.POSITIVE_INFINITY;
        double minY1 = Double.POSITIVE_INFINITY;
        double maxX2 = Double.NEGATIVE_INFINITY;
        double maxY2 = Double.NEGATIVE_INFINITY;

        for (hasMBR geometry : entries) {
            Rectangle r = geometry.mbr();
            if (r.x1 < minX1) {
                minX1 = r.x1;
            }
            if (r.y1 < minY1) {
                minY1 = r.y1;
            }
            if (r.x2 > maxX2) {
                maxX2 = r.x2;
            }
            if (r.y2 > maxY2) {
                maxY2 = r.y2;
            }
        }

        return new Rectangle(minX1, minY1, maxX2, maxY2);
    }
}

class Leaf extends Node {
    private List<Entry> entries;
    private Node parent;

    @Override
    public Node parent() {
        return this.parent;
    }

    Leaf(List<Entry> entries, Node parent) {
        this.entries = entries;
        this.parent = parent;
    }

    public boolean isLeaf() {
        return true;
    }

    List<Entry> entries() {
        return entries;
    }

    @Override
    List<Node> add(Entry e, int min, int max) {
        /*if (this.entries.size()+1 <= max) {
            List<Entry> newEntries = new ArrayList<>(entries);
            newEntries.add(e);
            return Arrays.asList(new Leaf(newEntries));
        } else {
            //split
            throw new RuntimeException();
        }*/
        throw new NotImplementedException();
    }

    @Override
    int count() {
        return entries.size();
    }

    @Override
    public Rectangle mbr() {
        return super.mbr(this.entries);
    }
}

class NonLeaf extends Node{
    private List<Node> children;
    private Node parent;

    @Override
    public Node parent() {
        return this.parent;
    }

    NonLeaf(List<Node> children, Node parent) {
        this.children = children;
        this.parent = parent;
    }

    public boolean isLeaf() {
        return false;
    }

    List<Node> children() {
        return children;
    }
    @Override
    List<Node> add(Entry e, int min, int max) {
        throw new RuntimeException();
    }

    @Override
    int count() {
        return children.size();
    }

    @Override
    public Rectangle mbr() {
        return super.mbr(this.children);
    }
}

class Entry implements hasMBR {
    private String value;
    private Rectangle r;

    Entry(String value, Rectangle r) {
        this.value = value;
        this.r = r;
    }

    public String value() {
        return value;
    }

    @Override
    public Rectangle mbr() {
        return this.r;
    }
}

class Combination {
    int[] first;
    int[] second;
    Combination(int[] first, int[] second) {
        this.first = first;
        this.second = second;
    }
}

interface hasMBR {
    Rectangle mbr();
}
