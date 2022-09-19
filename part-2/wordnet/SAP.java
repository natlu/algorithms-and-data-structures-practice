/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class SAP {

    private Digraph G;
    private HashMap<Integer, HashMap<Integer, Integer>> lengthCache;
    private HashMap<Integer, HashMap<Integer, Integer>> ancestorCache;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        this.G = G;
        lengthCache = new HashMap<>();
        ancestorCache = new HashMap<>();
    }

    private void cache(int v, int w, int minLen, int ancestor) {
        int x;
        int y;
        if (v <= w) {
            x = v;
            y = w;
        }
        else {
            x = w;
            y = v;
        }
        lengthCache.get(x).put(y, minLen);
        ancestorCache.get(x).put(y, ancestor);
    }

    private int getHashCode(Iterable<Integer> it) {
        HashSet<Integer> hs = new HashSet<>((Collection) it);
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

    public void calc(Iterable<Integer> vs, Iterable<Integer> ws) {
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
                if (vd >= len || wd >= len) {
                    break;
                }
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

    private Integer getLengthCache(int v, int w) {
        int x;
        int y;
        if (v <= w) {
            x = v;
            y = w;
        }
        else {
            x = w;
            y = v;
        }

        if (!lengthCache.containsKey(x)) {
            // System.out.println("length cache doesnt exist");
            lengthCache.put(x, new HashMap<>());
            return null;
        }
        // System.out.println("reading length cache");
        return lengthCache.get(x).get(y);
    }

    private Integer getAncestorCache(int v, int w) {
        int x;
        int y;
        if (v <= w) {
            x = v;
            y = w;
        }
        else {
            x = w;
            y = v;
        }

        if (!ancestorCache.containsKey(x)) {
            // System.out.println("ancestor cache doesnt exist");
            ancestorCache.put(x, new HashMap<>());
            return null;
        }
        // System.out.println("reading ancestor cache");
        return ancestorCache.get(x).get(y);
    }

    public void calc(int v, int w) {
        Integer resLength = getLengthCache(v, w);
        Integer resAncestor = getAncestorCache(v, w);
        if (resLength != null && resAncestor != null) {
            // return because already calculated this
            return;
        }

        BreadthFirstDirectedPaths vg = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths wg = new BreadthFirstDirectedPaths(G, w);

        Queue<Integer> lst = new Queue<>();
        lst.enqueue(v);
        lst.enqueue(w);

        // keep track of what has been put in the queue to avoid duplicates
        boolean[] enqueued = new boolean[G.V()];
        enqueued[v] = true;
        enqueued[w] = true;

        int len = Integer.MAX_VALUE;
        int ancestor = -1;

        while (!lst.isEmpty()) {
            Integer i = lst.dequeue();
            if (vg.hasPathTo(i) && wg.hasPathTo(i)) {
                int vd = vg.distTo(i);
                int wd = wg.distTo(i);
                if (vd >= len || wd >= len) {
                    break;
                }
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
        cache(v, w, len, ancestor);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        // System.out.println("getting length cache");
        calc(v, w);
        return getLengthCache(v, w);
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        // System.out.println("getting ancestor cache");
        calc(v, w);
        return getAncestorCache(v, w);
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        calc(v, w);
        return queryCache(ancestorCache, v, w);
    }

    // // a common ancestor that participates in shortest ancestral path; -1 if no such path
    // public int ancestor(Iterable<Integer> v, Iterable<Integer> w)

    public static void main(String[] args) {
        In in = new In("digraph1.txt");
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);

        int x;
        int y;

        x = 3;
        y = 11;
        StdOut.printf("length = %d ancestor = %d\n", sap.length(x, y), sap.ancestor(x, y));
        StdOut.printf("length = %d ancestor = %d\n", sap.length(x, y), sap.ancestor(x, y));

        x = 11;
        y = 3;
        StdOut.printf("length = %d ancestor = %d\n", sap.length(x, y), sap.ancestor(x, y));

        x = 9;
        y = 12;
        StdOut.printf("length = %d ancestor = %d\n", sap.length(x, y), sap.ancestor(x, y));

        x = 7;
        y = 2;
        StdOut.printf("length = %d ancestor = %d\n", sap.length(x, y), sap.ancestor(x, y));

        x = 1;
        y = 6;
        StdOut.printf("length = %d ancestor = %d\n", sap.length(x, y), sap.ancestor(x, y));


        // https://docs.oracle.com/javase/6/docs/api/java/util/List.html#hashCode()
        List<Integer> foo1 = new ArrayList<>();
        foo1.add(1);
        foo1.add(2);
        foo1.add(3);

        List<Integer> foo2 = new ArrayList<>();
        foo2.add(1);
        foo2.add(2);
        foo2.add(3);

        boolean iseq = foo1.hashCode() == foo2.hashCode();
        System.out.println(iseq);

        System.out.println(foo1.hashCode());

        List<Integer> foo0 = new ArrayList<>();
        foo0.add(2);
        System.out.println(foo0.hashCode());


        HashSet<Integer> hs = new HashSet<>();
        hs.add(1);
        hs.add(2);

        HashSet<Integer> hs2 = new HashSet<>();
        hs2.add(3);

        boolean hshs = hs.hashCode() == hs.hashCode();
        System.out.println(hshs);
        System.out.println(hs.hashCode());
        System.out.println(hs2.hashCode());


    }
}
