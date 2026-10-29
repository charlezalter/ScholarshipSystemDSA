// Student is a person who applies to a scholarship through the form.
// Like a Google form, it only keeps the email.
// The name and the other details are answers in the Application.
public class Student extends Person {

    // ---------- Attributes ----------

    private String email; // email of the student


    // ---------- Constructor ----------

    // Purpose: fill in the email of a new student.
    public Student(String email) {
        this.email = email.trim();
    }


    // ---------- Rules ----------

    // Purpose: check if a text can be used as an email.
    // Returns true if it is good, and false if not.
    public static boolean isValidEmail(String text) {
        String email = text.trim();
        int at = email.indexOf("@");        // place of the first @
        int dot = email.lastIndexOf(".");   // place of the last dot

        if (email.contains(" ")) {
            return false;                   // no spaces allowed
        }
        if (at < 1) {
            return false;                   // no @, or nothing before the @
        }
        if (email.indexOf("@", at + 1) != -1) {
            return false;                   // a second @
        }
        if (dot < at + 2) {
            return false;                   // no dot after the @, or nothing between them
        }
        if (dot == email.length() - 1) {
            return false;                   // nothing after the dot
        }
        return true;
    }


    // ---------- Abstract methods from Person ----------

    // Purpose: give the name of the role.
    @Override
    public String getRole() {
        return "Student";
    }

    // Purpose: give the menu items of the student.
    @Override
    public String[] getMenuOptions() {
        return new String[] {"View scholarships", "Back"};
    }


    // ---------- Getters ----------

    public String getEmail() {
        return email;
    }
}