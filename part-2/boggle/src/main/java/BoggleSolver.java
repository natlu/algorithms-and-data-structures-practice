import com.sun.jdi.Value;

import java.util.ArrayList;
import java.util.List;

public class BoggleSolver {
    static final int MIN_WORD_LEN = 3;
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
            if (character_index == key.length()) {
                node.wordEnd = true;
                return node;
            }
            char c = key.charAt(character_index);
            int pos = c - 'A';
            node.next[pos] = put(node.next[pos], key, character_index + 1);
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
            return contains(node.next[pos], key, character_index + 1);
        }

        public boolean isPrefix(String prefix) {
            return isPrefix(root, prefix, 0);

        }

        public boolean isPrefix(Node node, String prefix, int character_index) {
            if (node == null) return false;
            if (character_index == prefix.length()) return true;
            char c = prefix.charAt(character_index);
            int pos = c - 'A';
            return isPrefix(node.next[pos], prefix, character_index + 1);
        }

    }

    public BoggleSolver(String[] dictionary) {
        dictionaryTrie = new Trie();
        for (String word : dictionary) {
            dictionaryTrie.put(word);
        }
    }

    private static class TilePosition {
        public int i;
        public int j;

        public TilePosition(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }

    private int min(int x, int y) {
        if (x < y) { return x; }
        return y;
    }

    private int max(int x, int y) {
        if (x > y) { return x; }
        return y;
    }

    private List<TilePosition> getAdjacentTiles(BoggleBoard board, int i, int j) {
        ArrayList<TilePosition> adj = new ArrayList<>();

        int iMin = max(i - 1, 0);
        int iMax = min(i + 1, board.rows() - 1);
        int jMin = max(j - 1, 0);
        int jMax = min(j + 1, board.cols() - 1);

        for (int iIndex = iMin; iIndex <= iMax; iIndex++) {
            for (int jIndex = jMin; jIndex <= jMax; jIndex++) {
                if (iIndex == i && jIndex == j) continue;
                adj.add(new TilePosition(iIndex, jIndex));
            }
        }

        return adj;
    }

    public Iterable<String> getAllValidWords(BoggleBoard board) {
        BoggleDFS boggleDFS = new BoggleDFS(board);
        return boggleDFS.getAllWords();
    }

    public BoggleDFS getBoggleDFS(BoggleBoard board) {
        return new BoggleDFS(board);
    }

    private class BoggleDFS {
        private BoggleBoard board;
        private List<String> validWords;
        private boolean[][] visited;

        public BoggleDFS(BoggleBoard board) {
            this.board = board;
            validWords = new ArrayList<>();
        }

        public List<String> getAllWords() {
            for (int i = 0; i < board.rows(); i++) {
                for (int j = 0; j < board.cols(); j++) {
                    getAllWordsStartingFromTile(i, j);
                }
            }
            return validWords;
        }

        public void getAllWordsStartingFromTile(int i, int j) {
            visited = new boolean[board.rows()][board.cols()];
            getAllWordsStartingFromTile(i, j, new StringBuilder());
        }

        public void getAllWordsStartingFromTile(int i, int j, StringBuilder word) {
            visited[i][j] = true;
            word.append(board.getLetter(i, j));
            if (dictionaryTrie.isPrefix(word.toString())) {
                if (word.length() >= MIN_WORD_LEN && dictionaryTrie.contains(word.toString())) {
                    validWords.add(word.toString());
                }
                List<TilePosition> adjacentTiles = getAdjacentTiles(board, i, j);
                for (TilePosition adjacentTile : adjacentTiles) {
                    if (!visited[adjacentTile.i][adjacentTile.j]) {
                        getAllWordsStartingFromTile(adjacentTile.i, adjacentTile.j, word);
                    }
                }
            }
            word.deleteCharAt(word.length() - 1);
            visited[i][j] = false;
        }

    }


    // public int scoreOf(String word)


    public static void main(String[] args) {

        String[] dict = {"ONE", "FOUR", "FIVE", "SIX", "NINE"};
        BoggleSolver bs = new BoggleSolver(dict);

//        if (bs.dictionaryTrie.isPrefix("ONE")) {
//            System.out.println("yes");
//        }

        char[][] foo =  {
                { 'O', 'N', 'E' },
                { 'Z', 'F', 'O' },
                { 'X', 'R', 'U' }
        };
        BoggleBoard bb = new BoggleBoard(foo);
//        System.out.println(bb.toString());

       BoggleDFS bfs = bs.getBoggleDFS(bb);
       bfs.getAllWordsStartingFromTile(1, 1);
       Iterable<String> bar = bfs.validWords;
        for (String word : bar) {
            System.out.println(word);
        }

//        Iterable<String> words = bs.getAllValidWords(bb);
//        for (String word : words) {
//            System.out.println(word);
//        }

    }

}


