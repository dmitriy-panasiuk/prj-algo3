package dmitriypanasiuk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RTree {

}

abstract class Node implements hasMBR {
    abstract List<Node> add(Entry e, int min, int max);
    abstract int count();

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

    Leaf(List<Entry> entries) {
        this.entries = entries;
    }

    List<Entry> entries() {
        return entries;
    }

    @Override
    List<Node> add(Entry e, int min, int max) {
        if (this.entries.size()+1 <= max) {
            List<Entry> newEntries = new ArrayList<>(entries);
            newEntries.add(e);
            return Arrays.asList(new Leaf(newEntries));
        } else {
            //split
            throw new RuntimeException();
        }
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

    NonLeaf(List<Node> children) {
        this.children = children;
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

interface hasMBR {
    Rectangle mbr();
}
