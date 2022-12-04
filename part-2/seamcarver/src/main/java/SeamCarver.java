import edu.princeton.cs.algs4.Picture;

import java.util.Arrays;

public class SeamCarver {
    private static final double UNKNOWN_ENERGY = Double.POSITIVE_INFINITY;
    private static final double INFINITY = Double.POSITIVE_INFINITY;

    private Picture picture;
    private boolean isTransposed;

    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("picture must not be null");
        }
        this.picture = new Picture(picture);
        this.isTransposed = false;
    }

    public Picture picture() {
        if (isTransposed)
            transpose();
        return new Picture(picture);
    }

    public int width() {
        if (isTransposed)
            transpose();
        return (picture.width());
    }

    public int height() {
        if (isTransposed)
            transpose();
        return (picture.height());
    }

    private class RGB {
        int r;
        int g;
        int b;

        public RGB(int encodedRGB) {
            this.r = (encodedRGB >> 16) & 0xFF;
            this.g = (encodedRGB >> 8) & 0xFF;
            this.b = (encodedRGB >> 0) & 0xFF;
        }
    }

    private int squaredDelta(RGB rgb1, RGB rgb2) {
        int rd = (rgb1.r - rgb2.r);
        int gd = (rgb1.g - rgb2.g);
        int bd = (rgb1.b - rgb2.b);

        return (rd * rd + gd * gd + bd * bd);
    }

    private double getEnergyHelper(int x, int y) {
        if (x == 0 || x == (picture.width() - 1) || y == 0 || y == (picture.height() - 1)) {
            return 1000;
        }

        RGB rgbx1 = new RGB(picture.getRGB(x - 1, y));
        RGB rgbx2 = new RGB(picture.getRGB(x + 1, y));
        int xd = squaredDelta(rgbx1, rgbx2);

        RGB rgby1 = new RGB(picture.getRGB(x, y - 1));
        RGB rgby2 = new RGB(picture.getRGB(x, y + 1));
        int yd = squaredDelta(rgby1, rgby2);

        return Math.sqrt(xd + yd);
    }

    public double energy(int x, int y) {
        if (isTransposed)
            transpose();

        if ((x < 0 || x >= picture.width()) || (y < 0 || y >= picture.height())) {
            throw new IllegalArgumentException("Argument outside of prescribed range");
        }

        return getEnergyHelper(x, y);

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

    private int get2dArray(int[][] array, int c, int r) {
        return array[r][c];
    }

    private double get2dArray(double[][] array, int c, int r) {
        return array[r][c];
    }

    private void set2dArray(int[][] array, int c, int r, int val) {
        array[r][c] = val;
    }

    private void set2dArray(double[][] array, int c, int r, double val) {
        array[r][c] = val;
    }

    private class SeamHelper {
        private final int UNKNOWN_EDGE = -1;

        private final int width;
        private final int height;

        private final double[][] energy;
        private final double[][] distTo;
        private final int[][] edgeTo;

        private double minDist = INFINITY;
        private int[] seam;

        // private final boolean transposed;

        public SeamHelper(double[][] energy) {

            this.height = energy.length;
            this.width = energy[0].length;

            this.energy = energy;
            this.distTo = new double[height][width];
            this.edgeTo = new int[height][width];

            for (double[] array : this.distTo) {
                Arrays.fill(array, INFINITY);
                // array[0] = 1000 * 1000;
            }
            Arrays.fill(distTo[0], 1000);

            for (int[] array : this.edgeTo) {
                Arrays.fill(array, UNKNOWN_EDGE);
            }
            // since the borders have the same energy, just set the path to be itself
            // TODO: check if this is actually needed
            for (int i = 0; i < this.width; i++) {
                this.edgeTo[0][i] = i;
            }
        }

        public double getEnergy(int c, int r) {
            double e;
            if (get2dArray(energy, c, r) == UNKNOWN_ENERGY) {
                e = getEnergyHelper(c, r);
                set2dArray(energy, c, r, e);
            }
            else {
                e = get2dArray(energy, c, r);
            }
            return e;
        }

        public void relax(int fromC, int ToC, int r, double prevDist) {
            double currDist = get2dArray(distTo, ToC, r);
            double currEnergy = getEnergy(ToC, r);
            if (prevDist + currEnergy < currDist) {
                set2dArray(distTo, ToC, r, prevDist + currEnergy);
                set2dArray(edgeTo, ToC, r, fromC);
            }
        }

        public double getDist(int c, int r) {
            return get2dArray(distTo, c, r);
        }

        private int getSeamEnd() {
            int seamEnd = -1;
            for (int c = 0; c <= width - 1; c++) {
                double d = get2dArray(distTo, c, height - 1);
                if (d < minDist) {
                    seamEnd = c;
                    minDist = d;
                }
            }
            return seamEnd;
        }

        public int[] getSeam() {
            int seamEnd = getSeamEnd();
            seam = new int[picture.height()];
            int pos = seamEnd;
            for (int i = picture.height() - 1; i >= 0; i--) {
                seam[i] = pos;
                pos = get2dArray(edgeTo, pos, i);
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

    private void transpose() {
        Picture tPicture = new Picture(picture.height(), picture.width());
        for (int col = 0; col < picture.width(); col++)
            for (int row = 0; row < picture.height(); row++)
                tPicture.setRGB(row, col, picture.getRGB(col, row));
        this.picture = tPicture;
        this.isTransposed = !this.isTransposed;
    }

    // finds the vertical seam
    private int[] findSeam() {
        int height = picture.height();
        int width = picture.width();

        SeamHelper seamHelper;
        double currMinDist = INFINITY;
        int[] currMinSeam = new int[height];
        double[][] energy;
        energy = new double[height][width];
        for (double[] array : energy) {
            Arrays.fill(array, UNKNOWN_ENERGY);
        }

        // loop through the potential start positions
        // for each starting pos, get the shortest path
        for (int n = 0; n < width - 1; n++) {
            seamHelper = new SeamHelper(energy);
            for (int r = 0; r <= height - 2; r++) {
                for (int c : relevantIndices(n, r)) {
                    double prevDist = seamHelper.getDist(c, r);
                    for (int cc : adj(c)) {
                        seamHelper.relax(c, cc, r + 1, prevDist);
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

    public int[] findVerticalSeam() {
        if (isTransposed)
            transpose();
        return findSeam();
    }

    // can calc because it is a triangle with bounds
    private int[] relevantIndices(int startPos, int depth) {
        int start = max(0, -depth + startPos);
        int end = min(picture.width() - 1, depth + startPos);
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

    public int[] findHorizontalSeam() {
        if (!isTransposed)
            transpose();
        return findSeam();
    }


    // removes vertical seam
    private void removeSeam(int[] seam) {
        if (seam.length != picture.height()) {
            throw new IllegalArgumentException(
                    "expected seam of length " + picture.height() + " not " + seam.length);
        }
        for (int s : seam) {
            if (s < 0 || s > (picture.width() - 1)) {
                throw new IllegalArgumentException(
                        "Entry " + s + " is outside the presribed range: 0:" + (picture.width()
                                - 1));
            }
        }
        for (int i = 1; i < picture.height(); i++) {
            int delta;
            delta = seam[i - 1] - seam[i];
            if (delta < -1 || delta > 1) {
                throw new IllegalArgumentException(
                        "Invalid seam. Adjacent seam entries mustn't differ by more than 1");
            }
        }
        int seamPos;
        int oldCol;
        Picture newPicture = new Picture(picture.width() - 1, picture.height());
        for (int row = 0; row < picture.height(); row++) {
            seamPos = seam[row];
            for (int col = 0; col < picture.width() - 1; col++) {
                oldCol = col;
                if (oldCol >= seamPos) {
                    oldCol = oldCol + 1;
                }
                newPicture.setRGB(col, row, picture.getRGB(oldCol, row));
            }
        }
        this.picture = newPicture;
    }

    public void removeVerticalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException("seam cannot be null");
        }
        if (isTransposed)
            transpose();
        if (picture.width() <= 1) {
            throw new IllegalArgumentException(
                    "Cannot remove vertical seam on picture with width <= 1");
        }
        removeSeam(seam);
    }

    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException("seam cannot be null");
        }
        if (!isTransposed)
            transpose();
        if (picture.width() <= 1) { // width because its transposed
            throw new IllegalArgumentException(
                    "Cannot remove horizontal seam on picture with height <= 1");
        }
        removeSeam(seam);
    }

    public static void main(String[] args) {

        // Picture picture = new Picture("./7x10.png");
        Picture picture = new Picture("./3x4.png");

        SeamCarver sc = new SeamCarver(picture);

        System.out.println("------------------");
        int[] vertSeam = sc.findVerticalSeam();
        for (int a : vertSeam) {
            System.out.println(a);
        }

        sc.removeVerticalSeam(vertSeam);

        System.out.println("------------------");
        int[] horiSeam = sc.findHorizontalSeam();
        for (int a : horiSeam) {
            System.out.println(a);
        }

    }

}
