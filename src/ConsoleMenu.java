import java.time.LocalDate;
import java.util.Scanner;

// ConsoleMenu shows every menu and asks the user to type.
// It does not keep the rules itself. The rules are in Scholarship, Application and Grant.
public class ConsoleMenu {

    // ---------- Attributes ----------

    private DataStore store; // everything the system remembers
    private Scanner input;   // reads what the user types


    // ---------- Constructor ----------

    // Purpose: make the menu for a store.
    public ConsoleMenu(DataStore store) {
        this.store = store;
        this.input = new Scanner(System.in);
    }


    // ---------- Main menu ----------

    // Purpose: show the main menu until the user chooses Exit. No login: just pick a role.
    public void run() {
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("=== Scholarship System ===");
            System.out.println("1. Benefactor");
            System.out.println("2. Student");
            System.out.println("3. Exit");
            int choice = readNumber("Choose: ", 1, 3);

            if (choice == 1) {
                benefactorPage();
            }
            if (choice == 2) {
                studentPage();
            }
            if (choice == 3) {
                running = false;
            }
        }
        System.out.println("Goodbye.");
    }


    // ================= BENEFACTOR =================

    // Purpose: the benefactor page. The header shows the name of the benefactor.
    private void benefactorPage() {
        Benefactor benefactor = store.getBenefactor();
        String[] options = benefactor.getMenuOptions();
        boolean inPage = true;

        while (inPage) {
            System.out.println();
            System.out.println("=== " + benefactor.getName() + " - Scholarship Page ===");
            printMenu(options);
            int choice = readNumber("Choose: ", 1, options.length);

            if (choice == 1) {
                myScholarships();
            }
            if (choice == 2) {
                postScholarship();
            }
            if (choice == 3) {
                grantsPage();
            }
            if (choice == 4) {
                inPage = false;
            }
        }
    }

    // Purpose: ask for the details and post a new scholarship. It starts Open.
    private void postScholarship() {
        System.out.println();
        System.out.println("--- Post scholarship ---");
        String title = readText("Title: ");
        String description = readText("Description: ");
        String qualification = readText("Qualification (who can apply): ");
        String requirements = readText("Requirements (what the PDF must contain): ");
        String benefits = readText("Benefits (what the student gets): ");
        int slots = readNumber("Slots (how many students can be approved): ", 1, 100000);
        String deadline = readText("Deadline (shown to students, example 2026-12-15): ");

        Scholarship scholarship = new Scholarship(title, description, qualification,
                requirements, benefits, slots, deadline);
        store.addScholarship(scholarship);
        System.out.println("Scholarship posted: " + title);
    }

    // Purpose: list every scholarship that was posted, and open the one the benefactor picks.
    private void myScholarships() {
        while (true) {
            System.out.println();
            System.out.println("--- My scholarships ---");
            if (store.size() == 0) {
                System.out.println("No scholarships yet. Use Post scholarship.");
                return;
            }

            for (int i = 0; i < store.size(); i++) {
                System.out.println((i + 1) + ". " + describe(store.getAt(i)));
            }
            int pick = readNumber("Pick a scholarship (0 = Back): ", 0, store.size());
            if (pick == 0) {
                return;
            }
            scholarshipMenu(store.getAt(pick - 1));
        }
    }

    // Purpose: the menu of one scholarship.
    private void scholarshipMenu(Scholarship s) {
        boolean inMenu = true;

        while (inMenu) {
            int approvedCount = s.getApproved().size();
            int rejectedCount = s.getRejected().size();
            String closeLabel = "Close";
            if (s.isOpen() == false) {
                closeLabel = "Renew";
            }

            System.out.println();
            System.out.println("--- " + describe(s) + " ---");
            String[] options = {
                    "My applicants (" + s.getPending().size() + ")",
                    "Reviewed (" + (approvedCount + rejectedCount) + ")",
                    "Approved (" + approvedCount + " of " + s.getSlots() + ")",
                    "Rejected (" + rejectedCount + ")",
                    "Edit scholarship",
                    closeLabel,
                    "Back"
            };
            printMenu(options);
            int choice = readNumber("Choose: ", 1, 7);

            if (choice == 1) {
                myApplicants(s);
            }
            if (choice == 2) {
                showReviewed(s);
            }
            if (choice == 3) {
                approvedPage(s);
            }
            if (choice == 4) {
                showRejected(s);
            }
            if (choice == 5) {
                editScholarship(s);
            }
            if (choice == 6) {
                closeOrRenew(s);
            }
            if (choice == 7) {
                inMenu = false;
            }
        }
    }


    // ---------- My applicants (the Pending queue) ----------

    // Purpose: show the Pending queue, let the benefactor sort it, pick one, then approve or reject.
    private void myApplicants(Scholarship s) {
        while (true) {
            System.out.println();
            System.out.println("--- My applicants: " + s.getTitle() + " ---");
            if (s.getPending().isEmpty()) {
                System.out.println("No applicants yet.");
                return;
            }

            System.out.println("Sort the list?");
            String[] sortOptions = {"Queue order (oldest first)", "By name", "By date", "Back"};
            printMenu(sortOptions);
            int sortChoice = readNumber("Choose: ", 1, 4);
            if (sortChoice == 4) {
                return;
            }

            // the list is a copy, so the queue itself stays in submit order
            Application[] list = s.getPending().toArray();
            if (sortChoice == 2) {
                list = store.insertionSort(list, "name");
            }
            if (sortChoice == 3) {
                list = store.insertionSort(list, "date");
            }

            for (int i = 0; i < list.length; i++) {
                System.out.println((i + 1) + ". " + list[i].getName()
                        + " (" + list[i].getEmail() + ") - " + list[i].getSubmittedOn());
            }
            int pick = readNumber("Pick an applicant (0 = Back): ", 0, list.length);
            if (pick == 0) {
                return;
            }
            decideApplicant(s, list[pick - 1]);
        }
    }

    // Purpose: show one application, then Approve, Reject or Back.
    private void decideApplicant(Scholarship s, Application chosen) {
        showApplication(chosen);
        String[] options = {"Approve", "Reject", "Back"};
        printMenu(options);
        int choice = readNumber("Choose: ", 1, 3);

        // the position in the queue is needed, because the list may be sorted
        int position = s.getPending().indexOf(chosen);

        if (choice == 1) {
            String result = s.approveAt(position);
            if (result.equals("OK")) {
                System.out.println("Approved: " + chosen.getName());
            } else {
                System.out.println(result);
            }
        }
        if (choice == 2) {
            String result = s.rejectAt(position);
            if (result.equals("OK")) {
                System.out.println("Rejected: " + chosen.getName());
            } else {
                System.out.println(result);
            }
        }
    }


    // ---------- Reviewed, Approved, Rejected ----------

    // Purpose: show every decided application with its status.
    private void showReviewed(Scholarship s) {
        System.out.println();
        System.out.println("--- Reviewed: " + s.getTitle() + " ---");
        Application[] approvedList = s.getApproved().toArray();
        Application[] rejectedList = s.getRejected().toArray();

        if (approvedList.length == 0) {
            if (rejectedList.length == 0) {
                System.out.println("No decided applications yet.");
                return;
            }
        }
        for (int i = 0; i < approvedList.length; i++) {
            System.out.println("- " + describeApplicant(approvedList[i]) + " - " + approvedList[i].getStatus());
        }
        for (int i = 0; i < rejectedList.length; i++) {
            System.out.println("- " + describeApplicant(rejectedList[i]) + " - " + rejectedList[i].getStatus());
        }
    }

    // Purpose: show the approved students. The benefactor can pick one to remove.
    private void approvedPage(Scholarship s) {
        while (true) {
            System.out.println();
            System.out.println("--- Approved (" + s.getApproved().size() + " of " + s.getSlots() + ") ---");
            Application[] list = s.getApproved().toArray();
            if (list.length == 0) {
                System.out.println("No approved scholars yet.");
                return;
            }

            for (int i = 0; i < list.length; i++) {
                System.out.println((i + 1) + ". " + describeApplicant(list[i]));
            }
            int pick = readNumber("Pick a scholar (0 = Back): ", 0, list.length);
            if (pick == 0) {
                return;
            }

            showApplication(list[pick - 1]);
            String[] options = {"Remove", "Back"};
            printMenu(options);
            int choice = readNumber("Choose: ", 1, 2);
            if (choice == 1) {
                // the list is in queue order, so the position is pick - 1
                String result = s.removeApprovedAt(pick - 1);
                if (result.equals("OK")) {
                    System.out.println("Removed. The slot is free again.");
                } else {
                    System.out.println(result);
                }
            }
        }
    }

    // Purpose: show the rejected students (and the ones who were removed from Approved).
    private void showRejected(Scholarship s) {
        System.out.println();
        System.out.println("--- Rejected: " + s.getTitle() + " ---");
        Application[] list = s.getRejected().toArray();
        if (list.length == 0) {
            System.out.println("No rejected applications.");
            return;
        }
        for (int i = 0; i < list.length; i++) {
            System.out.println((i + 1) + ". " + describeApplicant(list[i]));
        }
    }


    // ---------- Edit, close, renew ----------

    // Purpose: show the details, let the benefactor pick a field, and type the new value.
    // It repeats until the benefactor chooses Back.
    private void editScholarship(Scholarship s) {
        while (true) {
            System.out.println();
            System.out.println("--- Edit scholarship ---");
            System.out.println("1. Title: " + s.getTitle());
            System.out.println("2. Description: " + s.getDescription());
            System.out.println("3. Qualification: " + s.getQualification());
            System.out.println("4. Requirements: " + s.getRequirements());
            System.out.println("5. Benefits: " + s.getBenefits());
            System.out.println("6. Slots: " + s.getSlots() + " (approved: " + s.getApproved().size() + ")");
            System.out.println("7. Deadline: " + s.getDeadline());
            int field = readNumber("Pick a field to change (0 = Back): ", 0, 7);
            if (field == 0) {
                return;
            }

            if (field == 6) {
                int newSlots = readNumber("New slots: ", 1, 100000);
                if (s.setSlots(newSlots)) {
                    System.out.println("Saved.");
                } else {
                    System.out.println("Refused. Slots cannot be below the approved count ("
                            + s.getApproved().size() + "). The old value stays.");
                }
            } else {
                String text = readText("New value: ");
                boolean saved = false;
                if (field == 1) {
                    saved = s.setTitle(text);
                }
                if (field == 2) {
                    saved = s.setDescription(text);
                }
                if (field == 3) {
                    saved = s.setQualification(text);
                }
                if (field == 4) {
                    saved = s.setRequirements(text);
                }
                if (field == 5) {
                    saved = s.setBenefits(text);
                }
                if (field == 7) {
                    saved = s.setDeadline(text);
                }
                if (saved) {
                    System.out.println("Saved.");
                } else {
                    System.out.println("Refused. The old value stays.");
                }
            }
        }
    }

    // Purpose: Close an open scholarship, or Renew a closed one.
    private void closeOrRenew(Scholarship s) {
        if (s.isOpen()) {
            s.close();
            System.out.println("Closed. Students cannot see it. The applicants and decisions stay.");
            return;
        }

        System.out.println();
        System.out.println("--- Renew: update the details first ---");
        System.out.println("Approved so far: " + s.getApproved().size() + " of " + s.getSlots() + " slots.");
        System.out.println("The new slots must be higher than the approved count.");

        while (true) {
            editScholarship(s);
            if (s.renew()) {
                System.out.println("Renewed. Students can see it again.");
                return;
            }

            System.out.println("Cannot renew yet. Slots (" + s.getSlots()
                    + ") must be higher than the approved count (" + s.getApproved().size() + ").");
            String[] options = {"Edit again", "Cancel"};
            printMenu(options);
            int choice = readNumber("Choose: ", 1, 2);
            if (choice == 2) {
                return;
            }
        }
    }


    // ---------- Grants ----------

    // Purpose: list the closed scholarships and open the one the benefactor picks.
    private void grantsPage() {
        while (true) {
            System.out.println();
            System.out.println("--- Grants ---");

            // collect the closed scholarships
            int closedCount = 0;
            for (int i = 0; i < store.size(); i++) {
                if (store.getAt(i).isOpen() == false) {
                    closedCount++;
                }
            }
            if (closedCount == 0) {
                System.out.println("No closed scholarships yet.");
                return;
            }
            Scholarship[] closed = new Scholarship[closedCount];
            int next = 0;
            for (int i = 0; i < store.size(); i++) {
                if (store.getAt(i).isOpen() == false) {
                    closed[next] = store.getAt(i);
                    next++;
                }
            }

            for (int i = 0; i < closed.length; i++) {
                System.out.println((i + 1) + ". " + closed[i].getTitle() + " - "
                        + closed[i].getApproved().size() + " of " + closed[i].getSlots() + " approved, "
                        + closed[i].getGrantCount() + " grants recorded");
            }
            int pick = readNumber("Pick a scholarship (0 = Back): ", 0, closed.length);
            if (pick == 0) {
                return;
            }
            grantsMenu(closed[pick - 1]);
        }
    }

    // Purpose: the grants menu of one scholarship.
    private void grantsMenu(Scholarship s) {
        boolean inMenu = true;
        String[] options = {"Record a grant", "Grant records", "Back"};

        while (inMenu) {
            System.out.println();
            System.out.println("--- Grants: " + s.getTitle() + " ---");
            printMenu(options);
            int choice = readNumber("Choose: ", 1, 3);

            if (choice == 1) {
                recordGrant(s);
            }
            if (choice == 2) {
                grantRecords(s);
            }
            if (choice == 3) {
                inMenu = false;
            }
        }
    }

    // Purpose: pick an approved scholar, type how much, and save the grant with status Sent.
    private void recordGrant(Scholarship s) {
        System.out.println();
        System.out.println("--- Record a grant ---");
        Application[] list = s.getApproved().toArray();
        if (list.length == 0) {
            System.out.println("No approved scholars to give a grant to.");
            return;
        }

        for (int i = 0; i < list.length; i++) {
            System.out.println((i + 1) + ". " + describeApplicant(list[i]));
        }
        int pick = readNumber("Pick a scholar (0 = Back): ", 0, list.length);
        if (pick == 0) {
            return;
        }

        double amount = readAmount("How much was sent: ");
        Grant grant = new Grant(list[pick - 1], amount, today());
        s.addGrant(grant);
        System.out.println("Grant recorded: " + list[pick - 1].getName() + " - "
                + String.format("%.2f", amount) + " - Sent");
    }

    // Purpose: list the grant records, and let the benefactor set one to Confirmed or Voided.
    private void grantRecords(Scholarship s) {
        while (true) {
            System.out.println();
            System.out.println("--- Grant records ---");
            if (s.getGrantCount() == 0) {
                System.out.println("No grant records yet.");
                return;
            }

            for (int i = 0; i < s.getGrantCount(); i++) {
                Grant g = s.getGrantAt(i);
                String line = (i + 1) + ". " + g.getScholar().getName() + " - "
                        + String.format("%.2f", g.getAmount()) + " - " + g.getStatus()
                        + " - " + g.getSentOn();
                if (g.getStatus().equals("Voided")) {
                    line = line + " - reason: " + g.getReason();
                }
                System.out.println(line);
            }
            int pick = readNumber("Pick a record (0 = Back): ", 0, s.getGrantCount());
            if (pick == 0) {
                return;
            }

            Grant chosen = s.getGrantAt(pick - 1);
            if (chosen.getStatus().equals("Voided")) {
                System.out.println("Voided is final. Nothing is changed.");
            } else {
                String[] options = {"Confirmed", "Voided", "Back"};
                printMenu(options);
                int choice = readNumber("Set the status to: ", 1, 3);
                if (choice == 1) {
                    chosen.markConfirmed();
                    System.out.println("Status set to Confirmed.");
                }
                if (choice == 2) {
                    String reason = readText("Reason: ");
                    chosen.markVoided(reason);
                    System.out.println("Status set to Voided.");
                }
            }
        }
    }


    // ================= STUDENT =================

    // Purpose: the student page. The student only types an email (no password).
    private void studentPage() {
        System.out.println();
        System.out.println("=== Student ===");
        Student student = new Student(readEmail());
        String[] options = student.getMenuOptions();
        boolean inPage = true;

        while (inPage) {
            System.out.println();
            System.out.println("=== Student: " + student.getEmail() + " ===");
            printMenu(options);
            int choice = readNumber("Choose: ", 1, options.length);

            if (choice == 1) {
                viewScholarships(student);
            }
            if (choice == 2) {
                inPage = false;
            }
        }
    }

    // Purpose: list the OPEN scholarships (closed ones are hidden) and open the one the student picks.
    private void viewScholarships(Student student) {
        while (true) {
            System.out.println();
            System.out.println("--- Open scholarships ---");

            // collect the open scholarships
            int openCount = 0;
            for (int i = 0; i < store.size(); i++) {
                if (store.getAt(i).isOpen()) {
                    openCount++;
                }
            }
            if (openCount == 0) {
                System.out.println("No open scholarships right now.");
                return;
            }
            Scholarship[] openList = new Scholarship[openCount];
            int next = 0;
            for (int i = 0; i < store.size(); i++) {
                if (store.getAt(i).isOpen()) {
                    openList[next] = store.getAt(i);
                    next++;
                }
            }

            for (int i = 0; i < openList.length; i++) {
                System.out.println((i + 1) + ". " + openList[i].getTitle() + " - "
                        + slotsText(openList[i]) + " - deadline: " + openList[i].getDeadline());
            }
            int pick = readNumber("Pick a scholarship (0 = Back): ", 0, openList.length);
            if (pick == 0) {
                return;
            }
            scholarshipDetails(student, openList[pick - 1]);
        }
    }

    // Purpose: show the details of one scholarship, then Apply or Back.
    private void scholarshipDetails(Student student, Scholarship s) {
        System.out.println();
        System.out.println("--- " + s.getTitle() + " ---");
        System.out.println("Description: " + s.getDescription());
        System.out.println("Qualification: " + s.getQualification());
        System.out.println("Requirements: " + s.getRequirements());
        System.out.println("Benefits: " + s.getBenefits());
        System.out.println("Slots: " + slotsText(s));
        System.out.println("Deadline: " + s.getDeadline());

        String[] options = {"Apply", "Back"};
        printMenu(options);
        int choice = readNumber("Choose: ", 1, 2);
        if (choice == 1) {
            apply(student, s);
        }
    }

    // Purpose: the student fills out the form and the application joins the back of the Pending queue.
    private void apply(Student student, Scholarship s) {
        // check full, closed and duplicate BEFORE the student types anything
        String check = s.canApply(student.getEmail());
        if (check.equals("OK") == false) {
            System.out.println(check);
            return;
        }

        System.out.println();
        System.out.println("--- Apply: " + s.getTitle() + " ---");
        System.out.println("Your PDF must contain the requirements, the qualification");
        System.out.println("and all your information.");
        String name = readText("Type your name: ");
        String pdf = readPdf();

        Application application = new Application(student.getEmail(), name, pdf, today());
        String result = s.apply(application);
        if (result.equals("OK")) {
            System.out.println("Application sent to " + s.getTitle() + " on " + today() + ".");
        } else {
            System.out.println(result);
        }
    }


    // ================= SHOW HELPERS =================

    // Purpose: one line about a scholarship for the benefactor.
    private String describe(Scholarship s) {
        String state = "Open";
        if (s.isOpen() == false) {
            state = "Closed";
        }

        String text = s.getTitle() + " - " + state;
        if (s.isFull()) {
            text = text + " (FULL)";
        }
        text = text + ", " + s.getApproved().size() + " of " + s.getSlots() + " slots, "
                + s.getPending().size() + " pending";
        return text;
    }

    // Purpose: tell the student how many slots are left, or FULL.
    private String slotsText(Scholarship s) {
        if (s.isFull()) {
            return "FULL";
        }
        return s.getSlotsLeft() + " of " + s.getSlots() + " slots left";
    }

    // Purpose: name and email of an applicant in one line.
    private String describeApplicant(Application a) {
        return a.getName() + " (" + a.getEmail() + ")";
    }

    // Purpose: show everything the system knows about one application.
    // All the other information of the student is inside the PDF.
    private void showApplication(Application a) {
        System.out.println();
        System.out.println("Email: " + a.getEmail());
        System.out.println("Name: " + a.getName());
        System.out.println("PDF file: " + a.getPdfFile());
        System.out.println("Date submitted: " + a.getSubmittedOn());
        System.out.println("Status: " + a.getStatus());
    }

    // Purpose: print the menu items with numbers 1, 2, 3...
    private void printMenu(String[] options) {
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
    }


    // ================= INPUT HELPERS =================

    // Purpose: show a message and read one line the user typed.
    // If there is no more input, the program ends.
    private String readLine(String prompt) {
        System.out.print(prompt);
        if (input.hasNextLine() == false) {
            System.out.println();
            System.out.println("Input ended. Goodbye.");
            System.exit(0);
        }
        return input.nextLine().trim();
    }

    // Purpose: read a text that is not empty. It asks again until the user types something.
    private String readText(String prompt) {
        String text = readLine(prompt);
        while (text.equals("")) {
            System.out.println("This cannot be empty.");
            text = readLine(prompt);
        }
        return text;
    }

    // Purpose: tell if a text is a whole number like 5 or 120 (digits only, up to 6 digits).
    private boolean isWholeNumber(String text) {
        if (text.equals("")) {
            return false;
        }
        if (text.length() > 6) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            if (Character.isDigit(text.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    // Purpose: read a whole number from min to max. It asks again until the number is good.
    private int readNumber(String prompt, int min, int max) {
        while (true) {
            String text = readLine(prompt);
            if (isWholeNumber(text)) {
                int number = Integer.parseInt(text);
                if (number >= min) {
                    if (number <= max) {
                        return number;
                    }
                }
            }
            System.out.println("Please type a number from " + min + " to " + max + ".");
        }
    }

    // Purpose: read an amount of money above 0, like 5000 or 2500.50.
    private double readAmount(String prompt) {
        while (true) {
            String text = readLine(prompt);
            try {
                double amount = Double.parseDouble(text);
                if (amount > 0) {
                    if (amount < 1000000000) {
                        return amount;
                    }
                }
            } catch (NumberFormatException e) {
                // it is not a number, so the message below is shown
            }
            System.out.println("Please type an amount above 0, like 5000 or 2500.50.");
        }
    }

    // Purpose: read an email. It asks again until the email is valid.
    private String readEmail() {
        String text = readLine("Type your email: ");
        while (Student.isValidEmail(text) == false) {
            System.out.println("That is not a valid email. Example: name@mail.com");
            text = readLine("Type your email: ");
        }
        return text;
    }

    // Purpose: read the PDF file name or path. It asks again until it ends with .pdf.
    private String readPdf() {
        String text = readLine("Type the PDF file name or path (must end with .pdf): ");
        while (Application.isPdfName(text) == false) {
            System.out.println("PDF files only. The name must end with .pdf");
            text = readLine("Type the PDF file name or path (must end with .pdf): ");
        }
        return text;
    }

    // Purpose: give today's date as yyyy-MM-dd (no time).
    private String today() {
        return LocalDate.now().toString();
    }
}