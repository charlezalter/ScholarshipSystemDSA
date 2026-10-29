// ApplicationQueue is a queue made of linked Nodes. First in, first out (FIFO).
// The oldest application is at the front (head). The newest is at the back (tail).
// A scholarship uses it for the Pending applications.
// The Approved and Rejected lists use the same class, because they also only need
// add at the back, remove at a position, and walk through the list.
public class ApplicationQueue {

    // ---------- Attributes ----------

    private Node head; // front of the queue (oldest)
    private Node tail; // back of the queue (newest)
    private int size;  // how many applications are in the queue


    // ---------- Constructor ----------

    // Purpose: make an empty queue.
    public ApplicationQueue() {
        head = null;
        tail = null;
        size = 0;
    }


    // ---------- Queue operations ----------

    // Purpose: enqueue. Add an application at the back of the queue.
    public void enqueue(Application application) {
        Node node = new Node(application);

        if (head == null) {
            head = node;       // the queue was empty, so this is the only node
            tail = node;
        } else {
            tail.next = node;  // link it after the last node
            tail = node;
        }
        size++;
    }

    // Purpose: dequeue. Take the application at the front of the queue.
    // Returns the application, or null if the queue is empty.
    public Application dequeue() {
        if (head == null) {
            return null;
        }

        Application front = head.data;
        head = head.next;
        if (head == null) {
            tail = null;       // the queue is empty now
        }
        size--;
        return front;
    }

    // Purpose: delete by position (Lesson 10). Position 0 is the front.
    // Returns the application, or null if the position does not exist.
    public Application removeAt(int position) {
        if (position < 0) {
            return null;
        }
        if (position >= size) {
            return null;
        }
        if (position == 0) {
            return dequeue();
        }

        // walk to the node just before the one to delete
        Node before = head;
        for (int i = 0; i < position - 1; i++) {
            before = before.next;
        }

        Node target = before.next;
        before.next = target.next;   // skip the target node
        if (target == tail) {
            tail = before;           // we deleted the last node
        }
        size--;
        return target.data;
    }


    // ---------- Reading the queue ----------

    // Purpose: give the application at a position, without removing it.
    // Returns null if the position does not exist.
    public Application get(int position) {
        if (position < 0) {
            return null;
        }
        if (position >= size) {
            return null;
        }

        Node node = head;
        for (int i = 0; i < position; i++) {
            node = node.next;
        }
        return node.data;
    }

    // Purpose: find the position of an application (the same object).
    // Returns -1 if it is not in the queue.
    public int indexOf(Application application) {
        Node node = head;
        int position = 0;

        while (node != null) {
            if (node.data == application) {
                return position;
            }
            node = node.next;
            position++;
        }
        return -1;
    }

    // Purpose: check if an email is in the queue. Capital letters do not matter.
    public boolean containsEmail(String email) {
        Node node = head;

        while (node != null) {
            if (node.data.getEmail().equalsIgnoreCase(email.trim())) {
                return true;
            }
            node = node.next;
        }
        return false;
    }

    // Purpose: copy the applications into an array, front first.
    // The queue itself is not changed.
    public Application[] toArray() {
        Application[] items = new Application[size];
        Node node = head;
        int i = 0;

        while (node != null) {
            items[i] = node.data;
            node = node.next;
            i++;
        }
        return items;
    }

    // Purpose: tell how many applications are in the queue.
    public int size() {
        return size;
    }

    // Purpose: tell if the queue has nothing in it.
    public boolean isEmpty() {
        return size == 0;
    }
}