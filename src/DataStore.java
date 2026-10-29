// DataStore keeps everything the system remembers while it runs (nothing is saved to a file).
// It holds the benefactor and a linked list of the scholarships the benefactor posted.
// It also has the sorting algorithm (insertion sort) for the applicants list.
public class DataStore {

    // ---------- Attributes ----------

    private Benefactor benefactor; // the one owner of the system
    private Scholarship head;      // first scholarship of the list
    private int count;             // how many scholarships were posted


    // ---------- Constructor ----------

    // Purpose: make a new store for the benefactor, with no scholarships yet.
    public DataStore(Benefactor benefactor) {
        this.benefactor = benefactor;
        this.head = null;
        this.count = 0;
    }


    // ---------- Scholarships (linked list) ----------

    // Purpose: add a scholarship at the end of the list (like Lesson 10 insertion).
    public void addScholarship(Scholarship scholarship) {
        if (head == null) {
            head = scholarship;
        } else {
            Scholarship last = head;
            while (last.getNext() != null) {
                last = last.getNext();
            }
            last.setNext(scholarship);
        }
        count++;
    }

    // Purpose: give the scholarship at a position (0 is the first posted), or null if there is none.
    public Scholarship getAt(int position) {
        if (position < 0) {
            return null;
        }
        if (position >= count) {
            return null;
        }

        Scholarship node = head;
        for (int i = 0; i < position; i++) {
            node = node.getNext();
        }
        return node;
    }

    // Purpose: tell how many scholarships were posted.
    public int size() {
        return count;
    }

    public Benefactor getBenefactor() {
        return benefactor;
    }


    // ---------- Sorting ----------

    // Purpose: insertion sort (Lesson 12) on a COPY of the array, so the queue stays in submit order.
    // by = "name" sorts A to Z, by = "date" sorts the oldest date first.
    // Equal items keep their submit order.
    // Returns the sorted copy.
    public Application[] insertionSort(Application[] source, String by) {
        // make the copy
        Application[] items = new Application[source.length];
        for (int i = 0; i < source.length; i++) {
            items[i] = source[i];
        }

        // take each item and slide it left until it is in the right place
        for (int i = 1; i < items.length; i++) {
            Application key = items[i];
            int j = i - 1;

            while (j >= 0) {
                if (isAfter(items[j], key, by) == false) {
                    break;                  // items[j] is already in the right order
                }
                items[j + 1] = items[j];    // move items[j] one place to the right
                j--;
            }
            items[j + 1] = key;
        }
        return items;
    }

    // Purpose: tell if application x must come AFTER application y.
    // Name: compares A to Z, capital letters do not matter.
    // Date: compares yyyy-MM-dd as text, which is the same as date order.
    private boolean isAfter(Application x, Application y, String by) {
        if (by.equals("date")) {
            return x.getSubmittedOn().compareTo(y.getSubmittedOn()) > 0;
        }
        return x.getName().compareToIgnoreCase(y.getName()) > 0;
    }
}