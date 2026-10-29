// Application is what a student sends after filling out the form.
// It keeps the email, the name, the PDF file, and the status of the application.
// All the other information of the student (requirements, qualification, contact) is inside the PDF.
public class Application {

    // ---------- Attributes ----------

    private String email;       // email of the student
    private String name;        // full name of the student
    private String pdfFile;     // file name or path of the PDF, ends with .pdf
    private String status;      // "Pending", "Approved" or "Rejected"
    private String submittedOn; // date it was sent, yyyy-MM-dd (no time)


    // ---------- Constructor ----------

    // Purpose: make a new application. today is the current date, yyyy-MM-dd.
    // The form must check the PDF with isPdfName first.
    // A new application always starts as Pending.
    public Application(String email, String name, String pdfFile, String today) {
        this.email = email.trim();
        this.name = name.trim();
        this.pdfFile = pdfFile.trim();
        this.submittedOn = today;
        this.status = "Pending";
    }


    // ---------- Rules ----------

    // Purpose: check if a file name is a PDF.
    // Returns true if it is good, and false if not.
    public static boolean isPdfName(String fileName) {
        String file = fileName.trim().toLowerCase();

        if (file.length() <= 4) {
            return false;                 // nothing before the .pdf
        }
        if (file.endsWith(".pdf") == false) {
            return false;                 // must end with .pdf
        }
        return true;
    }

    // Purpose: the benefactor approves the application.
    // Returns true if it is Approved now, and false if it was already decided.
    public boolean approve() {
        if (status.equals("Pending") == false) {
            return false;
        }
        status = "Approved";
        return true;
    }

    // Purpose: the benefactor rejects the application.
    // Returns true if it is Rejected now, and false if it was already decided.
    public boolean reject() {
        if (status.equals("Pending") == false) {
            return false;
        }
        status = "Rejected";
        return true;
    }

    // Purpose: the benefactor removes an approved student.
    // The status becomes Rejected.
    // Returns true if it was Approved, and false if not.
    public boolean removeApproval() {
        if (status.equals("Approved") == false) {
            return false;
        }
        status = "Rejected";
        return true;
    }

    // Purpose: tell if the benefactor has not decided yet.
    public boolean isPending() {
        return status.equals("Pending");
    }


    // ---------- Getters ----------

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPdfFile() {
        return pdfFile;
    }

    public String getStatus() {
        return status;
    }

    public String getSubmittedOn() {
        return submittedOn;
    }
}