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

import java.util.HashMap;

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

    // // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    // public int length(Iterable<Integer> v, Iterable<Integer> w)
    //
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
    }
}
