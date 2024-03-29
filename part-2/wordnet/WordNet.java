import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WordNet {

    private Digraph digraph;
    private Digraph reverseDigraph;
    private boolean[] marked;
    private boolean[] onStack;
    private HashSet<String> uniqueNouns;
    private List<String> synset;
    private HashMap<String, List<Integer>> synsetMap;
    private final Set<Integer> vertices = new HashSet<>();
    private final SAP mySap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null) {
            throw new IllegalArgumentException("argument sysets cannot be null");
        }
        if (hypernyms == null) {
            throw new IllegalArgumentException("argument hypernyms cannot be null");
        }

        Queue<ArrayList<Integer>> inputQueue = readHypernyms(hypernyms);
        buildDigraphs(inputQueue);
        readSynsets(synsets);

        marked = new boolean[digraph.V()];
        onStack = new boolean[digraph.V()];

        dfs(reverseDigraph, getRootVertex());

        mySap = new SAP(digraph);
    }

    private void readSynsets(String file) {
        In input = new In(file);
        synset = new ArrayList<>();
        synsetMap = new HashMap<>();
        uniqueNouns = new HashSet<>();
        while (input.hasNextLine()) {
            String line = input.readLine();
            if (line.isBlank()) {
                break;
            }
            String[] fields = line.split(",");
            int id = Integer.parseInt(fields[0]);
            String[] nouns = fields[1].split(" ");
            // String description = fields[2];
            synset.add(id, fields[1]);
            uniqueNouns.addAll(List.of(nouns));
            for (String n : nouns) {
                if (synsetMap.get(n) == null) {
                    synsetMap.put(n, new ArrayList<>());
                }
                synsetMap.get(n).add(id);
            }
        }
    }

    private Queue<ArrayList<Integer>> readHypernyms(String file) {
        // File foo = new File("./hypernyms.txt");
        In input = new In(file);

        Queue<ArrayList<Integer>> queue = new Queue<>();
        while (input.hasNextLine()) {
            String line = input.readLine();
            if (line.isBlank()) {
                break;
            }
            String[] fields = line.split(",");
            int fromV = Integer.parseInt(fields[0]);
            vertices.add(fromV);
            int n = fields.length;
            for (int i = 1; i < n; i++) {
                int toV = Integer.parseInt(fields[i]);
                vertices.add(toV);
                queue.enqueue(new ArrayList<Integer>(Arrays.asList(fromV, toV)));
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
        Integer rootVertex = null;
        for (int v = 0; v < reverseDigraph.V(); v++) {
            if (reverseDigraph.indegree(v) == 0) {
                if (rootVertex != null) {
                    throw new IllegalArgumentException("multiple root vertices found");
                }
                rootVertex = v;
            }
        }
        if (rootVertex == null) {
            throw new IllegalArgumentException("no root vertex found");
        }
        // System.out.println("root is: " + rootVertex);
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


    // returns all WordNet nouns
    public Iterable<String> nouns() {
        List<String> nounCopy = new ArrayList<>();
        nounCopy.addAll(uniqueNouns);
        return nounCopy;
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException("argument word cannot be null");
        }
        return uniqueNouns.contains(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null) {
            throw new IllegalArgumentException("argument nounA cannot be null");
        }
        if (nounB == null) {
            throw new IllegalArgumentException("argument nounB cannot be null");
        }
        if (!synsetMap.containsKey(nounA)) {
            throw new IllegalArgumentException("argument nounA not a wordnet noun");
        }
        if (!synsetMap.containsKey(nounB)) {
            throw new IllegalArgumentException("argument nounB not a wordnet noun");
        }
        return mySap.length(synsetMap.get(nounA), synsetMap.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null) {
            throw new IllegalArgumentException("argument nounA cannot be null");
        }
        if (nounB == null) {
            throw new IllegalArgumentException("argument nounB cannot be null");
        }
        if (!synsetMap.containsKey(nounA)) {
            throw new IllegalArgumentException("argument nounA not a wordnet noun");
        }
        if (!synsetMap.containsKey(nounB)) {
            throw new IllegalArgumentException("argument nounB not a wordnet noun");
        }
        int ancestorVertex = mySap.ancestor(synsetMap.get(nounA), synsetMap.get(nounB));
        return synset.get(ancestorVertex);
    }

    public static void main(String[] args) {

        // vertices always monotonically increasing from 0?
        // if not then could fail as Digraph needs the vertex index to be <=
        // the input V
        // will assume its fine so won't do any further processing
        WordNet foo = new WordNet(null, "./hypernyms-verysmall.txt");

        System.out.println("hypernyms-verysmall");
        // WordNet foo = new WordNet("./synsets3.txt", "./hypernyms-verysmall.txt");

        System.out.println("nouns ------------------");
        for (String n : foo.nouns()) {
            System.out.println(n);
        }

        System.out.println("is noun ------------------");
        System.out.println(foo.isNoun("a"));
        System.out.println(foo.isNoun("A"));
        System.out.println(foo.isNoun("foo"));


        // System.out.println("hypernyms");
        // new WordNet("blah", "./hypernyms.txt");

        // System.out.println("hypernyms3InvalidCycle");
        // new WordNet("blah", "./hypernyms3InvalidCycle.txt");

        // System.out.println("hypernyms3InvalidTwoRoots");
        // new WordNet("blah", "./hypernyms3InvalidTwoRoots.txt");

        // System.out.println("hypernyms3InvalidCycle+Path");
        // new WordNet("blah", "./hypernyms3InvalidCycle+Path.txt");

    }

}