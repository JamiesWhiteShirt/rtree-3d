package com.github.davidmoten.rtree3d;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.github.davidmoten.rtree3d.geometry.Box;

public class Utilities {

    static List<Entry<Object>> entries1000() {
        List<Entry<Object>> list = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                Utilities.class.getResourceAsStream("/1000.txt")));
        String line;
        try {
            while ((line = br.readLine()) != null) {
                String[] items = line.split(" ");
                int x = (int) Double.parseDouble(items[0]);
                int y = (int) Double.parseDouble(items[1]);
                list.add(Entry.entry(new Object(), Box.create(x, y, 0, x + 1, y + 1, 1)));
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

}
