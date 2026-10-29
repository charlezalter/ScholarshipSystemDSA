// Grant is one record of money given to an approved scholar.
// The scholar confirms or disputes it outside the system.
// The benefactor then sets the status here: Confirmed, or Voided with a reason.
// A record is never edited or deleted. A wrong one is Voided, and a new one is recorded.
// Grant is also a node of the grants list that a Scholarship keeps (next).
public class Grant {

    // ---------- Attributes ----------

    private Application scholar; // the approved student who got the money
    private double amount;       // how much was sent
    private String status;       // "Sent", "Confirmed" or "Voided"
    private String reason;       // why it was Voided (empty until then)
    private String sentOn;       // date it was recorded, yyyy-MM-dd
    private Grant next;          // next record in the list


    // ---------- Constructor ----------

    // Purpose: record a new grant. The status always starts as Sent.
    public Grant(Application scholar, double amount, String sentOn) {
        this.scholar = scholar;
        this.amount = amount;
        this.sentOn = sentOn;
        this.status = "Sent";
        this.reason = "";
        this.next = null;
    }


    // ---------- Rules ----------

    // Purpose: set the grant to Confirmed.
    // Returns true if it was changed, and false if the grant is Voided (Voided is final).
    public boolean markConfirmed() {
        if (status.equals("Voided")) {
            return false;
        }
        status = "Confirmed";
        return true;
    }

    // Purpose: set the grant to Voided and keep the reason.
    // Returns true if it was changed.
    // Returns false if the grant is already Voided, or the reason is empty.
    public boolean markVoided(String newReason) {
        if (status.equals("Voided")) {
            return false;
        }
        if (newReason.trim().equals("")) {
            return false;
        }
        status = "Voided";
        reason = newReason.trim();
        return true;
    }


    // ---------- Getters and list link ----------

    public Application getScholar() {
        return scholar;
    }

    public double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public String getSentOn() {
        return sentOn;
    }

    public Grant getNext() {
        return next;
    }

    public void setNext(Grant next) {
        this.next = next;
    }
}