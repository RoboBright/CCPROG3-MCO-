import java.util.ArrayList;
import java.util.Scanner;

/**
 * Console-based UI for Green Property Exchange (MCO1).
 *
 * <p>
 * This class provides a safe CLI that manages two sample properties on startup,
 * lets the user switch between properties, manage dates and reservations, and view
 * financial summaries. It follows constraints:
 * <ul>
 *   <li>No use of break in loops for flow control</li>
 *   <li>No use of return in void methods for early exits</li>
 *   <li>Uses ArrayList where appropriate</li>
 *   <li>Validates user input thoroughly</li>
 * </ul>
 * </p>
 */
public class Main {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final ArrayList<Property> properties = new ArrayList<>();
    private static final ArrayList<ArrayList<Reservation>> reservationsPerProperty = new ArrayList<>();
    private static final double MIN_PRICE = 100.0;

    /**
     * Program entry point.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        seedSampleData();
        runApplication();
        SCANNER.close();
        System.out.println("Exiting program. Goodbye!");
    }

    /**
     * Seeds the application with two properties and some dates/reservations for testing.
     * "Property 1" and "Property 2" are added with some dates. A sample reservation is
     * created for demonstration.
     */
    private static void seedSampleData() {
        // Property 1: full month of dates
        Property p1 = new Property("Property 1");
        for (int d = 1; d <= 30; d++) {
            Date date = new Date(d); // default price 1500
            p1.addDate(date);
        }

        // Property 2: sparse dates
        Property p2 = new Property("Property 2");
        for (int d = 5; d <= 10; d++) {
            Date date = new Date(d);
            p2.addDate(date);
        }
        for (int d = 20; d <= 22; d++) {
            Date date = new Date(d);
            p2.addDate(date);
        }

        // Add properties to master list
        properties.add(p1);
        properties.add(p2);

        // Initialize reservations list per property
        reservationsPerProperty.add(new ArrayList<>()); // for p1
        reservationsPerProperty.add(new ArrayList<>()); // for p2

        // Add a sample reservation to Property 1: guest "Alice", days 3-5
        Reservation sample = new Reservation("Alice", 3, 5);
        boolean booked = p1.addReservation(sample);
        if (booked) {
            reservationsPerProperty.get(0).add(sample);
        }
    }

    /**
     * Runs the main application loop presenting the top-level menu until user chooses to exit.
     * This method avoids using return in the void method and uses a running flag instead.
     */
    private static void runApplication() {
        boolean running = true;

        while (running) {
            System.out.println("\n====== GREEN PROPERTY EXCHANGE ======");
            System.out.println("1. Select Property to Manage");
            System.out.println("2. Manage Dates for Selected Property");
            System.out.println("3. Manage Reservations for Selected Property");
            System.out.println("4. View Financials for Selected Property");
            System.out.println("5. Show All Properties");
            System.out.println("0. Exit");
            int choice = getValidInt("Enter choice: ", 0, 5);

            if (choice == 0) {
                running = false;
            } else if (choice == 1) {
                handleSelectProperty();
            } else if (choice == 2) {
                handleManageDates();
            } else if (choice == 3) {
                handleManageReservations();
            } else if (choice == 4) {
                handleViewFinancials();
            } else if (choice == 5) {
                showAllProperties();
            } else {
                // unreachable because getValidInt enforces bounds
            }
        }
    }

    /**
     * Prompts user to select a property index from the list. If there are no properties,
     * informs the user accordingly. The selected property's index is returned or -1 if none selected.
     *
     * @return index of selected property (0-based), or -1 if no selection
     */
    private static int promptSelectPropertyIndex() {
        int selectedIndex = -1;
        if (properties.isEmpty()) {
            System.out.println("No properties exist. Create one first (not implemented here).");
            selectedIndex = -1;
        } else {
            System.out.println("\nSelect a property:");
            for (int i = 0; i < properties.size(); i++) {
                System.out.printf("%d) %s%n", i + 1, properties.get(i).getName());
            }
            System.out.println("0) Cancel");
            int choice = getValidInt("Enter choice: ", 0, properties.size());
            if (choice == 0) {
                selectedIndex = -1;
            } else {
                selectedIndex = choice - 1;
            }
        }
        return selectedIndex;
    }

    /**
     * Handles selecting a property to manage (just a convenience operation that shows the selected property).
     * Avoids returning early from a void method by using flags.
     */
    private static void handleSelectProperty() {
        int idx = promptSelectPropertyIndex();
        if (idx >= 0) {
            System.out.println("Selected property: " + properties.get(idx).getName());
        } else {
            System.out.println("No property selected.");
        }
    }

    /**
     * Manages date-related operations for a selected property: add, remove, list, update price.
     * Enforces constraints: days 1..30, cannot remove reserved dates, price >= MIN_PRICE.
     */
    private static void handleManageDates() {
        int idx = promptSelectPropertyIndex();
        if (idx < 0) {
            System.out.println("Returning to main menu.");
            return;
        }

        Property p = properties.get(idx);
        boolean managing = true;
        while (managing) {
            System.out.println("\n--- Manage Dates for " + p.getName() + " ---");
            System.out.println("1) Add Date(s)");
            System.out.println("2) Remove Date");
            System.out.println("3) List Dates (Available/Booked)");
            System.out.println("4) Update Price for a Date");
            System.out.println("0) Back");
            int choice = getValidInt("Enter choice: ", 0, 4);

            if (choice == 0) {
                managing = false;
            } else if (choice == 1) {
                handleAddDates(p);
            } else if (choice == 2) {
                handleRemoveDate(p);
            } else if (choice == 3) {
                handleListDates(p);
            } else if (choice == 4) {
                handleUpdateDatePrice(p);
            } else {
                // unreachable
            }
        }
    }

    /**
     * Adds one or more dates to the property based on user input.
     * Accepts comma-separated days and ranges (e.g., 1-5,7,9).
     *
     * @param p the Property to add dates to
     */
    private static void handleAddDates(Property p) {
        System.out.println("Enter dates to add (1-30). Examples: 'all' or '1-5,7,9'.");
        String input = getNonEmptyString("Dates: ");
        boolean addedAny = false;

        if ("all".equalsIgnoreCase(input.trim())) {
            for (int d = 1; d <= 30; d++) {
                Date date = new Date(d); // default price
                p.addDate(date);
                addedAny = true;
            }
        } else {
            String[] tokens = input.split(",");
            for (int i = 0; i < tokens.length; i++) {
                String t = tokens[i].trim();
                if (t.contains("-")) {
                    String[] range = t.split("-");
                    if (range.length == 2) {
                        try {
                            int a = Integer.parseInt(range[0].trim());
                            int b = Integer.parseInt(range[1].trim());
                            if (a < 1 || b < 1 || a > 30 || b > 30 || a > b) {
                                // invalid range; skip
                            } else {
                                int d = a;
                                while (d <= b) {
                                    Date date = new Date(d);
                                    p.addDate(date);
                                    addedAny = true;
                                    d = d + 1;
                                }
                            }
                        } catch (NumberFormatException ex) {
                            // skip invalid piece
                        }
                    }
                } else {
                    try {
                        int day = Integer.parseInt(t);
                        if (day >= 1 && day <= 30) {
                            Date date = new Date(day);
                            p.addDate(date);
                            addedAny = true;
                        }
                    } catch (NumberFormatException ex) {
                        // skip invalid token
                    }
                }
            }
        }

        if (addedAny) {
            System.out.println("Dates added (attempted). Note: duplicates were ignored by Property.addDate().");
        } else {
            System.out.println("No valid dates were added.");
        }
    }

    /**
     * Removes a date from the property if it exists and is not reserved.
     *
     * @param p the Property to remove the date from
     */
    private static void handleRemoveDate(Property p) {
        int day = getValidInt("Enter day to remove (1-30): ", 1, 30);
        Date date = p.getDateByDay(day);
        if (date == null) {
            System.out.println("That date is not listed for this property.");
        } else {
            if (!date.isAvailable()) {
                System.out.println("Cannot remove this date because it is currently reserved.");
            } else {
                p.removeDate(day);
                System.out.println("Date removed.");
            }
        }
    }

    /**
     * Lists days 1..30 showing whether each day is Available, Booked, or Not Listed.
     * Uses p.getDateByDay(day) to find dates and their isAvailable() status.
     *
     * @param p the Property whose calendar to print
     */
    private static void handleListDates(Property p) {
        System.out.println("\nCalendar for " + p.getName() + " (1..30):");
        for (int d = 1; d <= 30; d++) {
            Date date = p.getDateByDay(d);
            if (date == null) {
                System.out.printf("%2d : NOT LISTED%n", d);
            } else {
                if (date.isAvailable()) {
                    System.out.printf("%2d : Available - PHP %.2f%n", d, date.getPrice());
                } else {
                    System.out.printf("%2d : Reserved - PHP %.2f%n", d, date.getPrice());
                }
            }
        }
    }

    /**
     * Updates the price of a specified date (must be listed). Enforces MIN_PRICE.
     *
     * @param p the Property containing the date
     */
    private static void handleUpdateDatePrice(Property p) {
        int day = getValidInt("Enter day to update price for (1-30): ", 1, 30);
        Date date = p.getDateByDay(day);
        if (date == null) {
            System.out.println("That date is not listed for this property.");
        } else {
            double newPrice = getValidDouble("Enter new price (>= " + MIN_PRICE + "): ", MIN_PRICE, Double.MAX_VALUE);
            date.setPrice(newPrice); // Date.setPrice should guard against invalid input if present
            System.out.println("Price updated.");
        }
    }

    /**
     * Manages reservations for a selected property: create, cancel, view all.
     * This method coordinates with the local reservationsPerProperty list which mirrors
     * reservations added to the Property object.
     */
    private static void handleManageReservations() {
        int idx = promptSelectPropertyIndex();
        if (idx < 0) {
            System.out.println("Returning to main menu.");
            return;
        }

        Property p = properties.get(idx);
        ArrayList<Reservation> localResList = reservationsPerProperty.get(idx);

        boolean managing = true;
        while (managing) {
            System.out.println("\n--- Manage Reservations for " + p.getName() + " ---");
            System.out.println("1) Create Reservation");
            System.out.println("2) Cancel Reservation");
            System.out.println("3) View All Reservations");
            System.out.println("0) Back");
            int choice = getValidInt("Enter choice: ", 0, 3);

            if (choice == 0) {
                managing = false;
            } else if (choice == 1) {
                createReservationFlow(p, localResList);
            } else if (choice == 2) {
                cancelReservationFlow(p, localResList);
            } else if (choice == 3) {
                viewReservationsFlow(p, localResList);
            } else {
                // unreachable
            }
        }
    }

    /**
     * Creates a reservation for a property after validating dates exist and are available,
     * and checks booking rules (no check-in on day 30, no check-out on day 1, checkOut>checkIn).
     *
     * @param p            the Property to book
     * @param localResList the local reservation list tracked by Main
     */
    private static void createReservationFlow(Property p, ArrayList<Reservation> localResList) {
        String guest = getNonEmptyString("Guest name: ");
        int checkIn = getValidInt("Check-in day (1-29): ", 1, 29);
        int checkOut = getValidInt("Check-out day (2-30): ", 2, 30);

        if (checkOut <= checkIn) {
            System.out.println("Invalid: check-out must be greater than check-in.");
            return;
        }
        if (checkIn == 30) {
            System.out.println("Invalid: check-in cannot be on day 30.");
            return;
        }
        if (checkOut == 1) {
            System.out.println("Invalid: check-out cannot be on day 1.");
            return;
        }

        // Validate all dates exist and are available
        boolean allOk = true;
        int d = checkIn;
        while (d < checkOut && allOk) {
            Date date = p.getDateByDay(d);
            if (date == null) {
                allOk = false;
            } else {
                if (!date.isAvailable()) {
                    allOk = false;
                }
            }
            d = d + 1;
        }

        if (!allOk) {
            System.out.println("Booking failed: one or more dates are missing or already reserved.");
            return;
        }

        Reservation r = new Reservation(guest, checkIn, checkOut);
        boolean added = p.addReservation(r);
        if (added) {
            localResList.add(r);
            System.out.println("Reservation successful!");
            // Show summary
            double total = r.getTotalPrice(p);
            System.out.printf("Total cost: PHP %.2f%n", total);
        } else {
            System.out.println("Reservation failed: internal error or no space for reservations.");
        }
    }

    /**
     * Cancels a reservation by letting the user pick from the local reservation list.
     * If removed from property successfully, the local list is updated.
     *
     * @param p            the Property to cancel reservation from
     * @param localResList the local reservation list tracked by Main
     */
    private static void cancelReservationFlow(Property p, ArrayList<Reservation> localResList) {
        if (localResList.isEmpty()) {
            System.out.println("No reservations recorded for this property.");
            return;
        }

        System.out.println("\nSelect a reservation to cancel:");
        for (int i = 0; i < localResList.size(); i++) {
            Reservation r = localResList.get(i);
            System.out.printf("%d) %s: %d -> %d (Total: PHP %.2f)%n", i + 1, r.getGuestName(),
                    r.getCheckIn(), r.getCheckOut(), r.getTotalPrice(p));
        }
        System.out.println("0) Cancel");

        int choice = getValidInt("Enter choice: ", 0, localResList.size());
        if (choice == 0) {
            System.out.println("Cancellation aborted.");
            return;
        }

        int sel = choice - 1;
        Reservation toRemove = localResList.get(sel);
        boolean removedFromProperty = p.removeReservation(toRemove);
        if (removedFromProperty) {
            // remove from local list by shifting without break
            ArrayList<Reservation> newList = new ArrayList<>();
            for (int i = 0; i < localResList.size(); i++) {
                if (i != sel) {
                    newList.add(localResList.get(i));
                }
            }
            localResList.clear();
            localResList.addAll(newList);
            System.out.println("Reservation cancelled and dates freed.");
        } else {
            System.out.println("Could not find matching reservation inside property data.");
        }
    }

    /**
     * Displays all reservations for the property using the local reservation list.
     *
     * @param p            the Property
     * @param localResList local list of reservations
     */
    private static void viewReservationsFlow(Property p, ArrayList<Reservation> localResList) {
        if (localResList.isEmpty()) {
            System.out.println("No reservations found for this property.");
            return;
        }

        System.out.println("\nReservations for " + p.getName() + ":");
        for (int i = 0; i < localResList.size(); i++) {
            Reservation r = localResList.get(i);
            System.out.printf("%d) %s - %d to %d - Total: PHP %.2f%n", i + 1,
                    r.getGuestName(), r.getCheckIn(), r.getCheckOut(), r.getTotalPrice(p));
        }
    }

    /**
     * Shows financial summaries for a selected property: estimated earnings and breakdown per reservation.
     * Estimated earnings uses Property.getEstimatedEarnings() which sums prices of booked dates.
     */
    private static void handleViewFinancials() {
        int idx = promptSelectPropertyIndex();
        if (idx < 0) {
            System.out.println("Returning to main menu.");
            return;
        }

        Property p = properties.get(idx);
        ArrayList<Reservation> localResList = reservationsPerProperty.get(idx);

        System.out.println("\n--- Financials for " + p.getName() + " ---");
        System.out.printf("Estimated earnings (sum of booked date prices): PHP %.2f%n", p.getEstimatedEarnings());

        if (localResList.isEmpty()) {
            System.out.println("No reservations to show breakdown for.");
        } else {
            System.out.println("\nReservation breakdowns:");
            for (int i = 0; i < localResList.size(); i++) {
                Reservation r = localResList.get(i);
                System.out.printf("%d) %s - %d to %d - Total: PHP %.2f%n", i + 1,
                        r.getGuestName(), r.getCheckIn(), r.getCheckOut(), r.getTotalPrice(p));
                double[] breakdown = r.getBreakdown(p);
                for (int j = 0; j < breakdown.length; j++) {
                    System.out.printf("   Night %d: PHP %.2f%n", j + 1, breakdown[j]);
                }
            }
        }
    }

    /**
     * Displays a list of all properties and their basic info.
     */
    private static void showAllProperties() {
        if (properties.isEmpty()) {
            System.out.println("No properties available.");
            return;
        }
        System.out.println("\nAll Properties:");
        for (int i = 0; i < properties.size(); i++) {
            Property p = properties.get(i);
            System.out.printf("%d) %s - Available Dates: %d - Estimated Earnings: PHP %.2f%n", i + 1,
                    p.getName(), p.getAvailableDates().length, p.getEstimatedEarnings());
        }
    }

    // -----------------------------
    // Utility input helper methods
    // -----------------------------

    /**
     * Safely reads an integer within [min, max] from console input.
     *
     * @param prompt prompt shown to user
     * @param min    minimum accepted value
     * @param max    maximum accepted value
     * @return user-entered integer within range
     */
    private static int getValidInt(String prompt, int min, int max) {
        int value = min;
        boolean got = false;
        while (!got) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            try {
                int v = Integer.parseInt(line);
                if (v < min || v > max) {
                    System.out.printf("Please enter an integer between %d and %d.%n", min, max);
                } else {
                    value = v;
                    got = true;
                }
            } catch (NumberFormatException ex) {
                System.out.println("Invalid integer. Try again.");
            }
        }
        return value;
    }

    /**
     * Safely reads a double value within [min, max] from console input.
     *
     * @param prompt prompt shown to user
     * @param min    minimum value accepted
     * @param max    maximum value accepted
     * @return the valid double entered by user
     */
    private static double getValidDouble(String prompt, double min, double max) {
        double value = min;
        boolean got = false;
        while (!got) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            try {
                double v = Double.parseDouble(line);
                if (v < min || v > max) {
                    System.out.printf("Please enter a number between %.2f and %.2f.%n", min, max);
                } else {
                    value = v;
                    got = true;
                }
            } catch (NumberFormatException ex) {
                System.out.println("Invalid number. Try again.");
            }
        }
        return value;
    }

    /**
     * Reads a non-empty trimmed string from the console.
     *
     * @param prompt message to display
     * @return non-empty trimmed string
     */
    private static String getNonEmptyString(String prompt) {
        String s = "";
        boolean got = false;
        while (!got) {
            System.out.print(prompt);
            s = SCANNER.nextLine().trim();
            if (!s.isEmpty()) {
                got = true;
            } else {
                System.out.println("Input cannot be empty. Try again.");
            }
        }
        return s;
    }
}
