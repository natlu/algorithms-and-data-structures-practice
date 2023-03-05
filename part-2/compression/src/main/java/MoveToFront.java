import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.ArrayList;
import java.util.List;

public class MoveToFront {
    private static final int NUMBER_OF_LEGAL_CHARACTERS = 256;

    public MoveToFront() {

    }

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        List<Integer> characterSequence = new ArrayList<>(NUMBER_OF_LEGAL_CHARACTERS);
        for (int i = 0; i < NUMBER_OF_LEGAL_CHARACTERS; i++) {
            characterSequence.add(i);
        }
        while (!BinaryStdIn.isEmpty()) {
            char character = BinaryStdIn.readChar();
            int characterDecimalValue = (int) character;
            int characterPosition = characterSequence.indexOf(characterDecimalValue);
//            System.out.println(characterPosition);
            characterSequence.remove(characterPosition);
            characterSequence.add(0, characterDecimalValue);
            BinaryStdOut.write((char) characterPosition);
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {

    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        encode();
//        if      (args[0].equals("-")) compress();
//        else if (args[0].equals("+")) expand();
//        else throw new IllegalArgumentException("Illegal command line argument");

//        System.out.println("heelo");
    }

}
