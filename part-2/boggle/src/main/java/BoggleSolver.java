import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BoggleSolver {
    private static final int MIN_WORD_LEN = 3;
    private final Trie dictionaryTrie;

    private class Trie {
        private static final int R = 26;
        private Node root = new Node();

        private class Node {
            private boolean wordEnd = false;
            private Node[] next = new Node[R];
        }

        public void put(String key) {
            root = put(root, key, 0);
        }

        private Node put(Node node, String key, int characterIndex) {
            if (node == null) node = new Node();
            if (characterIndex == key.length()) {
                node.wordEnd = true;
                return node;
            }
            char c = key.charAt(characterIndex);
            int pos = c - 'A';
            node.next[pos] = put(node.next[pos], key, characterIndex + 1);
            return node;
        }

        public boolean contains(String key) {
            return contains(root, key, 0);
        }

        public boolean contains(Node node, String key, int characterIndex) {
            if (node == null) return false;
            if (characterIndex == key.length()) {
                if (node.wordEnd) return true;
                return false;
            }
            char c = key.charAt(characterIndex);
            int pos = c - 'A';
            return contains(node.next[pos], key, characterIndex + 1);
        }

        public boolean isPrefix(String prefix) {
            return isPrefix(root, prefix, 0);

        }

        public boolean isPrefix(Node node, String prefix, int characterIndex) {
            if (node == null) return false;
            if (characterIndex == prefix.length()) return true;
            char c = prefix.charAt(characterIndex);
            int pos = c - 'A';
            return isPrefix(node.next[pos], prefix, characterIndex + 1);
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

    private class BoggleDFS {
        private final BoggleBoard board;
        private final HashSet<String> validWords;
        private boolean[][] visited;
        private boolean[][] qFlag;

        public BoggleDFS(BoggleBoard board) {
            this.board = board;
            validWords = new HashSet<>();
        }

        public HashSet<String> getAllWords() {
            for (int i = 0; i < board.rows(); i++) {
                for (int j = 0; j < board.cols(); j++) {
                    includeAllWordsStartingFromTile(i, j);
                }
            }
            return validWords;
        }

        public void includeAllWordsStartingFromTile(int i, int j) {
            visited = new boolean[board.rows()][board.cols()];
            qFlag = new boolean[board.rows()][board.cols()];
            includeAllWordsStartingFromTile(i, j, new StringBuilder());
        }

        private String getLetter(int i, int j) {
            char letter = board.getLetter(i, j);
            if (letter == 'Q') {
                qFlag[i][j] = true;
                return "QU";
            }
            return String.valueOf(letter);
        }

        private void deleteCharacters(StringBuilder word, int i, int j) {
            word.deleteCharAt(word.length() - 1);
            if (qFlag[i][j]) {
                word.deleteCharAt(word.length() - 1);
            }
        }

        public void includeAllWordsStartingFromTile(int i, int j, StringBuilder word) {
            visited[i][j] = true;
            word.append(getLetter(i, j));
            if (dictionaryTrie.isPrefix(word.toString())) {
                if (word.length() >= MIN_WORD_LEN && dictionaryTrie.contains(word.toString())) {
                    validWords.add(word.toString());
                }
                List<TilePosition> adjacentTiles = getAdjacentTiles(board, i, j);
                for (TilePosition adjacentTile : adjacentTiles) {
                    if (!visited[adjacentTile.i][adjacentTile.j]) {
                        includeAllWordsStartingFromTile(adjacentTile.i, adjacentTile.j, word);
                    }
                }
            }
            deleteCharacters(word, i, j);
            visited[i][j] = false;
        }

    }


    public int scoreOf(String word) {
        if (!dictionaryTrie.contains(word)) return 0;
        if (word.length() < 3) return 0;
        if (word.length() <= 4) return 1;
        if (word.length() == 5) return 2;
        if (word.length() == 6) return 3;
        if (word.length() == 7) return 5;
        return 11;
     }


//    public static void main(String[] args) {
//    }

}


