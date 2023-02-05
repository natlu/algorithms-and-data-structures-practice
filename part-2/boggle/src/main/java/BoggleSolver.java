import com.sun.jdi.Value;

public class BoggleSolver {

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

        private Node put(Node node, String key, int character_index)
        {
            if (node == null) node = new Node();
            if (character_index == key.length()) { node.wordEnd = true; return node; }
            char c = key.charAt(character_index);
            node.next[c] = put(node.next[c], key, character_index+1);
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
            return contains(node.next[c], key, character_index+1);
        }


    }

    public BoggleSolver(String[] dictionary) {

    }

    }
    // public Iterable<String> getAllValidWords(BoggleBoard board)
    // public int scoreOf(String word)
}
