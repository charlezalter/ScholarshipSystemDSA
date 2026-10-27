// Student is a person who applies to the scholarship through the form.
// Like a Google form, it only keeps the email.
// The name, contact number and the other details are answers in the Application.
public class Student extends Person {

    // ---------- Attributes ----------

    private String email; // email of the student


    // ---------- Constructor ----------

    // Purpose: fill in the email of a new student.
    public Student(String email) {
        this.email = email.trim();
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
        return new String[] {"Fill out the form", "Check my application", "Back"};
    }


    // ---------- Getters ----------

    public String getEmail() {
        return email;
    }
}