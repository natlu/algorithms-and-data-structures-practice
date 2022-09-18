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

public class SAP {

    private Digraph G;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        this.G = G;
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
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
        while (!lst.isEmpty()) {
            Integer i = lst.dequeue();
            if (vg.hasPathTo(i) && wg.hasPathTo(i)) {
                int vd = vg.distTo(i);
                int wd = wg.distTo(i);
                // if any of the components is larger than the current min len,
                // then stop as all other vertices in the queue will have a
                // larger or equal distance
                if (vd >= len || wd >= len) {
                    break;
                }
                if ((vd + wd) < len) {
                    len = vd + wd;
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
            return -1;
        }
        return len;
    }

    // // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    // public int ancestor(int v, int w)
    //
    // // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    // public int length(Iterable<Integer> v, Iterable<Integer> w)
    //
    // // a common ancestor that participates in shortest ancestral path; -1 if no such path
    // public int ancestor(Iterable<Integer> v, Iterable<Integer> w)

    public static void main(String[] args) {
        In in = new In("digraph1.txt");
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);

        StdOut.printf("length = %d\n", sap.length(3, 11));
        StdOut.printf("length = %d\n", sap.length(9, 12));
        StdOut.printf("length = %d\n", sap.length(7, 2));
        StdOut.printf("length = %d\n", sap.length(1, 6));
    }
}
