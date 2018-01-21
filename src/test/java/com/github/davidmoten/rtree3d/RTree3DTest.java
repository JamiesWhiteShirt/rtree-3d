package com.github.davidmoten.rtree3d;

import static org.junit.Assert.assertEquals;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.junit.Test;

import com.github.davidmoten.rtree3d.geometry.Box;
import com.github.davidmoten.rtree3d.geometry.Geometry;
import com.github.davidmoten.rtree3d.geometry.Point;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;

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

    @Test
    public void test() throws IOException {
        // load entries, calculate bounds and normalize entries

        List<Entry<Object, Point>> entries = getGreekEarthquake3DDataShuffled();

        final Box bounds = getBounds(entries);
        System.out.println(bounds);
        List<Entry<Object, Point>> normalized = normalize(entries, bounds);

        // create RTree
        int maxChildren = 4;
        RTree<Object, Point> tree = RTree.star().minChildren((maxChildren) / 2)
                .maxChildren(maxChildren).bounds(bounds).create();
        tree = tree.add(normalized);
        System.out.format("tree size=%s, depth=%s\\n", tree.size(), tree.calculateDepth());
        System.out.println(tree.asString(3));

        // try search on RTree
        long t = System.currentTimeMillis();
        int count = tree.search(
                Box.createNormalized(bounds, 39.0f, 22.0f, 0f, 40.0f, 23.0f, 3.15684946E11f))
                .size();
        t = System.currentTimeMillis() - t;
        System.out.println("search=" + count + " in " + t + "ms");
        // expect 118 records returned from search
        assertEquals(count, 118);

        // print out nodes as csv records for reading by R code and plotting
        for (int depth = 0; depth <= 10; depth++) {
            print(tree.root().get(), depth);
            System.out.println("depth file written " + depth);
        }

        System.out.println("finished");
    }

    private static List<Entry<Object, Point>> normalize(
            List<Entry<Object, Point>> entries, final Box bounds) {
        return entries.stream()
                .map(entry -> Entry.entry(entry.value(), bounds.normalize(entry.geometry())))
                .collect(Collectors.toList());
    }

    private static Box getBounds(List<Entry<Object, Point>> entries) {
        return entries.stream()
                .map(entry -> entry.geometry().mbb())
                .reduce((box, p) -> Util.mbr(Lists.newArrayList(box, p))).get();
    }

    private static List<Entry<Object, Point>> getGreekEarthquake3DDataShuffled()
            throws IOException {
        final List<String> indexes = CharStreams.readLines(new InputStreamReader(
                RTree3DTest.class.getResourceAsStream("/greek-earthquake-shuffle.txt")));

        GZIPInputStream inputStream = new GZIPInputStream(GreekEarthquakes.class
                    .getResourceAsStream("/greek-earthquakes-1964-2000-with-times.txt.gz"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        List<Entry<Object, Point>> allEntries = new ArrayList<>();
        for (String line; (line = reader.readLine()) != null;) {
            if (!line.startsWith("DATE")) {
                line = line.trim();
                if (line.length() > 0) {
                    String[] items = line.split("\t");
                    SimpleDateFormat sdf = new SimpleDateFormat(
                            "yyy-MM-dd'T'HH:mm:ss.s");
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    try {
                        long time = sdf.parse(items[0]).getTime();
                        float lat = Float.parseFloat(items[1]);
                        float lon = Float.parseFloat(items[2]);
                        allEntries.add(Entry.entry(null, Point.create(lat, lon, time)));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        inputStream.close();

        List<Entry<Object, Point>> entries = new ArrayList<>();
        for (String index : indexes) {
            entries.add(allEntries.get(Integer.parseInt(index)));
        }
        return entries;
    }

    private static <T extends Geometry> void print(Node<Object, T> node, int depth)
            throws FileNotFoundException {

        PrintStream out = new PrintStream("target/out" + depth + ".txt");
        print(node, out, depth, depth);
        out.close();

    }

    private static <T extends Geometry> void print(Node<Object, T> node, PrintStream out,
            int minDepth, int maxDepth) {
        print(node, out, 0, minDepth, maxDepth);
    }

    private static <T extends Geometry> void print(Node<Object, T> node, PrintStream out, int depth,
            int minDepth, int maxDepth) {
        if (depth > maxDepth) {
            return;
        }
        if (node instanceof NonLeaf) {
            NonLeaf<Object, T> n = (NonLeaf<Object, T>) node;
            Box b = node.geometry().mbb();
            if (depth >= minDepth)
                print(b, out);
            for (Node<Object, T> child : n.children()) {
                print(child, out, depth + 1, minDepth, maxDepth);
            }
        } else if (node instanceof Leaf && depth >= minDepth) {
            Leaf<Object, T> n = (Leaf<Object, T>) node;
            print(n.geometry().mbb(), out);
        }
    }

    private static void print(Box b, PrintStream out) {
        out.format("%s,%s,%s,%s,%s,%s\n", b.x1(), b.y1(), b.z1(), b.x2(), b.y2(), b.z2());
    }

    private static long time(String isoDateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return sdf.parse(isoDateTime).getTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println(sdf.parse("2014-01-01T12:00:00Z").getTime());
    }

}
