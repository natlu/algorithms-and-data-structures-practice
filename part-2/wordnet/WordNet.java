import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class WordNet {

    private Digraph digraph;
    private Digraph reverseDigraph;
    private boolean[] marked;
    private boolean[] onStack;

    private Set<Integer> vertices = new HashSet<>();

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        Queue<ArrayList<Integer>> inputQueue = readHypernyms(new File(hypernyms));
        buildDigraphs(inputQueue);

        marked = new boolean[digraph.V()];
        onStack = new boolean[digraph.V()];

        dfs(reverseDigraph, getRootVertex());


    }

    private Queue<ArrayList<Integer>> readHypernyms(File file) {
        // File foo = new File("./hypernyms.txt");
        In input = new In(file);

        Queue<ArrayList<Integer>> queue = new Queue<>();
        while (input.hasNextLine()) {
            String line = input.readLine();
            if (line.isBlank()) {
                break;
            }
            String[] fields = line.split(",");
            Integer from_v = Integer.parseInt(fields[0]);
            vertices.add(from_v);
            int n = fields.length;
            for (int i = 1; i < n; i++) {
                int to_v = Integer.parseInt(fields[i]);
                vertices.add(to_v);
                queue.enqueue(new ArrayList<Integer>(Arrays.asList(from_v, to_v)));
            }
        }
        return queue;
    }

    private void buildDigraphs(Queue<ArrayList<Integer>> queue) {
        // build the digraph as well as the reverse digraph to save a loop from
        // running the reverse method
        digraph = new Digraph(vertices.size());
        reverseDigraph = new Digraph(vertices.size());
        while (!queue.isEmpty()) {
            ArrayList<Integer> bar = queue.dequeue();
            digraph.addEdge(bar.get(0), bar.get(1));
            reverseDigraph.addEdge(bar.get(1), bar.get(0));
        }
    }

    private int getRootVertex() {
        // try find the root vertex
        // it will be the one with no indegree in the reversed digraph
        Integer rootVertex = null;
        System.out.println(reverseDigraph.V());
        for (int v = 0; v < reverseDigraph.V(); v++) {
            // System.out.println(v);
            if (reverseDigraph.indegree(v) == 0) {
                if (rootVertex != null) {
                    throw new IllegalArgumentException("not all nodes lead to root 1");
                }
                rootVertex = v;
            }
        }

        if (rootVertex == null) {
            throw new IllegalArgumentException("not all nodes lead to root 2");
        }
        return rootVertex;
    }

    private void dfs(Digraph dg, int v) {
        marked[v] = true;
        onStack[v] = true;
        for (int w : dg.adj(v)) {
            if (!marked[w]) {
                dfs(dg, w);
            }
            else if (onStack[w]) {
                throw new IllegalArgumentException("Graph not acyclic");
            }
        }
        onStack[v] = false;
    }

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

        // vertices always monotonically increasing from 0?
        // if not then could fail as Digraph needs the vertex index to be <=
        // the input V
        // will assume its fine so won't do any further processing


        System.out.println("hypernyms-verysmall");
        new WordNet("blah", "./hypernyms-verysmall.txt");

        // System.out.println("hypernyms");
        // new WordNet("blah", "./hypernyms.txt");

        System.out.println("hypernyms3InvalidCycle");
        new WordNet("blah", "./hypernyms3InvalidCycle.txt");

        System.out.println("hypernyms3InvalidTwoRoots");
        new WordNet("blah", "./hypernyms3InvalidTwoRoots.txt");

        System.out.println("hypernyms3InvalidCycle+Path");
        new WordNet("blah", "./hypernyms3InvalidCycle+Path.txt");
    }

}