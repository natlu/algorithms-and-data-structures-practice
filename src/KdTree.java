/*
princeton assignment
*/

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.LinkedList;

public class KdTree {
    private static final int DIM = 2;
    private Node root;
    private int count;

    private class Node {
        private Point2D p;
        private Node left, right;
        private int depth;
        private RectHV rect;

        public Node(Point2D p, int depth) {
            this(p, depth, new RectHV(0.0, 0.0, 1.0, 1.0));
        }

        public Node(Point2D p, int depth, RectHV rect) {
            this.p = p;
            this.depth = depth;
            this.rect = rect;
        }
    }

    // construct an empty set of points
    public KdTree() {
        root = null;
        count = 0;
    }

    // is the set empty?
    public boolean isEmpty() {
        return (count == 0);
    }

    // number of points in the set
    public int size() {
        return count;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("null is an invalid Point2D");
        root = insert(root, null, p);
    }

    private Node insert(Node node, Node prevNode, Point2D p) {
        if (node == null) {
            count++;
            if (prevNode == null) return new Node(p, 1);
            else return new Node(p, prevNode.depth + 1, genRect(p, prevNode));
        }

        if (p.compareTo(node.p) == 0) return node;

        double compareVal = compare(p, node);
        if (compareVal < 0) node.left = insert(node.left, node, p);
        else node.right = insert(node.right, node, p);

        return node;
    }

    // create a new rectangle
    private RectHV genRect(Point2D p, Node prevNode) {
        int i = prevNode.depth % DIM;
        double comparePrevVal = compare(p, prevNode);
        if (i == 1) { // prevNode is an x-axis split
            double ymin = prevNode.rect.ymin();
            double ymax = prevNode.rect.ymax();
            if (comparePrevVal < 0)
                return new RectHV(prevNode.rect.xmin(), ymin, prevNode.p.x(), ymax);
            else
                return new RectHV(prevNode.p.x(), ymin, prevNode.rect.xmax(), ymax);
        }
        else {
            double xmin = prevNode.rect.xmin();
            double xmax = prevNode.rect.xmax();
            if (comparePrevVal < 0)
                return new RectHV(xmin, prevNode.rect.ymin(), xmax, prevNode.p.y());
            else
                return new RectHV(xmin, prevNode.p.y(), xmax, prevNode.rect.ymax());
        }
    }

    // compare point relative to a node
    // negative means point is less than the node
    private double compare(Point2D p, Node node) {
        int i = node.depth % DIM;
        if (i == 1) return p.x() - node.p.x();
        else return p.y() - node.p.y();
    }

    // returns either the x or y value depending on depth
    private double pointValue(Point2D p, int depth) {
        int i = depth % DIM;
        // used to interact with algs4 Point2D
        // gets x or y coord
        if (i == 0) return p.x();
        else return p.y(); // (i == 1)
    }

    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("null is an invalid Point2D");
        return contains(root, p);
    }

    private boolean contains(Node node, Point2D p) {
        if (node == null) return false;
        // check if points are the same
        if (node.p.compareTo(p) == 0) return true;
        double compareVal = compare(p, node);
        if (compareVal < 0) return contains(node.left, p);
        else return contains(node.right, p);
    }

    // draw all points to standard draw
    public void draw() {
        draw(root);
    }

    private void draw(Node node) {
        if (node == null) return;
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        node.p.draw();
        if (node.depth % DIM == 1) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.setPenRadius();
            RectHV linex = new RectHV(node.p.x(), node.rect.ymin(), node.p.x(), node.rect.ymax());
            linex.draw();
        }
        else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.setPenRadius();
            RectHV liney = new RectHV(node.rect.xmin(), node.p.y(), node.rect.xmax(), node.p.y());
            liney.draw();
        }

        draw(node.left);
        draw(node.right);
    }


    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("null not a valid RectHV");
        LinkedList<Point2D> ll = new LinkedList<>();
        range(root, rect, ll);
        return ll;
    }

    private void range(Node node, RectHV rect, LinkedList<Point2D> ll) {
        if (node == null) return;
        if (!rect.intersects(node.rect)) return;
        if (rect.contains(node.p)) ll.add(node.p);
        range(node.left, rect, ll);
        range(node.right, rect, ll);
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("null not a valid Point2D");
        if (root == null) return null;
        return nearest(root, p, root.p);
    }

    private Point2D nearest(Node node, Point2D p, Point2D smallest) {
        if (node == null) return null;

        Point2D localSmall = closestOf2(node.p, smallest, p);

        double compVal = compare(p, node);
        Node firstNode;
        Node secondNode;
        if (compVal < 0) {
            firstNode = node.left;
            secondNode = node.right;
        }
        else {
            firstNode = node.right;
            secondNode = node.left;
        }

        Point2D np = nearest(firstNode, p, localSmall);
        if (np != null) localSmall = closestOf2(node.p, np, p);

        if (secondNode != null) {
            if (localSmall.distanceSquaredTo(p) > secondNode.rect.distanceSquaredTo(p)) {
                np = nearest(secondNode, p, localSmall);
                if (np != null) localSmall = closestOf2(np, localSmall, p);
            }
        }

        return localSmall;
    }

    // returns the closest point, of p1 or p2, to another point, x
    private Point2D closestOf2(Point2D p1, Point2D p2, Point2D x) {
        if (p1.distanceSquaredTo(x) < p2.distanceSquaredTo(x)) return p1;
        else return p2;
    }

    public static void main(String[] args) {

    }
}
