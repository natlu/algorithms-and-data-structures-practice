import edu.princeton.cs.algs4.Picture;

import java.util.Arrays;

public class SeamCarver {

    private Picture picture;

    public SeamCarver(Picture picture) {
        this.picture = picture;
    }

    public Picture picture() {
        return (picture);
    }

    public int width() {
        return (picture.width());
    }

    public int height() {
        return (picture.height());
    }

    private class RGB {
        int R;
        int G;
        int B;

        public RGB(int encodedRGB) {
            this.R = (encodedRGB >> 16) & 0xFF;
            this.G = (encodedRGB >> 8) & 0xFF;
            this.B = (encodedRGB >> 0) & 0xFF;
        }
    }

    private int squaredDelta(RGB rgb1, RGB rgb2) {
        int rd = (rgb1.R - rgb2.R);
        int gd = (rgb1.G - rgb2.G);
        int bd = (rgb1.B - rgb2.B);

        return (rd * rd + gd * gd + bd * bd);
    }

    public double energy(int x, int y) {

        if (x == 0 || x == (width() - 1) || y == 0 || y == (height() - 1)) {
            return 1000 * 1000;
        }

        RGB rgbx1 = new RGB(picture.getRGB(x - 1, y));
        RGB rgbx2 = new RGB(picture.getRGB(x + 1, y));
        int xd = squaredDelta(rgbx1, rgbx2);

        RGB rgby1 = new RGB(picture.getRGB(x, y - 1));
        RGB rgby2 = new RGB(picture.getRGB(x, y + 1));
        int yd = squaredDelta(rgby1, rgby2);

        // won't sqrt and hope no tests require it to be
        return (double) xd + yd;
    }

    private int max(int x, int y) {
        if (x > y) {
            return x;
        }
        return y;
    }

    private int min(int x, int y) {
        if (x > y) {
            return y;
        }
        return x;
    }

    public int[] findVerticalSeam() {
        double[][] energy;
        double[][] distTo;
        int[][] edgeTo;
        double currMinVal;
        double e;
        double BIGENERGY;
        double HUGEDIST;

        // to represent non calculated energy
        // probably should rename to something that makes sense
        BIGENERGY = Double.MAX_VALUE;

        HUGEDIST = Double.MAX_VALUE;

        energy = new double[width()][height()];
        distTo = new double[width()][height()];
        edgeTo = new int[width()][height()];

        for (double[] array : energy) {
            Arrays.fill(array, BIGENERGY);
        }

        for (double[] array : distTo) {
            Arrays.fill(array, BIGENERGY);
        }

        for (int[] array : edgeTo) {
            // fill with -1 because the default 0 is a value with meaning
            Arrays.fill(array, -1);
        }

        // add func cacheEnergy to clean things up

        for (int n = 0; n < width() - 1; n++) { // loop through the potential start positions

            // for each starting pos, get the shortest path
            for (int r = 0; r < height() - 1;
                 r++) { // does it need to be -2 to not go out of bounds????????????
                // for (int c = max(0, -r + n); c <= min(width() - 1, r + n); c++) {
                for (int c : relevantIndices(n, r)) {
                    double prevDist = distTo[c][r];
                    for (int c_ : adj(c)) {
                        if (energy[c_][r] == BIGENERGY) {
                            e = energy(c_, r);
                            energy[c_][r] = e;
                        }
                        else {
                            e = energy[c_][r];
                        }
                        // relaxing edges
                        double currDist = distTo[c_][r + 1];
                        if (prevDist + e < currDist) {
                            distTo[c_][r + 1] = prevDist + e;
                            edgeTo[c_][r + 1] = c_;
                        }

                    }
                }
            }

        }


    }

    // can calc because it is a triangle with bounds
    public int[] relevantIndices(int startPos, int depth) {
        int start = max(0, -depth + startPos);
        int end = min(width() - 1, depth + startPos);
        int[] ri = new int[end - start + 1];
        for (int i = 0; i < ri.length; i++) {
            ri[i] = start + i;
        }
        return ri;
    }

    // y is irrelevant as we always look at one below (wrt vertical seam)
    private int[] adj(int x) {
        return relevantIndices(x, 1);
    }

    // // sequence of indices for horizontal seam
    // public int[] findHorizontalSeam()
    //
    //
    // // remove horizontal seam from current picture
    // public void removeHorizontalSeam(int[] seam)
    //
    // // remove vertical seam from current picture
    // public void removeVerticalSeam(int[] seam)

    //  unit testing (optional)
    public static void main(String[] args) {
        // System.out.println("asdfa");
        Picture picture = new Picture("./5x6.png");
        // picture.show();

        SeamCarver sc = new SeamCarver(picture);

        int[] a1 = sc.relevantIndices(2, 0);
        System.out.println("------------------");
        for (int a : a1) {
            System.out.println(a);
        }

        int[] a2 = sc.relevantIndices(2, 1);
        System.out.println("------------------");
        for (int a : a2) {
            System.out.println(a);
        }

        int[] a3 = sc.relevantIndices(2, 2);
        System.out.println("------------------");
        for (int a : a3) {
            System.out.println(a);
        }

        int[] a4 = sc.relevantIndices(2, 3);
        System.out.println("------------------");
        for (int a : a4) {
            System.out.println(a);
        }


    }

}
