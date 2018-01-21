package com.github.davidmoten.rtree3d;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import com.github.davidmoten.rtree3d.geometry.Box;

public class GreekEarthquakes {

    static List<Entry<Object>> entriesList() {
        List<Entry<Object>> result = new ArrayList<>();
        try {
            GZIPInputStream inputStream = new GZIPInputStream(GreekEarthquakes.class
                    .getResourceAsStream("/greek-earthquakes-1964-2000.txt.gz"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            for (String line; (line = reader.readLine()) != null;) {
                if (line.trim().length() > 0) {
                    String[] items = line.split(" ");
                    double lat = Double.parseDouble(items[0]);
                    double lon = Double.parseDouble(items[1]);
                    result.add(Entry.entry(new Object(), Box.create(lat, lon, 0, lat, lon, 0)));
                }
            }

            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
