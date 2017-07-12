package dmitriypanasiuk.advancedhashing;

import dmitriypanasiuk.util.FNV1a;
import dmitriypanasiuk.util.FloydWarshall;
import dmitriypanasiuk.util.StopWatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MinimalPerfectHashing {
    private Double[] values;
    private int[] G;

    public MinimalPerfectHashing(Map<String, Double> dict) {
        int size = dict.size();
        values = new Double[size];
        G = new int[size];
        construction(dict);
    }

    public Double evaluate(String key) {
        int hash = Math.abs(hash(0, key) % G.length);
        if (G[hash] > 0) {
            hash = Math.abs(hash(G[hash], key) % G.length);
        } else {
            hash = -G[hash] - 1;
        }
        return values[hash];
    }

    private void construction(Map<String, Double> dict) {
        ArrayList<String>[] buckets = new ArrayList[dict.size()];
        initArray(buckets);
        step1CHD(dict, buckets);
        arraySort(buckets);
        int i = 0;
        i = step2CHD(dict, buckets, i);
        Queue<Integer> freelist = new ArrayDeque<>();
        for (int j = 0; j < values.length; j++) {
            if (values[j] == null) freelist.add(j);
        }
        for (; i < buckets.length; i++) {
            ArrayList<String> bucket = buckets[i];
            if (bucket.size() == 0) break;
            int slot = freelist.poll();
            G[Math.abs(hash(0, bucket.get(0)) % G.length)] = - slot - 1;
            values[slot] = dict.get(bucket.get(0));
        }

    }

    private int step2CHD(Map<String, Double> dict, ArrayList<String>[] buckets, int i) {
        for (; i < buckets.length; i++) {
            ArrayList<String> bucket = buckets[i];
            if (bucket.size() <= 1) break;
            int d = 1;
            int item = 0;
            ArrayList<Integer> slots = new ArrayList<>();
            while (item < bucket.size()) {
                int hash = Math.abs(hash(d, bucket.get(item)) % buckets.length);
                if (values[hash] != null || slots.contains(hash)) {
                    item = 0;
                    slots = new ArrayList<>();
                    d++;
                } else {
                    slots.add(hash);
                    item++;
                }
            }
            //System.out.println("i = " + i + " Slots.size = " + slots.size() + " d = " + d);
            G[Math.abs(hash(0, bucket.get(0)) % G.length)] = d;
            for (int j = 0; j < bucket.size(); j++) {
                values[slots.get(j)] = dict.get(bucket.get(j));
            }
        }
        return i;
    }

    private void arraySort(ArrayList<String>[] buckets) {
        Arrays.sort(buckets, (o1, o2) -> {
            if (o1.size() > o2.size()) return -1;
            if (o1.size() < o2.size()) return 1;
            return 0;
        });
    }

    private void step1CHD(Map<String, Double> dict, ArrayList<String>[] buckets) {
        for (String key : dict.keySet()) {
            int hash = hash(0, key);
            hash %= buckets.length;
            buckets[Math.abs(hash)].add(key);
        }
    }

    private void initArray(ArrayList<String>[] buckets) {
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new ArrayList<>();
        }
    }

    private int hash(int d, String str) {
        int hash = d == 0 ? 0x811c9dc5 : d;
        byte[] data = str.getBytes();
        return FNV1a.hash32(data, data.length, hash);
    }

    private static Map<String, Double> readFreqsFromFile(String path) {
        Map<String, Double> words = new HashMap<>();
        File file = new File(MinimalPerfectHashing.class.getResource(path).getFile());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] entry = line.split("\t");
                words.put(entry[0], Double.parseDouble(entry[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return words;
    }

    private class Node {
        private Node next;
        private String key;
        Node(String key, Node next) {
            this.key = key;
            this.next = next;
        }
    }

    private static void checkCorrectnessMPH(Map<String, Double> dict, MinimalPerfectHashing h) {
        for (Map.Entry<String, Double> e : dict.entrySet()) {
            String key = e.getKey();
            Double expectedValue = e.getValue();
            Double actualValue = h.evaluate(key);
            if (!actualValue.equals(expectedValue)) {
                System.out.println("Key = " + key + " expected " + expectedValue + " but found " + actualValue);
            }
        }
    }

    public static void main(String[] args) {
        Map<String, Double> dict = readFreqsFromFile("Words-1400k.csv");
        double runtime = 0.0;

        int N = 10;
        for (int i = 0; i < N; i++) {
            StopWatch clock1 = new StopWatch();
            MinimalPerfectHashing h = new MinimalPerfectHashing(dict);
            runtime += clock1.elapsedTime();
        }
        System.out.println("runtime average = " + runtime / N);
    }
}
