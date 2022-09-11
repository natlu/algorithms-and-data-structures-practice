/*
princeton mooc
*/

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class QuickSort {
   
    public static <T extends Comparable<? super T>> void sort(T[] a) {
        shuffleArray(a);
        sort(a, 0, a.length - 1);
    }

    public static <T extends Comparable<? super T>> void sort(T[] a, int lo, int hi) {
        if (hi <= lo) return;
        int j = partition(a, lo, hi);
        sort(a, 0, j - 1);
        sort(a, j + 1, hi);
    }

    // return the position of the pivot element
    private static <T extends Comparable<? super T>> int partition(T[] a, int lo, int hi) {
        // a[lo] is the pivot element

        int i = 0;
        int j = hi + 1;

        while (true) {
            while (a[++i].compareTo(a[lo]) < 0) // increment i if a[i] < a[lo]
                if (i == hi) break;

            while (a[--j].compareTo(a[lo]) > 0) // decrement j if a[j] > a[lo]
                if (j == lo) break;

            // break when i and j cross
            // notes: cannot just check for i == j as there are no
            // comparisons b/w i and j in between the i increments and
            // j decrements
            if (i >= j) break;

            exch(a, i, j); // exchange to preserve the invariant
        }

        // but the pivot where it belongs (in pos j)
        exch(a, lo, j);
        return j;
    }

    private static <T extends Comparable<? super T>> void exch(T[] a, int i, int j) {
        T tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }

    // Fisher Yates Shuffle
    // https://stackoverflow.com/questions/1519736/random-shuffling-of-an-array
    public static <T extends Comparable<? super T>> void shuffleArray(T[] ar) {
        Random rnd = ThreadLocalRandom.current();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            T a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }


    public static void main(String[] args) {
        Integer[] a = new Integer[5];
        a[0] = 2;
        a[1] = 7;
        a[2] = 2;
        a[3] = 1;
        a[4] = 3;

        sort(a);

        for (Integer i : a) {
            System.out.println(i);
        }

    }

}
