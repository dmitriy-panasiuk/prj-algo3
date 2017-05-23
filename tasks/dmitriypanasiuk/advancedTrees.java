package dmitriypanasiuk;

import com.jayway.jsonpath.JsonPath;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class advancedTrees {
    public static void readPolygons(String filename) throws IOException {
        File file = new File(advancedTrees.class.getResource(filename).getFile());
        List<String> features = JsonPath.read(file, "$.features");
        System.out.println(features);
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
