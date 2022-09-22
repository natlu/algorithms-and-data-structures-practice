/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class SAP {

    private final Digraph G;
    private final HashMap<Integer, HashMap<Integer, Integer>> lengthCache;
    private final HashMap<Integer, HashMap<Integer, Integer>> ancestorCache;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException("argument G cannot be null");
        }
        this.G = G;
        lengthCache = new HashMap<>();
        ancestorCache = new HashMap<>();
    }

    private int getHashCode(Iterable<Integer> it) {
        HashSet<Integer> hs = new HashSet<>();
        for (Integer i : it) {
            hs.add(i);
        }
        ArrayList<Integer> al = new ArrayList<>();
        al.addAll(hs);
        Collections.sort(al);
        return al.hashCode();
    }

    private Integer queryCache(HashMap<Integer, HashMap<Integer, Integer>> cache,
                               Iterable<Integer> vs,
                               Iterable<Integer> ws) {
        int vhash = getHashCode(vs);
        int whash = getHashCode(ws);

        int x;
        int y;
        if (vhash <= whash) {
            x = vhash;
            y = whash;
        }
        else {
            x = whash;
            y = vhash;
        }

        if (!cache.containsKey(x)) {
            cache.put(x, new HashMap<>());
            return null;
        }
        return cache.get(x).get(y);
    }

    private void cache(Iterable<Integer> vs, Iterable<Integer> ws, int minLen, int ancestor) {
        int vhash = getHashCode(vs);
        int whash = getHashCode(ws);

        int x;
        int y;
        if (vhash <= whash) {
            x = vhash;
            y = whash;
        }
        else {
            x = whash;
            y = vhash;
        }

        lengthCache.get(x).put(y, minLen);
        ancestorCache.get(x).put(y, ancestor);
    }

    private void validateVertex(int v) {
        if (v < 0 || v >= G.V())
            throw new IllegalArgumentException(
                    "vertex " + v + " is not between 0 and " + (G.V() - 1));
    }

    private void validateVertices(Iterable<Integer> vertices) {
        if (vertices == null) {
            throw new IllegalArgumentException("argument is null");
        }
        for (Integer v : vertices) {
            if (v == null) {
                throw new IllegalArgumentException("vertex is null");
            }
            validateVertex(v);
        }
    }

    private void calc(Iterable<Integer> vs, Iterable<Integer> ws) {
        Integer resLength = queryCache(lengthCache, vs, ws);
        Integer resAncestor = queryCache(ancestorCache, vs, ws);
        if (resLength != null && resAncestor != null) {
            // return because already calculated this
            return;
        }

        BreadthFirstDirectedPaths vg = new BreadthFirstDirectedPaths(G, vs);
        BreadthFirstDirectedPaths wg = new BreadthFirstDirectedPaths(G, ws);

        Queue<Integer> lst = new Queue<>();
        // keep track of what has been put in the queue to avoid duplicates
        boolean[] enqueued = new boolean[G.V()];

        for (int v : vs) {
            lst.enqueue(v);
            enqueued[v] = true;
        }
        for (int w : ws) {
            lst.enqueue(w);
            enqueued[w] = true;
        }

        int len = Integer.MAX_VALUE;
        int ancestor = -1;

        while (!lst.isEmpty()) {
            Integer i = lst.dequeue();
            if (vg.hasPathTo(i) && wg.hasPathTo(i)) {
                int vd = vg.distTo(i);
                int wd = wg.distTo(i);
                // if (vd >= len || wd >= len) {
                //     break;
                // }
                if ((vd + wd) < len) {
                    len = vd + wd;
                    ancestor = i;
                }
            }
            for (int j : G.adj(i)) {
                if (!enqueued[j]) {
                    lst.enqueue(j);
                    enqueued[j] = true;
                }
            }
        }

        if (len == Integer.MAX_VALUE) {
            len = -1;
        }
        cache(vs, ws, len, ancestor);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        List<Integer> vs = new ArrayList<>();
        vs.add(v);
        List<Integer> ws = new ArrayList<>();
        ws.add(w);
        calc(vs, ws);
        return queryCache(lengthCache, vs, ws);
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        List<Integer> vs = new ArrayList<>();
        vs.add(v);
        List<Integer> ws = new ArrayList<>();
        ws.add(w);
        calc(vs, ws);
        return queryCache(ancestorCache, vs, ws);
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        validateVertices(v);
        validateVertices(w);
        calc(v, w);
        return queryCache(lengthCache, v, w);
    }

    // // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        validateVertices(v);
        validateVertices(w);
        calc(v, w);
        return queryCache(ancestorCache, v, w);
    }

    public static void main(String[] args) {
        In in = new In("digraph3.txt");
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);

        int x;
        int y;

        x = 13;
        y = 9;
        System.out.println(sap.length(x, y));
        // StdOut.printf("length = %d ancestor = %d\n", sap.length(x, y), sap.ancestor(x, y));

        // x = 11;
        // y = 3;
        // StdOut.printf("length = %d ancestor = %d\n", sap.length(x, y), sap.ancestor(x, y));
        //
        // x = 9;
        // y = 12;
        // StdOut.printf("length = %d ancestor = %d\n", sap.length(x, y), sap.ancestor(x, y));
        //
        // x = 7;
        // y = 2;
        // StdOut.printf("length = %d ancestor = %d\n", sap.length(x, y), sap.ancestor(x, y));
        //
        // x = 1;
        // y = 6;
        // StdOut.printf("length = %d ancestor = %d\n", sap.length(x, y), sap.ancestor(x, y));
        //
        //
        // // https://docs.oracle.com/javase/6/docs/api/java/util/List.html#hashCode()
        // List<Integer> foo1 = new ArrayList<>();
        // foo1.add(1);
        // foo1.add(2);
        // foo1.add(3);
        //
        // List<Integer> foo2 = new ArrayList<>();
        // foo2.add(1);
        // foo2.add(2);
        // foo2.add(3);
        //
        // boolean iseq = foo1.hashCode() == foo2.hashCode();
        // System.out.println(iseq);
        //
        // System.out.println(foo1.hashCode());
        //
        // List<Integer> foo0 = new ArrayList<>();
        // foo0.add(2);
        // System.out.println(foo0.hashCode());
        //
        //
        // HashSet<Integer> hs = new HashSet<>();
        // hs.add(1);
        // hs.add(2);
        //
        // HashSet<Integer> hs2 = new HashSet<>();
        // hs2.add(3);
        //
        // boolean hshs = hs.hashCode() == hs.hashCode();
        // System.out.println(hshs);
        // System.out.println(hs.hashCode());
        // System.out.println(hs2.hashCode());


    }
}
