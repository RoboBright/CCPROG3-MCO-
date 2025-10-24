/*
  This is to certify that this project is our own work, based on our
  personal efforts in studying and applying the concepts learned.
  We have constructed the functions and their respective algorithms
  and corresponding code by ourselves. The program was run, tested,
  and debugged by our own efforts. We further certify that we have not
  copied in part or whole or otherwise plagiarized the work of other
  students and/or persons.
  <Mandanas, Ammiel Vashti M.>, DLSU ID# <12414557>
  <Patawaran, Paolo Theodore L.>, DLSU ID# <12484008>
 */

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Main.java — Green Property Exchange (MCO1)
 *
 * Constraints:
 * - Properties have unique names.
 * - Each property manages 1–30 dates (1..30) with a default base price of PHP 1,500.
 * - Dates may be available or reserved.
 * - Reservations are validated (no overlap, valid range, etc.).
 * - Base price updates are only allowed when no active reservations exist.
 * - Uses ArrayLists for dynamic storage.
 * - Does not use break/return for control flow or Locale/InputMismatchException.
 * </p>
 *
 * Author: Green Property Team (Mandanas & Patawaran)
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final ArrayList<Property> properties = new ArrayList<>();
    private static final ArrayList<ArrayList<Reservation>> reservationsPerProperty = new ArrayList<>();
    private static final double MIN_BASE_PRICE = 100.0;

    /**
     * Application entry point.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        seedTwoSampleProperties();
        boolean running = true;

        while (running) {
            System.out.println("\n===== GREEN PROPERTY EXCHANGE (MCO1) =====");
            System.out.println("1. Create Property Listing");
            System.out.println("2. View Property");
            System.out.println("3. Manage Property");
            System.out.println("4. Simulate Booking");
            System.out.println("5. Exit");
            int choice = getValidInt("Enter choice: ", 1, 5);

            if (choice == 1) createPropertyFlow();
            else if (choice == 2) viewPropertyFlow();
            else if (choice == 3) managePropertyFlow();
            else if (choice == 4) simulateBookingFlow();
            else if (choice == 5) running = false;
        }

        System.out.println("Exiting system... Goodbye!");
        scanner.close();
    }

    /**
     * Seeds two sample properties to demonstrate initial data.
     */
    private static void seedTwoSampleProperties() {
        Property p1 = new Property("Property A");
        for (int d = 1; d <= 30; d++) p1.addDate(new Date(d));
        properties.add(p1);
        reservationsPerProperty.add(new ArrayList<>());

        Property p2 = new Property("Property B");
        for (int d = 5; d <= 10; d++) p2.addDate(new Date(d));
        for (int d = 20; d <= 22; d++) p2.addDate(new Date(d));
        properties.add(p2);
        reservationsPerProperty.add(new ArrayList<>());

        Reservation r = new Reservation("Alice", 3, 5);
        boolean ok = p1.addReservation(r);
        if (ok) reservationsPerProperty.get(0).add(r);
    }

    // ============================
    // 1. CREATE PROPERTY LISTING
    // ============================

    /**
     * Creates a new property listing following MCO1 rules.
     * Ensures unique name, at least one available date, and sets default base price.
     */
    private static void createPropertyFlow() {
        System.out.println("\n--- Create Property Listing ---");
        String name = "";
        boolean validName = false;

        while (!validName) {
            name = getNonEmptyString("Enter unique property name: ");
            if (isDuplicatePropertyName(name)) System.out.println("Name already exists. Try again.");
            else validName = true;
        }

        Property p = new Property(name);
        System.out.println("Add available dates (1..30). Enter 'all' for full month, or comma-separated numbers (e.g. 1,2,3):");

        boolean addedDates = false;
        while (!addedDates) {
            String input = getNonEmptyString("Enter dates: ");
            if (input.equalsIgnoreCase("all")) {
                for (int i = 1; i <= 30; i++) p.addDate(new Date(i));
                addedDates = true;
            } else {
                String[] parts = input.split(",");
                for (String part : parts) {
                    try {
                        int day = Integer.parseInt(part.trim());
                        if (day >= 1 && day <= 30) p.addDate(new Date(day));
                    } catch (NumberFormatException ignored) {}
                }
                if (p.getAvailableDates().length > 0) addedDates = true;
                else System.out.println("Please add at least one valid date.");
            }
        }

        properties.add(p);
        reservationsPerProperty.add(new ArrayList<>());
        System.out.println("Property '" + name + "' created successfully with " + p.getAvailableDates().length + " dates.");
    }

    // ============================
    // 2. VIEW PROPERTY
    // ============================

    /**
     * Allows the user to view property data: Calendar, High-Level Info, Detailed Info.
     */
    private static void viewPropertyFlow() {
        System.out.println("\n--- View Property ---");
        int idx = selectPropertyIndex();
        if (idx < 0) return;

        Property p = properties.get(idx);
        boolean viewing = true;

        while (viewing) {
            System.out.println("\nViewing: " + p.getName());
            System.out.println("1. Calendar View");
            System.out.println("2. High-Level Information");
            System.out.println("3. Detailed Information");
            System.out.println("0. Back");
            int choice = getValidInt("Enter choice: ", 0, 3);

            if (choice == 0) viewing = false;
            else if (choice == 1) showCalendarView(p);
            else if (choice == 2) showHighLevelInfo(p);
            else if (choice == 3) showDetailedInfo(p);
        }
    }

    /**
     * Displays a 30-day calendar showing available, booked, and unlisted days.
     *
     * @param p property to display
     */
    private static void showCalendarView(Property p) {
        System.out.println("\n--- Calendar View ---");
        ArrayList<Reservation> localRes = getLocalReservationListForProperty(p);

        for (int d = 1; d <= 30; d++) {
            Date date = getDateFromPropertyByDay(p, d);
            if (date == null)
                System.out.printf("%2d : NOT LISTED%n", d);
            else if (date.isAvailable())
                System.out.printf("%2d : Available - PHP %.2f%n", d, date.getPrice());
            else {
                String guest = findGuestForBookedDay(localRes, d);
                System.out.printf("%2d : BOOKED (%s) - PHP %.2f%n", d, guest, date.getPrice());
            }
        }
    }

    /**
     * Displays high-level info about a property.
     *
     * @param p property to display
     */
    private static void showHighLevelInfo(Property p) {
        System.out.println("\n--- High-Level Information ---");
        System.out.println("Property Name: " + p.getName());
        System.out.println("Total Available Dates: " + p.getAvailableDates().length);
        System.out.printf("Estimated Earnings: PHP %.2f%n", p.getEstimatedEarnings());
    }

    /**
     * Displays a submenu for detailed information.
     * Allows user to choose between viewing specific date info or reservation details.
     *
     * @param p property to display
     */
    private static void showDetailedInfo(Property p) {
        boolean showing = true;
        while (showing) {
            System.out.println("\n--- Detailed Information ---");
            System.out.println("1. Information for a Specific Date");
            System.out.println("2. Reservation Details for this Property");
            System.out.println("0. Back");
            int choice = getValidInt("Enter choice: ", 0, 2);

            if (choice == 0) showing = false;
            else if (choice == 1) showSpecificDateInfo(p);
            else if (choice == 2) showReservationDetails(p);
        }
    }

    /**
     * Displays information for a specific date.
     *
     * @param p property
     */
    private static void showSpecificDateInfo(Property p) {
        int day = getValidInt("Enter date (1–30): ", 1, 30);
        Date date = getDateFromPropertyByDay(p, day);
        if (date == null) System.out.println("Date not listed for this property.");
        else {
            System.out.printf("Day %d - Price: PHP %.2f - %s%n",
                    day, date.getPrice(), date.isAvailable() ? "Available" : "Reserved");
        }
    }

    /**
     * Displays reservation details for a property.
     *
     * @param p property
     */
    private static void showReservationDetails(Property p) {
        ArrayList<Reservation> resList = getLocalReservationListForProperty(p);
        if (resList.isEmpty()) {
            System.out.println("No reservations for this property.");
        } else {
            for (Reservation r : resList) {
                System.out.printf("Guest: %s | Check-In: %d | Check-Out: %d | Total: PHP %.2f%n",
                        r.getGuestName(), r.getCheckIn(), r.getCheckOut(), r.getTotalPrice(p));
                double[] breakdown = r.getBreakdown(p);
                for (int i = 0; i < breakdown.length; i++) {
                    System.out.printf("   Night %d: PHP %.2f%n", i + 1, breakdown[i]);
                }
            }
        }
    }

    // ============================
    // 3. MANAGE PROPERTY
    // ============================

    /**
     * Menu to manage property configuration.
     */
    private static void managePropertyFlow() {
        System.out.println("\n--- Manage Property ---");
        int idx = selectPropertyIndex();
        if (idx < 0) return;

        Property p = properties.get(idx);
        ArrayList<Reservation> localResList = reservationsPerProperty.get(idx);
        boolean managing = true;

        while (managing) {
            System.out.println("\nManaging: " + p.getName());
            System.out.println("1. Change Property Name");
            System.out.println("2. Update Base Price (only if no reservations)");
            System.out.println("3. Remove Reservation");
            System.out.println("4. Remove Property");
            System.out.println("0. Back");
            int choice = getValidInt("Enter choice: ", 0, 4);

            if (choice == 0) managing = false;
            else if (choice == 1) {
                String newName = getNonEmptyString("Enter new property name: ");
                if (isDuplicatePropertyName(newName)) System.out.println("Name already exists.");
                else {
                    p.setName(newName);
                    System.out.println("Name updated successfully.");
                }
            } else if (choice == 2) {
                if (!localResList.isEmpty()) System.out.println("Cannot update base price: property has reservations.");
                else {
                    double newBase = getValidDouble("Enter new base price (>=100): ", 100, Double.MAX_VALUE);
                    for (Date d : p.getAvailableDates()) d.setPrice(newBase);
                    System.out.println("Base price updated across all dates.");
                }
            } else if (choice == 3) {
                if (localResList.isEmpty()) System.out.println("No reservations to remove.");
                else {
                    for (int i = 0; i < localResList.size(); i++) {
                        Reservation r = localResList.get(i);
                        System.out.printf("%d) %s | %d-%d%n", i + 1, r.getGuestName(), r.getCheckIn(), r.getCheckOut());
                    }
                    System.out.println("0) Cancel");
                    int sel = getValidInt("Select: ", 0, localResList.size());
                    if (sel > 0) {
                        Reservation target = localResList.get(sel - 1);
                        if (p.removeReservation(target)) {
                            localResList.remove(sel - 1);
                            System.out.println("Reservation removed.");
                        }
                    }
                }
            } else if (choice == 4) {
                if (!localResList.isEmpty()) System.out.println("Cannot remove property with active reservations.");
                else {
                    boolean confirm = confirmAction("Are you sure? (Y/N): ");
                    if (confirm) {
                        properties.remove(idx);
                        reservationsPerProperty.remove(idx);
                        System.out.println("Property removed successfully.");
                        managing = false;
                    }
                }
            }
        }
    }

    // ============================
    // 4. SIMULATE BOOKING
    // ============================

    /**
     * Simulates booking a reservation for a property.
     */
    private static void simulateBookingFlow() {
        System.out.println("\n--- Simulate Booking ---");
        int idx = selectPropertyIndex();
        if (idx < 0) return;

        Property p = properties.get(idx);
        ArrayList<Reservation> localRes = reservationsPerProperty.get(idx);

        String guest = getNonEmptyString("Guest Name: ");
        int checkIn = getValidInt("Check-in (1-29): ", 1, 29);
        int checkOut = getValidInt("Check-out (2-30): ", 2, 30);

        if (checkOut <= checkIn || checkIn == 30 || checkOut == 1) {
            System.out.println("Invalid check-in/check-out combination.");
            return;
        }

        boolean allAvailable = true;
        int d = checkIn;
        while (d < checkOut) {
            Date date = getDateFromPropertyByDay(p, d);
            if (date == null || !date.isAvailable()) allAvailable = false;
            d++;
        }

        if (!allAvailable) {
            System.out.println("Some dates unavailable or not listed.");
        } else {
            Reservation r = new Reservation(guest, checkIn, checkOut);
            if (p.addReservation(r)) {
                localRes.add(r);
                System.out.println("Reservation successful!");
                System.out.printf("Total: PHP %.2f%n", r.getTotalPrice(p));
                double[] breakdown = r.getBreakdown(p);
                for (int i = 0; i < breakdown.length; i++) {
                    System.out.printf("   Night %d: PHP %.2f%n", i + 1, breakdown[i]);
                }
            } else System.out.println("Reservation could not be added.");
        }
    }

    // ============================
    // UTILITIES
    // ============================

    private static int selectPropertyIndex() {
        if (properties.isEmpty()) {
            System.out.println("No properties found.");
            return -1;
        }
        for (int i = 0; i < properties.size(); i++)
            System.out.printf("%d) %s%n", i + 1, properties.get(i).getName());
        System.out.println("0) Cancel");
        int choice = getValidInt("Select: ", 0, properties.size());
        return choice == 0 ? -1 : choice - 1;
    }

    private static boolean isDuplicatePropertyName(String name) {
        for (Property p : properties) if (p.getName().equals(name)) return true;
        return false;
    }

    private static String getNonEmptyString(String prompt) {
        String s = "";
        while (s.isEmpty()) {
            System.out.print(prompt);
            s = scanner.nextLine().trim();
            if (s.isEmpty()) System.out.println("Input cannot be empty.");
        }
        return s;
    }

    private static int getValidInt(String prompt, int min, int max) {
        int val = min;
        boolean ok = false;
        while (!ok) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int x = Integer.parseInt(input);
                if (x < min || x > max) System.out.println("Out of range.");
                else {
                    val = x;
                    ok = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
        return val;
    }

    private static double getValidDouble(String prompt, double min, double max) {
        double val = min;
        boolean ok = false;
        while (!ok) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double x = Double.parseDouble(input);
                if (x < min || x > max) System.out.println("Out of range.");
                else {
                    val = x;
                    ok = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number.");
            }
        }
        return val;
    }

    private static boolean confirmAction(String prompt) {
        boolean valid = false, result = false;
        while (!valid) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            if (s.equalsIgnoreCase("y") || s.equalsIgnoreCase("yes")) {
                result = true;
                valid = true;
            } else if (s.equalsIgnoreCase("n") || s.equalsIgnoreCase("no")) valid = true;
            else System.out.println("Please enter Y or N.");
        }
        return result;
    }

    private static ArrayList<Reservation> getLocalReservationListForProperty(Property p) {
        ArrayList<Reservation> result = new ArrayList<>();
        for (int i = 0; i < properties.size(); i++)
            if (properties.get(i) == p) result = reservationsPerProperty.get(i);
        return result;
    }

    private static Date getDateFromPropertyByDay(Property p, int day) {
        for (Date d : p.getAvailableDates())
            if (d != null && d.getDay() == day) return d;
        return null;
    }

    private static String findGuestForBookedDay(ArrayList<Reservation> resList, int day) {
        for (Reservation r : resList)
            if (r.getCheckIn() <= day && day < r.getCheckOut()) return r.getGuestName();
        return "Unknown";
    }
}
