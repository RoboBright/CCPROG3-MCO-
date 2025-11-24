import java.util.ArrayList;
import java.util.Locale;

/**
 * The BookingMenuManager class handles all reservation and booking menu actions.
 * It uses MenuHelper for user input and PropertySystem for booking logic.
 * This class does not contain any business logic.
 */
public class BookingMenuManager {

    private PropertySystem system;
    private MenuHelper helper;

    /**
     * Constructs a BookingMenuManager with the required helpers.
     *
     * @param system the PropertySystem used to manage reservations
     * @param helper the MenuHelper used for user input
     */
    public BookingMenuManager(PropertySystem system, MenuHelper helper) {
        this.system = system;
        this.helper = helper;
    }

    /**
     * Opens the booking menu.
     */
    public void openBookingMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n--- Booking Menu ---");
            System.out.println("1. Simulate Booking");
            System.out.println("2. View Reservations for a Property");
            System.out.println("3. Remove Reservation");
            System.out.println("0. Back");

            int choice = helper.getValidInt("Enter choice: ", 0, 3);

            if (choice == 0)
                running = false;
            else if (choice == 1)
                simulateBooking();
            else if (choice == 2)
                viewReservations();
            else if (choice == 3)
                removeReservation();
        }
    }

    /**
     * Lets the user select a property.
     *
     * @return the property index, or -1 if none selected
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
     * Simulates booking a reservation for a property.
     * The reservation is added only if all dates are available.
     */
    private void simulateBooking() {
        System.out.println("\n--- Simulate Booking ---");

        int propertyIndex = chooseProperty();
        if (propertyIndex < 0)
            return;

        String guest = helper.getNonEmptyString("Guest name: ");
        int checkIn = helper.getValidInt("Check-in (1–29): ", 1, 29);
        int checkOut = helper.getValidInt("Check-out (2–30): ", 2, 30);

        if (checkOut <= checkIn) {
            System.out.println("Invalid date range.");
            return;
        }

        boolean available = system.areDatesAvailable(propertyIndex, checkIn, checkOut);

        if (!available) {
            System.out.println("Some dates are unavailable.");
            return;
        }

        Reservation res = system.addReservation(propertyIndex, guest, checkIn, checkOut);

        if (res == null) {
            System.out.println("Unable to create reservation.");
        } else {
            System.out.println("Reservation successful!");

            Property p = system.getProperty(propertyIndex);
            double total = res.getTotalPrice(p);

            System.out.printf(Locale.US, "Total: %.2f%n", total);

            double[] nights = res.getBreakdown(p);
            int i = 0;
            while (i < nights.length) {
                System.out.printf(Locale.US, "  Night %d: %.2f%n",
                        i + 1, nights[i]);
                i = i + 1;
            }
        }
    }

    /**
     * Shows all reservations for a specific property.
     */
    private void viewReservations() {
        System.out.println("\n--- View Reservations ---");

        int index = chooseProperty();
        if (index < 0)
            return;

        ArrayList<Reservation> list = system.getReservationsForProperty(index);

        if (list.isEmpty()) {
            System.out.println("No reservations for this property.");
            return;
        }

        int i = 0;
        while (i < list.size()) {
            Reservation r = list.get(i);
            Property p = system.getProperty(index);

            double total = r.getTotalPrice(p);

            System.out.printf(Locale.US,
                    "%d) %s | %d to %d | Total: %.2f%n",
                    i + 1,
                    r.getGuestName(),
                    r.getCheckIn(),
                    r.getCheckOut(),
                    total);

            i = i + 1;
        }
    }

    /**
     * Removes a reservation from a property.
     */
    private void removeReservation() {
        System.out.println("\n--- Remove Reservation ---");

        int propertyIndex = chooseProperty();
        if (propertyIndex < 0)
            return;

        ArrayList<Reservation> list = system.getReservationsForProperty(propertyIndex);

        if (list.isEmpty()) {
            System.out.println("No reservations to remove.");
            return;
        }

        int i = 0;
        while (i < list.size()) {
            Reservation r = list.get(i);

            System.out.printf(Locale.US,
                    "%d) %s | %d to %d%n",
                    i + 1,
                    r.getGuestName(),
                    r.getCheckIn(),
                    r.getCheckOut());

            i = i + 1;
        }

        System.out.println("0) Cancel");

        int choice = helper.getValidInt("Select: ", 0, list.size());
        if (choice == 0)
            return;

        boolean ok = system.removeReservation(propertyIndex, choice - 1);

        if (ok)
            System.out.println("Reservation removed.");
        else
            System.out.println("Unable to remove reservation.");
    }
}
