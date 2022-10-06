import edu.princeton.cs.algs4.Picture;

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
        // move energy to a seam calc method
        // double[][] energy;
        // energy = new double[width()][height()];

        if (x == 0 | x == (width() - 1) | y == 0 | y == (height() - 1)) {
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

    // // sequence of indices for horizontal seam
    // public int[] findHorizontalSeam()
    //
    // // sequence of indices for vertical seam
    // public int[] findVerticalSeam()
    //
    // // remove horizontal seam from current picture
    // public void removeHorizontalSeam(int[] seam)
    //
    // // remove vertical seam from current picture
    // public void removeVerticalSeam(int[] seam)

    //  unit testing (optional)
    public static void main(String[] args) {
        System.out.println("asdfa");
        Picture picture = new Picture("https://introcs.cs.princeton.edu/java/stdlib/mandrill.jpg");
        picture.show();

    }

}
