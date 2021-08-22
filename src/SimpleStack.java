import java.util.NoSuchElementException;

public class SimpleStack<T> {
    private Node first;
    private int n; // to keep track of size

    // helper class Node for linked list implementation
    private class Node {
        private T obj;
        private Node next;
    }

    public SimpleStack() {
        first = null;
        n = 0;
    }

    // insert onto stack
    public void push(T obj) {
        Node newObj = new Node();
        newObj.obj = obj;
        newObj.next = first;
        first = newObj;
        n++;
    }

    // remove from stack and return removed obj (most recent)
    public T pop() {
        if (isEmpty())
            throw new NoSuchElementException("Cannot pop from empty stack");
        Node poppedNode = first;
        first = first.next;
        poppedNode.next = null;
        n--;
        return poppedNode.obj;
    }

    public T peek() {
        if (isEmpty())
            throw new NoSuchElementException("Cannot peek from empty stack");
        return first.obj;
    }

    public boolean isEmpty() {
        return n == 0;
    }

    public int size() {
        return n;
    }

    public static void main(String[] args)
    {
        SimpleStack<String> strStack = new SimpleStack<>();

        // insert onto stack
        strStack.push("a");
        strStack.push("b");
        strStack.push("c");

        // print information about current stack
        System.out.println("stack is size: " + strStack.size());
        System.out.println("top of stack contains: " + strStack.peek());

        // remove from top of stack
        strStack.pop();
        System.out.println("stack is size: " + strStack.size());
        System.out.println("top of stack contains: " + strStack.peek());

        // remove all from stack
        strStack.pop();
        strStack.pop();
        System.out.println("stack is size: " + strStack.size());

        // try to peek at empty stack
        System.out.println("top of stack contains: " + strStack.peek());
    }
}
