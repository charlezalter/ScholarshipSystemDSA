// Benefactor is the one person who owns the system.
// It keeps only the name, which is shown on the benefactor page.
public class Benefactor extends Person {

    // ---------- Attributes ----------

    private String name; // full name of the benefactor


    // ---------- Constructor ----------

    // Purpose: fill in the name of the benefactor.
    public Benefactor(String name) {
        this.name = name.trim();
    }


    // ---------- Abstract methods from Person ----------

    // Purpose: give the name of the role.
    @Override
    public String getRole() {
        return "Benefactor";
    }

    // Purpose: give the menu items of the benefactor page.
    @Override
    public String[] getMenuOptions() {
        return new String[] {"My scholarships", "Post scholarship", "Grants", "Back"};
    }


    // ---------- Getters ----------

    public String getName() {
        return name;
    }
}