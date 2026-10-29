// Scholarship is the one thing the benefactor posts.
// It keeps its details and its own three lists of applications:
// Pending (a queue), Approved and Rejected.
// It also keeps its list of grants, and the link to the next scholarship (next).
public class Scholarship {

    // ---------- Attributes ----------

    private String title;          // name of the scholarship
    private String description;    // what the scholarship is about
    private String qualification;  // who can apply
    private String requirements;   // what the PDF must contain
    private String benefits;       // what the student gets
    private int slots;             // how many students can be approved
    private String deadline;       // shown to students only, not checked
    private boolean open;          // true = students can see it, false = closed

    private ApplicationQueue pending;   // waiting for the benefactor, oldest first
    private ApplicationQueue approved;  // approved students
    private ApplicationQueue rejected;  // rejected or removed students

    private Grant grantHead;       // first record of the grants list
    private int grantCount;        // how many grant records
    private Scholarship next;      // next scholarship in the DataStore list


    // ---------- Constructor ----------

    // Purpose: post a new scholarship. It starts Open, with empty lists.
    public Scholarship(String title, String description, String qualification,
                       String requirements, String benefits, int slots, String deadline) {
        this.title = title.trim();
        this.description = description.trim();
        this.qualification = qualification.trim();
        this.requirements = requirements.trim();
        this.benefits = benefits.trim();
        this.slots = slots;
        this.deadline = deadline.trim();
        this.open = true;
        this.pending = new ApplicationQueue();
        this.approved = new ApplicationQueue();
        this.rejected = new ApplicationQueue();
        this.grantHead = null;
        this.grantCount = 0;
        this.next = null;
    }


    // ---------- Edit rules ----------
    // Each setter returns true if the new value was saved.
    // It returns false if the value is empty (the old value stays).

    public boolean setTitle(String value) {
        if (value.trim().equals("")) {
            return false;
        }
        title = value.trim();
        return true;
    }

    public boolean setDescription(String value) {
        if (value.trim().equals("")) {
            return false;
        }
        description = value.trim();
        return true;
    }

    public boolean setQualification(String value) {
        if (value.trim().equals("")) {
            return false;
        }
        qualification = value.trim();
        return true;
    }

    public boolean setRequirements(String value) {
        if (value.trim().equals("")) {
            return false;
        }
        requirements = value.trim();
        return true;
    }

    public boolean setBenefits(String value) {
        if (value.trim().equals("")) {
            return false;
        }
        benefits = value.trim();
        return true;
    }

    public boolean setDeadline(String value) {
        if (value.trim().equals("")) {
            return false;
        }
        deadline = value.trim();
        return true;
    }

    // Purpose: change the number of slots.
    // Returns false if it is below 1, or below the number of students already approved.
    public boolean setSlots(int value) {
        if (value < 1) {
            return false;
        }
        if (value < approved.size()) {
            return false;
        }
        slots = value;
        return true;
    }


    // ---------- Open, close, renew ----------

    // Purpose: close the scholarship. Students cannot see it. Applicants and decisions stay.
    public void close() {
        open = false;
    }

    // Purpose: open a closed scholarship again, after the benefactor updated the details.
    // Returns true if it is open now.
    // Returns false if it was already open, or the slots are not higher than the approved count.
    public boolean renew() {
        if (open) {
            return false;
        }
        if (slots <= approved.size()) {
            return false;
        }
        open = true;
        return true;
    }

    // Purpose: tell if every slot is taken by an approved student.
    public boolean isFull() {
        return approved.size() >= slots;
    }

    // Purpose: tell how many slots are still free.
    public int getSlotsLeft() {
        return slots - approved.size();
    }


    // ---------- Applications ----------

    // Purpose: check if this email can apply now, before the student types anything else.
    // Returns "OK" if it can, or the message why not.
    public String canApply(String email) {
        if (open == false) {
            return "This scholarship is closed.";
        }
        if (isFull()) {
            return "Scholarship full.";
        }
        if (pending.containsEmail(email)) {
            return "This email already applied to this scholarship.";
        }
        if (approved.containsEmail(email)) {
            return "This email already applied to this scholarship.";
        }
        if (rejected.containsEmail(email)) {
            return "This email already applied to this scholarship.";
        }
        return "OK";
    }

    // Purpose: a student applies. The application joins the back of the Pending queue.
    // It repeats the canApply checks, so it is safe even if the menu forgot to call it.
    // Returns "OK" if it was added, or the message why it was refused.
    public String apply(Application application) {
        String check = canApply(application.getEmail());
        if (check.equals("OK") == false) {
            return check;
        }
        pending.enqueue(application);
        return "OK";
    }

    // Purpose: approve the pending application at a position (0 is the oldest).
    // Returns "OK" or the message why it was refused.
    public String approveAt(int position) {
        Application chosen = pending.get(position);
        if (chosen == null) {
            return "No such applicant.";
        }
        if (isFull()) {
            return "Scholarship full. Remove an approved student or raise the slots first.";
        }

        pending.removeAt(position);
        chosen.approve();
        approved.enqueue(chosen);
        return "OK";
    }

    // Purpose: reject the pending application at a position (0 is the oldest).
    // Returns "OK" or the message why it was refused.
    public String rejectAt(int position) {
        Application chosen = pending.get(position);
        if (chosen == null) {
            return "No such applicant.";
        }

        pending.removeAt(position);
        chosen.reject();
        rejected.enqueue(chosen);
        return "OK";
    }

    // Purpose: remove an approved student (position 0 is the first approved).
    // The student goes to the Rejected list and the slot is free again.
    // Grant records are not touched.
    // Returns "OK" or the message why it was refused.
    public String removeApprovedAt(int position) {
        Application chosen = approved.get(position);
        if (chosen == null) {
            return "No such approved student.";
        }

        approved.removeAt(position);
        chosen.removeApproval();
        rejected.enqueue(chosen);
        return "OK";
    }


    // ---------- Grants ----------

    // Purpose: add a grant record at the end of the grants list (like Lesson 10 insertion).
    public void addGrant(Grant grant) {
        if (grantHead == null) {
            grantHead = grant;
        } else {
            Grant last = grantHead;
            while (last.getNext() != null) {
                last = last.getNext();
            }
            last.setNext(grant);
        }
        grantCount++;
    }

    // Purpose: give the grant record at a position (0 is the first), or null if there is none.
    public Grant getGrantAt(int position) {
        if (position < 0) {
            return null;
        }
        if (position >= grantCount) {
            return null;
        }

        Grant node = grantHead;
        for (int i = 0; i < position; i++) {
            node = node.getNext();
        }
        return node;
    }

    public int getGrantCount() {
        return grantCount;
    }


    // ---------- Getters and list link ----------

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getQualification() {
        return qualification;
    }

    public String getRequirements() {
        return requirements;
    }

    public String getBenefits() {
        return benefits;
    }

    public int getSlots() {
        return slots;
    }

    public String getDeadline() {
        return deadline;
    }

    public boolean isOpen() {
        return open;
    }

    public ApplicationQueue getPending() {
        return pending;
    }

    public ApplicationQueue getApproved() {
        return approved;
    }

    public ApplicationQueue getRejected() {
        return rejected;
    }

    public Scholarship getNext() {
        return next;
    }

    public void setNext(Scholarship next) {
        this.next = next;
    }
}