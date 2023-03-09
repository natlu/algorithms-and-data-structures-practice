import java.util.Arrays;

public class CircularSuffixArray {

    private final String string;
    private final int[] index;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        this.string = s + '\0';
        this.index = new int[this.string.length()];
        for (int i = 0; i < this.string.length(); i++) this.index[i] = i;
        sort(0, this.string.length() - 1, 0);
//        Arrays.copyOfRange(index, 1, string.length());
    }

    private char characterAt(int stringStartIndex, int charIndex) {
        int trueCharIndex = (index[stringStartIndex] + charIndex)%string.length();
        return string.charAt(trueCharIndex);
    }

    private void sort(int startIndex, int endIndex, int charIndex) {
        if (endIndex <= startIndex) return;

        int lessThanMarker = startIndex;
        int greaterThanMarker = endIndex;

        char character = characterAt(startIndex, charIndex);
        int i = startIndex + 1;
        while (i <= greaterThanMarker) {
            char otherCharacter = characterAt(i, charIndex);
            if      (otherCharacter < character) exchange(lessThanMarker++, i++);
            else if (otherCharacter > character) exchange(i, greaterThanMarker--);
            else                                 i++;
        }

        sort(startIndex, lessThanMarker-1, charIndex);
        if (character > 0) sort(lessThanMarker, greaterThanMarker, charIndex+1);
        sort(greaterThanMarker+1, endIndex, charIndex);
    }

    private void exchange(int i, int j) {
        int temp = index[i];
        index[i] = index[j];
        index[j] = temp;
    }

    // length of s
    public int length() {
        return string.length() - 1;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        return index[i+1];
    }

    // unit testing (required)
    public static void main(String[] args) {
//        CircularSuffixArray circularSuffixArray = new CircularSuffixArray("helloworld");
        CircularSuffixArray circularSuffixArray = new CircularSuffixArray("bdcca");

        int arrayLength = circularSuffixArray.length();
        System.out.println(arrayLength);

        for (int i = 0; i < arrayLength; i++) {
            System.out.println(i + " : " + circularSuffixArray.index(i));
        }



    }
}
