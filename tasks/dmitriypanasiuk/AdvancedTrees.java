package dmitriypanasiuk;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.w3c.dom.css.Rect;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AdvancedTrees {
    public static void readPolygons(String filename) throws IOException {
        File file = new File(AdvancedTrees.class.getResource(filename).getFile());
        JSONArray features = JsonPath.read(file, "$.features");
        for (Object o : features) {
            LinkedHashMap map = (LinkedHashMap)o;
            LinkedHashMap properties = (LinkedHashMap) map.get("properties");
            System.out.println(properties.get("NAME"));
            LinkedHashMap geometry = (LinkedHashMap) map.get("geometry");
            JSONArray c = (JSONArray) ((JSONArray) ((JSONArray) geometry.get("coordinates")).get(0)).get(0);
            for (Object coords : c) {
                System.out.println(coords);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        readPolygons("london.geojson");
        /*RTree<String, Rectangle> tree = RTree.create();
        tree = tree.add("first", Geometries.rectangle(0,0,4,4));
        tree = tree.add("second", Geometries.rectangle(2,2,6,6));
        tree = tree.add("third", Geometries.rectangle(0,2,4,4));

        Observable<Entry<String, Rectangle>> results = tree.search(Geometries.point(5, 5));
        results.map(Entry::value).subscribe(System.out::println);*/
    }
}

class Point {
    double x, y;
    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
}

class Polygon {
    private List<Point> borders = new ArrayList<>();
    private Rectangle mbr;

    Polygon(List<Point> borders) {
        this.borders = borders;
    }

    boolean contains(Point p) {
        int intersections = 0;

        for (int side = 0; side < borders.size() - 1; side++) {
            if (areIntersecting(p.x, p.y, mbr().x1, mbr().y1,
                                borders.get(side).x, borders.get(side).y,
                                borders.get(side+1).x, borders.get(side+1).y)) {
                intersections++;
            }
        }
        if (areIntersecting(p.x, p.y, mbr().x1, mbr().y1,
                borders.get(0).x, borders.get(0).y,
                borders.get(borders.size()-1).x, borders.get(borders.size()-1).y)) {
            intersections++;
        }
        return (intersections & 1) == 1;
    }

    private boolean areIntersecting(double v1x1, double v1y1, double v1x2, double v1y2,
                                    double v2x1, double v2y1, double v2x2, double v2y2) {
        double d1, d2;
        double a1, a2, b1, b2, c1, c2;
        a1 = v1y2 - v1y1;
        b1 = v1x1 - v1x2;
        c1 = (v1x2 * v1y1) - (v1x1 * v1y2);

        d1 = (a1 * v2x1) + (b1 * v2y1) + c1;
        d2 = (a1 * v2x2) + (b1 * v2y2) + c1;

        if (d1 > 0 && d2 > 0) return false;
        if (d1 < 0 && d2 < 0) return false;

        a2 = v2y2 - v2y1;
        b2 = v2x1 - v2x2;
        c2 = (v2x2 * v2y1) - (v2x1 * v2y2);

        d1 = (a2 * v1x1) + (b2 * v1y1) + c2;
        d2 = (a2 * v1x2) + (b2 * v1y2) + c2;

        if (d1 > 0 && d2 > 0) return false;
        if (d1 < 0 && d2 < 0) return false;

        return true;
    }

    Rectangle mbr() {
        if (this.mbr == null) {
            double xmin = Double.POSITIVE_INFINITY, xmax = Double.NEGATIVE_INFINITY, ymin = Double.POSITIVE_INFINITY, ymax = Double.NEGATIVE_INFINITY;
            for (Point p : borders) {
                if (p.x < xmin) xmin = p.x;
                if (p.x > xmax) xmax = p.x;
                if (p.y < ymin) ymin = p.y;
                if (p.y > ymax) ymax = p.y;
            }
            this.mbr = new Rectangle(xmin, xmax, ymin, ymax);
        }
        return this.mbr;
    }
}

class Rectangle {
    double x1, x2, y1, y2;

    Rectangle(double x1, double x2, double y1, double y2) {
        assert x1 < x2;
        assert y1 < y2;
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    public boolean contains(Point p) {
        if (x1 <= p.x && x2 >= p.x && y1 <= p.y && y2 >= p.y) return true;
        return false;
    }
}
