package Menu;

import java.util.ArrayList;
import java.util.Locale;

import Objects.Property;
import Objects.Date;
import Objects.Reservation;
import Objects.PropertyType;

import System.PropertySystem;

/**
 * The PropertyMenuManager class handles property-related menu actions.
 * It uses MenuHelper for user input and PropertySystem for logic.
 * This class does not contain any business logic.
 */
public class PropertyMenuManager {

    private PropertySystem system;
    private MenuHelper helper;
    private CalendarView calendarView;

    /**
     * Constructs a PropertyMenuManager with the required helpers.
     *
     * @param system the PropertySystem used to manage properties
     * @param helper the MenuHelper used for user input
     * @param calendarView the CalendarView used for displaying calendars
     */
    public PropertyMenuManager(PropertySystem system,
                               MenuHelper helper,
                               CalendarView calendarView) {

        this.system = system;
        this.helper = helper;
        this.calendarView = calendarView;
    }

    /**
     * Opens the main property menu.
     */
    public void openPropertyMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n--- Property Menu ---");
            System.out.println("1. Create Property");
            System.out.println("2. View Property");
            System.out.println("3. Manage Property");
            System.out.println("0. Back");

            int choice = helper.getValidInt("Enter choice: ", 0, 3);

            if (choice == 0)
                running = false;
            else if (choice == 1)
                createProperty();
            else if (choice == 2)
                viewProperty();
            else if (choice == 3)
                manageProperty();
        }
    }

    /**
     * Handles creating a new property.
     */
    private void createProperty() {
        System.out.println("\n--- Create Property ---");

        String name = helper.getNonEmptyString("Enter property name: ");

        if (system.propertyNameExists(name)) {
            System.out.println("That name already exists.");
            return;
        }

        PropertyType type = chooseType();

        boolean added = false;
        int[] days = new int[0];

        while (!added) {
            String input = helper.getNonEmptyString("Enter dates (comma-separated or 'all'): ");

            if (input.equalsIgnoreCase("all")) {
                days = new int[30];
                int i = 0;
                while (i < 30) {
                    days[i] = i + 1;
                    i = i + 1;
                }
                added = true;
            } else {
                String[] parts = input.split(",");
                ArrayList<Integer> dayList = new ArrayList<Integer>();

                int i = 0;
                while (i < parts.length) {
                    try {
                        int d = Integer.parseInt(parts[i].trim());
                        if (d >= 1 && d <= 30)
                            dayList.add(d);
                    } catch (Exception ignored) {}
                    i = i + 1;
                }

                if (!dayList.isEmpty()) {
                    days = new int[dayList.size()];
                    int j = 0;
                    while (j < dayList.size()) {
                        days[j] = dayList.get(j);
                        j = j + 1;
                    }
                    added = true;
                } else {
                    System.out.println("Please enter at least one valid day.");
                }
            }
        }

        int index = system.createProperty(name, type, days);

        if (index >= 0)
            System.out.println("Property created successfully.");
        else
            System.out.println("Unable to create property.");
    }

    /**
     * Lets the user select a property index.
     *
     * @return the index, or -1 if cancelled
     */
    private int chooseProperty() {
        int count = system.getPropertyCount();

        if (count == 0) {
            System.out.println("No properties found.");
            return -1;
        }

        int i = 0;
        while (i < count) {
            Property p = system.getProperty(i);
            System.out.printf("%d) %s (%s)%n",
                    i + 1,
                    p.getName(),
                    p.getType().getDisplayName());
            i = i + 1;
        }

        System.out.println("0) Cancel");
        int choice = helper.getValidInt("Select: ", 0, count);

        if (choice == 0)
            return -1;

        return choice - 1;
    }

    /**
     * Handles viewing a property's information.
     */
    private void viewProperty() {
        System.out.println("\n--- View Property ---");

        int index = chooseProperty();
        if (index < 0)
            return;

        Property p = system.getProperty(index);

        boolean viewing = true;

        while (viewing) {
            System.out.println("\nViewing: " + p.getName());
            System.out.println("1. Calendar Grid View");
            System.out.println("2. High-Level Info");
            System.out.println("3. Detailed Info");
            System.out.println("0. Back");

            int choice = helper.getValidInt("Enter choice: ", 0, 3);

            if (choice == 0)
                viewing = false;
            else if (choice == 1)
                calendarView.printCalendar(p);
            else if (choice == 2)
                showHighLevelInfo(p);
            else if (choice == 3)
                showDetailedInfo(p, index);
        }
    }

    /**
     * Displays high-level property information.
     *
     * @param p the property to display
     */
    private void showHighLevelInfo(Property p) {
        System.out.println("\n--- High-Level Info ---");
        System.out.println("Name: " + p.getName());
        System.out.println("Type: " + p.getType().getDisplayName());
        System.out.println("Available Dates: " + p.getAvailableDates().length);
        System.out.printf(Locale.US,
                "Estimated Earnings: PHP %.2f%n",
                p.getEstimatedEarnings());
    }

    /**
     * Displays detailed property information.
     *
     * @param p the property
     * @param index the property index
     */
    private void showDetailedInfo(Property p, int index) {
        boolean showing = true;

        while (showing) {
            System.out.println("\n--- Detailed Info ---");
            System.out.println("1. Specific Date");
            System.out.println("2. All Reservations");
            System.out.println("0. Back");

            int choice = helper.getValidInt("Enter choice: ", 0, 2);

            if (choice == 0)
                showing = false;
            else if (choice == 1)
                viewSpecificDate(p);
            else if (choice == 2)
                viewAllReservations(index);
        }
    }

    /**
     * Displays information for a single date.
     *
     * @param p the property
     */
    private void viewSpecificDate(Property p) {
        int day = helper.getValidInt("Enter date (1–30): ", 1, 30);
        Date d = p.getDateByDay(day);

        if (d == null) {
            System.out.println("Date not listed.");
        } else {
            double finalPrice = d.getFinalPrice(p.getType().getMultiplier());
            int percent = (int)(d.getEnvironmentalRate() * 100);

            System.out.printf(Locale.US,
                    "Day %d - Base: %.2f Final: %.2f Env: %d%% - %s%n",
                    day,
                    d.getPrice(),
                    finalPrice,
                    percent,
                    d.isAvailable() ? "Available" : "Reserved");
        }
    }

    /**
     * Shows all reservations for a property.
     *
     * @param index the property index
     */
    private void viewAllReservations(int index) {
        ArrayList<Reservation> list = system.getReservationsForProperty(index);

        if (list.isEmpty()) {
            System.out.println("No reservations.");
            return;
        }

        int i = 0;
        while (i < list.size()) {
            Reservation r = list.get(i);
            Property p = system.getProperty(index);

            double total = r.getTotalPrice(p);

            System.out.printf(Locale.US,
                    "%s | %d to %d | Total: %.2f%n",
                    r.getGuestName(),
                    r.getCheckIn(),
                    r.getCheckOut(),
                    total);

            double[] nights = r.getBreakdown(p);
            int j = 0;
            while (j < nights.length) {
                System.out.printf(Locale.US,
                        "  Night %d: %.2f%n",
                        j + 1, nights[j]);
                j = j + 1;
            }

            i = i + 1;
        }
    }

    /**
     * Handles the manage property menu.
     */
    private void manageProperty() {
        System.out.println("\n--- Manage Property ---");

        int index = chooseProperty();
        if (index < 0)
            return;

        boolean managing = true;

        while (managing) {
            System.out.println("\nManaging: " +
                    system.getProperty(index).getName());

            System.out.println("1. Change Name");
            System.out.println("2. Change Type");
            System.out.println("3. Update Base Price");
            System.out.println("4. Environmental Rates");
            System.out.println("5. Remove Property");
            System.out.println("0. Back");

            int choice = helper.getValidInt("Enter choice: ", 0, 5);

            if (choice == 0)
                managing = false;
            else if (choice == 1)
                changePropertyName(index);
            else if (choice == 2)
                changePropertyType(index);
            else if (choice == 3)
                updateBasePrice(index);
            else if (choice == 4)
                environmentalRateMenu(index);
            else if (choice == 5)
                removeProperty(index);
        }
    }

    /**
     * Changes a property’s name.
     */
    private void changePropertyName(int index) {
        String newName = helper.getNonEmptyString("Enter new name: ");

        boolean ok = system.changePropertyName(index, newName);

        if (ok)
            System.out.println("Name updated.");
        else
            System.out.println("Unable to change name.");
    }

    /**
     * Changes a property’s type.
     */
    private void changePropertyType(int index) {
        PropertyType type = chooseType();
        boolean ok = system.changePropertyType(index, type);

        if (ok)
            System.out.println("Type updated.");
        else
            System.out.println("Unable to update type.");
    }

    /**
     * Updates the base price of a property.
     */
    private void updateBasePrice(int index) {
        if (system.hasReservations(index)) {
            System.out.println("Cannot update price with reservations.");
            return;
        }

        double price = helper.getValidDouble("Enter new base price (>=100): ",
                100, 999999);

        boolean ok = system.updateBasePrice(index, price);

        if (ok)
            System.out.println("Base price updated.");
        else
            System.out.println("Unable to update base price.");
    }

    /**
     * Handles the environmental rate menu.
     *
     * @param index the property index
     */
    private void environmentalRateMenu(int index) {
        boolean running = true;

        while (running) {
            System.out.println("\n--- Environmental Rates ---");
            System.out.println("1. Set rate for a day");
            System.out.println("2. Set rate for all days");
            System.out.println("3. Set rate for a range of days");
            System.out.println("4. Randomize all rates");
            System.out.println("0. Back");

            int choice = helper.getValidInt("Enter choice: ", 0, 3);

            if (choice == 0)
                running = false;
            else if (choice == 1)
                setRateOneDay(index);
            else if (choice == 2)
                setRateAll(index);
            else if (choice == 3)
                setRateRange(index);
            else if (choice == 4)
                randomizeRates(index);
        }
    }

    /**
     * Sets the environmental rate for a specific date.
     */
    private void setRateOneDay(int index) {
        int day = helper.getValidInt("Enter date (1–30): ", 1, 30);
        double rate = helper.getValidDouble("Rate (0.80–1.20): ", 0.80, 1.20);

        boolean ok = system.setEnvironmentalRateForDate(index, day, rate);

        if (ok)
            System.out.println("Updated.");
        else
            System.out.println("Unable to update rate.");
    }

    /**
     * Sets the environmental rate for all listed dates.
     */
    private void setRateAll(int index) {
        double rate = helper.getValidDouble("Rate (0.80–1.20): ", 0.80, 1.20);

        boolean ok = system.setEnvironmentalRateForAllDates(index, rate);

        if (ok)
            System.out.println("Updated.");
        else
            System.out.println("Unable to update rate.");
    }

    /**
     * Randomizes the environmental rate for all listed dates.
     */
    private void randomizeRates(int index) {
        system.randomizeEnvironmentalRates(index);
        System.out.println("Rates randomized.");
    }

    /**
     * Attempts to remove a property.
     */
    private void removeProperty(int index) {
        if (system.hasReservations(index)) {
            System.out.println("Cannot remove property with reservations.");
            return;
        }

        boolean confirm = helper.confirm("Delete property? (Y/N): ");

        if (!confirm)
            return;

        boolean ok = system.removeProperty(index);

        if (ok)
            System.out.println("Property removed.");
        else
            System.out.println("Unable to remove property.");
    }

    /**
     * Lets the user choose a property type.
     *
     * @return the selected PropertyType
     */
    private PropertyType chooseType() {
        System.out.println("\nSelect Property Type:");
        System.out.println("1. Eco-Apartment");
        System.out.println("2. Sustainable House");
        System.out.println("3. Green Resort");
        System.out.println("4. Eco-Glamping");

        int choice = helper.getValidInt("Enter choice: ", 1, 4);
        return PropertyType.fromChoice(choice);
    }
    /**
     * Sets the environmental rate for a range of days.
     *
     * The user may enter input like:
     *   6-11
     *   12-15
     *
     * @param index the property index
     */
    private void setRateRange(int index) {

        String input = helper.getNonEmptyString("Enter range (ex: 6-11): ");

        int dash = input.indexOf('-');

        if (dash == -1) {
            System.out.println("Invalid format. Use start-end, like 6-11.");
            return;
        }

        try {
            int start = Integer.parseInt(input.substring(0, dash).trim());
            int end   = Integer.parseInt(input.substring(dash + 1).trim());

            if (start < 1 || end > 30 || start > end) {
                System.out.println("Invalid day range.");
                return;
            }

            double rate = helper.getValidDouble("Rate (0.80–1.20): ", 0.80, 1.20);

            boolean ok = system.setEnvironmentalRateForRange(index, start, end, rate);

            if (ok)
                System.out.println("Updated rate for days " + start + " to " + end + ".");
            else
                System.out.println("Unable to update rate for range.");

        } catch (NumberFormatException e) {
            System.out.println("Invalid range. Use numbers only.");
        }
    }

}

