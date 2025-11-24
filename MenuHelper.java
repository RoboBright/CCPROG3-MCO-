import java.util.Scanner;

/**
 * The MenuHelper class provides common input methods for menu-driven classes.
 * It handles numeric validation, string input, and yes/no confirmation.
 * This class does not contain any business logic.
 */
public class MenuHelper {

    private Scanner scanner;

    /**
     * Constructs a MenuHelper with a specific Scanner.
     *
     * @param scanner the Scanner used for user input
     */
    public MenuHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Asks for a non-empty string until one is entered.
     *
     * @param prompt the message shown to the user
     * @return the non-empty string entered by the user
     */
    public String getNonEmptyString(String prompt) {
        String s = "";
        boolean ok = false;

        while (!ok) {
            System.out.print(prompt);
            s = scanner.nextLine().trim();

            if (s.isEmpty()) {
                System.out.println("Input cannot be empty.");
            } else {
                ok = true;
            }
        }

        return s;
    }

    /**
     * Reads an integer inside a valid range.
     * If the user enters anything invalid, they are asked again.
     *
     * @param prompt the message shown to the user
     * @param min the minimum allowed value
     * @param max the maximum allowed value
     * @return a valid integer within the range
     */
    public int getValidInt(String prompt, int min, int max) {
        boolean ok = false;
        int value = min;

        while (!ok) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                int x = Integer.parseInt(input);
                if (x < min || x > max) {
                    System.out.println("Out of range.");
                } else {
                    value = x;
                    ok = true;
                }
            } catch (Exception e) {
                System.out.println("Invalid number.");
            }
        }

        return value;
    }

    /**
     * Reads a double value inside a valid range.
     * If the user enters anything invalid, they are asked again.
     *
     * @param prompt the message shown to the user
     * @param min the minimum allowed value
     * @param max the maximum allowed value
     * @return a valid double within the range
     */
    public double getValidDouble(String prompt, double min, double max) {
        boolean ok = false;
        double value = min;

        while (!ok) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                double x = Double.parseDouble(input);
                if (x < min || x > max) {
                    System.out.println("Out of range.");
                } else {
                    value = x;
                    ok = true;
                }
            } catch (Exception e) {
                System.out.println("Invalid number.");
            }
        }

        return value;
    }

    /**
     * Asks the user a yes/no question and repeats until valid.
     *
     * @param prompt the message shown to the user
     * @return true if the user answers yes; false if no
     */
    public boolean confirm(String prompt) {
        boolean valid = false;
        boolean result = false;

        while (!valid) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();

            if (s.equalsIgnoreCase("y") || s.equalsIgnoreCase("yes")) {
                result = true;
                valid = true;
            } else if (s.equalsIgnoreCase("n") || s.equalsIgnoreCase("no")) {
                result = false;
                valid = true;
            } else {
                System.out.println("Please enter Y or N.");
            }
        }

        return result;
    }
}
