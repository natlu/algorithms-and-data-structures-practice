/*
 * Princeton MOOC assignment
 */

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {

    private Node front;
    private Node back;
    private int n; // size of the Deque

    // helper linked list class
    private class Node {
        private Item item;
        private Node next;
        private Node prev;
    }

    // construct an empty deque
    public Deque() {
        front = null;
        back = null;
        n = 0;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return n == 0;
    }

    // return the number of items on the deque
    public int size() {
        return n;
    }

    // throw exception if item is invalid
    private void checkItem(Item item) {
        if (item == null)
            throw new IllegalArgumentException("null not a valid input");
    }

    // add the item to the front
    public void addFirst(Item item) {
        checkItem(item);
        Node newFront = new Node();
        newFront.item = item;
        newFront.next = front;
        newFront.prev = null;
        if (n > 0) front.prev = newFront;
        front = newFront;
        n++;
        if (size() == 1) back = front;
    }

    // add the item to the back
    public void addLast(Item item) {
        checkItem(item);
        Node newBack = new Node();
        newBack.item = item;
        newBack.next = null;
        newBack.prev = back;
        if (n > 0) back.next = newBack;
        back = newBack;
        n++;
        if (size() == 1) front = back;
    }

    // check if removing from the deque is possible
    private void checkRemovability() {
        if (isEmpty())
            throw new NoSuchElementException("Cannot remove from empty deque");
    }

    // remove and return the item from the front
    public Item removeFirst() {
        checkRemovability();
        Item returnItem = front.item;
        front = front.next;
        if (front != null) {
            front.prev = null;
        } else {
            back = null;
        }
        n--;
        return returnItem;
    }

    // remove and return the item from the back
    public Item removeLast() {
        checkRemovability();
        Item returnItem = back.item;
        back = back.prev;
        if (back != null) {
            back.next = null;
        } else {
            front = null;
        }
        n--;
        return returnItem;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<Item> {
        private Node current = front;

        public boolean hasNext() {
            return current != null;
        }

        public Item next() {
            if (!hasNext())
                throw new NoSuchElementException("Iterator has no next element");
            Item returnItem = current.item;
            current = current.next;
            return returnItem;
        }

        public void remove() {
            throw new UnsupportedOperationException("you shall not remove!");
        }
    }

    public static void main(String[] args) {

    }

}
