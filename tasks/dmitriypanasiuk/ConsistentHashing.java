package dmitriypanasiuk;

import dmitriypanasiuk.util.FNV1a;
import dmitriypanasiuk.util.RandomString;

import java.util.*;

public class ConsistentHashing {
    private int numberOfReplicas;
    private SortedMap<Integer, String> circle = new TreeMap<>();

    public ConsistentHashing(int numberOfReplicas, Collection<String> nodes) {
        this.numberOfReplicas = numberOfReplicas;
        for (String node : nodes) {
            add(node);
        }
    }

    public void add(String node) {
        Random r = new Random();
        for (int i = 0; i < numberOfReplicas; i++) {
            //circle.put(FNV1a.hash32((node + r.nextInt()).getBytes()), node);
            circle.put(FNV1a.hash32((node + i*i).getBytes()), node);
        }
    }

    public void remove(String node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.remove(FNV1a.hash32((node + i*i).getBytes()));
        }
    }

    public String get(String key) {
        if (circle.isEmpty()) {
            return null;
        }
        int hash = FNV1a.hash32(key.getBytes());
        if (!circle.containsKey(hash)) {
            SortedMap<Integer, String> tailMap = circle.tailMap(hash);
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        return circle.get(hash);
    }

    public static double std(Collection<Integer> c) {
        double mean = 1000.0;
        double std = 0.0;
        for (int i : c) {
            std += (i - mean) * (i - mean);
        }
        return (Math.sqrt(std / c.size()) / mean) * 100;
    }

    public static void main(String[] args) {
        int numberOfReplicas = 1000;
        int numberOfStrings = 10000;
        List<String> nodes = Arrays.asList("one", "two", "three", "four", "five", "six", "seven", "eighth", "nine", "ten");
        Map<String, Integer> counts = new HashMap<>();

        for (String node : nodes) {
            counts.put(node, 0);
        }
        ConsistentHashing hashing = new ConsistentHashing(numberOfReplicas, nodes);
        RandomString rString = new RandomString();
        Random r = new Random();
        for (int i = 0; i < numberOfStrings; i++) {
            String node = hashing.get(rString.nextString(2 + r.nextInt(14)));
            counts.put(node, counts.get(node) + 1);
        }
        System.out.println(std(counts.values()));
    }
}
