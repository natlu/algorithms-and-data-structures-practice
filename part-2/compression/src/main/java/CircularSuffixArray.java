import java.util.Arrays;

public class CircularSuffixArray {

    private final String string;
    private final int[] index;

    private class CircularSuffix implements Comparable<CircularSuffix> {
        private String s;
        private int index;

        public CircularSuffix(String s, int index) {
            this.s = s;
            this.index = index;
        }

        @Override
        public int compareTo(CircularSuffix other) {
            for (int i = 0; i < s.length(); i++) {
                char character = s.charAt((this.index + i) % s.length());
                char otherCharacter = s.charAt((other.index + i) % s.length());
                if (character > otherCharacter) return 1;
                if (character < otherCharacter) return -1;
            }
            return 0;
        }
    }

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException("String s must not be null");
        this.string = s;
        CircularSuffix[] circularSuffixes = new CircularSuffix[s.length()];
        for (int i = 0; i < s.length(); i++) circularSuffixes[i] = new CircularSuffix(s, i);
        Arrays.sort(circularSuffixes);
        this.index = new int[s.length()];
        for (int i = 0; i < s.length(); i++) index[i] = circularSuffixes[i].index;
    }

    // length of s
    public int length() {
        return string.length();
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= length()) throw new IllegalArgumentException("i out of bounds");
        return index[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
//        CircularSuffixArray circularSuffixArray = new CircularSuffixArray("helloworld");
        CircularSuffixArray circularSuffixArray = new CircularSuffixArray("BAB");

        int arrayLength = circularSuffixArray.length();
        System.out.println(arrayLength);

        for (int i = 0; i < arrayLength; i++) {
            System.out.println(i + " : " + circularSuffixArray.index(i));
        }



    }
}
