import com.sun.jdi.Value;

public class BoggleSolver {
    Trie dictionaryTrie;

    private class Trie {
        static final int R = 26;
        private Node root = new Node();

        private static class Node {
            private boolean wordEnd = false;
            private Node[] next = new Node[R];
        }

        public void put(String key) {
            root = put(root, key, 0);
        }

        private Node put(Node node, String key, int character_index) {
            if (node == null) node = new Node();
            if (character_index == key.length()) { node.wordEnd = true; return node; }
            char c = key.charAt(character_index);
            int pos = c - 'A';
            node.next[pos] = put(node.next[pos], key, character_index+1);
            return node;
        }

        public boolean contains(String key) {
            return contains(root, key, 0);
        }

        public boolean contains(Node node, String key, int character_index) {
            if (node == null) return false;
            if (node.wordEnd) return true;
            if (character_index == key.length()) return false; // must be after checking wordEnd
            char c = key.charAt(character_index);
            int pos = c - 'A';
            return contains(node.next[pos], key, character_index+1);
        }
    }

    public BoggleSolver(String[] dictionary) {
        dictionaryTrie = new Trie();
        for (String word: dictionary) {
            dictionaryTrie.put(word);
        }
    }

    public boolean foo(String key) {
        return dictionaryTrie.contains(key);
    }

    // public Iterable<String> getAllValidWords(BoggleBoard board)
    // public int scoreOf(String word)
    public static void main(String[] args) {

//        Integer[] a = new Integer[26];
//        for (int i=0; i < 26; i++) {
//            a[i] = i;
//        }
//        System.out.println(a['a'-'a']);
//        System.out.println(a['b'-'a']);
//        System.out.println(a['A'-'A']);
//        System.out.println(a['B'-'A']);

        String[] dict = {"ONE","TWO", "THREE", "FOUR", "FIVE"};
        BoggleSolver bs = new BoggleSolver(dict);

        String[] tests = {"ONE", "FOUR", "EIGHT", "AAA"};
        for (String test: tests) {
            if (bs.foo(test)) {
                System.out.println(test);
            }
        }


    }
}
