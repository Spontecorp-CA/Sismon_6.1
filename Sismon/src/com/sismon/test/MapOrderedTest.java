package com.sismon.test;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author jgcastillo
 */
public class MapOrderedTest {
    public static void main(String[] args) {
        Map<Integer, String> map = new TreeMap<>();

        // Add Items to the TreeMap
        map.put(3, "Three");
        map.put(5, "Cinco");
        map.put(2, "Two");
        map.put(1, "One");
        map.put(4, "Cuatro");

        // Iterate over them
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " => " + entry.getValue());
        }
    }
    
}
