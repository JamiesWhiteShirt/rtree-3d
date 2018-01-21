package com.github.davidmoten.rtree3d;


import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import org.junit.Test;

public class RTree3DTest {

    @Test
    public void createShuffle() throws FileNotFoundException {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < 38377; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        PrintStream out = new PrintStream("target/order.txt");
        for (int i : list) {
            out.println(i);
        }
        out.close();
    }

    public static void main(String[] args) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println(sdf.parse("2014-01-01T12:00:00Z").getTime());
    }

}
