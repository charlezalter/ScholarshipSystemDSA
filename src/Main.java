// Main starts the program.
public class Main {

    // Purpose: make the benefactor and the store, then open the console menu.
    // Change the name below to the real benefactor.
    public static void main(String[] args) {
        Benefactor benefactor = new Benefactor("admin");
        DataStore store = new DataStore(benefactor);
        ConsoleMenu menu = new ConsoleMenu(store);
        menu.run();
    }
}