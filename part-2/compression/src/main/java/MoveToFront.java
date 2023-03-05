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
            characterSequence.remove(characterPosition);
            characterSequence.add(0, characterDecimalValue);
            BinaryStdOut.write((char) characterPosition);
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        List<Integer> characterSequence = new ArrayList<>(NUMBER_OF_LEGAL_CHARACTERS);
        for (int i = 0; i < NUMBER_OF_LEGAL_CHARACTERS; i++) {
            characterSequence.add(i);
        }
        while (!BinaryStdIn.isEmpty()) {
            int characterPosition = (int) BinaryStdIn.readChar();
            int characterDecimalValue = Integer.valueOf(characterSequence.get(characterPosition));
            BinaryStdOut.write((char) characterDecimalValue);
            characterSequence.remove(characterPosition);
            characterSequence.add(0, characterDecimalValue);
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if      (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
        else throw new IllegalArgumentException("Illegal command line argument");
    }

}
