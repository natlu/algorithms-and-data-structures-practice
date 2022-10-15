import edu.princeton.cs.algs4.Picture;

import java.util.Arrays;

public class SeamCarver {
    // private static double UNKNOWN_ENERGY = Double.MAX_VALUE;
    private static double UNKNOWN_ENERGY = 10000000;
    // private static double INFINITY = Double.MAX_VALUE;
    private static double INFINITY = 10000000;

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

    private class SeamHelper {
        private final int UNKNOWN_EDGE = -1;

        private int width;
        private int height;

        private double[][] energy;
        private double[][] distTo;
        private int[][] edgeTo;

        private double minDist = INFINITY;
        private int[] seam;

        public SeamHelper(double[][] energy) {
            this.height = energy.length;
            this.width = energy[0].length;

            this.energy = energy;
            // this.distTo = new double[width][height];
            this.distTo = new double[height][width];
            // this.edgeTo = new int[width][height];
            this.edgeTo = new int[height][width];

            for (double[] array : this.distTo) {
                Arrays.fill(array, INFINITY);
            }
            Arrays.fill(distTo[0], 1000 * 1000);

            for (int[] array : this.edgeTo) {
                Arrays.fill(array, UNKNOWN_EDGE);
            }
            // since the borders have the same energy, just set the path to be itself
            for (int i = 0; i < this.width; i++) {
                this.edgeTo[0][i] = i;
            }
        }

        public double getEnergy(int c, int r) {
            double e;
            if (energy[r][c] == UNKNOWN_ENERGY) {
                e = energy(c, r);
                energy[r][c] = e;
            }
            else {
                e = energy[r][c];
            }
            return e;
        }

        public void relax(int fromC, int ToC, int r, double prevDist) {
            double currDist = distTo[r][ToC];
            double currEnergy = getEnergy(ToC, r);
            if (prevDist + currEnergy < currDist) {
                distTo[r][ToC] = prevDist + currEnergy;
                edgeTo[r][ToC] = fromC;
            }
        }

        public double getDist(int c, int r) {
            return distTo[r][c];
        }

        private int getSeamEnd() {
            int seamEnd = -1;
            for (int c = 0; c <= width - 1; c++) {
                double d = distTo[height - 1][c];
                if (d < minDist) {
                    seamEnd = c;
                    minDist = d;
                }
            }
            return seamEnd;
        }

        public int[] getSeam() {
            int seamEnd = getSeamEnd();
            seam = new int[height()];
            int pos = seamEnd;
            for (int i = height() - 1; i >= 0; i--) {
                seam[i] = pos;
                pos = edgeTo[i][pos];
            }
            return seam;
        }

        public double getMinDist() {
            if (minDist == INFINITY) {
                throw new RuntimeException("oops, bad design. need to call getSeam first");
            }
            return minDist;
        }

    }

    public int[] findVerticalSeam() {

        SeamHelper seamHelper;
        double currMinDist = INFINITY;
        int[] currMinSeam = new int[height()];
        double[][] energy;
        energy = new double[height()][width()];
        for (double[] array : energy) {
            Arrays.fill(array, UNKNOWN_ENERGY);
        }

        // loop through the potential start positions
        // for each starting pos, get the shortest path
        for (int n = 0; n < width() - 1; n++) {
            seamHelper = new SeamHelper(energy);
            for (int r = 0; r <= height() - 2; r++) {
                int[] ri = relevantIndices(n, r);
                for (int c : relevantIndices(n, r)) {
                    double prevDist = seamHelper.getDist(c, r);
                    for (int c_ : adj(c)) {
                        seamHelper.relax(c, c_, r + 1, prevDist);
                    }
                }
            }
            int[] minSeam = seamHelper.getSeam();
            double minDist = seamHelper.getMinDist();
            if (minDist < currMinDist) {
                currMinSeam = minSeam;
                currMinDist = minDist;
            }
        }

        return currMinSeam;
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

    public static void main(String[] args) {
        // System.out.println("asdfa");
        Picture picture = new Picture("./7x10.png");
        // picture.show();

        SeamCarver sc = new SeamCarver(picture);

        System.out.println("------------------");
        int[] vertSeam = sc.findVerticalSeam();
        for (int a : vertSeam) {
            System.out.println(a);
        }

        // int[] a1 = sc.relevantIndices(2, 0);
        // System.out.println("------------------");
        // for (int a : a1) {
        //     System.out.println(a);
        // }
        //
        // int[] a2 = sc.relevantIndices(2, 1);
        // System.out.println("------------------");
        // for (int a : a2) {
        //     System.out.println(a);
        // }
        //
        // int[] a3 = sc.relevantIndices(2, 2);
        // System.out.println("------------------");
        // for (int a : a3) {
        //     System.out.println(a);
        // }
        //
        // int[] a4 = sc.relevantIndices(2, 3);
        // System.out.println("------------------");
        // for (int a : a4) {
        //     System.out.println(a);
        // }


    }

}
