// Person is the parent class of everyone who uses the system.
// It has no attributes: it only says what every role must be able to tell.
// It is abstract: only a Student or the Benefactor can be created.
public abstract class Person {

    // ---------- Abstract methods ----------
    // Each child class (Student, Benefactor) writes its own body.

    // Purpose: give the name of the role, "Student" or "Benefactor".
    public abstract String getRole();

    // Purpose: give the menu items of this role.
    public abstract String[] getMenuOptions();
}