package dmitriypanasiuk;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdvancedTrees {
    public static Map<String, Polygon> readPolygons(String filename) throws IOException {
        Map<String, Polygon> polygons = new HashMap<>();
        File file = new File(AdvancedTrees.class.getResource(filename).getFile());
        JSONArray features = JsonPath.read(file, "$.features");
        for (Object o : features) {
            LinkedHashMap map = (LinkedHashMap)o;
            LinkedHashMap properties = (LinkedHashMap) map.get("properties");
            String polygonName = (String)properties.get("NAME");
            LinkedHashMap geometry = (LinkedHashMap) map.get("geometry");
            JSONArray c = (JSONArray) ((JSONArray) ((JSONArray) geometry.get("coordinates")).get(0)).get(0);
            List<Point> borders = new ArrayList<>();
            for (Object coords : c) {
                double x = (double)((List)coords).get(0);
                double y = (double)((List)coords).get(1);
                borders.add(new Point(x, y));
            }
            polygons.put(polygonName, new Polygon(borders));
        }

        return polygons;
    }

    public static void bruteForce(Map<String, Polygon> polygons, Point p) {
        for (Map.Entry<String, Polygon> entry : polygons.entrySet()) {
            if (entry.getValue().contains(p)) {
                //System.out.println(entry.getKey());
                return;
            }
        }
        System.out.println("Outside of London");
    }

    public static void optimizedBruteForce(Map<String, Polygon> polygons, Point p) {
        for (Map.Entry<String, Polygon> entry : polygons.entrySet()) {
            if (entry.getValue().mbr().contains(p) && entry.getValue().contains(p)) {
                //System.out.println(entry.getKey());
                return;
            }
        }
        System.out.println("Outside of London");
    }

    public static void main(String[] args) throws IOException {
        int N = 10000;
        Point londonCityCenter = new Point(-0.118092, 51.509865);
        Point bigBen = new Point(-0.1246, 51.5007);
        Point tower = new Point(-0.076111, 51.508056);
        Point zoo = new Point(-0.155833, 51.535556);
        Point stPaulCathedral = new Point(-0.098056, 51.513611);
        Point FairfieldHalls = new Point(-0.095833, 51.372222);
        Map<String, Polygon> polygons = readPolygons("london.geojson");
        StopWatch clock = new StopWatch();
        for (int i = 0; i < N; i++) {
            bruteForce(polygons, FairfieldHalls);
        }
        System.out.println(clock.elapsedTime());
        clock = new StopWatch();
        for (int i = 0; i < N; i++) {
            optimizedBruteForce(polygons, FairfieldHalls);
        }

        System.out.println(clock.elapsedTime());

        /*RTree<String, Rectangle> tree = RTree.create();
        tree = tree.add("first", Geometries.rectangle(0,0,4,4));
        tree = tree.add("second", Geometries.rectangle(2,2,6,6));
        tree = tree.add("third", Geometries.rectangle(0,2,4,4));

        Observable<Entry<String, Rectangle>> results = tree.search(Geometries.point(5, 5));
        results.map(Entry::value).subscribe(System.out::println);*/
        /*List<Point> p1 = Arrays.asList(new Point(0, 3), new Point(1, 4), new Point(2, 6), new Point(3, 4),
                                           new Point(2, 2), new Point(1, 1));
        List<Point> p2 = Arrays.asList(new Point(2, 6), new Point(5, 6), new Point(3, 4));
        List<Point> p3 = Arrays.asList(new Point(3, 4), new Point(5, 6), new Point(7, 3), new Point(5, 1));
        List<Point> p4 = Arrays.asList(new Point(1, 1), new Point(2, 2), new Point(3, 4), new Point(5, 1), new Point(3, 0));
        Polygon poly1 = new Polygon(p1);
        Polygon poly2 = new Polygon(p2);
        Polygon poly3 = new Polygon(p3);
        Polygon poly4 = new Polygon(p4);
        Point pp = new Point(1.2,1.1);*/

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
                /*System.out.print("(" + p.x + "," + p.y + ") - (" + mbr().x1 + "," + mbr().y1 + ")");
                System.out.println(" intersects with (" + borders.get(side).x + "," + borders.get(side).y + ") - (" + borders.get(side+1).x + "," + borders.get(side+1).y + ")");*/
                intersections++;
            }
        }
        if (areIntersecting(p.x, p.y, mbr().x1, mbr().y1,
                borders.get(borders.size()-1).x, borders.get(borders.size()-1).y,
                borders.get(0).x, borders.get(0).y)) {
            /*System.out.print("(" + p.x + "," + p.y + ") - (" + mbr().x1 + "," + mbr().y1 + ")");
            System.out.println(" intersects with (" + borders.get(borders.size()-1).x + "," + borders.get(borders.size()-1).y + ") - (" + borders.get(0).x + "," + borders.get(0).y + ")");*/
            intersections++;
        }
        //System.out.println("intersections " + intersections);
        return (intersections & 1) == 1;
    }

    public boolean areIntersecting(double v1x1, double v1y1, double v1x2, double v1y2,
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
            this.mbr = new Rectangle(xmin, ymin, xmax, ymax);
        }
        return this.mbr;
    }
}

class Rectangle {
    double x1, x2, y1, y2;

    @Override
    public String toString() {
        return "Rectangle{" +
                "x1=" + x1 +
                ", y1=" + y1 +
                ", x2=" + x2 +
                ", y2=" + y2 +
                '}';
    }

    Rectangle(double x1, double y1, double x2, double y2) {
        if (!(x1 < x2 && y1 < y2)) throw new RuntimeException("Invalid rectangle");
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public boolean contains(Point p) {
        if (x1 <= p.x && x2 >= p.x && y1 <= p.y && y2 >= p.y) return true;
        return false;
    }
}
