import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class WordNet {


    // // constructor takes the name of the two input files
    // public WordNet(String synsets, String hypernyms)
    //
    // // returns all WordNet nouns
    // public Iterable<String> nouns()
    //
    // // is the word a WordNet noun?
    // public boolean isNoun(String word)
    //
    // // distance between nounA and nounB (defined below)
    // public int distance(String nounA, String nounB)
    //
    // // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // // in a shortest ancestral path (defined below)
    // public String sap(String nounA, String nounB)

    // do unit testing of this class
    public static void main(String[] args) {

        // File foo = new File("./hypernyms-small.txt");
        File foo = new File("./hypernyms.txt");

        In b = new In(foo);
        // System.out.println(b.toString());
        // String bb = b.readLine();
        // System.out.println(bb);

        // vertices always monotonically increasing from 0?
        // if not then could fail as Digraph needs the vertex index to be <=
        // the input V
        // will assume its fine so won't do any further processing

        Queue<ArrayList<Integer>> myQueue = new Queue<>();
        while (b.hasNextLine()) {
            String line = b.readLine();
            if (line.isBlank()) {
                break;
            }
            String[] fields = line.split(",");
            Integer from_v = Integer.parseInt(fields[0]);
            int n = fields.length;
            for (int i = 1; i < n; i++) {
                myQueue.enqueue(new ArrayList<Integer>(
                        Arrays.asList(from_v, Integer.parseInt(fields[i]))
                ));
            }
        }

        System.out.println(myQueue.size());

        Digraph myGraph = new Digraph(myQueue.size());

        while (!myQueue.isEmpty()) {
            ArrayList<Integer> bar = myQueue.dequeue();
            myGraph.addEdge(bar.get(0), bar.get(1));
        }

        // System.out.println(myGraph.toString());


        // Bag<Integer>[] a = (Bag<Integer>[]) new Bag[5];
        // for (int v = 0; v < 5; v++) {
        //     a[v] = new Bag<Integer>();
        // }
        //
        // a[0].add(1);
        // a[0].add(2);
        // a[1].add(3);
        // a[1].add(4);
        //
        // System.out.printf(a.toString());
        //
        // System.out.printf("\n--------------\n");
        //
        // System.out.printf(a[0].toString());
        //
        // Iterator<Integer> ai = a[0].iterator();
        //
        // System.out.println("\n");
        //
        // while (ai.hasNext()) {
        //     System.out.printf(ai.next().toString());
        //     System.out.println("\n");
        // }

    }

}
