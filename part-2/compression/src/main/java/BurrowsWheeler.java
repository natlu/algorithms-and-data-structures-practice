import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        int first = -1;
        String string = BinaryStdIn.readString();
        char[] t = new char[string.length()];

        CircularSuffixArray circularSuffixArray = new CircularSuffixArray(string);
        for (int i = 0; i < circularSuffixArray.length(); i++) {
            int index = circularSuffixArray.index(i);
            if (index == 0) first = i;
            t[i] = LastCharOfJthoriginalSuffix(string, index);
        }
        BinaryStdOut.write(first);
        for (int i = 0; i < t.length; i++) BinaryStdOut.write(t[i]);
        BinaryStdOut.close();
    }

    private static char LastCharOfJthoriginalSuffix(String string, int j) {
        int charIndex = (j + string.length() - 1)%string.length();
        return string.charAt(charIndex);
    }


    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String string = BinaryStdIn.readString();

        int[] next = new int[string.length()];
        for (int i = 0; i < next.length; i++) {
            next[i] = i;
        }
        next = sort(string, next);

        int current = first;
        for (int i = 0; i < string.length(); i++) {
            int nextI = next[current];
            BinaryStdOut.write(string.charAt(nextI));
            current = nextI;
        }
        BinaryStdOut.close();
    }

    private static int[] sort(String string, int[] array) {
        return sort(string, array, new int[array.length], 0, array.length - 1);
    }

    private static int[] sort(String string, int[] array, int[] temp, int leftStart, int rightEnd) {
        if (leftStart == rightEnd) return array;
        int middle = (leftStart + rightEnd) / 2;
        array = sort(string, array, temp, leftStart, middle);
        array = sort(string, array, temp, middle + 1, rightEnd);
        return merge(string, array, temp, leftStart, rightEnd);
    }

    private static int[] merge(String string, int[] array, int[] temp, int leftStart, int rightEnd) {
        int middle = (leftStart + rightEnd) / 2;
        int leftEnd = middle;
        int rightStart = middle + 1;

        int leftIndex = leftStart;
        int rightIndex = rightStart;
        int tempIndex = leftStart;

        while (leftIndex <= leftEnd && rightIndex <= rightEnd) {
            if (string.charAt(array[leftIndex]) <= string.charAt(array[rightIndex])) {
                temp[tempIndex] = array[leftIndex];
                leftIndex++;
            } else {
                temp[tempIndex] = array[rightIndex];
                rightIndex++;
            }
            tempIndex++;
        }

        for (int i = leftIndex; i <= leftEnd; i++) {
            temp[tempIndex] = array[i];
            tempIndex++;
        }

        for (int i = rightIndex; i <= rightEnd; i++) {
            temp[tempIndex] = array[i];
            tempIndex++;
        }

        for (int i = leftStart; i <= rightEnd; i++) {
            array[i] = temp[i];
        }

        return array;
    }


    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if      (args[0].equals("-")) transform();
        else if (args[0].equals("+")) inverseTransform();
        else throw new IllegalArgumentException("Illegal command line argument");
    }

}