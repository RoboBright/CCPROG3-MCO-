package Main;



import java.util.Scanner;

import Menu.MenuHelper;
import Menu.PropertyMenuManager;
import Menu.BookingMenuManager;
import Menu.CalendarView;
import System.PropertySystem;
/**
 * The Main class runs the Green Property Exchange program.
 * It creates the required managers and opens the main menu.
 */
public class Main {

    /**
     * The entry point of the program.
     *
     * @param args the command-line arguments (unused)
     */
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Core logic layer
        PropertySystem system = new PropertySystem();

        // Helpers and managers
        MenuHelper helper = new MenuHelper(scanner);
        CalendarView calendarView = new CalendarView();
        PropertyMenuManager propertyMenu = new PropertyMenuManager(system, helper, calendarView);
        BookingMenuManager bookingMenu = new BookingMenuManager(system, helper);

        boolean running = true;

        while (running) {
            System.out.println("\n===== GREEN PROPERTY EXCHANGE =====");
            System.out.println("1. Property Menu");
            System.out.println("2. Booking Menu");
            System.out.println("0. Exit");

            int choice = helper.getValidInt("Enter choice: ", 0, 2);

            if (choice == 0)
                running = false;
            else if (choice == 1)
                propertyMenu.openPropertyMenu();
            else if (choice == 2)
                bookingMenu.openBookingMenu();
        }

        System.out.println("Exiting system... Goodbye!");
        scanner.close();
    }
}
