import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHashing {
    private int numberOfNodes;
    private int numberOfReplicas;
    private SortedMap<Integer, String> circle = new TreeMap<>();
}
