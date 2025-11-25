package Main;

import Menu.MenuHelper;
import Menu.PropertyMenuManager;
import Menu.BookingMenuManager;


public class TextPrinter {
    public static void initProgram(boolean running, MenuHelper helper, PropertyMenuManager propertyMenu, BookingMenuManager bookingMenu){
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
    }
}
