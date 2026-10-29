// Node is one box of the linked list.
// It holds one Application (data) and the link to the next Node (next).
// If next is null, this Node is the last one.
public class Node {
    Application data;
    Node next;

    // Purpose: make a Node for an application. It has no next Node yet.
    public Node(Application data) {
        this.data = data;
        this.next = null;
    }
}